package com.yzt400.yzt.bean;

/**
 * Created by  on 2016/11/17.
 */
public class AuthenticationBody {

    public static final int REGISTER = 1;
    public static final int QUERY = 2;
    public static final int CLEAR = 0;
    public String s = "0002";
    public String mobile;
    public String appId = "test";
    //    业务编号:
//    000:默认
//    001:注册
//    002:转帐
    public String busi;
//    0:清除服务器拨号消息
//    1:拨号前注册
//    2:拨号查询
    public int op;
    public String mid;
}
