package com.yzt400.yzt.bean;

/**
 * Created by  on 2016/11/17.
 */
public class TokenRequest {
    public HeaderBase header;
    public String body;
    public TokenRequest() {
    }
    public TokenRequest(HeaderBase header, String body) {
        this.header = header;
        this.body = body;
    }
}
