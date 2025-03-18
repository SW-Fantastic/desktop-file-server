package org.swdc.rmdisk.service.verticle;

import io.vertx.core.net.NetSocket;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.swdc.ours.common.annotations.AnnotationDescription;
import org.swdc.ours.common.annotations.Annotations;
import org.swdc.rmdisk.core.entity.User;
import org.swdc.rmdisk.core.ftp.FTPMsg;
import org.swdc.rmdisk.core.ftp.FTPRespCode;
import org.swdc.rmdisk.core.ftp.FTPSession;
import org.swdc.rmdisk.core.ftp.FTPSessionContext;
import org.swdc.rmdisk.service.FTPControl;
import org.swdc.rmdisk.service.SecureService;

import java.lang.reflect.Method;

public abstract class VertxFTPAbstractHandler {

    public static final Logger logger = LoggerFactory.getLogger(VertxFTPAbstractHandler.class);

    @Inject
    protected SecureService secureService;

    protected FTPSession getFTPSession(FTPSessionContext context , NetSocket sender) {
        String sessionKey = sender.remoteAddress().hostAddress();
        return context.getSession(sessionKey);
    }

    public void initialize(FTPSessionContext context) {

        Method[] methods = this.getClass().getDeclaredMethods();
        for (Method method : methods) {
            AnnotationDescription annotation = Annotations.findAnnotation(method, FTPControl.class);
            if (annotation == null) {
                continue;
            }

            String[] commands = annotation.getProperty(String[].class, "value");
            for (String command : commands) {
                context.registerCommandHandler(command, (sender, msg) -> {
                    try {
                        method.invoke(this,context, sender, msg);
                    } catch (Exception e) {
                        logger.error("Error invoking method {}", method.getName(), e);
                    }
                });
            }
        }

    }

}
