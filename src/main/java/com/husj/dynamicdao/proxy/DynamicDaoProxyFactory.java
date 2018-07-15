package com.husj.dynamicdao.proxy;

import com.husj.dynamicdao.reflect.Reflection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

/**
 *
 * @author 胡胜钧
 * @date 8/5 0005
 */
public class DynamicDaoProxyFactory {

    private DynamicDaoProxyFactory() {}

    public static <T> T create(Class<T> interfaceType, JdbcTemplate jdbcTemplate) {
        Assert.isTrue(interfaceType.isInterface(), String.format("Dynamic dao just support interface ! '%s' is not an interface !", interfaceType.getName()));
        Assert.isTrue(jdbcTemplate != null, "'jdbcTemplate' can't be null !");

        DynamicDaoInvocationHandler invocationHandler = new DynamicDaoInvocationHandler(jdbcTemplate);

        return Reflection.newProxy(interfaceType, invocationHandler);
    }

}
