package org.swdc.rmdisk.service.verticle;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.apache.tika.Tika;
import org.slf4j.Logger;

import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Singleton
public class VertxWebResourceHandler implements Handler<RoutingContext> {

    @Inject
    private Logger logger;

    private Cache<String,Buffer> cache = Caffeine
            .newBuilder()
            .weakValues()
            .build();


    @Override
    public void handle(RoutingContext ctx) {

        HttpServerRequest request = ctx.request();
        HttpServerResponse response = request.response();
        String path = URLDecoder.decode(request.path(), StandardCharsets.UTF_8);
        String name = path.substring(path.lastIndexOf('/') + 1);

        if (!name.contains(".")) {
            if (!path.endsWith("/")) {
                path = path + "/";
            }
            path = path + "index.html";
            name = "index.html";
        }

        Tika tika = new Tika();
        String contentType = tika.detect(name);
        final String thePath = path;

        Buffer buffer = cache.get(thePath, (key) -> {
            try {
                Module module = this.getClass().getModule();
                String resource = thePath.replace("/@web", "/webcontent");

                InputStream is = module.getResourceAsStream(resource);
                if (is == null) {
                    return null;
                }

                byte[] data = is.readAllBytes();
                is.close();

                return Buffer.buffer(data);

            } catch (Exception e) {
                logger.error("handle web resource error", e);
                return null;
            }
        });

        if (buffer == null) {
            request.response().setStatusCode(404).end();
        } else {
            response.putHeader("Content-Type", contentType);
            response.end(buffer);
        }

    }

}
