package org.swdc.rmdisk.service.verticle.dav;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.swdc.rmdisk.core.DAVUtils;
import org.swdc.rmdisk.core.SecureUtils;
import org.swdc.rmdisk.core.entity.*;
import org.swdc.rmdisk.core.xmlns.XMLMapper;
import org.swdc.rmdisk.core.dav.locks.DLockDiscovery;
import org.swdc.rmdisk.core.dav.locks.DLockInfo;
import org.swdc.rmdisk.core.dav.locks.DLockTarget;
import org.swdc.rmdisk.core.dav.locks.DLockTimeout;
import org.swdc.rmdisk.service.DiskFileService;
import org.swdc.rmdisk.service.DiskLockService;
import org.swdc.rmdisk.service.SecureService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Singleton
public class VertxDAVLockHandler implements Handler<RoutingContext> {

    @Inject
    private Logger logger;

    @Inject
    private DiskFileService diskFileService;

    @Inject
    private DiskLockService diskLockService;

    @Inject
    private SecureService secureService;


    @Override
    public void handle(RoutingContext ctx) {

        HttpServerRequest request = ctx.request();
        HttpServerResponse response = ctx.response();

        try {

            User currentUser = secureService.requestAuth(request,response);
            if (currentUser == null) {
                // 用户尚未登录。
                return;
            }

            String lockTokenIf = request.getHeader("If");
            if (lockTokenIf != null && !lockTokenIf.isBlank()) {
                // do refresh lock
                String lockToken = DAVUtils.getLockToken(lockTokenIf);
                LockedResource refreshed = diskLockService.refreshLock(currentUser,lockToken);
                if (refreshed == null) {
                    response.setStatusCode(412);
                    response.end();
                    return;
                }

                DLockInfo lockInfo = new DLockInfo();
                lockInfo.setLockRoot(new DLockTarget(URLDecoder.decode(request.absoluteURI(),StandardCharsets.UTF_8)));
                lockInfo.setLockType(refreshed.getLockType());
                lockInfo.setLockScope(refreshed.getLockScope());
                lockInfo.setOwner(new DLockTarget(currentUser.getUsername()));
                lockInfo.setLockToken(new DLockTarget("urn:uuid:" + refreshed.getCacheId()));
                lockInfo.setDepth(refreshed.getResourceType() == DiskFolder.class ? "infinity" : "0");
                lockInfo.setTimeout(new DLockTimeout(60 * 60 * 24L));

                DLockDiscovery dLockDiscovery = new DLockDiscovery(
                        Collections.singletonList(lockInfo)
                );

                response.end(XMLMapper.writeObjectAsString(dLockDiscovery));


            } else {
                request.body().onComplete(buf -> {

                    if (buf.failed()) {
                        logger.error("Can not read request body:");
                        response.setStatusCode(400);
                        response.setStatusMessage("Bad request");
                        response.end();
                        return;
                    }

                    doRequestLock(request,response,currentUser,buf.result());
                });
            }

        } catch (Exception e) {
            SecureUtils.failed(response);
            logger.error("Failed to process lock request ", e);
        }
    }

    private void doRequestLock(
            HttpServerRequest request,
            HttpServerResponse response,
            User currentUser,
            Buffer requestBody
    ) {
        try {

            String xmlLockRequest = requestBody.toString();
            DLockInfo info = XMLMapper.readObjectFromString(DLockInfo.class,xmlLockRequest);


            // Path可能已经通过URLEncoder编码过，首先解码处理之
            String path = URLDecoder.decode(request.path(), StandardCharsets.UTF_8)
                    .replaceAll("/+", "/");
            DiskFile file = diskFileService.getFileByPath(currentUser,path);

            DiskResource resource = null;

            if (file == null) {
                // 此Path不存在File的时候
                DiskFolder folder = diskFileService.getFolderByPath(currentUser,path);
                if (folder == null) {
                    // 此Path不存在Folder的时候。
                    String name = path.substring(path.lastIndexOf("/") + 1);
                    String parent = path.substring(0, path.lastIndexOf("/"));
                    DiskFolder parentFolder = diskFileService.getFolderByPath(currentUser,parent);
                    if (parentFolder == null) {
                        // 规范虽然说明需要再不存在的时候创建，但是如果就连Parent也不存在，那么此时应该不能继续创建它。
                        response.setStatusCode(409);
                        response.setStatusMessage("Conflict");
                        response.end();
                        return;
                    }

                    // 按照规范，文件或者目录不存在的时候，应当在Lock时创建之。
                    if (name.indexOf(".") > 0) {
                        // 推测此Path的目标为文件，因为存在拓展名
                        resource = diskFileService.createFile(parentFolder,name);

                    } else {
                        // 推测此Path的目标为文件夹，因为没有拓展名。
                        resource = diskFileService.createFolder(parentFolder,name);
                    }

                } else {
                    resource = folder;
                }

            } else {
                resource = file;
            }

            LockedResource locked = diskLockService.tryLock(
                    currentUser,info,resource
            );

            if (locked == null) {

                response.setStatusCode(432);
                response.setStatusMessage("Locked");
                response.end();

            } else {

                DLockInfo lockInfo = new DLockInfo();
                lockInfo.setLockRoot(new DLockTarget(URLDecoder.decode(request.absoluteURI(),StandardCharsets.UTF_8)));
                lockInfo.setLockType(locked.getLockType());
                lockInfo.setLockScope(locked.getLockScope());
                lockInfo.setOwner(new DLockTarget(currentUser.getUsername()));
                lockInfo.setLockToken(new DLockTarget("urn:uuid:" + locked.getCacheId()));
                lockInfo.setDepth(locked.getResourceType() == DiskFolder.class ? "infinity" : "0");
                lockInfo.setTimeout(new DLockTimeout(60 * 60 * 24L));

                DLockDiscovery dLockDiscovery = new DLockDiscovery(
                        Collections.singletonList(lockInfo)
                );

                response.end(XMLMapper.writeObjectAsString(dLockDiscovery));

            }

        } catch (Exception e) {
            logger.error("failed to process lock request", e);
        }
    }

}
