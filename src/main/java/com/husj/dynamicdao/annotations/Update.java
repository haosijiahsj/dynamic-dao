package com.husj.dynamicdao.annotations;

import java.lang.annotation.*;

/**
 * Created by 胡胜钧 on 8/2 0002.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Update {
    String value() default "";
}
