package com.xiaobukuaipao.youngmam.http;

import org.json.JSONException;

import java.util.Map;

/**
 * Created by xiaobu1 on 15-5-11.
 */
public class HttpResultParser implements HttpResultRequest.ResponseParserListener {

    public HttpResult doParse(final int statusCode, final String data, final Map<String, String> headers) throws JSONException {
        HttpResult httpResult = new HttpResult.Builder()
                    .statusCode(statusCode)
                    .data(data)
                    .headers(headers)
                    .build();
        return httpResult;
    }

}
