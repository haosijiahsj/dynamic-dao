package com.husj.dynamicdao;

import com.husj.dynamicdao.exceptions.DynamicDaoException;
import com.husj.dynamicdao.utils.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.lang.reflect.Field;

/**
 * @author 胡胜钧
 * @date 6/30 0030.
 */
public class AutoInjectDynamicDaoBean implements BeanPostProcessor {

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private String daoPackage;

    public AutoInjectDynamicDaoBean() {}

    public AutoInjectDynamicDaoBean(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public AutoInjectDynamicDaoBean(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setDaoPackage(String daoPackage) {
        this.daoPackage = daoPackage;
    }

    /**
     * 检查参数
     */
    private void checkArgument() {
        if (dataSource == null && jdbcTemplate == null) {
            throw new IllegalArgumentException("You must initialize 'dataSource' or 'jdbcTemplate' in AutoInjectDynamicDaoBean !");
        }
        if (jdbcTemplate == null) {
            jdbcTemplate = new JdbcTemplate(dataSource);
        }
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        this.checkArgument();
        // 不在指定包路径下则跳过
        if (StringUtils.isNotEmpty(daoPackage)) {
            Package appPackage = bean.getClass().getPackage();
            if (appPackage == null || !daoPackage.equals(appPackage.getName())) {
                return bean;
            }
        }

        Field[] fields = bean.getClass().getDeclaredFields();
        if (fields == null || fields.length == 0) {
            return bean;
        }

        for (Field field : fields) {
            DynamicDao dynamicDaoAnnotation = field.getAnnotation(DynamicDao.class);
            if (dynamicDaoAnnotation == null) {
                continue;
            }

            field.setAccessible(true);
            Object dynamicDao = DynamicDaoProxyFactory.create(field.getType(), jdbcTemplate);
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
