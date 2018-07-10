package com.husj.dynamicdao.utils;

/**
 * @author 胡胜钧
 * @date 7/6 0006.
 */
public class StringUtils {

    private StringUtils() {}

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

}
