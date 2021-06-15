package com.husj.dynamicdao.annotations.mapping;

import com.husj.dynamicdao.support.IdentifierGenerator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by 胡胜钧 on 8/2 0002.
 * id列注解
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {

    String value() default "";
    GenerationType generationType() default GenerationType.IDENTITY;
    Class<? extends IdentifierGenerator> generator() default IdentifierGenerator.class;

}
