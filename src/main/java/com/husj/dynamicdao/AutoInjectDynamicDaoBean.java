package com.husj.dynamicdao;

import com.husj.dynamicdao.exceptions.DynamicDaoException;
import com.husj.dynamicdao.proxy.DynamicDaoProxyFactory;
import com.husj.dynamicdao.annotations.support.AssignDataSource;
import com.husj.dynamicdao.support.MultiDataSourceScope;
import com.husj.dynamicdao.utils.CollectionUtils;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author 胡胜钧
 * @date 6/30 0030.
 */
@Deprecated
@Setter
public class AutoInjectDynamicDaoBean implements BeanPostProcessor {

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private Map<String, DataSource> dataSourceMap;
    private MultiDataSourceScope scope = MultiDataSourceScope.CLASS;

    public AutoInjectDynamicDaoBean() {}

    public AutoInjectDynamicDaoBean(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public AutoInjectDynamicDaoBean(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 预检查
     */
    private void preCheck() {
        // 当数据源使用范围为CLASS时，必须传入一个默认的数据源以在没有使用@AccessDataSource注解的dao中注入
        if (CollectionUtils.isEmpty(dataSourceMap) || MultiDataSourceScope.CLASS.equals(scope)) {
            boolean check = dataSource == null && jdbcTemplate == null;
            Assert.isTrue(!check, "You must initialize 'dataSource' or 'jdbcTemplate' in AutoInjectDynamicDaoBean !");
        }
    }

    /**
     * 获取jdbcTemplate，根据数据源的不同来获取
     * @param field
     * @return
     */
    private JdbcTemplate getJdbcTemplate(Field field) {
        // 单个数据源
        if (CollectionUtils.isEmpty(dataSourceMap)) {
            if (jdbcTemplate == null) {
                return new JdbcTemplate(dataSource);
            }

            return jdbcTemplate;
        }

        // 将数据源定义在包范围中
        if (MultiDataSourceScope.PACKAGE.equals(scope)) {
            Package daoPackage = field.getType().getPackage();
            if (daoPackage != null) {
                String packageName = daoPackage.getName();
                DataSource dataSource = dataSourceMap.get(packageName);
                Assert.isTrue(dataSource != null, String.format("Can't find dataSource [%s] in '%s'", packageName, field.getType()));
                return new JdbcTemplate(dataSource);
            }

            throw new IllegalArgumentException(String.format("Can't get '%s' package !", field.getType()));
        }
        // 通过注解中指定的数据源key名来获取数据源
        AssignDataSource assignDataSourceAnno = field.getType().getAnnotation(AssignDataSource.class);
        if (assignDataSourceAnno != null) {
            String dataSourceName = assignDataSourceAnno.value();
            DataSource dataSource = dataSourceMap.get(dataSourceName);
            Assert.isTrue(dataSource != null, String.format("Can't find dataSource [%s] in '%s'", dataSourceName, field.getType()));

            return new JdbcTemplate(dataSource);
        } else {
            // 返回默认数据源
            if (dataSource != null) {
                return new JdbcTemplate(dataSource);
            }

            return jdbcTemplate;
        }
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        this.preCheck();

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

            JdbcTemplate jdbcTemplate = this.getJdbcTemplate(field);
            Assert.isTrue(jdbcTemplate != null, "Can't initialize 'jdbcTemplate !'");

            Object dynamicDao = DynamicDaoProxyFactory.create(field.getType(), jdbcTemplate);
            field.setAccessible(true);
            try {
                field.set(bean, dynamicDao);
            } catch (IllegalAccessException e) {
                throw new DynamicDaoException("Dynamic dao can't be injected !", e);
            }
            field.setAccessible(false);
        }

        return bean;
    }

}
