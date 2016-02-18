package com.xiaobukuaipao.youngmam.http;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.xiaobukuaipao.youngmam.HuaYoungApplication;
import com.xiaobukuaipao.youngmam.http.multipart.FilePart;
import com.xiaobukuaipao.youngmam.http.multipart.MultiPartRequest;
import com.xiaobukuaipao.youngmam.http.multipart.MultipartEntity;
import com.xiaobukuaipao.youngmam.http.multipart.StringPart;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiaobu1 on 15-5-13.
 */
public class HttpResultMultiPartRequest extends MultiPartRequest<HttpResult> implements Response.Listener<HttpResult> {
    private static final String TAG = HttpResultMultiPartRequest.class.getSimpleName();

    /** request identifier*/
    private int requestId;

    private String key;

    /** request headers*/
    private Map<String, String> headers;

    private HttpResultRequest.ResponseParserListener parserListener;

    private IEventLogic eventLogic;

    private MultipartEntity entity;

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

    public HttpResultMultiPartRequest(final int requestId, String url, int method,
                                      HttpResultRequest.ResponseParserListener parserListener, final IEventLogic eventLogic, final String key) {

        super(method, url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                eventLogic.onResult(requestId, key, error);
            }
        });

        this.parserListener = parserListener;
        this.requestId = requestId;
        this.eventLogic = eventLogic;
        this.key = key;

        this.headers = new HashMap<String, String>();
        this.entity = new MultipartEntity();

        /**
         * 如果是Post请求,重传机制改变为0次重传
         */
        if (method == Method.POST) {
            setRetryPolicy(new DefaultRetryPolicy(HTTPRESULT_TIMEOUT_MS, HTTPRESULT_MAX_RETRIES, HTTPRESULT_BACKOFF_MULT));
        }
    }

    /**
     * Response回调监听器
     */
    @Override
    public void onResponse(HttpResult response) {
        eventLogic.onResult(requestId, key, response);
    }


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
            HuaYoungApplication.getInstance().addCookie(headers);
            return headers;
        } else {
            return super.getHeaders();
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////

    /**
     * 解析网络返回数据----这里已经获得了网络请求的返回字符串
     *
     * @see com.android.volley.Request#parseNetworkResponse(com.android.volley.NetworkResponse)
     */
    @Override
    protected Response<HttpResult> parseNetworkResponse(NetworkResponse response) {

        try {
            Log.d(TAG, "time ms :" + response.networkTimeMs);
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
        this.onResponse(response);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public byte[] getBody() throws AuthFailureError {
        File file = new File(getFilesToUpload().get("filename"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (entity != null) {
            try {
                entity.addPart(new FilePart(getFilesToUpload().get("name"), file, file.getName(), "multipart/form-data"));
                // 遍历Map--传String参数
                if (getMultipartParams().size() > 0) {
                    for (String key : getMultipartParams().keySet()) {
                        entity.addPart(new StringPart(key, getMultipartParams().get(key).value, getProtocolCharset()));
                    }
                }
                entity.writeTo(baos);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return baos.toByteArray();
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data; boundary=\"" + entity.getBoundary() + '"';
    }
}
