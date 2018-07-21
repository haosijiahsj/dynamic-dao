package com.husj.dynamicdao.utils;

import java.util.Collection;
import java.util.Map;

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

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

}
