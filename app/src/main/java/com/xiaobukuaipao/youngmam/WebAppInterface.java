package com.xiaobukuaipao.youngmam;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.widget.Toast;
import android.util.Log;

/**
 * Created by xiaobu1 on 15-7-7.
 */
public class WebAppInterface {

    private static final String TAG = WebAppInterface.class.getSimpleName();

    Context context;

    OnWebShareListener onWebShareListener;

    OnEvaluateListener onEvaluateListener;

    public void setOnWebShareListener(OnWebShareListener onWebShareListener) {
        this.onWebShareListener = onWebShareListener;
    }

    public void setOnEvaluateListener(OnEvaluateListener onEvaluateListener) {
        this.onEvaluateListener = onEvaluateListener;
    }

    /** Instantiate the interface and set the context */
    WebAppInterface(Context context) {
        this.context = context;
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void appShare(String shareTitle, String shareContent, String shareUrl,
                         String  shareImgUrl, String sharePlatform) {
        onWebShareListener.onWebShare(shareTitle, shareContent, shareUrl, shareImgUrl, sharePlatform);
    }

    @JavascriptInterface
    public void appNativeShare(String nativeShareTitle, String nativeShareContent,
                               String nativeShareUrl, String  nativeShareImgUrl, String nativeSharePlatform) {
        onWebShareListener.onWebShare(nativeShareTitle, nativeShareContent, nativeShareUrl, nativeShareImgUrl, nativeSharePlatform);
    }

    @JavascriptInterface
    public void appNeedLogin() {
        Toast.makeText(context, "参加活动需要先登录哦~", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, RegisterAndLoginActivity.class);
        context.startActivity(intent);
    }

    @JavascriptInterface
    public void appEvaluate() {
        Log.d(TAG, "app evaluate");
        if (onEvaluateListener != null) {
            onEvaluateListener.onEvaluate();
        }
    }

    public interface OnWebShareListener {
        void onWebShare(String shareTitle, String shareContent, String shareUrl,
                               String  shareImgUrl, String sharePlatform);
    }

    public interface OnEvaluateListener {
        void onEvaluate();
    }

}
