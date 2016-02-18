package com.xiaobukuaipao.youngmam.wrap;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.xiaobukuaipao.youngmam.AppActivityManager;
import com.xiaobukuaipao.youngmam.BaseFragmentActivity;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.SplashActivity;
import com.xiaobukuaipao.youngmam.fragment.UiInterface;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.http.IEventLogic;
import com.xiaobukuaipao.youngmam.http.YoungEventLogic;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.view.LoadingView;

/**
 * Created by xiaobu1 on 15-5-12.
 */
public abstract class BaseHttpFragmentActivity extends BaseFragmentActivity implements UiInterface {
    private static final String TAG = BaseHttpFragmentActivity.class.getSimpleName();

    public static final int SHARE_TYPE_ARTICLE = 0;
    public static final int SHARE_TYPE_ACTIVITY = 1;
    public static final int SHARE_TYPE_TOPIC = 2;
    public static final int SHARE_TYPE_PUBLISH_ARTICLE = 3;
    public static final int SHARE_TYPE_THEME = 4;
    public static final int SHARE_TYPE_GIFT = 5;
    public static final int SHARE_TYPE_REGISTER = 6;
    public static final int SHARE_TYPE_QUESTION = 7;

    // Activity是否已销毁
    private boolean isDestroyed;
    /**
     * 网络逻辑
     */
    protected YoungEventLogic mEventLogic;

    /**
     * 加载进度
     */
    private LoadingView mLoadingView;

    /**
     * 进度框
     */
    private Dialog progressDialog;

    /**
     * 积分框
     */
    private Dialog creditDialog;
    private TextView creditTextView;


    protected boolean isPaused;

    protected TextView tipTextView;
    protected boolean dialogHidden = true;

    /** 基类Toast */
    private Toast mToast;

    protected boolean isMultiRequest = false;

    private Handler baseHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEventLogic = new YoungEventLogic(this);

        initLoading();

        delayedLoadDatas();

        executeHttpRequest();
    }

    protected void initLoading() {
        mLoadingView = (LoadingView) findViewById(R.id.loading_view);
        if (mLoadingView != null) {
            mLoadingView.register(this);
        }
    }

    protected void delayedLoadDatas() {

    }

    /**
     * 空函数--子函数可以重写
     */
    protected void executeHttpRequest() {

    }

    ///////////////////////////////////////////////////////////////////////////////
    /**
     * 正在加载
     */
    protected void onLoading() {
        onLoading(R.string.loading);
    }

    /**
     * 正在加载
     * @param obj
     */
    protected void onLoading(Object obj) {
        onLoading(R.string.loading, obj);
    }

    /**
     * 正在加载
     * @param stringId 描述信息
     */
    protected void onLoading(int stringId) {
        onLoading(getResources().getString(stringId));
    }

    /**
     * 正在加载
     * @param stringId 描述信息
     * @param obj
     */
    public void onLoading(int stringId, Object obj) {
        onLoading(getResources().getString(stringId), obj);
    }

    /**
     * 正在加载
     * @param loadDesc 描述信息
     */
    protected void onLoading(String loadDesc) {
        mLoadingView.onLoading(loadDesc, null);
    }

    /**
     * 正在加载
     * @param loadDesc 描述信息
     * @param obj 传递的参数
     */
    public void onLoading(String loadDesc, Object obj) {
        mLoadingView.onLoading(loadDesc, obj);
    }

    /**
     * 失败
     */
    protected void onFailure() {
        onFailure(R.string.loading_failure);
    }

    /**
     * 失败
     * @param stringId 描述信息
     */
    protected void onFailure(int stringId) {
        onFailure(getResources().getString(stringId));
    }

    /**
     * 失败
     * @param errorDesc 描述信息
     */
    protected void onFailure(String errorDesc) {
        mLoadingView.onFailure(errorDesc);
    }

    /**
     * 成功
     */
    protected void onSuccess() {
        mLoadingView.onSuccess();
    }

    ////////////////////////////////////////////////////////////////////////////////////

    public void showProgress(String message) {
        showProgress(message, true);
    }

    public void showProgress(String message, boolean cancelable) {
        if (progressDialog == null) {
            progressDialog = createLoadingDialog(message);
        } else if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        } if (!TextUtils.isEmpty(message)) {
            tipTextView.setText(message);
        } else {
            tipTextView.setText(getString(R.string.loading));
        }
        progressDialog.setCancelable(cancelable);
        progressDialog.show();


    }

    /**
     * 得到自定义的progressDialog
     *
     * @param msg
     * @return
     */
    private Dialog createLoadingDialog(String msg) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.dialog_loading, null);
        tipTextView = (TextView) v.findViewById(R.id.tipTextView);
        if (!TextUtils.isEmpty(msg)) {
            tipTextView.setText(msg);
        }
        Dialog loadingDialog = new Dialog(this, R.style.loading_dialog);
        loadingDialog.setCancelable(true);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setContentView(v);
        return loadingDialog;
    }

    public void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }
    }

    /**
     * 设置网络请求结束是否关闭对话框, 默认是关闭
     *
     * @param hidden true关闭 false不关闭
     */
    protected void defaultDialogHidden(boolean hidden) {
        dialogHidden = hidden;
    }

    ////////////////////////////////////////////////////////////////////////////////////

    /**
     * 显示积分框
     */
    public void showCreditDialog(String message) {
        showCreditDialog(message, true);
    }

    public void showCreditDialog(String message, boolean cancelable) {
        if (creditDialog == null) {
            creditDialog = createCreditDialog(message);
        } else if (creditDialog.isShowing()) {
            creditDialog.dismiss();
        } if (!TextUtils.isEmpty(message)) {
            creditTextView.setText(message);
        } else {
            creditTextView.setText(getString(R.string.loading));
        }
        creditDialog.setCancelable(cancelable);

        creditDialog.show();

        baseHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                creditDialog.dismiss();
            }
        }, 1000);
    }

    /**
     * 得到自定义的progressDialog
     *
     * @param msg
     */
    private Dialog createCreditDialog(String msg) {
        LayoutInflater inflater = LayoutInflater.from(this);

        View v = inflater.inflate(R.layout.credit_dialog, null);

        creditTextView = (TextView) v.findViewById(R.id.credit_text);
        if (!TextUtils.isEmpty(msg)) {
            creditTextView.setText(msg);
        }
        Dialog loadingDialog = new Dialog(this, R.style.credit_dialog);
        loadingDialog.setCancelable(true);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setContentView(v);
        return loadingDialog;
    }

    public void hideCreditProgress() {
        if (creditDialog != null && creditDialog.isShowing()) {
            creditDialog.dismiss();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        hideProgress();

        isDestroyed = true;
        /**
         * 解绑所有订阅者
         */
        unregisterAll(mEventLogic);
    }

    /**
     * 解绑当前订阅者
     */
    protected void unregister(IEventLogic... iEventLogics) {
        for (IEventLogic iEventLogic : iEventLogics) {
            if (iEventLogic != null) {
                iEventLogic.cancelAll();
                iEventLogic.unregister(this);
            }
        }
    }

    /**
     * 解绑所有订阅者
     */
    protected void unregisterAll(IEventLogic... iEventLogics) {
        for (IEventLogic iEventLogic : iEventLogics) {
            if (iEventLogic != null) {
                iEventLogic.cancelAll();
                iEventLogic.unregisterAll();
            }
        }
    }

    /**
     * EventBus订阅者事件通知的函数--UI线程
     */
    public void onEventMainThread(Message msg) {
        if (!isDestroyed && !isFinishing()) {

            if (dialogHidden && !isMultiRequest) {
                hideProgress();
            }

            onResponse(msg);
        }
    }

    public abstract void onResponse(Message msg);


    /**
     * 检测回复
     */
    protected boolean checkResponse(Message msg) {
        return checkResponse(msg, null, null, true);
    }

    protected boolean checkResponse(Message msg, boolean tipError) {
        return checkResponse(msg, null, null, tipError);
    }

    protected boolean checkResponse(Message msg, String errorTip) {
        return checkResponse(msg, null, errorTip, true);
    }

    protected boolean checkResponse(Message msg, String successTip, String errorTip) {
        return checkResponse(msg, successTip, errorTip, true);
    }

    /**
     * 校验服务器响应结果
     * @param msg
     * @param successTip
     * @param errorTip
     * @param tipError
     * @return true 业务成功, false 业务失败
     */
    public boolean checkResponse(Message msg, String successTip, String errorTip, boolean tipError) {
        if (msg.obj instanceof HttpResult) {
            HttpResult httpResult = (HttpResult) msg.obj;

            if (StringUtil.isNotEmpty(httpResult.getData())) {

                final char[] strChar = httpResult.getData().substring(0, 1).toCharArray();
                final char firstChar = strChar[0];

                // 此时是JSONObject
                if (firstChar == '{') {
                    JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());

                    if (jsonObject != null) {
                        if (jsonObject.getInteger(GlobalConstants.JSON_STATUS) != null) {

                            int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);

                            if (status == 100) {
                                // 此时,T票校验失败
                                AppActivityManager.getInstance().popAllActivity();
                                Intent intent = new Intent(this, SplashActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                                return false;
                            }
                        }
                    }
                }
            }

            if (httpResult.getStatusCode() == HttpResult.HTTP_STATUS_200) {
                // 此时,代表网络请求成功
                if (!TextUtils.isEmpty(successTip)) {
                    showToast(successTip);
                }
                return true;
            } else {
                // 此时,代表网络请求不成功
                if (tipError) {
                    if (!TextUtils.isEmpty(errorTip)) {
                        // 此时,错误提示不为空
                        showToast(errorTip);
                    } else {
                        // 此时,错误提示为空
                        showToast(getString(R.string.request_failure));
                    }
                }
                return false;
            }
        } else {
            if (tipError) {
                if (!TextUtils.isEmpty(errorTip)) {
                    // 此时,错误提示不为空
                    showToast(errorTip);
                } else {
                    // 此时,错误提示为空
                    showToast(getString(R.string.request_failure));
                }
            }
            return false;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     *
     * 根据字符串 show toast<BR>
     *
     * @param message 字符串
     */
    public void showToast(CharSequence message) {
        if (isPaused) {
            return;
        }

        if (mToast == null) {
            mToast = Toast.makeText(this,
                    message,
                    Toast.LENGTH_SHORT);
        } else {
            mToast.setText(message);
        }
        mToast.show();
    }


    ///////////////////////////////////////////////////////////////////////////////////
    /**
     * 分享
     */
    protected void configPlatforms(final UMSocialService mController) {
        // 添加新浪SSO授权
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        mController.getConfig().setSinaCallbackUrl(GlobalConstants.WEIBO_REDIRECT_URL);
        // 添加QQ平台
        UMQQSsoHandler umqqSsoHandler = new UMQQSsoHandler(this, GlobalConstants.QQ_APPID, GlobalConstants.QQ_APPKEY);
        umqqSsoHandler.setTargetUrl("http://www.youngmam.com/");
        umqqSsoHandler.addToSocialSDK();
        // 添加到QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, GlobalConstants.QQ_APPID, GlobalConstants.QQ_APPKEY);
        qZoneSsoHandler.addToSocialSDK();
        // 添加到微信平台
        UMWXHandler umwxHandler = new UMWXHandler(this, GlobalConstants.WECHAT_APPID, GlobalConstants.WECHAT_APPSECRET);
        umwxHandler.addToSocialSDK();
        // 支持微信朋友圈
        UMWXHandler umwxCircleHandler = new UMWXHandler(this, GlobalConstants.WECHAT_APPID, GlobalConstants.WECHAT_APPSECRET);
        umwxCircleHandler.setToCircle(true);
        umwxCircleHandler.addToSocialSDK();
    }

    // 设置分享内容
    protected void setShareContent(final UMSocialService mController, String targetUrl, String shareContent, String imageUrl, int shareType) {
        UMImage localImage = new UMImage(this, R.mipmap.share_huayoung);
        UMImage urlImage = new UMImage(this, imageUrl);

        // 微信个人或群
        WeiXinShareContent weiXinShareContent = new WeiXinShareContent();
        weiXinShareContent.setShareContent(shareContent);

        switch(shareType) {
            case BaseHttpFragmentActivity.SHARE_TYPE_ARTICLE:
                weiXinShareContent.setTitle(getResources().getString(R.string.str_share_title_article));
                break;
            case BaseHttpFragmentActivity.SHARE_TYPE_QUESTION:
                weiXinShareContent.setTitle(getResources().getString(R.string.str_share_title_question));
                break;
            case BaseHttpFragmentActivity.SHARE_TYPE_ACTIVITY:
                weiXinShareContent.setTitle(getResources().getString(R.string.str_share_title_activity));
                break;
            case BaseHttpFragmentActivity.SHARE_TYPE_TOPIC:
                weiXinShareContent.setTitle(getResources().getString(R.string.str_share_title_topic));
                break;
            case BaseHttpFragmentActivity.SHARE_TYPE_PUBLISH_ARTICLE:
                weiXinShareContent.setTitle(getResources().getString(R.string.str_share_title_publish_article));
                break;
            case BaseHttpFragmentActivity.SHARE_TYPE_GIFT:
                weiXinShareContent.setTitle(getResources().getString(R.string.str_share_gift));
                break;
            case BaseHttpFragmentActivity.SHARE_TYPE_REGISTER:
                weiXinShareContent.setTitle(getResources().getString(R.string.str_share_invite_register));
                break;

        }

        weiXinShareContent.setTargetUrl(targetUrl);

        if (StringUtil.isEmpty(imageUrl)) {
            weiXinShareContent.setShareMedia(localImage);
        } else {
            weiXinShareContent.setShareMedia(urlImage);
        }
        mController.setShareMedia(weiXinShareContent);

        // 微信朋友圈
        CircleShareContent circleShareContent = new CircleShareContent();
        circleShareContent.setShareContent(shareContent);

        switch(shareType) {
            case BaseHttpFragmentActivity.SHARE_TYPE_ARTICLE:
                circleShareContent.setTitle(getResources().getString(R.string.str_share_title_article));
                break;
            case BaseHttpFragmentActivity.SHARE_TYPE_QUESTION:
                weiXinShareContent.setTitle(getResources().getString(R.string.str_share_title_question));
                break;
            case BaseHttpFragmentActivity.SHARE_TYPE_ACTIVITY:
                circleShareContent.setTitle(getResources().getString(R.string.str_share_title_activity));
                break;
            case BaseHttpFragmentActivity.SHARE_TYPE_TOPIC:
                circleShareContent.setTitle(getResources().getString(R.string.str_share_title_topic));
                break;
            case BaseHttpFragmentActivity.SHARE_TYPE_PUBLISH_ARTICLE:
                circleShareContent.setTitle(getResources().getString(R.string.str_share_title_publish_article));
                break;
            case BaseHttpFragmentActivity.SHARE_TYPE_REGISTER:
                circleShareContent.setTitle(getResources().getString(R.string.str_share_invite_register));
                break;
        }

        circleShareContent.setTargetUrl(targetUrl);

        if (StringUtil.isEmpty(imageUrl)) {
            circleShareContent.setShareMedia(localImage);
        } else {
            circleShareContent.setShareMedia(urlImage);
        }
        mController.setShareMedia(circleShareContent);

        // QQ
        QQShareContent qqShareContent = new QQShareContent();

        if (!StringUtil.isEmpty(shareContent)) {
            qqShareContent.setShareContent(shareContent);
        } else {
            qqShareContent.setShareContent(" ");
        }

        switch(shareType) {
            case BaseHttpFragmentActivity.SHARE_TYPE_ARTICLE:
                qqShareContent.setTitle(getResources().getString(R.string.str_share_title_article));
                break;
            case BaseHttpFragmentActivity.SHARE_TYPE_QUESTION:
                weiXinShareContent.setTitle(getResources().getString(R.string.str_share_title_question));
                break;
            case BaseHttpFragmentActivity.SHARE_TYPE_ACTIVITY:
                qqShareContent.setTitle(getResources().getString(R.string.str_share_title_activity));
                break;
            case BaseHttpFragmentActivity.SHARE_TYPE_TOPIC:
                qqShareContent.setTitle(getResources().getString(R.string.str_share_title_topic));
                break;
            case BaseHttpFragmentActivity.SHARE_TYPE_PUBLISH_ARTICLE:
                qqShareContent.setTitle(getResources().getString(R.string.str_share_title_publish_article));
                break;
            case BaseHttpFragmentActivity.SHARE_TYPE_REGISTER:
                qqShareContent.setTitle(getResources().getString(R.string.str_share_invite_register));
                break;
        }

        qqShareContent.setTargetUrl(targetUrl);

        if (StringUtil.isEmpty(imageUrl)) {
            qqShareContent.setShareMedia(localImage);
        } else {
            qqShareContent.setShareMedia(urlImage);
        }
        mController.setShareMedia(qqShareContent);

        // QQ空间
        QZoneShareContent qZoneShareContent = new QZoneShareContent();

        if (!StringUtil.isEmpty(shareContent)) {
            qZoneShareContent.setShareContent(shareContent);
        } else {
            qZoneShareContent.setShareContent(" ");
        }

        switch(shareType) {
            case BaseHttpFragmentActivity.SHARE_TYPE_ARTICLE:
                qZoneShareContent.setTitle(getResources().getString(R.string.str_share_title_article));
                break;
            case BaseHttpFragmentActivity.SHARE_TYPE_QUESTION:
                weiXinShareContent.setTitle(getResources().getString(R.string.str_share_title_question));
                break;
            case BaseHttpFragmentActivity.SHARE_TYPE_ACTIVITY:
                qZoneShareContent.setTitle(getResources().getString(R.string.str_share_title_activity));
                break;
            case BaseHttpFragmentActivity.SHARE_TYPE_TOPIC:
                qZoneShareContent.setTitle(getResources().getString(R.string.str_share_title_topic));
                break;
            case BaseHttpFragmentActivity.SHARE_TYPE_PUBLISH_ARTICLE:
                qZoneShareContent.setTitle(getResources().getString(R.string.str_share_title_publish_article));
                break;
            case BaseHttpFragmentActivity.SHARE_TYPE_REGISTER:
                qZoneShareContent.setTitle(getResources().getString(R.string.str_share_invite_register));
                break;
        }

        qZoneShareContent.setTargetUrl(targetUrl);

        if (StringUtil.isEmpty(imageUrl)) {
            qZoneShareContent.setShareMedia(localImage);
        } else {
            qZoneShareContent.setShareMedia(urlImage);
        }
        mController.setShareMedia(qZoneShareContent);

    }

    // 设置分享内容
    protected void setShareContent(final UMSocialService mController, String targetUrl, String shareContent, String imageUrl, String title, int shareType) {
        UMImage localImage = new UMImage(this, R.mipmap.share_huayoung);
        UMImage urlImage = new UMImage(this, imageUrl);

        // 微信个人或群
        WeiXinShareContent weiXinShareContent = new WeiXinShareContent();

        weiXinShareContent.setShareContent(shareContent);

        if (!StringUtil.isEmpty(title) && shareType != BaseHttpFragmentActivity.SHARE_TYPE_THEME) {
            weiXinShareContent.setTitle(title);
        } else {
            switch (shareType) {
                case BaseHttpFragmentActivity.SHARE_TYPE_ARTICLE:
                    weiXinShareContent.setTitle(getResources().getString(R.string.str_share_title_article));
                    break;
                case BaseHttpFragmentActivity.SHARE_TYPE_ACTIVITY:
                    weiXinShareContent.setTitle(getResources().getString(R.string.str_share_title_activity));
                    break;
                case BaseHttpFragmentActivity.SHARE_TYPE_TOPIC:
                    weiXinShareContent.setTitle(getResources().getString(R.string.str_share_title_topic));
                    break;
                case BaseHttpFragmentActivity.SHARE_TYPE_PUBLISH_ARTICLE:
                    weiXinShareContent.setTitle(getResources().getString(R.string.str_share_title_publish_article));
                    break;
                case BaseHttpFragmentActivity.SHARE_TYPE_THEME:
                    weiXinShareContent.setTitle(getResources().getString(R.string.str_share_title_theme_begin)
                            + "\"" + title + "\"" + getResources().getString(R.string.str_share_title_theme_end));
                    break;
                case BaseHttpFragmentActivity.SHARE_TYPE_GIFT:
                    weiXinShareContent.setTitle(getResources().getString(R.string.str_share_gift));
                    break;
                case BaseHttpFragmentActivity.SHARE_TYPE_REGISTER:
                    weiXinShareContent.setTitle(getResources().getString(R.string.str_share_invite_register));
                    break;
            }
        }

        weiXinShareContent.setTargetUrl(targetUrl);

        if (StringUtil.isEmpty(imageUrl)) {
            weiXinShareContent.setShareMedia(localImage);
        } else {
            weiXinShareContent.setShareMedia(urlImage);
        }
        mController.setShareMedia(weiXinShareContent);

        // 微信朋友圈
        CircleShareContent circleShareContent = new CircleShareContent();
        circleShareContent.setShareContent(shareContent);

        if (!StringUtil.isEmpty(title) && shareType != BaseHttpFragmentActivity.SHARE_TYPE_THEME) {
            circleShareContent.setTitle(title);
        } else {
            switch (shareType) {
                case BaseHttpFragmentActivity.SHARE_TYPE_ARTICLE:
                    circleShareContent.setTitle(getResources().getString(R.string.str_share_title_article));
                    break;
                case BaseHttpFragmentActivity.SHARE_TYPE_ACTIVITY:
                    circleShareContent.setTitle(getResources().getString(R.string.str_share_title_activity));
                    break;
                case BaseHttpFragmentActivity.SHARE_TYPE_TOPIC:
                    circleShareContent.setTitle(getResources().getString(R.string.str_share_title_topic));
                    break;
                case BaseHttpFragmentActivity.SHARE_TYPE_PUBLISH_ARTICLE:
                    circleShareContent.setTitle(getResources().getString(R.string.str_share_title_publish_article));
                    break;
                case BaseHttpFragmentActivity.SHARE_TYPE_THEME:
                    circleShareContent.setTitle(getResources().getString(R.string.str_share_title_theme_begin)
                            + title + getResources().getString(R.string.str_share_title_theme_end));
                    break;
                case BaseHttpFragmentActivity.SHARE_TYPE_REGISTER:
                    circleShareContent.setTitle(getResources().getString(R.string.str_share_invite_register));
                    break;
            }
        }

        circleShareContent.setTargetUrl(targetUrl);

        if (StringUtil.isEmpty(imageUrl)) {
            circleShareContent.setShareMedia(localImage);
        } else {
            circleShareContent.setShareMedia(urlImage);
        }
        mController.setShareMedia(circleShareContent);

        // QQ
        QQShareContent qqShareContent = new QQShareContent();

        if (!StringUtil.isEmpty(shareContent)) {
            qqShareContent.setShareContent(shareContent);
        } else {
            qqShareContent.setShareContent(" ");
        }


        if (!StringUtil.isEmpty(title) && shareType != BaseHttpFragmentActivity.SHARE_TYPE_THEME) {
            qqShareContent.setTitle(title);
        } else {
            switch (shareType) {
                case BaseHttpFragmentActivity.SHARE_TYPE_ARTICLE:
                    qqShareContent.setTitle(getResources().getString(R.string.str_share_title_article));
                    break;
                case BaseHttpFragmentActivity.SHARE_TYPE_ACTIVITY:
                    qqShareContent.setTitle(getResources().getString(R.string.str_share_title_activity));
                    break;
                case BaseHttpFragmentActivity.SHARE_TYPE_TOPIC:
                    qqShareContent.setTitle(getResources().getString(R.string.str_share_title_topic));
                    break;
                case BaseHttpFragmentActivity.SHARE_TYPE_PUBLISH_ARTICLE:
                    qqShareContent.setTitle(getResources().getString(R.string.str_share_title_publish_article));
                    break;
                case BaseHttpFragmentActivity.SHARE_TYPE_THEME:

                    Log.d(TAG, "QQ share tag !");
                    qqShareContent.setTitle(getResources().getString(R.string.str_share_title_theme_begin)
                            + "\"" + title + "\"" + getResources().getString(R.string.str_share_title_theme_end));
                    break;
                case BaseHttpFragmentActivity.SHARE_TYPE_REGISTER:
                    qqShareContent.setTitle(getResources().getString(R.string.str_share_invite_register));
                    break;
            }
        }

        qqShareContent.setTargetUrl(targetUrl);

        if (StringUtil.isEmpty(imageUrl)) {
            qqShareContent.setShareMedia(localImage);
        } else {
            qqShareContent.setShareMedia(urlImage);
        }
        mController.setShareMedia(qqShareContent);

        // QQ空间
        QZoneShareContent qZoneShareContent = new QZoneShareContent();

        if (!StringUtil.isEmpty(shareContent)) {
            qZoneShareContent.setShareContent(shareContent);
        } else {
            qZoneShareContent.setShareContent(" ");
        }

        if (!StringUtil.isEmpty(title) && shareType != BaseHttpFragmentActivity.SHARE_TYPE_THEME) {
            qZoneShareContent.setTitle(title);
        } else {
            switch (shareType) {
                case BaseHttpFragmentActivity.SHARE_TYPE_ARTICLE:
                    qZoneShareContent.setTitle(getResources().getString(R.string.str_share_title_article));
                    break;
                case BaseHttpFragmentActivity.SHARE_TYPE_ACTIVITY:
                    qZoneShareContent.setTitle(getResources().getString(R.string.str_share_title_activity));
                    break;
                case BaseHttpFragmentActivity.SHARE_TYPE_TOPIC:
                    qZoneShareContent.setTitle(getResources().getString(R.string.str_share_title_topic));
                    break;
                case BaseHttpFragmentActivity.SHARE_TYPE_PUBLISH_ARTICLE:
                    qZoneShareContent.setTitle(getResources().getString(R.string.str_share_title_publish_article));
                    break;
                case BaseHttpFragmentActivity.SHARE_TYPE_THEME:
                    qZoneShareContent.setTitle(getResources().getString(R.string.str_share_title_theme_begin)
                            + title + getResources().getString(R.string.str_share_title_theme_end));
                    break;
                case BaseHttpFragmentActivity.SHARE_TYPE_REGISTER:
                    qZoneShareContent.setTitle(getResources().getString(R.string.str_share_invite_register));
                    break;
            }
        }

        qZoneShareContent.setTargetUrl(targetUrl);

        if (StringUtil.isEmpty(imageUrl)) {
            qZoneShareContent.setShareMedia(localImage);
        } else {
            qZoneShareContent.setShareMedia(urlImage);
        }
        mController.setShareMedia(qZoneShareContent);

    }
}
