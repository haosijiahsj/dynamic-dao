package com.husj.dynamicdao.annotations.support;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import java.lang.annotation.RetentionPolicy;

/**
 * @author hushengjun
 * @date 2019/5/15
 *
 * 转换器
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Convert {

    Class converter() default void.class;
    String attributeName() default "";

}
