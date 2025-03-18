package org.swdc.rmdisk.service.verticle.ftp;

import io.vertx.core.net.NetSocket;
import jakarta.inject.Inject;
import org.swdc.dependency.EventEmitter;
import org.swdc.dependency.event.AbstractEvent;
import org.swdc.dependency.event.Events;
import org.swdc.rmdisk.core.entity.User;
import org.swdc.rmdisk.core.ftp.*;
import org.swdc.rmdisk.service.FTPControl;
import org.swdc.rmdisk.service.UserManageService;
import org.swdc.rmdisk.service.events.UserStateChangeEvent;
import org.swdc.rmdisk.service.verticle.VertxFTPAbstractHandler;

public class FTPAuthHandler extends VertxFTPAbstractHandler implements EventEmitter {


    @Inject
    private UserManageService userManageService;

    private Events events;

    @FTPControl("USER")
    public void loginRequest(FTPSessionContext context, NetSocket sender, FTPClientMsg msg) {
        // 处理登录请求的逻辑
        if (msg.getArgs() == null || msg.getArgs().isBlank() ) {
            FTPMsg error = new FTPMsg(FTPRespCode.NOT_RECOGNIZED, "Syntax error");
            sender.write(error.prepare());
            return;
        }

        FTPSession session = getFTPSession(context,sender);
        String username = msg.getArgs();

        if (session.getSessionToken() != null && !session.getSessionToken().isBlank()) {
            FTPMsg reply = new FTPMsg(FTPRespCode.USER_LOGGED, "Already authenticated");
            sender.write(reply.prepare());
            return;
        }

        if(userManageService.checkUserExist(username)) {

            session.setUserName(username);

            FTPMsg reply = new FTPMsg(FTPRespCode.NEED_PASSWORD, "Username OK. need password");
            sender.write(reply.prepare());
            return;

        }

        FTPMsg notFound = new FTPMsg(FTPRespCode.NEED_ACCOUNT, "User not found");
        sender.write(notFound.prepare());
    }

    @FTPControl("PASS")
    public void passwordRequest(FTPSessionContext context, NetSocket sender, FTPClientMsg msg) {
        // 处理密码请求的逻辑
        FTPSession session = getFTPSession(context,sender);
        if (session.getUserName() == null || session.getUserName().isBlank()) {
            FTPMsg error = new FTPMsg(FTPRespCode.USER_NOT_LOGGED, "User not found");
            sender.write(error.prepare());
            return;
        }

        if (session.getSessionToken() != null && !session.getSessionToken().isBlank()) {
            FTPMsg reply = new FTPMsg(FTPRespCode.USER_LOGGED, "Already authenticated");
            sender.write(reply.prepare());
            return;
        }

        if (msg.getArgs() == null || msg.getArgs().isBlank()) {
            FTPMsg error = new FTPMsg(FTPRespCode.NOT_RECOGNIZED, "Syntax error");
            sender.write(error.prepare());
            return;
        }

        String password = msg.getArgs();
        if (password.isBlank()) {
            FTPMsg error = new FTPMsg(FTPRespCode.USER_NOT_LOGGED, "Password is required");
            sender.write(error.prepare());
            return;
        }
        try {

            String sessionKey = secureService.loginWithPassword(session.getUserName(), password);
            if (sessionKey == null) {
                FTPMsg error = new FTPMsg(FTPRespCode.USER_NOT_LOGGED, "Password is incorrect");
                sender.write(error.prepare());
                return;
            }

            User user = secureService.requestAuth(sessionKey);

            session.setUserId(user.getId());
            session.setSessionToken(sessionKey);
            session.setCurrentDirectory("/");

            FTPMsg reply = new FTPMsg(FTPRespCode.USER_LOGGED, "Logged in successfully");
            sender.write(reply.prepare());

            emit(new UserStateChangeEvent(user));

        } catch (Exception e) {
            logger.error("Error while logging in", e);
            FTPMsg error = new FTPMsg(FTPRespCode.NOT_EXECUTED, "Internal error");
            sender.write(error.prepare());
        }
    }

    @FTPControl("QUIT")
    public void quitRequest(FTPSessionContext context, NetSocket sender, FTPClientMsg msg) {
        // 处理退出请求的逻辑
        FTPSession session = getFTPSession(context,sender);
        User user = secureService.requestAuth(session.getSessionToken());
        if (user != null) {
            context.removeSession(
                    sender.remoteAddress().hostAddress()
            );
        }
        FTPMsg reply = new FTPMsg(FTPRespCode.SERVICE_CLOSING, "Goodbye");
        sender.write(reply.prepare());
        sender.end();

    }

    @Override
    public <T extends AbstractEvent> void emit(T t) {
        this.events.dispatch(t);
    }

    @Override
    public void setEvents(Events events) {
        this.events = events;
    }
}
