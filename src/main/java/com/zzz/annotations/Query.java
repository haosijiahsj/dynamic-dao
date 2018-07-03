package com.zzz.annotations;

import java.lang.annotation.*;

/**
 * Created by 胡胜钧 on 8/2 0002.
 * 查询注解
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {
    String value();
    boolean named() default false;
    Class<?> entityClass() default Void.class;
}
