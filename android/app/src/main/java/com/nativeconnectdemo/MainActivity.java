package com.nativeconnectdemo;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.ReactActivity;
import com.yzt400.yzt.ValidateSDK;
import com.yzt400.yzt.callback.InitCallback;

public class MainActivity extends ReactActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.i("1", "onCreate:++++ ");
        Toast.makeText(getApplicationContext(), "默认的Toast", Toast.LENGTH_SHORT);
        try {
            //第二步需要设置公钥，私钥（第一步在MyApplication中先注册）
            ValidateSDK.getInstance().setPrivateKey(getResources().getAssets()
                    .open("private_key.pem"));
            ValidateSDK.getInstance().setPublicKey(getResources().getAssets()
                    .open("public_key.pem"));
            //第三步需要初始化，初始化成功之后在调用电话验证的方法
        ValidateSDK.getInstance().requestToken(MainApplication.APPID,new InitCallback() {
                @Override
                public void onSuccess() {
                    Log.e("1", "初始化成功=====");
                }
                @Override
                public void onError(int code, String error) {
                    Log.e("2", "初始化失败：" + code + "---error:" + error);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the name of the main component registered from JavaScript.
     * This is used to schedule rendering of the component.
     */

    @Override
    protected String getMainComponentName() {
        return "NativeConnectDemo";
    }
}
