package com.yzt400.yzt.bean;

/**
 * Created by  on 2016/11/17.
 */
public class AuthenticationBean {

    public static final int PENDING_VERIFICATION = 0;
    public String code;
    public String appId;
    public String error;
    //    0:待验证
//    1:验证通过
//    2:验证失败
//    3:请再注册并拨号
    public int status;
    //    400电话
//    registe==true时响应二、字段
    public String ivrId;
    public String mid;

    public boolean isSuccess() {
        return "0000".equals(code);
    }


    @Override
    public String toString() {
        return "AuthenticationBean{" +
                "code='" + code + '\'' +
                ", appId='" + appId + '\'' +
                ", error='" + error + '\'' +
                ", status=" + status +
                ", ivrId='" + ivrId + '\'' +
                ", mid='" + mid + '\'' +
                '}';
    }
}
