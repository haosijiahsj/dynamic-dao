package com.husj.dynamicdao.annotations.mapping;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by 胡胜钧 on 8/2 0002.
 * 列注解
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    String value() default "";
    boolean ignore() default false;
    Class<?> converter() default void.class;
    EnumType enumType() default EnumType.NONE;

}
