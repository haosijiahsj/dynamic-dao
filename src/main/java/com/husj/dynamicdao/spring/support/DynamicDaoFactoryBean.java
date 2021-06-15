package com.husj.dynamicdao.spring.support;

import com.husj.dynamicdao.annotations.support.AssignDataSource;
import com.husj.dynamicdao.exceptions.DynamicDaoException;
import com.husj.dynamicdao.proxy.DynamicDaoProxyFactory;
import com.husj.dynamicdao.support.InjectDaoConfiguration;
import com.husj.dynamicdao.utils.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * MapperFactoryBean
 *
 * @author shengjun.hu
 * @date 2021/6/15
 */
public class DynamicDaoFactoryBean<T> extends DynamicDaoSupport implements ApplicationContextAware, FactoryBean<T> {

    private final Class<T> daoInterfaceClass;
    private ApplicationContext applicationContext;

    public DynamicDaoFactoryBean(Class<T> daoInterfaceClass) {
        this.daoInterfaceClass = daoInterfaceClass;
    }

    @Override
    public T getObject() throws Exception {
        InjectDaoConfiguration configuration = super.getConfiguration();
        if (configuration == null) {
            configuration = InjectDaoConfiguration.builder().build();
        }
        return DynamicDaoProxyFactory.create(daoInterfaceClass, this.getRealJdbcTemplate(), configuration);
    }

    @Override
    public Class<?> getObjectType() {
        return daoInterfaceClass;
    }

    /**
     * 可通过指定数据源注解修改数据源
     * @return
     */
    private JdbcTemplate getRealJdbcTemplate() {
        JdbcTemplate jdbcTemplate = super.getJdbcTemplate();
        AssignDataSource assignDataSource = daoInterfaceClass.getAnnotation(AssignDataSource.class);
        if (assignDataSource != null && StringUtils.isNotEmpty(assignDataSource.value())) {
            String beanName = assignDataSource.value();
            try {
                DataSource dataSource = applicationContext.getBean(beanName, DataSource.class);
                jdbcTemplate = new JdbcTemplate(dataSource);
            } catch (Exception e) {
                throw new DynamicDaoException(String.format("the dataSource '%s' undefined !", beanName));
            }
        }

        return jdbcTemplate;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
