package com.xiaobukuaipao.youngmam;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;

import com.alibaba.fastjson.JSONObject;
import com.umeng.socialize.media.UMImage;
import com.xiaobukuaipao.youngmam.WebAppInterface.OnWebShareListener;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;
import com.xiaobukuaipao.youngmam.widget.YoungShareBoard;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

/**
 * Created by xiaobu1 on 15-7-6.
 */
public class BannerH5Activity extends BaseWebViewActivity implements OnWebShareListener {

    private static final String TAG = BannerH5Activity.class.getSimpleName();

    private Handler handler = new Handler();

    private WebAppInterface webAppInterface;

    /**
     * 初始化WebView
     */
    public void initWebView() {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_banner_h5);
        getIntentDatas();

        webView = (WebView) findViewById(R.id.webview);

        webAppInterface = new WebAppInterface(this);
        webAppInterface.setOnWebShareListener(this);
        webView.addJavascriptInterface(webAppInterface, "Android");
    }

    private void getIntentDatas() {
        webUrl = getIntent().getStringExtra("target_url");
    }

    /**
     * 设置ActionBar
     */
    public void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setRightAction(YoungActionBar.Type.TEXT, R.drawable.general_share, getResources().getString(R.string.str_share));

        setBackClickListener(this);

        actionBar.getRightFrame().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });
    }

    private void share() {
        if (pageFinished) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl("javascript:androidShareNotify()");
                }
            });
        } else {
            Log.d(TAG, "JS未加载完毕");
        }
    }

    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {
            case R.id.share_webpage_bonus:
                if (checkResponse(msg)) {
                    HttpResult httpResult = (HttpResult) msg.obj;
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());
                    int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);

                    if (status == 0) {
                        // 3
                        if (jsonObject.containsKey(GlobalConstants.JSON_BONUSPOINT)) {
                            showCreditDialog(getResources().getString(R.string.str_dialog_credit,
                                    jsonObject.getInteger(GlobalConstants.JSON_BONUSPOINT)));
                        }
                    }
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onWebShare(final String shareTitle, final String shareContent, final String shareUrl,
                           final String  shareImgUrl, final String sharePlatform) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                YoungShareBoard shareBoard = new YoungShareBoard(BannerH5Activity.this, sharePlatform);

                shareBoard.showAtLocation(BannerH5Activity.this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
                setShareContent(mController, shareUrl, shareContent, shareImgUrl, shareTitle, BaseHttpFragmentActivity.SHARE_TYPE_ACTIVITY);

                /**
                 * 针对微博
                 */
                mController.setShareContent(StringUtil.buildWeiboShareActivity(shareContent, shareUrl));
                mController.setShareMedia(new UMImage(BannerH5Activity.this, shareImgUrl));
            }
        });

    }
}
