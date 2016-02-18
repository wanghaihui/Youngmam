package com.xiaobukuaipao.youngmam;

import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;

import com.alibaba.fastjson.JSONObject;
import com.umeng.socialize.media.UMImage;
import com.xiaobukuaipao.youngmam.domain.BannerActivity;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.message.YoungBusinessType;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;
import com.xiaobukuaipao.youngmam.widget.YoungShareBoard;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

/**
 * Created by xiaobu1 on 15-7-15.
 * Banner WebPage Activity
 */
public class WebPageActivity extends BaseWebViewActivity {

    private BannerActivity bannerActivity;

    /**
     * 初始化WebView
     */
    public void initWebView() {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_web_page);
        getIntentDatas();

        webView = (WebView) findViewById(R.id.webview);
    }

    private void getIntentDatas() {
        bannerActivity = getIntent().getParcelableExtra("banner_webpage");
        if (bannerActivity != null) {
            webUrl = bannerActivity.getTargetUrl();
        }
    }

    /**
     * 设置ActionBar
     */
    public void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setRightAction(YoungActionBar.Type.TEXT, R.drawable.general_share, getResources().getString(R.string.str_share));

        if (bannerActivity != null && !StringUtil.isEmpty(bannerActivity.getTitle())) {
            actionBar.setMiddleAction2(YoungActionBar.Type.TEXT, 0, bannerActivity.getTitle());
        }

        setBackClickListener(this);

        actionBar.getRightFrame().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });
    }

    private void share() {
        if (bannerActivity != null) {
            YoungShareBoard shareBoard = new YoungShareBoard(this);

            shareBoard.setShareParams(bannerActivity.getTargetUrl(), YoungBusinessType.BUSINESS_TYPE_WEBPAGE);
            shareBoard.setOnShareSuccessListener(this);

            shareBoard.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);

            setShareContent(mController, bannerActivity.getTargetUrl(), bannerActivity.getTitle(),
                    bannerActivity.getPosterUrl(), BaseHttpFragmentActivity.SHARE_TYPE_ACTIVITY);

            /**
             * 针对微博
             */
            mController.setShareContent(StringUtil.buildWeiboShareActivity(bannerActivity.getTitle(), bannerActivity.getTargetUrl()));
            mController.setShareMedia(new UMImage(this, bannerActivity.getPosterUrl()));
        }
    }

    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {
        switch (msg.what) {
            case R.id.share_bonus:
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

}
