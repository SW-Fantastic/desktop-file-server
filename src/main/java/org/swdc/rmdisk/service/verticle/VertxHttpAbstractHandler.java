package org.swdc.rmdisk.service.verticle;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.swdc.ours.common.annotations.AnnotationDescription;
import org.swdc.ours.common.annotations.Annotations;
import org.swdc.rmdisk.service.RequestMapping;

import java.lang.reflect.Method;

public class VertxHttpAbstractHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String prefix = "/@api";

    public void initialize(Router router) {

        Method[] methods = this.getClass().getDeclaredMethods();
        for (Method method : methods) {
            AnnotationDescription annotation = Annotations.findAnnotation(method, RequestMapping.class);
            if (annotation == null) {
                continue;
            }
            String path = annotation.getProperty(String.class, "value");
            String methodName = annotation.getProperty(String.class, "method");
            if(path.startsWith("/")) {
                path = prefix + path;
            } else {
                path = prefix + "/" + path;
            }
            HttpMethod httpMethod = HttpMethod.valueOf(methodName);
            router.route(path).method(httpMethod).handler(ctx -> {
                try {
                    method.invoke(this, ctx.request(),ctx.response());
                } catch (Exception e) {
                    logger.error("Error in " + methodName + " at " + ctx.normalizedPath(), e);
                }
            });
            logger.info("Registered " + methodName + " at " + path);
        }

    }

}
