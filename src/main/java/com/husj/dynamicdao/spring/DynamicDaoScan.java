package com.husj.dynamicdao.spring;

import com.husj.dynamicdao.spring.support.DynamicDaoScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * MapperScan
 *
 * @author shengjun.hu
 * @date 2021/6/15
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(DynamicDaoScannerRegistrar.class)
public @interface DynamicDaoScan {

    String[] value() default {};
    String[] basePackages() default {};
    String dataSourceRef() default "";
    String jdbcTemplateRef() default "";

}
