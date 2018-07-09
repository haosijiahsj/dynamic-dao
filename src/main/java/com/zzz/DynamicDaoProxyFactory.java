package com.zzz;

import com.google.common.base.Preconditions;
import com.google.common.reflect.Reflection;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author 胡胜钧
 * @date 8/5 0005
 */
public class DynamicDaoProxyFactory {

    private DynamicDaoProxyFactory() {}

    public static <T> T create(Class<T> interfaceType, JdbcTemplate jdbcTemplate) {
        Preconditions.checkArgument(interfaceType.isInterface(), String.format("Dynamic dao just support interface ! '%s' is not an interface !", interfaceType.getName()));

        DynamicDaoInvocationHandler invocationHandler = new DynamicDaoInvocationHandler(jdbcTemplate);

        return Reflection.newProxy(interfaceType, invocationHandler);
    }

}
