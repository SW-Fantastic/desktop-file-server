package org.swdc.rmdisk.core.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;

public class ExceptionLogFilter extends AbstractMatcherFilter<ILoggingEvent> {


    @Override
    public FilterReply decide(ILoggingEvent event) {
        IThrowableProxy throwable = event.getThrowableProxy();
        if (throwable != null) {
            String name = throwable.getClassName();
            if (name.startsWith("io.netty.channel")) {
                // 忽略netty的异常，这些异常通常是由于网络问题导致的，不影响接下来的处理。
                return FilterReply.DENY;
            }
            if (name.startsWith("java.io.IOException") && event.getLoggerName().equals("io.vertx.core.net.impl.ConnectionBase")) {
                // 忽略Vert.x中来自ConnectionBase的IO异常，这些通常是由于网络问题导致的，不影响接下来的处理。
                return FilterReply.DENY;
            }
        }
        return FilterReply.NEUTRAL;
    }


    @Override
    public void start() {
        super.start();
    }
}
