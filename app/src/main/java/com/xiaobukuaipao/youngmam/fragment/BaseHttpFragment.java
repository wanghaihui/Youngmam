package com.xiaobukuaipao.youngmam.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

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
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.http.IEventLogic;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;
import com.xiaobukuaipao.youngmam.wrap.BaseHttpFragmentActivity;

/**
 * Created by xiaobu1 on 15-5-15.
 */
public abstract  class BaseHttpFragment extends BaseFragment {

    private UiInterface uiInterface;

    /**
     * 积分框
     */
    private Dialog creditDialog;
    private TextView creditTextView;

    private Handler baseHandler = new Handler();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof BaseHttpFragmentActivity)) {
            throw new RuntimeException("Activity must implements interface UiInterface");
        }

        uiInterface = (UiInterface) activity;
    }

    /**
     * 解绑当前订阅者
     */
    protected void unregister(IEventLogic... logics) {
        for (IEventLogic logic : logics) {
            if (logic != null) {
                logic.cancelAll();
                logic.unregister(this);
            }
        }
    }

    /**
     * 解绑所有订阅者
     */
    protected void unregisterAll(IEventLogic... logics) {
        for (IEventLogic logic : logics) {
            if (logic != null) {
                logic.cancelAll();
                logic.unregisterAll();
            }
        }
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
        LayoutInflater inflater = LayoutInflater.from(this.getActivity());

        View v = inflater.inflate(R.layout.credit_dialog, null);

        creditTextView = (TextView) v.findViewById(R.id.credit_text);
        if (!TextUtils.isEmpty(msg)) {
            creditTextView.setText(msg);
        }
        Dialog loadingDialog = new Dialog(this.getActivity(), R.style.credit_dialog);
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

    /**
     * EventBus订阅者事件通知的函数,UI线程
     */
    public void onEventMainThread(Message msg) {
        if (isAdded() && !isDetached() && !isRemoving()) {
            onResponse(msg);
        }
    }

    protected abstract void onResponse(Message msg);


    /////////////////////////////////////////////////////////////////////////////////////////////

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
     * @param successTip 成功提示
     * @param errorTip 失败提示
     * @param tipError 是否提示错误信息
     * @return true 业务成功, false业务失败
     */
    protected boolean checkResponse(Message msg, String successTip, String errorTip, boolean tipError) {
        return ((BaseHttpFragmentActivity) uiInterface).checkResponse(msg, successTip, errorTip, tipError);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 分享
     */
    protected void configPlatforms(final UMSocialService mController) {
        // 添加新浪SSO授权
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        mController.getConfig().setSinaCallbackUrl(GlobalConstants.WEIBO_REDIRECT_URL);
        // 添加QQ平台
        UMQQSsoHandler umqqSsoHandler = new UMQQSsoHandler(this.getActivity(), GlobalConstants.QQ_APPID, GlobalConstants.QQ_APPKEY);
        umqqSsoHandler.setTargetUrl("http://www.youngmam.com/");
        umqqSsoHandler.addToSocialSDK();
        // 添加到QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this.getActivity(), GlobalConstants.QQ_APPID, GlobalConstants.QQ_APPKEY);
        qZoneSsoHandler.addToSocialSDK();
        // 添加到微信平台
        UMWXHandler umwxHandler = new UMWXHandler(this.getActivity(), GlobalConstants.WECHAT_APPID, GlobalConstants.WECHAT_APPSECRET);
        umwxHandler.addToSocialSDK();
        // 支持微信朋友圈
        UMWXHandler umwxCircleHandler = new UMWXHandler(this.getActivity(), GlobalConstants.WECHAT_APPID, GlobalConstants.WECHAT_APPSECRET);
        umwxCircleHandler.setToCircle(true);
        umwxCircleHandler.addToSocialSDK();
    }

    // 设置分享内容
    protected void setShareContent(final UMSocialService mController, String targetUrl, String shareContent, String imageUrl, int shareType) {
        UMImage localImage = new UMImage(this.getActivity(), R.mipmap.share_huayoung);
        UMImage urlImage = new UMImage(this.getActivity(), imageUrl);

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
        }

        qZoneShareContent.setTargetUrl(targetUrl);

        if (StringUtil.isEmpty(imageUrl)) {
            qZoneShareContent.setShareMedia(localImage);
        } else {
            qZoneShareContent.setShareMedia(urlImage);
        }
        mController.setShareMedia(qZoneShareContent);
    }

    protected boolean checkTavailable(String datas) {
        final char[] strChar = datas.substring(0, 1).toCharArray();
        final char firstChar = strChar[0];

        // 此时是JSONObject
        if (firstChar == '{') {
            JSONObject jsonObject = JSONObject.parseObject(datas);

            if (jsonObject != null && jsonObject.getInteger(GlobalConstants.JSON_STATUS) != null) {

                int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);

                if (status == 100) {
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}
