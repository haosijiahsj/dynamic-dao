package com.zzz.annotations.query;

import java.lang.annotation.*;

/**
 * @author 胡胜钧
 * @date 7/9 0009.
 *
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SingleColumn {
    boolean returnFirst() default false;
}
