package com.husj.dynamicdao.annotations.query;

import java.lang.annotation.*;

/**
 * Created by 胡胜钧 on 8/2 0002.
 * 查询注解
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
    String value();
}
