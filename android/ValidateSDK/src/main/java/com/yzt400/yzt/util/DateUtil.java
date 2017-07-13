package com.yzt400.yzt.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author yangningbo
 * @version V1.0
 * @Description：时间工具类 <p>创建日期：2013-9-9 </p>
 * @see
 */
public class DateUtil {
    private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyMMddHHmmss");
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    /**
     * 获取一个 格式为yyMMddHHmmss 的时间字符串
     *
     * @return
     */
    public static String getDateStr() {
        return dateTimeFormat.format(new Date());
    }

    /**
     * 获取一个 格式为yyMMddHHmmss 的时间字符串
     *
     * @return
     */
    public static String getDateLongStr() {
        return dateFormat.format(new Date());
    }

    public static boolean compareDate(String expiredTime, Date date2)  {
        try {
            Date date1 = dateTimeFormat.parse(expiredTime);
            if (date1.getTime() <= date2.getTime()) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
