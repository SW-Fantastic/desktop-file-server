package org.swdc.rmdisk.core.xmlns;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface XmlProperty {

    /**
     * Tag 名称
     */
    String value();

    /**
     * 是否为标签属性（Attribute）
     * True则生成为Attribute，否则为Element
     */

    boolean tagProperty() default false;


    /**
     * 命名空间
     */
    Namespace[] namespace() default {};

    /**
     * 是否包装集合，如果为True，则在集合外围增加一层标签，否则直接输出子元素
     */
    boolean wrapCollection() default true;

    boolean ignoreEmptyValue() default true;

}
