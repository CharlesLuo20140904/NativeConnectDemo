package com.yzt400.yzt.bean;

/**
 * Created by  on 2016/11/17.
 */
public class HeaderBase {
    public String k;
    //    0000	成功
//    0001	解码失败
//    0002	参数错误
//    0003	密钥过期
//    0004	认证失败
//    0005	数据未找到
//    9999	服务器错误
    public String code;
    public String sign;
    public String error;

    public boolean isRefresh() {
        return "0001".equals(code) || "0003".equals(code) || "0004".equals(code);
    }
}
