package com.xiaobukuaipao.youngmam;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMSsoHandler;
import com.xiaobukuaipao.youngmam.WebAppInterface.OnWebShareListener;
import com.xiaobukuaipao.youngmam.http.ApiConstants;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.utils.VersionUtil;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;
import com.xiaobukuaipao.youngmam.widget.YoungShareBoard;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

/**
 * Created by xiaobu1 on 15-10-26.
 */
public class WelfareActivity extends BaseWebViewActivity implements OnWebShareListener, WebAppInterface.OnEvaluateListener {
    private static final String TAG = WelfareActivity.class.getSimpleName();

    public static final int REQUEST_MARKET = 101;
    public static final int DEVICE_TYPE = 3;

    private Handler handler = new Handler();

    private WebAppInterface webAppInterface;

    /**
     * 初始化WebView
     */
    public void initWebView() {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_welfare);

        webUrl = ApiConstants.WELFARE_URL;
        webView = (WebView) findViewById(R.id.webview);

        webAppInterface = new WebAppInterface(this);
        webAppInterface.setOnWebShareListener(this);
        webAppInterface.setOnEvaluateListener(this);
        webView.addJavascriptInterface(webAppInterface, "Android");
    }

    /**
     * 设置ActionBar
     */
    public void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_welfare));

        actionBar.getLeftFrame().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
            }
        });
    }

    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {
            case R.id.app_praise_bonus:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject.containsKey(GlobalConstants.JSON_BONUSPOINT)) {
                        showCreditDialog(getResources().getString(R.string.str_dialog_credit,
                                jsonObject.getInteger(GlobalConstants.JSON_BONUSPOINT)));
                    }
                }
                break;
        }
    }

    @Override
    public void onWebShare(final String shareTitle, final String shareContent, final String shareUrl,
                           final String  shareImgUrl, final String sharePlatform) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                YoungShareBoard shareBoard = new YoungShareBoard(WelfareActivity.this, sharePlatform);

                shareBoard.showAtLocation(WelfareActivity.this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
                setShareContent(mController, shareUrl, shareContent, shareImgUrl, shareTitle, BaseHttpFragmentActivity.SHARE_TYPE_ACTIVITY);

                /**
                 * 针对微博
                 */
                mController.setShareContent(StringUtil.buildWeiboShareWelfare(shareContent, shareUrl));
                mController.setShareMedia(new UMImage(WelfareActivity.this, shareImgUrl));
            }
        });

    }

    /**
     * 评价应用
     */
    @Override
    public void onEvaluate() {
        // 首先判断手机是否安装市场软件
        if (VersionUtil.hasAnyMarketInstalled(this)) {

            Log.d(TAG, "此时存在应用市场");
            try {
                String str = "market://details?id=" + getPackageName();
                Intent localIntent = new Intent("android.intent.action.VIEW");
                localIntent.setData(Uri.parse(str));
                startActivityForResult(localIntent, REQUEST_MARKET);
            } catch (android.content.ActivityNotFoundException anfe) {
                anfe.printStackTrace();
            }
        } else {
            Toast.makeText(this, "不存在Android应用市场哦~~先去装一个吧", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_MARKET) {
            // 在这里执行增加积分请求
            // 3--Android
            mEventLogic.appPraiseBonus(String.valueOf(DEVICE_TYPE), VersionUtil.getVersionName(this));
        }
        super.onActivityResult(requestCode, resultCode, data);
        UMSsoHandler ssoHandler = SocializeConfig.getSocializeConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
