package com.husj.dynamicdao.spring.support;

import com.husj.dynamicdao.spring.DynamicDaoScan;
import com.husj.dynamicdao.utils.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MapperScannerRegistrar
 *
 * @author shengjun.hu
 * @date 2021/6/15
 */
public class DynamicDaoScannerRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        // 获取MapperScan注解信息
        AnnotationAttributes mapperScanAttrs = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(DynamicDaoScan.class.getName()));

        List<String> basePackages = new ArrayList<>();
        basePackages.addAll(
                Arrays.stream(mapperScanAttrs.getStringArray("value"))
                        .filter(StringUtils::isNotEmpty)
                        .collect(Collectors.toList())
        );
        basePackages.addAll(
                Arrays.stream(mapperScanAttrs.getStringArray("basePackages"))
                        .filter(StringUtils::isNotEmpty)
                        .collect(Collectors.toList())
        );

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DynamicDaoScannerConfigurer.class);
        builder.addPropertyValue("basePackage", StringUtils.join(",", basePackages));
        String dataSourceRef = mapperScanAttrs.getString("dataSourceRef");
        if (StringUtils.isNotEmpty(dataSourceRef)) {
            builder.addPropertyValue("dataSourceRef", dataSourceRef);
        }
        String jdbcTemplateRef = mapperScanAttrs.getString("jdbcTemplateRef");
        if (StringUtils.isNotEmpty(jdbcTemplateRef)) {
            builder.addPropertyValue("jdbcTemplateRef", jdbcTemplateRef);
        }

        String beanName = annotationMetadata.getClassName() + "#" + DynamicDaoScannerRegistrar.class.getSimpleName() + "#" + 0;

        beanDefinitionRegistry.registerBeanDefinition(beanName, builder.getBeanDefinition());
    }

}
