package com.xiaobukuaipao.youngmam.http;

import java.util.Map;

/**
 * Created by xiaobu1 on 15-5-11.
 */
public class HttpResult {
    // 所有状态码
    public static final int HTTP_STATUS_200 = 200;

    // 状态码
    private int statusCode;
    // 返回的实际数据
    private String data;
    // headers
    private Map<String, String> headers;

    private HttpResult(Builder builder) {
        this.statusCode = builder.statusCode;
        this.data = builder.data;
        this.headers = builder.headers;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return this.data;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public static class Builder {
        private int statusCode;
        private String data;
        private Map<String, String> headers;

        public Builder statusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder data(String data) {
            this.data = data;
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public HttpResult build() {
            return new HttpResult(this);
        }
    }

}
