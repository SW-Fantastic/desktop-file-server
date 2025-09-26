package org.swdc.rmdisk.service.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.*;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.swdc.dependency.annotations.Prototype;
import org.swdc.rmdisk.service.SecureService;
import org.swdc.rmdisk.service.verticle.dav.*;
import org.swdc.rmdisk.service.verticle.http.AdminVertxHandlers;
import org.swdc.rmdisk.service.verticle.http.UsersVertxHandlers;

import java.util.HashSet;

@Prototype
public class VertxHttpVerticle extends AbstractVerticle {

    private int port;

    private HttpServer server;

    private Router router;

    private Logger logger = LoggerFactory.getLogger(VertxHttpVerticle.class);

    @Inject
    private VertxDAVPropFindHandler handler;

    @Inject
    private VertxDAVMkColHandler mkColHandler;

    @Inject
    private VertxDAVPatchPropHandler patchPropHandler;

    @Inject
    private VertxDAVMoveHandler moveHandler;

    @Inject
    private VertxDAVHeadHandler headHandler;

    @Inject
    private VertxDAVPutHandler putHandler;

    @Inject
    private VertxDAVGetHandler getHandler;

    @Inject
    private VertxDAVLockHandler lockHandler;

    @Inject
    private VertxDAVUnlockHandler unlockHandler;

    @Inject
    private VertxDAVDeleteHandler deleteHandler;

    @Inject
    private VertxWebResourceHandler webResourceHandler;

    @Inject
    private VertxWebAuthHandler authHandler;

    @Inject
    private UsersVertxHandlers usersVertxHandlers;

    @Inject
    private AdminVertxHandlers adminVertxHandlers;

    @Inject
    private SecureService secureService;


    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void start() throws Exception {

        HashSet<HttpMethod> methods = new HashSet<>();
        for (HttpMethod method : HttpMethod.values()){
            methods.add(method);
        }

        router = Router.router(vertx);
        router.route().handler(CorsHandler
                .create()
                .allowedMethods(methods)
                .allowedHeader("*")
        );

        router.route("/*").method(HttpMethod.OPTIONS).handler(ctx -> {
            HttpServerResponse response = ctx.response();
            response.putHeader("Allow","POST,PROPFIND,PROPPATCH,MKCOL,MOVE,PUT,LOCK,UNLOCK,GET,DELETE");
            response.putHeader("DAV","1,2");
            response.putHeader("Content-Length", "0");
            response.setStatusCode(200);
            response.end();
        });

        // Web Handlers
        router.route("/@web/*").method(HttpMethod.GET)
                .handler(webResourceHandler);

        router.route("/@auth/login").method(HttpMethod.POST)
                .handler(authHandler);

        usersVertxHandlers.initialize(router);
        adminVertxHandlers.initialize(router);

        // DAV Handlers

        router.route("/*").method(HttpMethod.PROPFIND)
                .handler(handler);

        router.route("/*").method(HttpMethod.MKCOL)
                .handler(mkColHandler);

        router.route("/*").method(HttpMethod.PROPPATCH)
                .handler(patchPropHandler);

        router.route("/*").method(HttpMethod.MOVE)
                .handler(moveHandler);

        router.route("/*").method(HttpMethod.LOCK)
                .handler(lockHandler);

        router.route("/*").method(HttpMethod.UNLOCK)
                .handler(unlockHandler);

        router.route("/*").method(HttpMethod.PUT)
                .handler(putHandler);

        router.route("/*").method(HttpMethod.GET)
                .handler(getHandler);

        router.route("/*").method(HttpMethod.HEAD)
                .handler(headHandler);

        router.route("/*").method(HttpMethod.DELETE)
                .handler(deleteHandler);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port)
                .onSuccess(rs -> {
                    this.server = rs;
                    logger.info("server is running with port : " + port);
                }).onFailure(e -> {
                    logger.error("failed to start http server : ", e);
                });

    }

    @Override
    public void stop() throws Exception {
        this.server.close();
        this.secureService.clear();
        logger.info("server has stopped.");
    }

}
