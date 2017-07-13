package com.yzt400.yzt.util;

/**
 * Created by tianxiaoyang on 2016/12/13.
 */
public class YZTStringUtil {
    /**
     * 是否是空的字符串
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str == null || "".equals(str)) {
            return true;
        }
        return false;
    }

    /**
     * 是否是非空的字符串
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        if (str != null && !"".equals(str)) {
            return true;
        }
        return false;
    }

}