package org.swdc.rmdisk.service.verticle.dav;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.swdc.dependency.EventEmitter;
import org.swdc.dependency.event.AbstractEvent;
import org.swdc.dependency.event.Events;
import org.swdc.rmdisk.core.DAVUtils;
import org.swdc.rmdisk.core.SecureUtils;
import org.swdc.rmdisk.core.dav.ResponseStatus;
import org.swdc.rmdisk.core.entity.*;
import org.swdc.rmdisk.core.xmlns.XMLMapper;
import org.swdc.rmdisk.core.dav.multiple.DMultiStatus;
import org.swdc.rmdisk.core.dav.multiple.DPropertyStatus;
import org.swdc.rmdisk.core.dav.multiple.DResponse;
import org.swdc.rmdisk.service.ActivityService;
import org.swdc.rmdisk.service.DiskFileService;
import org.swdc.rmdisk.service.DiskLockService;
import org.swdc.rmdisk.service.SecureService;
import org.swdc.rmdisk.service.events.UserStateChangeEvent;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class VertxDAVDeleteHandler implements Handler<RoutingContext>, EventEmitter {

    @Inject
    private DiskFileService diskFileService;

    @Inject
    private SecureService secureService;

    @Inject
    private DiskLockService lockService;

    @Inject
    private ActivityService activityService;

    @Inject
    private Events events;

    @Inject
    private Logger logger;

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

            // Path可能已经通过URLEncoder编码过，首先解码处理之
            String path = URLDecoder.decode(request.path(), StandardCharsets.UTF_8)
                    .replaceAll("/+", "/");;
            String uri = URLDecoder.decode(request.absoluteURI(),StandardCharsets.UTF_8)
                    .replaceAll("/+", "/");;

            DiskFile diskFile = diskFileService.getFileByPath(currentUser,path);
            String lockTokenIf = request.getHeader("If");

            if (diskFile == null) {

                DiskFolder folder = diskFileService.getFolderByPath(currentUser,path);
                if (folder == null) {
                    response.setStatusCode(404);
                    response.setStatusMessage("Not Found");
                    response.end();
                    return;
                }

                List<DResponse> responses = new ArrayList<>();

                // 删除文件夹
                List<DiskFolder> subFolders = new ArrayList<>();
                String lockToken = DAVUtils.getLockToken(lockTokenIf);
                diskFileService.collectionResources(folder,subFolders);
                boolean hasUnRemovableFile = false;
                for (DiskFolder diskFolder: subFolders) {
                    List<DiskFile> files = diskFileService.getFilesByParent(diskFolder);
                    if (files != null) {
                        for (DiskFile file: files) {
                            if (!lockService.writable(currentUser, lockToken,file)) {

                                DPropertyStatus state = new DPropertyStatus();
                                state.setStatus(ResponseStatus.LOCKED);
                                responses.add(new DResponse(uri + "/" + file.getName(),state));

                                hasUnRemovableFile = true;
                            } else {
                                if(diskFileService.trashFile(file.getId())) {
                                    activityService.createDeleteActivity(
                                            currentUser,
                                            file,
                                            request.remoteAddress().hostAddress()
                                    );
                                }
                            }
                        }
                    }

                    if(diskFileService.trashFolder(diskFolder.getId())) {
                        activityService.createDeleteActivity(
                                currentUser,
                                diskFolder,
                                request.remoteAddress().hostAddress()
                        );
                    }

                }

                if (hasUnRemovableFile) {
                    response.setStatusCode(207);
                    DMultiStatus multipleStatus = new DMultiStatus(responses);
                    response.end(XMLMapper.writeObjectAsString(multipleStatus));
                } else {
                    response.setStatusCode(204);
                    response.end();
                }

                emit(new UserStateChangeEvent(currentUser));

            } else {

                if (!lockService.writable(currentUser,DAVUtils.getLockToken(lockTokenIf),diskFile)) {
                    response.setStatusCode(423);
                    response.setStatusMessage("Locked");
                    response.end();
                } else {
                    if(diskFileService.trashFile(diskFile.getId())){
                        response.setStatusCode(204);
                        response.end();
                        emit(new UserStateChangeEvent(currentUser));
                    } else {
                        response.setStatusCode(500);
                        response.end();
                    }
                }

            }

        } catch (Exception e) {
            SecureUtils.failed(response);
            logger.error("failed to process delete request", e);
        }
    }

    @Override
    public <T extends AbstractEvent> void emit(T t) {
        this.events.dispatch(t);
    }

    @Override
    public void setEvents(Events events) {
        this.events = events;
    }

}
