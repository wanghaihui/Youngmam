package com.xiaobukuaipao.youngmam.http;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.xiaobukuaipao.youngmam.HuaYoungApplication;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by xiaobu1 on 15-5-11.
 */
public class HttpResultRequest extends Request<HttpResult> implements Listener<HttpResult> {
    private static final String TAG = HttpResultRequest.class.getSimpleName();

    /** 请求编码 */
    private static final String PROTOCOL_CHARSET = "utf-8";
    /** 请求id */
    private int requestId;

    private String key;

    /** 请求头部 */
    private Map<String, String> headers;
    /** 回调业务层做解析封装 */
    private ResponseParserListener parserListener;
    /** 分发解析好的数据到业务层 */
    private IEventLogic logic;

    private String body;
    private Map<String, String> params;

    /**
     * socket timeout in milliseconds for requests
     */
    private static final int HTTPRESULT_TIMEOUT_MS = 10000;

    /**
     * default number of retries for requests
     */
    private static final int HTTPRESULT_MAX_RETRIES = 0;

    /**
     * default backoff multiplier for requests
     */
    private static final float HTTPRESULT_BACKOFF_MULT = 2f;


    public HttpResultRequest(final int requestId, String url, int method, ResponseParserListener parserListener, final IEventLogic logic) {
        this(requestId, url, method, null, null, parserListener, logic, null);
    }

    public HttpResultRequest(final int requestId, String url, ResponseParserListener parserListener, final IEventLogic logic) {
        this(requestId, url, Method.GET, null, null, parserListener, logic, null);
    }

    public HttpResultRequest(final int requestId, String url, Map<String, String> headers, ResponseParserListener parserListener, final IEventLogic logic) {
        this(requestId, url, Method.GET, null, headers, parserListener, logic, null);
    }

    public HttpResultRequest(final int requestId, String url, int method, Map<String, String> params,
                             Map<String, String> headers, ResponseParserListener parserListener, final IEventLogic logic, final String key) {

        super(method, url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                logic.onResult(requestId, key, error);
            }
        });

        /**
         * 如果是Post请求,重传机制改变为0次重传
         */
        if (method == Method.POST) {
            setRetryPolicy(new DefaultRetryPolicy(HTTPRESULT_TIMEOUT_MS, HTTPRESULT_MAX_RETRIES, HTTPRESULT_BACKOFF_MULT));
        }

        this.params = params;
        this.headers = headers;
        this.parserListener = parserListener;
        this.requestId = requestId;
        this.logic = logic;
        this.key = key;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        try {
            return body == null ? super.getBody() : body.getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                    body, PROTOCOL_CHARSET);
            return null;
        }
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void addHeaders(Map<String, String> headers) {
        if (this.headers == null || headers == null) {
            setHeaders(headers);
        } else {
            this.headers.putAll(headers);
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (headers != null) {
            Log.d(TAG, "request id : " + requestId);
            HuaYoungApplication.getInstance().addCookie(headers);
            return headers;
        } else {
            return super.getHeaders();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Response回调监听器
     */
    @Override
    public void onResponse(HttpResult response) {
        logic.onResult(requestId, key, response);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    // 先解析Response,然后分发Response

    /**
     * 解析网络返回数据----这里已经获得了网络请求的返回字符串
     *
     * @see com.android.volley.Request#parseNetworkResponse(com.android.volley.NetworkResponse)
     */
    @Override
    protected Response<HttpResult> parseNetworkResponse(NetworkResponse response) {
        try {
            String strData = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            HttpResult httpResult = parserListener.doParse(response.statusCode, strData, response.headers);

            if (httpResult == null) {
                // 解析失败
                return Response.error(new VolleyError("parse response error >>> " + strData));
            } else {
                // 解析成功
                return Response.success(httpResult, HttpHeaderParser.parseCacheHeaders(response));
            }
        } catch (UnsupportedEncodingException e) {
            return Response.error(new VolleyError("UnsupportedEncodingException"));
        } catch (Exception e) {
            return Response.error(new VolleyError("Exception is >>> " + e.getMessage()));
        }
    }

    /**
     * 分发Response
     */
    protected void deliverResponse(HttpResult response) {
        Log.d(TAG, response.getData());
        this.onResponse(response);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 解析接口
     */
    public interface ResponseParserListener {
        HttpResult doParse(final int statusCode, final String data, final Map<String, String> headers) throws Exception;
    }
}
