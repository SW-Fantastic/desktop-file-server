package org.swdc.rmdisk.service.verticle.dav;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.swdc.rmdisk.core.SecureUtils;
import org.swdc.rmdisk.core.entity.*;
import org.swdc.rmdisk.service.ActivityService;
import org.swdc.rmdisk.service.DiskFileService;
import org.swdc.rmdisk.service.SecureService;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * 用来移动或者重命名文件和目录。
 */
public class VertxDAVMoveHandler implements Handler<RoutingContext> {

    @Inject
    private SecureService secureService;

    @Inject
    private DiskFileService diskFileService;

    @Inject
    private ActivityService activityService;

    @Inject
    private Logger logger;

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

            if (request.getHeader("Destination") == null) {
                response.setStatusMessage("Bad request");
                response.setStatusCode(400);
                response.end();
                return;
            }

            String path = URLDecoder.decode(request.path(), StandardCharsets.UTF_8)
                    .replaceAll("/+", "/");

            String destination = new URI(request.getHeader("Destination")).getPath()
                    .replaceAll("/+", "/");

            String targetLocation = destination.substring(0, destination.lastIndexOf("/"));
            String oldLocation = path.substring(0,path.lastIndexOf("/"));

            if (targetLocation.equals(oldLocation)) {

                // rename
                String newName = destination.substring(destination.lastIndexOf("/") + 1);
                DiskFile file = diskFileService.getFileByPath(currentUser,path);
                if(file == null) {
                    DiskFolder folder = diskFileService.getFolderByPath(currentUser,path);
                    if (folder == null) {
                        // no folder
                        response.setStatusCode(404);
                        response.setStatusMessage("Not Found");
                        response.end();
                        return;
                    }
                    String oldName = folder.getName();
                    folder = diskFileService.renameFolder(folder,newName);
                    if (folder == null) {
                        response.setStatusCode(409);
                        response.setStatusMessage("Conflict");
                        response.end();
                    } else {

                        activityService.createRenameActivity(
                                currentUser,
                                oldName,
                                newName,
                                request.remoteAddress().hostAddress(),
                                folder
                        );

                        response.setStatusCode(201);
                        response.setStatusMessage("Created");
                        response.end();
                    }
                } else {
                    String oldName = file.getName();
                    file = diskFileService.renameFile(file,newName);
                    if (file == null) {
                        response.setStatusCode(409);
                        response.setStatusMessage("Conflict");
                        response.end();
                    } else {

                        activityService.createRenameActivity(
                                currentUser,
                                oldName,
                                newName,
                                request.remoteAddress().hostAddress(),
                                file
                        );

                        response.putHeader("Location", targetLocation + "/" + newName);
                        response.setStatusCode(201);
                        response.setStatusMessage("Created");
                        response.end();
                    }
                }
            } else {

                // move
                DiskFolder targetFolder = diskFileService.getFolderByPath(currentUser,targetLocation);
                if (targetFolder == null) {
                    if (targetLocation.isBlank()) {
                        targetFolder = diskFileService.getRoot(currentUser);
                    } else {
                        // no such target folder.
                        response.setStatusCode(404);
                        response.setStatusMessage("Not Found");
                        response.end();
                        return;
                    }
                }

                DiskFile srcFile = diskFileService.getFileByPath(currentUser,path);
                if (srcFile == null) {
                    DiskFolder srcFolder = diskFileService.getFolderByPath(currentUser,path);
                    if (srcFolder == null) {
                        // no source resource.
                        response.setStatusCode(404);
                        response.setStatusMessage("Not Found");
                        response.end();
                        return;
                    }

                    DiskFolder parentOld = srcFolder.getParent();
                    DiskFolder result = diskFileService.moveFolderInto(srcFolder,targetFolder);
                    if (result == null) {
                        response.setStatusCode(409);
                        response.setStatusMessage("Conflict");
                        response.end();
                    } else {

                        activityService.createMoveActivity(
                                currentUser,
                                parentOld,
                                targetFolder,
                                request.remoteAddress().hostAddress(),
                                result
                        );

                        response.setStatusCode(201);
                        response.setStatusMessage("Created");
                        response.end();
                    }
                } else {

                    DiskFolder parentOld = srcFile.getParent();
                    DiskFile result = diskFileService.moveFileInto(srcFile,targetFolder);

                    if (result == null) {
                        response.setStatusCode(409);
                        response.setStatusMessage("Conflict");
                        response.end();
                    } else {

                        activityService.createMoveActivity(
                                currentUser,
                                parentOld,
                                targetFolder,
                                request.remoteAddress().hostAddress(),
                                result
                        );

                        response.setStatusCode(201);
                        response.setStatusMessage("Created");
                        response.end();
                    }

                }
            }

        } catch (Exception e) {
            SecureUtils.failed(response);
            logger.error("failed to process request, unknown exception : ", e);
        }
    }


}
