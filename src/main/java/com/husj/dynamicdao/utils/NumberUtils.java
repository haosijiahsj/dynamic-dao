package com.husj.dynamicdao.utils;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author hushengjun
 * @date 2019/9/11
 */
public class NumberUtils {

    /**
     * 判断是否是数字类型
     * @param clazz
     * @return
     */
    public static boolean isNumberType(Class clazz) {
        return Byte.class.equals(clazz)
                || byte.class.equals(clazz)
                || Short.class.equals(clazz)
                || short.class.equals(clazz)
                || Integer.class.equals(clazz)
                || int.class.equals(clazz)
                || Long.class.equals(clazz)
                || long.class.equals(clazz)
                || Float.class.equals(clazz)
                || float.class.equals(clazz)
                || Double.class.equals(clazz)
                || BigInteger.class.equals(clazz)
                || BigDecimal.class.equals(clazz)
                || Number.class.equals(clazz);
    }

    /**
     * 转到包装类型
     * @param clazz
     * @return
     */
    public static Class convertPrimitiveTypeToWrapType(Class clazz) {
        // 只转换数值类型
        if (!isNumberType(clazz)) {
            return clazz;
        }
        // 不是原始类型直接返回
        if (!clazz.isPrimitive()) {
            return clazz;
        }

        Class wrapClass;
        if (byte.class.equals(clazz)) {
            wrapClass = Byte.class;
        } else if (short.class.equals(clazz)) {
            wrapClass = Short.class;
        } else if (int.class.equals(clazz)) {
            wrapClass = Integer.class;
        } else if (long.class.equals(clazz)) {
            wrapClass = Long.class;
        } else if (float.class.equals(clazz)) {
            wrapClass = Float.class;
        } else if (double.class.equals(clazz)) {
            wrapClass = Double.class;
        } else {
            throw new IllegalArgumentException(String.format("illegal type for '%s'", clazz));
        }

        return wrapClass;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Number> T parseNumber(String text, Class<T> targetClass) {
        Class clazz = NumberUtils.convertPrimitiveTypeToWrapType(targetClass);

        return (T) org.springframework.util.NumberUtils.parseNumber(text, clazz);
    }

}
