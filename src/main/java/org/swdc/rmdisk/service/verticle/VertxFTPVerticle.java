package org.swdc.rmdisk.service.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.swdc.dependency.annotations.EventListener;
import org.swdc.dependency.annotations.Prototype;
import org.swdc.rmdisk.core.entity.User;
import org.swdc.rmdisk.core.ftp.*;
import org.swdc.rmdisk.service.SecureService;
import org.swdc.rmdisk.service.UserManageService;
import org.swdc.rmdisk.service.events.UserStateChangeEvent;
import org.swdc.rmdisk.service.verticle.ftp.FTPAuthHandler;
import org.swdc.rmdisk.service.verticle.ftp.FTPFilesHandler;
import org.swdc.rmdisk.service.verticle.ftp.FTPFolderHandler;
import org.swdc.rmdisk.service.verticle.ftp.FTPSessionHandler;

import java.nio.charset.StandardCharsets;

@Prototype
public class VertxFTPVerticle extends AbstractVerticle {


    @Inject
    private UserManageService userManageService;

    @Inject
    private SecureService secureService;

    @Inject
    private FTPAuthHandler authHandler;

    @Inject
    private FTPSessionHandler sessionHandler;

    @Inject
    private FTPFolderHandler foldersHandler;

    @Inject
    private FTPFilesHandler filesHandler;

    @Inject
    private Logger logger;

    private NetServer server;

    private FTPSessionContext context;

    private int port;

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    @EventListener(type = UserStateChangeEvent.class)
    public void onUserStateChange(UserStateChangeEvent event) {
        User user = event.getUser();
        if (user == null) {
            context.removeAllSessions();
            return;
        }
        if(!secureService.isOnline(user.getId())) {
            context.removeSessionById(user.getId());
        }
    }

    @Override
    public void start() throws Exception {

        context = new FTPSessionContext();
        context.setThirdPartyContext(vertx);

        authHandler.initialize(context);
        sessionHandler.initialize(context);
        foldersHandler.initialize(context);
        filesHandler.initialize(context);

        NetServer server = vertx.createNetServer();
        server.connectHandler(socket -> {

            FTPMsg authMsg = new FTPMsg(FTPRespCode.SERVICE_READY, "Welcome to FTP Server");
            socket.write(authMsg.prepare());

            socket.handler(buf -> handleClientMessage(buf,socket));
            socket.closeHandler(v -> handleSocketClose(socket));

        });
        server.listen(port).onSuccess(v -> {
            this.server = v;
            logger.info("FTP server started on port {}", port);
        });
    }

    private void handleSocketClose(NetSocket socket) {
        context.removeSession(socket.remoteAddress().hostAddress());
    }

    private void handleClientMessage(Buffer buf, NetSocket socket) {
        String commandReceived = buf.toString(StandardCharsets.UTF_8);
        String[] commands = commandReceived.split("\r\n");

        for (String command : commands) {

            command = command.replaceAll("\r\n", "");
            if (command.isEmpty()) {
                continue;
            }

            logger.info("Command received: {}", command);

            String action = null;
            if (!command.contains(" ")) {
                action = command.replaceAll("\r\n", "");
            } else {
                action = command.substring(0, command.indexOf(" "));
            }

            action = action.toUpperCase();
            String param = "";
            if (command.contains(" ")) {
                param = command.substring(command.indexOf(" ") + 1);
            }

            FTPClientMsg clientMsg = new FTPClientMsg(action.toUpperCase(), param);

            FTPMessageHandler handler = context.getCommandHandler(action);
            if (handler == null) {
                FTPMsg error = new FTPMsg(FTPRespCode.NOT_RECOGNIZED, "Command not recognized");
                socket.write(error.prepare());
                logger.warn("Command not impl: {}", command);
                return;
            }
            handler.handle(socket, clientMsg);
        }
    }


    @Override
    public void stop() throws Exception {
        this.server.close();
        this.secureService.clear();
        logger.info("FTP server stopped");
    }
}
