package com.husj.dynamicdao.reflect;


import java.lang.reflect.*;

/**
 *
 * @author 胡胜钧
 * @date 8/7 0007
 */
public class Reflection {

    private Reflection() {}

    /**
     * 代理接口
     * @param interfaceType
     * @param handler
     * @param <T>
     * @return
     */
    public static <T> T newProxy(Class<T> interfaceType, InvocationHandler handler) {
        if (!interfaceType.isInterface()) {
            throw new IllegalArgumentException(String.format("[%s] is't a interface !", interfaceType));
        }

        Object object = Proxy.newProxyInstance(interfaceType.getClassLoader(), new Class<?>[] { interfaceType }, handler);

        return interfaceType.cast(object);
    }

}
