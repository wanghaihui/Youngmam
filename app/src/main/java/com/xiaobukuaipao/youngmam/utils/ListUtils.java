package com.xiaobukuaipao.youngmam.utils;

import java.util.List;

/**
 * Created by xiaobu1 on 15-6-18.
 */
public class ListUtils {
    private ListUtils() {
        throw new AssertionError();
    }

    /**
     * get size of list
     *
     * <pre>
     * getSize(null)   =   0;
     * getSize({})     =   0;
     * getSize({1})    =   1;
     * </pre>
     *
     * @param <V>
     * @param sourceList
     * @return if list is null or empty, return 0, else return {@link List#size()}.
     */
    public static <V> int getSize(List<V> sourceList) {
        return sourceList == null ? 0 : sourceList.size();
    }
}
