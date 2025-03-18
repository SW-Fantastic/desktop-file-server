package org.swdc.rmdisk.service.verticle.dav;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.swdc.rmdisk.core.SecureUtils;
import org.swdc.rmdisk.core.entity.DiskFile;
import org.swdc.rmdisk.core.entity.DiskFolder;
import org.swdc.rmdisk.core.entity.User;
import org.swdc.rmdisk.service.DiskFileService;
import org.swdc.rmdisk.service.SecureService;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class VertxDAVGetHandler implements Handler<RoutingContext> {


    @Inject
    private DiskFileService diskFileService;

    @Inject
    private SecureService secureService;

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

            // Path可能已经通过URLEncoder编码过，首先解码处理之
            String path = URLDecoder.decode(request.path(), StandardCharsets.UTF_8)
                    .replaceAll("/+", "/");
            DiskFile file = diskFileService.getFileByPath(currentUser,path);
            if (file == null) {
                DiskFolder folder = diskFileService.getFolderByPath(currentUser,path);
                if (folder == null) {
                    response.setStatusCode(404);
                    response.setStatusMessage("Not Found");
                    response.end();
                    return;
                }
                if (SecureUtils.isBrowser(request)) {
                    response.setStatusCode(302);
                    response.putHeader("Location", "/@web/");
                    response.end();
                }
                // 文件夹
                response.setStatusCode(200);
                response.end();
            } else {
                File target = diskFileService.getSystemFile(file);
                if (target == null) {
                    response.setStatusCode(404);
                    response.end();
                    return;
                }
                String range = request.getHeader("Range");
                if (range != null && !range.isBlank()) {
                    range = range.replace("bytes=", "");
                    long start = 0;
                    if (range.contains("-")) {
                        String[] split = range.split("-");
                        start = Long.parseLong(split[0]);
                        if (split.length == 2) {
                            long end = Long.parseLong(split[1]);
                            response.putHeader("Content-Range", "bytes " + start + "-" + end + "/" + file.getFileSize());
                            response.setChunked(true);
                            response.setStatusCode(206);
                            response.sendFile(target.getAbsolutePath(), start, end - start);
                            return;
                        }
                    } else {
                        start = Long.parseLong(range);
                    }
                    response.putHeader("Content-Range", "bytes " + start + "-/" + file.getFileSize());
                    response.setChunked(true);
                    response.setStatusCode(206);
                    response.sendFile(target.getAbsolutePath(), start, file.getFileSize() - start);
                    return;
                }
                response.putHeader("Accept-Ranges", "bytes");
                response.setStatusCode(200);
                response.sendFile(target.getAbsolutePath());
            }

        } catch (Exception e){
            SecureUtils.failed(response);
            logger.error("failed to sending file", e);
        }

    }
}
