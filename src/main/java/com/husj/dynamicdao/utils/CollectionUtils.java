package com.husj.dynamicdao.utils;

import java.util.Collection;

/**
 * @author 胡胜钧
 * @date 7/6 0006.
 */
public class CollectionUtils {

    private CollectionUtils() {}

    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

}
