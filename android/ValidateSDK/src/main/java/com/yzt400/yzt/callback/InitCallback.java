package com.yzt400.yzt.callback;

/**
 * Created by  on 2016/11/18.
 */
public interface InitCallback {

    public static final int INIT_TIME_OUT = -1;
    public static final int SERVICE_ERROR = -2;

    void onSuccess();

    void onError(int code, String error);
}
