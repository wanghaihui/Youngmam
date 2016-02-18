package com.xiaobukuaipao.youngmam;

import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;

import com.alibaba.fastjson.JSONObject;
import com.umeng.socialize.media.UMImage;
import com.xiaobukuaipao.youngmam.domain.Topic;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.message.YoungBusinessType;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;
import com.xiaobukuaipao.youngmam.widget.YoungShareBoard;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

/**
 * Created by xiaobu1 on 15-7-15.
 * Topic WebPage
 */
public class TopicWebActivity extends BaseWebViewActivity {

    private Topic topic;

    private String topicTitle;

    /**
     * 初始化WebView
     */
    public void initWebView() {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_topic_web);
        getIntentDatas();

        webView = (WebView) findViewById(R.id.webview);
    }

    private void getIntentDatas() {
        topic = getIntent().getParcelableExtra("topic");
        if (topic != null) {
            webUrl = topic.getTargetUrl();
            topicTitle = topic.getTitle();
        }
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
        if (topic != null) {
            YoungShareBoard shareBoard = new YoungShareBoard(this);

            shareBoard.setShareParams(topic.getTargetUrl(), YoungBusinessType.BUSINESS_TYPE_URL);
            shareBoard.setOnShareSuccessListener(this);

            shareBoard.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
            setShareContent(mController, topic.getTargetUrl(), topic.getDesc(),
                    topic.getPosterUrl(), topicTitle, BaseHttpFragmentActivity.SHARE_TYPE_TOPIC);

            /**
             * 针对微博
             */
            mController.setShareContent(StringUtil.buildWeiboShareTopic(topicTitle, topic.getTargetUrl()));
            if (!StringUtil.isEmpty(topic.getPosterUrl())) {
                mController.setShareMedia(new UMImage(this, topic.getPosterUrl()));
            } else {
                mController.setShareMedia(new UMImage(this, R.mipmap.share_huayoung));
            }
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
