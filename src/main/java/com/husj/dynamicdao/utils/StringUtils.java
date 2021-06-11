package com.husj.dynamicdao.utils;

import java.util.Iterator;

/**
 * @author 胡胜钧
 * @date 7/6 0006.
 */
public class StringUtils {

    private static final String UNDERLINE = "_";

    private StringUtils() {}

    /**
     * 是否为空
     * @param cs
     * @return
     */
    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * 是否不为空
     * @param cs
     * @return
     */
    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    /**
     * 以为某字符串连接
     * @param separator
     * @param iterable
     * @return
     */
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

    /**
     * 从驼峰转为下划线分割
     * @param str
     * @return
     */
    public static String humpToUnderline(String str) {
        if (isEmpty(str)) {
            return str;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            // 第一位是大写，不加下划线
            if (Character.isUpperCase(c) && i == 0) {
                sb.append(Character.toLowerCase(c));
                continue;
            }
            if (Character.isUpperCase(c)) {
                sb.append(UNDERLINE).append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * 从下划线分割转驼峰
     * @param str
     * @return
     */
    public static String underlineToHump(String str) {
        if (isEmpty(str) || !str.contains(UNDERLINE)) {
            return str;
        }

        boolean flag = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (UNDERLINE.equals(String.valueOf(c))) {
                flag = true;
                continue;
            }
            if (flag) {
                sb.append(Character.toUpperCase(c));
                flag = false;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

}
