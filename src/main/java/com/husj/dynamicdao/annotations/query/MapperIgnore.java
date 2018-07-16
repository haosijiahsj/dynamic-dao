package com.husj.dynamicdao.annotations.query;

import java.lang.annotation.*;

/**
 * Created by 胡胜钧 on 8/2 0002.
 * 在列与字段映射时忽略列名中某个字符如下划线
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MapperIgnore {
    String value() default "_";
}
