package com.husj.dynamicdao;

import com.husj.dynamicdao.exceptions.DynamicDaoException;
import com.husj.dynamicdao.proxy.DynamicDaoProxyFactory;
import com.husj.dynamicdao.annotations.support.AssignDataSource;
import com.husj.dynamicdao.utils.CollectionUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * @author 胡胜钧
 * @date 6/30 0030.
 *
 */
@Slf4j
public class InjectDaoBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {

    @Setter
    private Map<String, DataSource> dataSourceMap;
    private ApplicationContext applicationContext;

    /**
     * 获取jdbcTemplate，根据数据源的不同来获取
     * @param field
     * @return
     */
    private JdbcTemplate getJdbcTemplate(Field field) {
        // 单个数据源
        if (CollectionUtils.isEmpty(dataSourceMap)) {
            AssignDataSource assignDataSourceAnno = field.getType().getAnnotation(AssignDataSource.class);
            DataSource dataSource;
            try {
                if (assignDataSourceAnno == null) {
                    // 没有该注解时使用默认的数据源
                    dataSource = applicationContext.getBean(DataSource.class);
                    log.debug("[{}] use default dateSource !", field.getType());
                } else {
                    // 有该注解时使用注解中的value作为beanName到spring上下文中获取数据源（class级别多数据源支持）
                    dataSource = applicationContext.getBean(assignDataSourceAnno.value(), DataSource.class);
                    log.debug("[{}] use dateSource name [{}]!", field.getType(), assignDataSourceAnno.value());
                }
            } catch (Exception e) {
                throw new DynamicDaoException("Can't get dataSource from spring context ! need define 'dataSource' as a bean !", e);
            }

            return new JdbcTemplate(dataSource);
        }

        // 指定了dataSourceMap，定义某个包使用某个数据源map中key为包名（包级别多数据源支持）
        Package daoPackage = field.getType().getPackage();
        if (daoPackage != null) {
            String packageName = daoPackage.getName();
            DataSource dataSource = dataSourceMap.get(packageName);
            Assert.isTrue(dataSource != null, String.format("Can't find dataSource by '%s'", packageName));

            return new JdbcTemplate(dataSource);
        }

        throw new DynamicDaoException(String.format("Can't get '%s' package !", field.getType()));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();
        if (fields == null || fields.length == 0) {
            return bean;
        }

        for (Field field : fields) {
            DynamicDao dynamicDaoAnnotation = field.getAnnotation(DynamicDao.class);
            InjectDao injectDaoAnnotation = field.getAnnotation(InjectDao.class);

            if (dynamicDaoAnnotation == null && injectDaoAnnotation == null) {
                continue;
            }

            // 生成代理
            JdbcTemplate jdbcTemplate = this.getJdbcTemplate(field);
            Object dynamicDao = DynamicDaoProxyFactory.create(field.getType(), jdbcTemplate);

            if ((!Modifier.isPublic(field.getModifiers()) ||
                    !Modifier.isPublic(field.getDeclaringClass().getModifiers()) ||
                    Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
                field.setAccessible(true);
            }
            try {
                field.set(bean, dynamicDao);
            } catch (IllegalAccessException e) {
                throw new DynamicDaoException("Dynamic dao can't be injected !", e);
            }
        }

        return bean;
    }

}
