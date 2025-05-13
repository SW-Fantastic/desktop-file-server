package org.swdc.rmdisk.service.verticle.dav;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.swdc.dependency.EventEmitter;
import org.swdc.dependency.event.AbstractEvent;
import org.swdc.dependency.event.Events;
import org.swdc.rmdisk.core.DAVUtils;
import org.swdc.rmdisk.core.SecureUtils;
import org.swdc.rmdisk.core.entity.*;
import org.swdc.rmdisk.service.ActivityService;
import org.swdc.rmdisk.service.DiskFileService;
import org.swdc.rmdisk.service.DiskLockService;
import org.swdc.rmdisk.service.SecureService;
import org.swdc.rmdisk.service.events.UserStateChangeEvent;

import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadPoolExecutor;

@Singleton
public class VertxDAVPutHandler implements Handler<RoutingContext>, EventEmitter {

    @Inject
    private SecureService secureService;

    @Inject
    private DiskFileService diskFileService;

    @Inject
    private DiskLockService lockService;

    @Inject
    private Logger logger;

    @Inject
    private ThreadPoolExecutor executor;

    @Inject
    private ActivityService activityService;

    private Events events;

    @Override
    public void handle(RoutingContext ctx) {

        HttpServerRequest request = ctx.request();
        HttpServerResponse response = ctx.response();

        try {

            User currentUser = secureService.requestAuth(request, response);
            if (currentUser == null) {
                // 用户尚未登录。
                return;
            }

            String path = URLDecoder.decode(request.path(), StandardCharsets.UTF_8)
                    .replaceAll("/+", "/");

            DiskFile target = diskFileService.getFileByPath(currentUser, path);
            DiskFolder folder = null;

            if (target == null) {
                String parent = path.substring(0, path.lastIndexOf("/"));
                if (parent.isBlank()) {
                    folder = diskFileService.getRoot(currentUser);
                } else {
                    folder = diskFileService.getFolderByPath(currentUser,parent);
                }

            } else {
                folder = target.getParent();
            }

            if (folder == null) {
                // no parent folder
                response.setStatusCode(409);
                response.setStatusMessage("Conflict");
                response.end();
                return;
            }

            Long contentLength = Long.parseLong(request.getHeader("Content-Length"));
            String lockTokenIf = request.getHeader("If");

            boolean isNewFile = target == null;

            if (contentLength > 0) {
                long usedSizeAfterUpload = currentUser.getUsedSize() + contentLength;
                if (usedSizeAfterUpload >= currentUser.getTotalSize()) {
                    // 超出空间限制
                    response.setStatusCode(507);
                    response.setStatusMessage("Insufficient Storage");
                    response.end();
                    return;
                }
                if (target == null) {
                    String name = path.substring(path.lastIndexOf("/") + 1);
                    target = diskFileService.createFile(folder,name);
                    if (target == null) {
                        response.setStatusCode(409);
                        response.setStatusMessage("Conflict");
                        response.end();
                        return;
                    }
                }

                String lockToken = DAVUtils.getLockToken(lockTokenIf);
                if(!lockService.writable(currentUser,lockToken,target)) {
                    response.setStatusCode(423);
                    response.setStatusMessage("Locked");
                    response.end();
                    return;
                }

                OutputStream os = diskFileService.getOutputStream(target);
                if (os == null) {
                    response.setStatusCode(500);
                    response.setStatusMessage("Internal Error");
                    response.end();
                    return;
                }

                DiskFile finalFile = target;
                // 处理上传
                request.handler(buf -> {
                    try {
                        // 这里写入数据
                        os.write(buf.getBytes());
                    } catch (Exception e) {
                        logger.error("failed to write file : ", e);
                    }
                }).endHandler(v -> {
                    try {

                        Long oldSize = finalFile.getFileSize();
                        // 关闭输出流
                        os.close();

                        // 交换文件，更新文件信息
                        if(!diskFileService.swapFile(finalFile) && !diskFileService.updateFileInfo(finalFile)) {
                            logger.error("failed to update file info : " + finalFile.getUuid());
                            response.setStatusCode(500);
                            response.setStatusMessage("Internal Error");
                            response.end();
                            return;
                        } else {
                            // 刷新用户信息
                            emit(new UserStateChangeEvent(currentUser));
                        }
                        // 返回结果
                        response.setStatusCode(200);
                        response.setStatusMessage("OK");
                        response.end();


                        activityService.createUploadActivity(
                                currentUser,
                                request.remoteAddress().hostAddress(),
                                oldSize,
                                finalFile.getFileSize(),
                                finalFile
                        );

                    } catch (Exception e) {

                        logger.error("failed to close files", e);

                        response.setStatusCode(500);
                        response.setStatusMessage("Internal Error");
                        response.end();

                    }
                }).exceptionHandler(e -> {
                    executor.submit(() -> {

                        try {
                            // 清理资源
                            os.close();
                            diskFileService.clearFailedResource(finalFile);
                            if (isNewFile) {
                                // 文件没有正确上传，需要清理文件
                                diskFileService.trashFile(finalFile.getId());
                            }
                        } catch (Exception ex) {
                            logger.error("failed to close files", ex);
                        }

                    });

                });

            } else {
                // 单纯的创建
                if (target != null) {
                    response.setStatusCode(201);
                    response.setStatusMessage("Created");
                    response.end();
                } else {
                    String name = path.substring(path.lastIndexOf("/") + 1);
                    target = diskFileService.createFile(folder,name);
                    if (target != null) {
                        activityService.createAddResourceActivity(
                                currentUser,
                                target,
                                request.remoteAddress().hostAddress()
                        );
                        response.setStatusCode(201);
                        response.setStatusMessage("Created");
                        response.end();
                    } else {
                        response.setStatusCode(409);
                        response.setStatusMessage("Conflict");
                        response.end();
                    }
                }
            }


        } catch (Exception e) {
            SecureUtils.failed(response);
            logger.error("failed to process request, unknown exception : ", e);
        }
    }

    @Override
    public <T extends AbstractEvent> void emit(T t) {
        events.dispatch(t);
    }

    @Override
    public void setEvents(Events events) {
        this.events = events;
    }


}
