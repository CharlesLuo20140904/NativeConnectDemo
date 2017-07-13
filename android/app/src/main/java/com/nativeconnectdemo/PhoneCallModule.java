package com.nativeconnectdemo;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.IllegalViewOperationException;
import com.yzt400.yzt.ValidateSDK;
import com.yzt400.yzt.callback.AuthenticationCallback;
import com.yzt400.yzt.callback.InitCallback;

/**
 * Created by charles on 2017/7/3.
 */

public class PhoneCallModule extends ReactContextBaseJavaModule {
    private static String callPhone;
    private static ReactContext mReactContext;
    public PhoneCallModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
    }

    @Override
    public String getName() {
        return "testname";
    }

    @ReactMethod
    public void show(String message, int duration) {
//        Toast.makeText(getReactApplicationContext(), message, duration).show();
    }

    @ReactMethod
    public void startToCall(final String phoneStr) throws Exception {
        Log.i("PhoneCallModule", "startToCal: "+phoneStr);
        if(phoneStr == null ){
            return;
        }
        this.callPhone = phoneStr;
        ValidateSDK.getInstance().createVerification(phoneStr, "001", new AuthenticationCallback() {
            @Override
            public void onSuccess(String phone) {
                call(phone);
            }
            @Override
            public void onError(int code, String error) {
                //请求失败，状态为验证失败，其他业务逻辑
                Log.i("1", "fail: ++"+error);
                if (code == AuthenticationCallback.TOKEN_FAIL) {
//                            showMyDialog("验证失败，请重新验证！");
                    requestToken();
                } else {
//                            showMyDialog("验证失败，请重新验证！");
                }
            }
        });
    }

    private void requestToken() {
        try {
            ValidateSDK.getInstance().requestToken(MainApplication.APPID,new InitCallback() {
                @Override
                public void onSuccess() {
                    Log.i("PhoneCallModule","初始化成功,设置密钥");
                }

                @Override
                public void onError(int code, String error) {
                    Log.i("PhoneCallModule","初始化失败：" + code + "---error:" + error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;


    private void call(String phone) {
        if (ContextCompat.checkSelfPermission(this.getCurrentActivity(),
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this.getCurrentActivity(),
                    new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
        } else {
            if (phone == null || phone.length() == 0) {
                throw new RuntimeException("传入的电话为null");
            }
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
//            if (ActivityCompat.checkSelfPermission(this.getCurrentActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                return;
//            }
            this.getCurrentActivity().startActivity(intent);
        }

    }

    public static class PhoneStateReceiver extends BroadcastReceiver {

        private int mCurrentState = TelephonyManager.CALL_STATE_IDLE ;
        private int mOldState = TelephonyManager.CALL_STATE_IDLE ;
        private int tempState = TelephonyManager.CALL_STATE_IDLE ;

        private Context mContext;

        @Override
        public void onReceive(Context context, Intent intent) {
            mContext = context;
            if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
                tm.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);
            }
        }

        private class MyPhoneStateListener extends PhoneStateListener {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);

//            mOldState = SharedPreferencesHelper.getInstance().getData("asdas");
//                    PreferenceHelper.getInt(Config.FLAG_CALL_STATE, mContext);
                mOldState = tempState;
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        mCurrentState = TelephonyManager.CALL_STATE_IDLE;
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        mCurrentState = TelephonyManager.CALL_STATE_OFFHOOK;
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        mCurrentState = TelephonyManager.CALL_STATE_RINGING;
                        break;
                }
                if(mOldState == TelephonyManager.CALL_STATE_IDLE && mCurrentState == TelephonyManager.CALL_STATE_OFFHOOK ) {
                    Log.i("PhoneStateReceiver", "onCallStateChanged: 接通");
                    tempState = mCurrentState;
                    Log.i("PhoneCallModule", "里面的onCallStateChanged:mOldState "+mOldState);
                } else if (mOldState == TelephonyManager.CALL_STATE_IDLE && mCurrentState == TelephonyManager.CALL_STATE_IDLE) {
                    Log.i("PhoneStateReceiver", "onCallStateChanged: 挂断");
                    try {
                        checkVerifyResult();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    tempState = mCurrentState;
                }
            }

            public void  checkVerifyResult() throws Exception{
                Log.i("PhoneStateReceiver", "callPhone: "+callPhone);
                ValidateSDK.getInstance().queryVerification(callPhone,"001",new AuthenticationCallback() {
                    @Override
                    public void onSuccess(String phone) {
                        Log.i("phoneCall", "checksucceed: "+phone);
                        WritableMap params = Arguments.createMap();
                        params.putString("key", "succeed");
                        sendEvent(mReactContext, "showToast", params);
                    }

                    @Override
                    public void onError(int code, String error) {
                        Log.i("error", "onError: "+error);
                    }
                });
            }

            private void sendEvent(ReactContext reactContext,
                                   String eventName,
                                   @Nullable WritableMap params) {
                reactContext
                        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                        .emit(eventName, params);
            }

        }
    }
}
