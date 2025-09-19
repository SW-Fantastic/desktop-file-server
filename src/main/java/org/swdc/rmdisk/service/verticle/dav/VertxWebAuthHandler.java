package org.swdc.rmdisk.service.verticle.dav;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.swdc.dependency.EventEmitter;
import org.swdc.dependency.event.AbstractEvent;
import org.swdc.dependency.event.Events;
import org.swdc.rmdisk.core.dav.BarerLoginRequest;
import org.swdc.rmdisk.core.entity.User;
import org.swdc.rmdisk.service.SecureService;
import org.swdc.rmdisk.service.UserManageService;
import org.swdc.rmdisk.service.events.UserStateChangeEvent;

import java.nio.charset.StandardCharsets;

public class VertxWebAuthHandler implements Handler<RoutingContext>, EventEmitter {

    @Inject
    private Logger logger;

    @Inject
    private SecureService secureService;

    @Inject
    private UserManageService userManageService;

    private Events events;

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(RoutingContext ctx) {

        HttpServerResponse response = ctx.response();
        HttpServerRequest request = ctx.request();
        String length = request.getHeader("Content-Length");
        if (length == null || Integer.parseInt(length) == 0) {
            response.setStatusCode(400).end();
            return;
        }
        request.bodyHandler(body -> {
            try {

                String remoteAddress = request.remoteAddress().hostAddress();
                String content = body.toString(StandardCharsets.UTF_8);
                BarerLoginRequest loginRequest = mapper.readValue(content, BarerLoginRequest.class);
                User user = userManageService.findByName(loginRequest.getUsername());
                if (user == null) {
                    response.setStatusCode(404).end();
                    return;
                }

                String token = secureService.loginWithPassword(
                        remoteAddress,loginRequest.getUsername(), loginRequest.getPassword()
                );

                if (token != null) {
                    response.putHeader("Content-Type", "text/plain");
                    response.end(token);
                    emit(new UserStateChangeEvent(user));
                } else {
                    response.setStatusCode(403).end();
                }
            } catch (Exception e) {
                logger.error("Failed to read login request: ", e);
                response.setStatusCode(500).end();
            }
        });

    }

    @Override
    public <T extends AbstractEvent> void emit(T t) {
        events.dispatch(t);
    }

    @Override
    public void setEvents(Events events) {
        this.events = events;
    }
}
