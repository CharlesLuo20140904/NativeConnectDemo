package com.yzt400.yzt.callback;

/**
 * Created by  on 2016/11/18.
 */
public interface AuthenticationCallback {
    public static final int AUTHENTICATION_TIME_OUT = -1;
    public static final int SERVICE_ERROR = -2;
    public static final int TOKEN_FAIL = -3;

    void onSuccess(String phone);

    void onError(int code, String error);
}
