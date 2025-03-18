package org.swdc.rmdisk.core.xmlns;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Namespace {

    /**
     * namespace，默认为空，此时为无前缀的默认命名空间
     */
    String prefix() default "";

    /**
     * 命名空间URI，默认为空字符串
     */
    String uri() default "";


}
