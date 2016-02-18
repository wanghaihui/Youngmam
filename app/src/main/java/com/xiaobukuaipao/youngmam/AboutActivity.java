package com.xiaobukuaipao.youngmam;

import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.widget.YoungActionBar;

/**
 * Created by xiaobu1 on 15-6-2.
 */
public class AboutActivity extends BaseFragmentActivity {

    private WebView webView;
    /**
     * 初始化View
     */
    public void initViews() {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);

        setContentView(R.layout.activity_about);

        actionBar = (YoungActionBar) findViewById(R.id.action_bar);
        setYoungActionBar();

        webView = (WebView) findViewById(R.id.webview);

        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                AboutActivity.this.setProgress(progress * 1000);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(AboutActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
            }
        });

        webView.loadUrl(GlobalConstants.ABOUT);
    }

    /**
     * 设置ActionBar
     */
    private void setYoungActionBar() {
        actionBar.setLeftAction(YoungActionBar.Type.IMAGE, R.drawable.btn_back, null);
        actionBar.setMiddleAction(YoungActionBar.Type.TEXT, 0, getResources().getString(R.string.str_about_us));

        setBackClickListener(this);
    }
}
