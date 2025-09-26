package org.swdc.rmdisk.service.verticle.dav;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.swdc.rmdisk.core.DAVUtils;
import org.swdc.rmdisk.core.SecureUtils;
import org.swdc.rmdisk.core.entity.ActivityType;
import org.swdc.rmdisk.core.entity.DiskFolder;
import org.swdc.rmdisk.core.entity.User;
import org.swdc.rmdisk.service.ActivityService;
import org.swdc.rmdisk.service.DiskFileService;
import org.swdc.rmdisk.service.DiskLockService;
import org.swdc.rmdisk.service.SecureService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * 本Handler用于处理WebDAV的MKCOL请求，
 * 该请求的目的是创建一个新的目录。
 */
@Singleton
public class VertxDAVMkColHandler implements Handler<RoutingContext> {

    @Inject
    private Logger logger;

    @Inject
    private SecureService secureService;

    @Inject
    private DiskFileService diskFileService;

    @Inject
    private DiskLockService lockService;

    @Inject
    private ActivityService activityService;

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

            // Path可能已经通过URLEncoder编码过，首先解码处理之
            String path = URLDecoder.decode(request.path(), StandardCharsets.UTF_8)
                    .replaceAll("/+", "/");
            if (path.endsWith("/")) {
                path = path.substring(0,path.length() - 1);
            }

            DiskFolder folder = diskFileService
                    .getFolderByPath(currentUser,path);

            if (folder != null) {
                // 文件夹已经存在。
                response.setStatusCode(201);
                response.setStatusMessage("Created");
                response.end();
                return;
            } else {
                int lastPath = path.lastIndexOf("/");
                String parent = path.substring(0,lastPath);
                String name = path.substring(lastPath + 1);

                DiskFolder parentFolder = diskFileService.getFolderByPath(currentUser,parent);
                if (parentFolder == null) {
                    if (parent.isBlank()) {
                        // root folder
                        parentFolder = diskFileService.getRoot(currentUser,true);
                    } else {
                        // no such parent folder
                        // 服务器不应当自动创建缺少的中间目录
                        response.setStatusCode(409);
                        response.setStatusMessage("Conflict");
                        response.end();
                        return;
                    }
                }

                String lockTokenIf = request.getHeader("If");
                String lockToken = DAVUtils.getLockToken(lockTokenIf);
                if (!lockService.writable(currentUser,lockToken,parentFolder)) {
                    response.setStatusCode(423);
                    response.setStatusMessage("Locked");
                    response.end();
                    return;
                }

                DiskFolder created = diskFileService.createFolder(parentFolder,name);
                if (created != null) {

                    activityService.createAddResourceActivity(
                            currentUser,
                            created,
                            SecureUtils.remoteAddress(request)
                    );

                    // 目录已经被创建了。
                    response.setStatusCode(201);
                    response.setStatusMessage("Created");
                    response.end();
                    return;
                }
                response.setStatusCode(400);
                response.setStatusMessage("Bad request");
                response.end();
            }

        } catch (Exception e) {
            SecureUtils.failed(response);
            logger.error("failed to process request, unknown exception : ", e);
        }

    }

}
