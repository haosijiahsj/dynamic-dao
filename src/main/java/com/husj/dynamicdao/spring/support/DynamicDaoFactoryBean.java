package com.husj.dynamicdao.spring.support;

import com.husj.dynamicdao.proxy.DynamicDaoProxyFactory;
import com.husj.dynamicdao.support.InjectDaoConfiguration;
import org.springframework.beans.factory.FactoryBean;

/**
 * MapperFactoryBean
 *
 * @author shengjun.hu
 * @date 2021/6/15
 */
public class DynamicDaoFactoryBean<T> extends DynamicDaoSupport implements FactoryBean<T> {

    private final Class<T> daoInterfaceClass;

    public DynamicDaoFactoryBean(Class<T> daoInterfaceClass) {
        this.daoInterfaceClass = daoInterfaceClass;
    }

    @Override
    public T getObject() throws Exception {
        return DynamicDaoProxyFactory.create(daoInterfaceClass, super.getJdbcTemplate(), InjectDaoConfiguration.builder().build());
    }

    @Override
    public Class<?> getObjectType() {
        return daoInterfaceClass;
    }

}
