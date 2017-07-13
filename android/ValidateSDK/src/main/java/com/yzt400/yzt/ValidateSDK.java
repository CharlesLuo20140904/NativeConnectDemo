package com.yzt400.yzt;

import android.app.Application;
import android.util.Base64;

import com.yzt400.yzt.bean.AuthenticationBean;
import com.yzt400.yzt.bean.AuthenticationBody;
import com.yzt400.yzt.bean.HeaderA;
import com.yzt400.yzt.bean.HeaderB;
import com.yzt400.yzt.bean.TokenBean;
import com.yzt400.yzt.bean.TokenBody;
import com.yzt400.yzt.bean.TokenRequest;
import com.yzt400.yzt.bean.TokenResponse;
import com.yzt400.yzt.callback.AuthenticationCallback;
import com.yzt400.yzt.callback.InitCallback;
import com.yzt400.yzt.util.AES128CBC;
import com.yzt400.yzt.util.DateUtil;
import com.yzt400.yzt.util.FastJsonUtils;
import com.yzt400.yzt.util.SDKLog;
import com.yzt400.yzt.util.RSAUtil;
import com.yzt400.yzt.util.SHA1Utils;
import com.yzt400.yzt.util.SharedPreferencesUtil;
import com.yzt400.yzt.util.YZTStringUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * Created by  on 2016/11/10.
 */
public class ValidateSDK {

    private String url = "https://www.400yzt.com:8443/app.core";
    public Application context;
    private byte[] publicKey;
    private byte[] privateKey;
    private TokenBean tokenABean;
    private InitCallback initCallback;
    private AuthenticationCallback authenticationCallback;
    private String mid;

    private ValidateSDK() {
        okHttpConfig();
    }

    private static ValidateSDK instance = new ValidateSDK();

    public static ValidateSDK getInstance() {
        return instance;
    }

    public void register(Application context, String url) {
        this.context = context;
        this.url = url;
    }


    private String getURL_A() {
        return url + "/service/rsa/channal.do";
    }

    private String getURL_B() {
        return url + "/service/aes/channal.do";
    }

    //配置okhttp 超时时间，支持https
    private void okHttpConfig() {
        X509TrustManager xtm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] x509Certificates = new X509Certificate[0];
                return x509Certificates;
            }
        };
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15000L, TimeUnit.MILLISECONDS)
                .readTimeout(30000L, TimeUnit.MILLISECONDS)
                .sslSocketFactory(sslContext.getSocketFactory())
                .hostnameVerifier(DO_NOT_VERIFY)
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    public void setPublicKey(InputStream in) {
        try {
            publicKey = RSAUtil.loadPemKey(in, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setPrivateKey(InputStream in) {
        try {
            privateKey = RSAUtil.loadPemKey(in, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] getPrivateKey() {
        if (privateKey == null || privateKey.length == 0) {
            throw new RuntimeException("请先传入私钥，调用此方法setPrivateKey");
        }
        return privateKey;
    }

    private byte[] getPublicKey() {
        if (publicKey == null || publicKey.length == 0) {
            throw new RuntimeException("请先传入公钥，调用此方法setPublicKey");
        }
        return publicKey;
    }

    private HeaderA getHeaderA(String appid, String key) throws Exception {
        HeaderA headerA = new HeaderA();
        headerA.appId = appid;
        byte[] bytes = RSAUtil.bcEncryptByPublicKey(key.getBytes(), getPublicKey(), RSAUtil.RSA_ECB_PKCS1Padding);
        headerA.k = new String(Base64.encodeToString(bytes, Base64.NO_WRAP));
        return headerA;
    }

    private TokenBody getTokenBody() throws Exception {
        TokenBody tokenBody = new TokenBody();
        tokenBody.appId = "test";
        TokenBean tokenBean = getTokenBean();
        if (tokenBean != null) {
            tokenBody.token = tokenBean.token;
        }
        return tokenBody;
    }

    private void requestToken(String appid) throws Exception {
        String key = AES128CBC.createKeyAndIV()[0];
        String iv = AES128CBC.createKeyAndIV()[1];
        HeaderA headerA = getHeaderA(appid, key + iv);
        TokenBody tokenBody = getTokenBody();
        String bodyStr = FastJsonUtils.toJson(tokenBody);
        String bodyEncrypt = AES128CBC.encrypt(bodyStr, key, iv);
        TokenRequest request = new TokenRequest(headerA, bodyEncrypt);
        requestA(FastJsonUtils.toJson(request));
    }

    public void requestToken(String appid, InitCallback initCallback) throws Exception {
        this.initCallback = initCallback;
        TokenBean tokenBean = getTokenBean();
        if (tokenBean != null && tokenBean.expiredTime != null) {
            boolean isBig = DateUtil.compareDate(tokenBean.expiredTime, new Date());
            if (isBig) {
                SDKLog.e("已经验证过了，但是token过期了");
                requestToken(appid);
            } else {
                SDKLog.e("已经验证过了，并且token没有过期");
                if (initCallback != null) {
                    initCallback.onSuccess();
                }
            }
        } else {
            SDKLog.e("第一次验证");
            requestToken(appid);
        }
    }

    private void requestA(String content) {
        SDKLog.e("请求数据：" + content);
        OkHttpUtils.postString().url(getURL_A()).content(content).build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (initCallback != null) {
                            if (e instanceof TimeoutException) {
                                initCallback.onError(InitCallback.INIT_TIME_OUT, "注册超时，请重新注册");
                            } else {
                                initCallback.onError(InitCallback.SERVICE_ERROR, "服务器异常，请重新注册");
                            }
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            TokenResponse bean = FastJsonUtils.getSingleBean(response, TokenResponse.class);
                            String k = new String(RSAUtil.bcDecryptByPrivateKey(Base64.decode(bean.header.k, Base64.NO_WRAP), getPrivateKey(), RSAUtil.RSA_ECB_PKCS1Padding));
                            String s = AES128CBC.decrypt(bean.body, k.substring(0, 16), k.substring(16));
                            if (s != null && s.length() >= 0) {
                                TokenBean tokenBean = FastJsonUtils.getSingleBean(s, TokenBean.class);
                                saveTokenBean(s);
                                SDKLog.e(tokenBean.toString());
                                if (initCallback != null) {
                                    initCallback.onSuccess();
                                }
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (initCallback != null) {
                            initCallback.onError(InitCallback.SERVICE_ERROR, "服务器异常，请重新注册");
                        }
                    }
                });
    }


    private void requestB(String content, final int op) {
        SDKLog.e("请求数据：" + content);
        OkHttpUtils.postString().url(getURL_B()).content(content).build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        SDKLog.e("onErroron:" + e.getMessage());
                        if (authenticationCallback != null) {
                            if (e instanceof SocketTimeoutException) {
                                authenticationCallback.onError(AuthenticationCallback.AUTHENTICATION_TIME_OUT, "验证超时，请重新验证");
                            } else {
                                authenticationCallback.onError(AuthenticationCallback.SERVICE_ERROR, "服务器异常，请重新验证");
                            }
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        SDKLog.e("onResponse:" + response);
                        if (YZTStringUtil.isNotEmpty(response)) {
                            try {
                                TokenResponse bean = FastJsonUtils.getSingleBean(response, TokenResponse.class);
                                TokenBean tokenBean = getTokenBean();
                                if (bean.header.code == null || bean.header.code.length() == 0) {
                                    String strin = AES128CBC.decrypt(bean.body, tokenBean.aesKey, tokenBean.aesIv);
                                    AuthenticationBean tokenBBean = FastJsonUtils.getSingleBean(strin, AuthenticationBean.class);
                                    SDKLog.e(tokenBBean.toString());
                                    if (tokenBBean.isSuccess()) {
                                        if (op == AuthenticationBody.REGISTER || op == AuthenticationBody.CLEAR) {
                                            ValidateSDK.this.mid = tokenBBean.mid;
                                            if (authenticationCallback != null) {
                                                authenticationCallback.onSuccess(tokenBBean.ivrId);
                                            }
                                        } else {
                                            if (authenticationCallback != null) {
                                                if (tokenBBean.status == 1) {
                                                    authenticationCallback.onSuccess(tokenBBean.ivrId);
                                                } else {
                                                    authenticationCallback.onError(tokenBBean.status, tokenBBean.error);

                                                }
                                            }
                                        }

                                    } else if (bean.header.isRefresh()) {
                                        SDKLog.e("验证出错，清除缓存token，需要重新调用初始化");
                                        clearTokenBean();
                                        if (authenticationCallback != null) {
                                            authenticationCallback.onError(AuthenticationCallback.TOKEN_FAIL, "验证出错，需要重新调用初始化");
                                        }
                                    } else {
                                        if (authenticationCallback != null) {
                                            authenticationCallback.onError(AuthenticationCallback.SERVICE_ERROR, tokenBBean.error);
                                        }
                                    }
                                } else {
                                    if (bean.header.isRefresh()) {
                                        SDKLog.e("验证出错，清除缓存token，需要重新调用初始化");
                                        clearTokenBean();
                                        if (authenticationCallback != null) {
                                            authenticationCallback.onError(AuthenticationCallback.TOKEN_FAIL, "验证出错，需要重新调用初始化");
                                        }
                                    } else {
                                        if (authenticationCallback != null) {
                                            authenticationCallback.onError(AuthenticationCallback.SERVICE_ERROR, bean.header.error);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (authenticationCallback != null) {
                                    authenticationCallback.onError(AuthenticationCallback.SERVICE_ERROR, "服务器异常，请重新验证");
                                }
                            }
                        }

                    }
                });
    }

    private void saveTokenBean(String tokenBean) {
        try {
            SharedPreferencesUtil.put("tokenBean", AES128CBC.encrypt(tokenBean, getAA(), getBB()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getBB() {
        String bb = SharedPreferencesUtil.getString("bb");
        if (bb == null || bb.length() == 0) {
            String[] keyAndIV = AES128CBC.createKeyAndIV();
            setAAndB(keyAndIV[0], keyAndIV[1]);
            bb = keyAndIV[1];
        }
        return bb;

    }

    private void setAAndB(String aa, String bb) {
        SharedPreferencesUtil.put("aa", aa);
        SharedPreferencesUtil.put("bb", bb);
    }

    private String getAA() {
        String aa = SharedPreferencesUtil.getString("aa");
        if (aa == null || aa.length() == 0) {
            String[] keyAndIV = AES128CBC.createKeyAndIV();
            setAAndB(keyAndIV[0], keyAndIV[1]);
            aa = keyAndIV[0];
        }
        return aa;
    }

    private void clearTokenBean() {
        tokenABean = null;
        saveTokenBean("");
    }

    private TokenBean getTokenBean() throws Exception {
        if (tokenABean == null) {
            String string = SharedPreferencesUtil.getString("tokenBean");
            if (string != null && string.length() > 0) {
                tokenABean = FastJsonUtils.getSingleBean(AES128CBC.decrypt(string, getAA(), getBB()), TokenBean.class);
            }
        }
        return tokenABean;
    }

    /**
     * 注册
     *
     * @param phone                  查询的电话
     * @param busi                   业务编号:000:默认 001:注册 002:转帐
     * @param authenticationCallback 回调
     * @throws Exception
     */
    public void createVerification(String phone, String busi, AuthenticationCallback authenticationCallback) throws Exception {
        authentication(phone, AuthenticationBody.REGISTER, busi, authenticationCallback);
    }

    /**
     * 查询
     *
     * @param phone                  查询的电话
     * @param busi                   业务编号:000:默认 001:注册 002:转帐
     * @param authenticationCallback 回调
     * @throws Exception
     */
    public void queryVerification(String phone, String busi, AuthenticationCallback authenticationCallback) throws Exception {
        authentication(phone, AuthenticationBody.QUERY, busi, authenticationCallback);
    }

    /**
     * 清除
     *
     * @param phone                  查询的电话
     * @param busi                   业务编号:000:默认 001:注册 002:转帐
     * @param authenticationCallback 回调
     * @throws Exception
     */
    public void cleanVerification(String phone, String busi, AuthenticationCallback authenticationCallback) throws Exception {
        authentication(phone, AuthenticationBody.CLEAR, busi, authenticationCallback);
    }

    /**
     * @param phone                  查询的电话
     * @param op                     模式 0:清除服务器拨号消息 1:拨号前注册 2:拨号查询
     * @param busi                   业务编号:000:默认 001:注册 002:转帐
     * @param authenticationCallback 回调
     * @throws Exception
     */
    private void authentication(String phone, int op, String busi, AuthenticationCallback authenticationCallback) throws Exception {
        this.authenticationCallback = authenticationCallback;
        TokenBean tokenBean = getTokenBean();
        if (tokenBean == null) {
            if (authenticationCallback != null) {
                authenticationCallback.onError(AuthenticationCallback.TOKEN_FAIL, "验证出错，需要重新调用初始化");
            }
            return;
        }
        AuthenticationBody authentication = new AuthenticationBody();
        authentication.busi = busi;
        authentication.mobile = phone;
        authentication.op = op;
        if (op == AuthenticationBody.QUERY) {
            authentication.mid = mid;
        }
        HeaderB headerB = new HeaderB();
        headerB.token = tokenBean.token;
        String bodyString = FastJsonUtils.toJson(authentication);
        headerB.sign = SHA1Utils.hex_sha1(headerB.token + bodyString).toLowerCase();
        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.header = headerB;
        tokenRequest.body = AES128CBC.encrypt(bodyString, tokenBean.aesKey, tokenBean.aesIv);
        requestB(FastJsonUtils.toJson(tokenRequest), op);
    }
}
