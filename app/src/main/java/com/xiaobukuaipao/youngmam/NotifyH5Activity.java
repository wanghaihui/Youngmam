package com.xiaobukuaipao.youngmam;

import android.os.Message;
import android.view.Window;
import android.webkit.WebView;

import com.xiaobukuaipao.youngmam.widget.YoungActionBar;

/**
 * Created by xiaobu1 on 15-7-12.
 */
public class NotifyH5Activity extends BaseWebViewActivity {
    /**
     * 初始化WebView
     */
    public void initWebView() {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_notify_h5);
        getIntentDatas();

        webView = (WebView) findViewById(R.id.webview);
    }

    private void getIntentDatas() {
        webUrl = getIntent().getStringExtra("target_url");
    }

    /**
     * 设置ActionBar
     */
    public void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);

        setBackClickListener(this);
    }

    @Override
    public void onResponse(Message msg) {

    }
}
