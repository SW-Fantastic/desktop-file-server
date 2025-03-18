package org.swdc.rmdisk.service.verticle.dav;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.swdc.rmdisk.core.SecureUtils;
import org.swdc.rmdisk.core.entity.User;
import org.swdc.rmdisk.service.SecureService;

import java.nio.charset.StandardCharsets;

/**
 * 尚未完成的handler，
 * 用来更新文件或者目录的属性。
 */
@Singleton
public class VertxDAVPatchPropHandler implements Handler<RoutingContext> {

    @Inject
    private Logger logger;

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

            request.body().onSuccess(buffer -> {
                logger.warn("body: " + buffer.toString(StandardCharsets.UTF_8));
            });
            logger.warn("body: " + ctx.body().asString());
            response.setStatusCode(500);
            response.end();

        } catch (Exception e) {
            SecureUtils.failed(response);
            logger.error("failed to process request, unknown exception : ", e);
        }
    }

}
