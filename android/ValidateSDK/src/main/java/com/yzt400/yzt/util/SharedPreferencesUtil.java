package com.yzt400.yzt.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.yzt400.yzt.ValidateSDK;

/**
 * Created by xiaoyang on 2015/12/14.
 */
public class SharedPreferencesUtil {
    private static final SharedPreferences sp = ValidateSDK.getInstance().context.getSharedPreferences(ValidateSDK.getInstance().context.getPackageName(), Context.MODE_PRIVATE);

    public static void put(String name, Object value) {
        SharedPreferences.Editor edit = sp.edit();
        if (value instanceof Boolean) {
            edit.putBoolean(name, (Boolean) value);
        } else if (value instanceof Integer) {
            edit.putInt(name, (Integer) value);
        } else if (value instanceof Long) {
            edit.putLong(name, (Long) value);
        } else if (value instanceof Float) {
            edit.putFloat(name, (Float) value);
        } else if (value instanceof String) {
            edit.putString(name, (String) value);
        } else {
            throw new IllegalArgumentException("不要往sp中乱存东西,只支持基本类型，如果要持久化，那你就序列化");
        }
        edit.commit();
    }

    public static String getString(String name) {
        return sp.getString(name, "");
    }

    public static String getString(String name, String str) {
        return sp.getString(name, str);
    }

    public static boolean getBoolean(String name) {
        return sp.getBoolean(name, false);
    }

    public static boolean getBoolean(String name, boolean b) {
        return sp.getBoolean(name, b);
    }

}
