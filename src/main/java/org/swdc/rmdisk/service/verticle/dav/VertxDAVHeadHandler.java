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

/**
 * VertxDAVHeadHandler 负责处理HTTP HEAD请求，
 * 用以获取文件或文件夹的元数据信息。
 *
 * 这种类型的请求一般用于检查文件是否存在，而不实际传输文件内容。
 *
 */
public class VertxDAVHeadHandler  implements Handler<RoutingContext> {

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
                    response.setStatusCode(200);
                    response.end();
                    return;
                }
                response.putHeader("Content-Length", String.valueOf(target.length()));
                response.putHeader("Accept-Ranges", "bytes");
                response.setStatusCode(200);
                response.end();
            }

        } catch (Exception e){
            SecureUtils.failed(response);
            logger.error("failed to sending file", e);
        }
    }
}
