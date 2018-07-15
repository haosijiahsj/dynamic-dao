package com.husj.dynamicdao.utils;

import java.util.Iterator;

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

    public static String join(String separator, Iterable iterable) {
        StringBuilder sb = new StringBuilder();
        Iterator iterator = iterable.iterator();
        if (iterator.hasNext()) {
            sb.append(iterator.next().toString());
            while (iterator.hasNext()) {
                sb.append(separator);
                sb.append(iterator.next().toString());
            }
        }

        return sb.toString();
    }

}
