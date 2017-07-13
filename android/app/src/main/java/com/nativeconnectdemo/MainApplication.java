package com.nativeconnectdemo;

import android.app.Application;
import com.yzt400.yzt.ValidateSDK;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;

import java.util.Arrays;
import java.util.List;

public class MainApplication extends Application implements ReactApplication {
  public static MainApplication instance;
  private String url = "https://www.400yzt.com:8443/app.core";
  public static final String APPID = "test";
  private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
    @Override
    public boolean getUseDeveloperSupport() {
      return BuildConfig.DEBUG;
    }

    @Override
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
          new MainReactPackage(),
          new MainPackage()
      );
    }
  };

  @Override
  public ReactNativeHost getReactNativeHost() {
    return mReactNativeHost;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    SharedPreferencesHelper shareHelper = SharedPreferencesHelper.init(getApplicationContext());
    instance = this;
    //第一步需要先注册
    ValidateSDK.getInstance().register(this,url);
    SoLoader.init(this, /* native exopackage */ false);
  }
}
