package com.husj.dynamicdao.spring.support;

import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

/**
 * MapperScannerConfigurer
 *
 * @author shengjun.hu
 * @date 2021/6/15
 */
public class DynamicDaoScannerConfigurer implements BeanDefinitionRegistryPostProcessor {

    @Setter
    private String basePackage;
    @Setter
    private String dataSourceRef;
    @Setter
    private String configurationRef;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        ClassPathDaoScanner scanner = new ClassPathDaoScanner(beanDefinitionRegistry);
        scanner.setDataSourceRef(dataSourceRef);
        scanner.setConfigurationRef(configurationRef);
        scanner.registerFilters();

        scanner.scan(StringUtils.tokenizeToStringArray(this.basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

}
