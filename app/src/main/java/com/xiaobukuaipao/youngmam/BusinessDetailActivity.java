package com.xiaobukuaipao.youngmam;

import android.os.Message;
import android.view.Window;
import android.webkit.WebView;

import com.xiaobukuaipao.youngmam.widget.YoungActionBar;

/**
 * Created by xiaobu1 on 15-7-9.
 */
public class BusinessDetailActivity extends BaseWebViewActivity {

    /**
     * 初始化WebView
     */
    public void initWebView() {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_business_detail);
        getIntentDatas();

        webView = (WebView) findViewById(R.id.webview);
    }

    /**
     * 设置ActionBar
     */
    public void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);

        setBackClickListener(this);
    }

    private void getIntentDatas() {
        webUrl = getIntent().getStringExtra("target_url");
    }

    /**
     * 网络数据返回处理
     * @param msg
     */
    @Override
    public void onResponse(Message msg) {

    }
}
