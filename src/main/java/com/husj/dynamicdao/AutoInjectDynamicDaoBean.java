package com.husj.dynamicdao;

import com.husj.dynamicdao.exceptions.DynamicDaoException;
import com.husj.dynamicdao.proxy.DynamicDaoProxyFactory;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.lang.reflect.Field;

/**
 * @author 胡胜钧
 * @date 6/30 0030.
 */
@Setter
public class AutoInjectDynamicDaoBean implements BeanPostProcessor {

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    public AutoInjectDynamicDaoBean() {}

    public AutoInjectDynamicDaoBean(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public AutoInjectDynamicDaoBean(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 检查参数
     */
    private void init() {
        boolean check = dataSource == null && jdbcTemplate == null;
        Assert.isTrue(!check, "You must initialize 'dataSource' or 'jdbcTemplate' in AutoInjectDynamicDaoBean !");

        if (jdbcTemplate == null) {
            jdbcTemplate = new JdbcTemplate(dataSource);
        }
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        this.init();

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
