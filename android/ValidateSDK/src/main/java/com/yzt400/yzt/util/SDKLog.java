package com.yzt400.yzt.util;

import android.util.Log;


public class SDKLog {

    public static boolean isDebug = false;

    public static void i(String tag, String msg) {
        if (isDebug && msg != null) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (isDebug && msg != null) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isDebug && msg != null) {
            Log.e(tag, msg);
        }
    }

    public static void e(String msg) {
        if (isDebug && msg != null) {
            Log.e("123", msg);
        }
    }

    public static void d(String tag, String msg) {
        if (isDebug && msg != null) {
            Log.d(tag, msg);
        }
    }
    public static void d(String msg) {
        if (isDebug && msg != null) {
            Log.d("123", msg);
        }
    }
}
