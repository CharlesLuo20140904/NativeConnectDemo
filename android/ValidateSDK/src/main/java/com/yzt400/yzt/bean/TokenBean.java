package com.yzt400.yzt.bean;

/**
 * Created by  on 2016/11/17.
 */
public class TokenBean {
    public int code;
    public String error;
    public String appId;
    public String token;
    //YYYYMMddHHmmSS
    public String expiredTime;
    //客户端AES KEY
    public String aesKey;
    //客户端AES  IV
    public String aesIv;


    @Override
    public String toString() {
        return "TokenBean{" +
                "code=" + code +
                ", error='" + error + '\'' +
                ", appId='" + appId + '\'' +
                ", token='" + token + '\'' +
                ", expiredTime='" + expiredTime + '\'' +
                ", aesKey='" + aesKey + '\'' +
                ", aesIv='" + aesIv + '\'' +
                '}';
    }
}
