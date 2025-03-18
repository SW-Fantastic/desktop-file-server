package org.swdc.rmdisk.service.verticle.dav;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.swdc.rmdisk.core.DAVUtils;
import org.swdc.rmdisk.core.SecureUtils;
import org.swdc.rmdisk.core.entity.User;
import org.swdc.rmdisk.service.DiskFileService;
import org.swdc.rmdisk.service.DiskLockService;
import org.swdc.rmdisk.service.SecureService;

public class VertxDAVUnlockHandler implements Handler<RoutingContext> {

    @Inject
    private Logger logger;

    @Inject
    private DiskFileService diskFileService;

    @Inject
    private DiskLockService lockService;

    @Inject
    private SecureService secureService;


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


            String lockTokenHeader = request.getHeader("Lock-Token");
            if (lockTokenHeader == null || lockTokenHeader.isBlank()) {
                response.setStatusCode(400);
                response.setStatusMessage("Bad request");
                response.end();
                return;
            }

            String lockToken = DAVUtils.getLockToken(lockTokenHeader);

            if(lockService.unlock(currentUser,lockToken)) {
                response.setStatusCode(204);
                response.end();
            } else {
                response.setStatusCode(409);
                response.end();
            }

        } catch (Exception e) {
            SecureUtils.failed(response);
            logger.error("failed to process unlock request: ", e);
        }

    }

}
