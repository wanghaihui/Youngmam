package com.xiaobukuaipao.youngmam;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;
import com.xiaobukuaipao.youngmam.utils.CommonUtil;
import com.xiaobukuaipao.youngmam.utils.DisplayUtil;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.utils.VersionUtil;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;
import com.xiaobukuaipao.youngmam.widget.YoungShareBoard;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

import java.io.IOException;
import java.net.URL;

/**
 * Created by xiaobu1 on 15-8-6.
 */
public abstract class BaseWebViewActivity extends BaseHttpFragmentActivity implements YoungShareBoard.OnShareSuccessListener {
    private static final String TAG = BaseWebViewActivity.class.getSimpleName();

    protected WebView webView;
    /**
     * Web Url
     */
    protected String webUrl;

    /**
     * 分享控制器
     */
    final UMSocialService mController = UMServiceFactory.getUMSocialService(GlobalConstants.UMENG_DESCRIPTOR);
    // 分享的标题
    protected String shareTitle;
    // 分享的内容
    protected String shareContent;
    // 分享的内容的链接
    protected String targetUrl;
    // 分享的图片--活动或文章或主题
    protected String imageUrl;

    // 文件上传
    private ValueCallback<Uri> mUploadMessage;
    private final static int FILECHOOSER_RESULTCODE = 1;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;

    // ActionBar
    private int leftFrameWidth;
    private int rightFrameWidth;

    // WebView是否加载完毕
    protected boolean pageFinished = false;

    /**
     * 初始化View
     */
    @Override
    public void initViews() {
        initWebView();
        initActionBar();
        setWebView();
        // 设置Cookie
        setCookies();
        initShare();
    }

    /**
     * 初始化WebViews
     */
    protected abstract void initWebView();

    /**
     * 设置ActionBar
     */
    protected abstract void setYoungActionBar();

    /**
     * 初始化ActionBar
     */
    private void initActionBar() {
        actionBar = (YoungActionBar) findViewById(R.id.action_bar);

        setYoungActionBar();
    }

    /**
     * 设置WebView基本信息
     */
    private void setWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setDefaultTextEncodingName("HTF-8");

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                BaseWebViewActivity.this.setProgress(progress * 1000);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);

                if (actionBar != null) {
                    setActionBarMiddleAction(title);
                }
            }

            // For Android 3.0-
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "File Chooser"), FILECHOOSER_RESULTCODE);
            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);
            }

            // For Android 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "File Chooser"), FILECHOOSER_RESULTCODE);
            }

            // openFileChooser function is not called on Android 4.4, 4.4.1, 4.4.2
            // This bug was fixed on Android 4.4.3

            // For Android 5.0+
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {

                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }

                uploadMessage = filePathCallback;

                Intent intent = fileChooserParams.createIntent();
                try {
                    startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e) {
                    uploadMessage = null;
                    Toast.makeText(BaseWebViewActivity.this, "暂不支持选择文件", Toast.LENGTH_SHORT).show();
                    return false;
                }

                return true;
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(BaseWebViewActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
            }

            public void onPageFinished(WebView view, String url) {
                pageFinished = true;

                view.loadUrl("javascript:!function(){" +
                        "s=document.createElement('style');s.innerHTML="
                        + "\"@font-face{font-family:FZLTXHK--GBK1-0;src:url('**injection**/fonts/FZLTXIHK.TTF');}\";"
                        + "document.getElementsByTagName('head')[0].appendChild(s);" +
                        "}()");
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view,  String url) {
                WebResourceResponse response =  super.shouldInterceptRequest(view, url);
                if (url != null && url.contains("**injection**/")) {

                    //String assertPath = url.replace("**injection**/", "");
                    String assertPath = url.substring(url.indexOf("**injection**/") + "**injection**/".length(), url.length());
                    try {

                        response = new WebResourceResponse("application/x-font-ttf",
                                "UTF8", getAssets().open(assertPath)
                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return response;
            }
        });

        /**
         * 设置WebView获得焦点
         */
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!v.hasFocus()) {
                            v.requestFocus();
                        }
                        break;
                }
                return false;
            }
        });

    }

    /**
     * 整体设置Cookie
     */
    private void setCookies() {
        if (webUrl != null) {
            if (CommonUtil.isLogin(this)) {
                synUidCookies(this, webUrl);
                synTCookies(this, webUrl);
            } else {
                clearCookies(this);
            }

            setDeviceTypeCookie(this, webUrl);
            setVerCookie(this, webUrl);

            webView.loadUrl(webUrl);
        }
    }

    /**
     * 设置分享信息
     */
    private void initShare() {
        // 配置需要分享的相关平台
        configPlatforms(mController);
    }

    private void setActionBarMiddleAction(final String title) {
        if (!StringUtil.isEmpty(title)) {
            actionBar.setMiddleAction2(YoungActionBar.Type.TEXT, 0, title);

            leftFrameWidth = DisplayUtil.dip2px(this, 54);
            actionBar.getRightFrame().getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            rightFrameWidth = actionBar.getRightFrame().getWidth();

                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins(leftFrameWidth, 0, rightFrameWidth, 0);
                            params.addRule(RelativeLayout.CENTER_IN_PARENT);
                            actionBar.getMiddleFrame().setLayoutParams(params);

                            actionBar.setMiddleAction2(YoungActionBar.Type.TEXT, 0, title);
                            actionBar.getRightFrame().getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                    });
        }
    }

    /**
     * 同步Cookie
     * @param context
     * @param url
     */
    private void synUidCookies(Context context, String url) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(context);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            // 移除
            cookieManager.removeSessionCookie();
            // cookies是在HttpClient中获得的cookie
            try {
                URL urlURL = new URL(url);

                StringBuilder sb = new StringBuilder();
                sb.append(HuaYoungApplication.getInstance().getCookieValue());

                sb.append("Max-Age=");
                sb.append("3600");
                sb.append(";");
                sb.append("Domain=");
                sb.append(urlURL.getHost());
                sb.append(";");
                sb.append("Path=");
                sb.append("/");

                cookieManager.setCookie(url, sb.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }

            CookieSyncManager.getInstance().sync();
        } else {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            // 移除
            cookieManager.removeSessionCookies(null);
            // cookies是在HttpClient中获得的cookie
            try {
                URL urlURL = new URL(url);

                StringBuilder sb = new StringBuilder();
                sb.append(HuaYoungApplication.getInstance().getCookieValue());

                sb.append("Max-Age=");
                sb.append("3600");
                sb.append(";");
                sb.append("Domain=");
                sb.append(urlURL.getHost());
                sb.append(";");
                sb.append("Path=");
                sb.append("/");

                cookieManager.setCookie(url, sb.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }

            cookieManager.flush();
        }

    }

    /**
     * 设置T票Cookie
     * @param context
     * @param url
     */
    private void synTCookies(Context context, String url) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(context);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            // 移除
            cookieManager.removeSessionCookie();
            // cookies是在HttpClient中获得的cookie
            try {
                URL urlURL = new URL(url);

                StringBuilder sb = new StringBuilder();

                if (HuaYoungApplication.mCookie_T != null) {
                    sb.append("t=");
                    sb.append(HuaYoungApplication.mCookie_T);
                    sb.append(";");
                }

                sb.append("Max-Age=");
                sb.append("3600");
                sb.append(";");
                sb.append("Domain=");
                sb.append(urlURL.getHost());
                sb.append(";");
                sb.append("Path=");
                sb.append("/");
                cookieManager.setCookie(url, sb.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }

            CookieSyncManager.getInstance().sync();
        } else {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            // 移除
            cookieManager.removeSessionCookies(null);
            // cookies是在HttpClient中获得的cookie
            try {
                URL urlURL = new URL(url);

                StringBuilder sb = new StringBuilder();

                if (HuaYoungApplication.mCookie_T != null) {
                    sb.append("t=");
                    sb.append(HuaYoungApplication.mCookie_T);
                    sb.append(";");
                }

                sb.append("Max-Age=");
                sb.append("3600");
                sb.append(";");
                sb.append("Domain=");
                sb.append(urlURL.getHost());
                sb.append(";");
                sb.append("Path=");
                sb.append("/");

                cookieManager.setCookie(url, sb.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }

            cookieManager.flush();
        }
    }

    /**
     * 清空Cookie
     * @param context
     */
    private void clearCookies(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(context);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            CookieSyncManager.getInstance().sync();
        } else {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookies(null);
            cookieManager.flush();
        }
    }

    /**
     * 设置设备类型cookie
     * @param context
     * @param url
     */
    private void setDeviceTypeCookie(Context context, String url) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(context);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            // 移除
            cookieManager.removeSessionCookie();
            // cookies是在HttpClient中获得的cookie
            try {
                URL urlURL = new URL(url);

                StringBuilder sb = new StringBuilder();

                sb.append("deviceType=");
                sb.append("android");
                sb.append(";");

                sb.append("Max-Age=");
                sb.append("3600");
                sb.append(";");
                sb.append("Domain=");
                sb.append(urlURL.getHost());
                sb.append(";");
                sb.append("Path=");
                sb.append("/");

                cookieManager.setCookie(url, sb.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
            CookieSyncManager.getInstance().sync();
        } else {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            // 移除
            cookieManager.removeSessionCookies(null);
            // cookies是在HttpClient中获得的cookie
            try {
                URL urlURL = new URL(url);

                StringBuilder sb = new StringBuilder();

                sb.append("deviceType=");
                sb.append("android");
                sb.append(";");

                sb.append("Max-Age=");
                sb.append("3600");
                sb.append(";");
                sb.append("Domain=");
                sb.append(urlURL.getHost());
                sb.append(";");
                sb.append("Path=");
                sb.append("/");

                cookieManager.setCookie(url, sb.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
            cookieManager.flush();
        }
    }

    private void setVerCookie(Context context, String url) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(context);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            // 移除
            cookieManager.removeSessionCookie();
            // cookies是在HttpClient中获得的cookie
            try {
                URL urlURL = new URL(url);

                StringBuilder sb = new StringBuilder();

                // 添加Android版本信息
                sb.append("version=");
                sb.append(VersionUtil.getVersionName(this));
                sb.append(";");

                sb.append("Max-Age=");
                sb.append("3600");
                sb.append(";");
                sb.append("Domain=");
                sb.append(urlURL.getHost());
                sb.append(";");
                sb.append("Path=");
                sb.append("/");

                cookieManager.setCookie(url, sb.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
            CookieSyncManager.getInstance().sync();
        } else {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            // 移除
            cookieManager.removeSessionCookies(null);
            // cookies是在HttpClient中获得的cookie
            try {
                URL urlURL = new URL(url);

                StringBuilder sb = new StringBuilder();

                // 添加Android版本信息
                sb.append("version=");
                sb.append(VersionUtil.getVersionName(this));
                sb.append(";");

                sb.append("Max-Age=");
                sb.append("3600");
                sb.append(";");
                sb.append("Domain=");
                sb.append(urlURL.getHost());
                sb.append(";");
                sb.append("Path=");
                sb.append("/");

                cookieManager.setCookie(url, sb.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
            cookieManager.flush();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMSsoHandler ssoHandler = SocializeConfig.getSocializeConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

        /**
         * 文件选择返回
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 5.0以上
            if (requestCode == REQUEST_SELECT_FILE) {
                if (null == uploadMessage) {
                    return;
                }

                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                uploadMessage = null;
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) {
                return;
            }

            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }

    @Override
    public void onShareSuccess(String shareId, int shareType) {
        mEventLogic.shareBonus(shareId, shareType);
    }
}
