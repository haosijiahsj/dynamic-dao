package com.husj.dynamicdao.spring.support;

import com.husj.dynamicdao.utils.CollectionUtils;
import com.husj.dynamicdao.utils.StringUtils;
import lombok.Setter;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

import javax.sql.DataSource;
import java.util.Set;

/**
 * ClassPathMapperScanner
 *
 * @author shengjun.hu
 * @date 2021/6/15
 */
public class ClassPathDaoScanner extends ClassPathBeanDefinitionScanner {

    @Setter
    private String dataSourceRef;
    @Setter
    private String jdbcTemplateRef;
    @Setter
    private DataSource dataSource;

    public ClassPathDaoScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> definitionHolders = super.doScan(basePackages);
        if (CollectionUtils.isEmpty(definitionHolders)) {
            throw new IllegalArgumentException();
        }

        // 注入dao bean
        this.processBeanDefinitions(definitionHolders);

        return definitionHolders;
    }

    private void processBeanDefinitions(Set<BeanDefinitionHolder> definitionHolders) {
        for (BeanDefinitionHolder holder : definitionHolders) {
            GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();
            String beanClassName = definition.getBeanClassName();
            // 设置真实的beanName
            definition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName);
            // 通过该FactoryBean.getObject获取代理对象
            definition.setBeanClass(DynamicDaoFactoryBean.class);

            boolean autoWiredDataSource = true;
            if (StringUtils.isNotEmpty(dataSourceRef)) {
                definition.getPropertyValues().add("dataSource", new RuntimeBeanReference(dataSourceRef));
                autoWiredDataSource = false;
            } else if (dataSource != null) {
                definition.getPropertyValues().add("dataSource", dataSource);
                autoWiredDataSource = false;
            }
            if (StringUtils.isNotEmpty(jdbcTemplateRef)) {
                definition.getPropertyValues().add("jdbcTemplate", new RuntimeBeanReference(jdbcTemplateRef));
                autoWiredDataSource = false;
            }
            // 自动注入数据源依赖，DynamicDaoFactoryBean继承的setDataSource方法会自动执行
            if (autoWiredDataSource) {
                definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
            }
        }
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }

    public void registerFilters() {
        addIncludeFilter((metadataReader, metadataReaderFactory) -> true);
        // exclude package-info.java
        addExcludeFilter((metadataReader, metadataReaderFactory) -> {
            String className = metadataReader.getClassMetadata().getClassName();
            return className.endsWith("package-info");
        });
    }

}
