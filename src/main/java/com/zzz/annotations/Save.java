package com.zzz.annotations;

import java.lang.annotation.*;

/**
 * Created by 胡胜钧 on 8/2 0002.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Save {
    String value() default "";
    boolean named() default false;
    boolean returnKey() default false;
}
