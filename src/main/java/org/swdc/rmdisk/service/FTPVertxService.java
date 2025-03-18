package org.swdc.rmdisk.service;

import io.vertx.core.Vertx;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.swdc.dependency.Dynamic;
import org.swdc.rmdisk.core.ServerConfigure;
import org.swdc.rmdisk.service.verticle.VertxFTPVerticle;

import java.util.function.Consumer;

public class FTPVertxService extends Dynamic {

    private volatile boolean inProgress = false;

    private VertxFTPVerticle verticle;

    private Vertx vertx = Vertx.vertx();

    @Inject
    private ServerConfigure config;

    @Inject
    private Logger logger;

    public boolean isStarted() {
        if (verticle != null && inProgress) {
            return false;
        }
        return verticle != null;
    }

    public void stopService(Consumer<Boolean> callback) {

        if (verticle == null || vertx == null || inProgress) {
            return;
        }

        inProgress = true;
        vertx.undeploy(verticle.deploymentID(),v -> {

            if (v.failed()) {
                inProgress = false;
                logger.error("failed to close service ", v.cause());
                callback.accept(false);
                return;
            }

            inProgress = false;
            verticle = null;
            callback.accept(true);

        });

    }

    public void startService(Consumer<Boolean> callback) {

        if (inProgress) {
            return;
        }

        inProgress = true;
        if (vertx == null) {
            vertx = Vertx.vertx();
        }

        if (verticle == null) {

            verticle = getByType(VertxFTPVerticle.class);
            verticle.setPort(Integer.parseInt(config.getFtpServerPort()));
            vertx.deployVerticle(verticle, v -> {

                inProgress = false;
                if (v.failed()) {
                    logger.error("failed to startup service " ,v.cause());
                    verticle = null;
                }

                callback.accept(v.succeeded());
            });


        } else {

            vertx.undeploy(verticle.deploymentID(),v -> {

                if (v.failed()) {
                    inProgress = false;
                    logger.error("failed to close service ", v.cause());
                    callback.accept(false);
                    return;
                }

                inProgress = false;
                verticle = null;
                startService(callback);

            });

        }

    }

}
