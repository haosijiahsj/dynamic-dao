package com.zzz.annotations;

import java.lang.annotation.*;

/**
 * Created by hushengjun on 2017/8/9.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BatchUpdate {
    String value();
    boolean named() default false;
}
