package com.xiaobukuaipao.youngmam.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.xiaobukuaipao.youngmam.BannerH5Activity;
import com.xiaobukuaipao.youngmam.HotTopicActivity;
import com.xiaobukuaipao.youngmam.LatestActivity;
import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.SearchDetailActivity;
import com.xiaobukuaipao.youngmam.SpecialTopicActivity;
import com.xiaobukuaipao.youngmam.utils.CommonUtil;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaobu1 on 15-5-29.
 */
public class YoungShareBoard extends PopupWindow implements View.OnClickListener {

    private static final String TAG = YoungShareBoard.class.getSimpleName();

    public static final String PLATFORM_SINA = "sina";
    public static final String PLATFORM_WXSESSION = "wxsession";
    public static final String PLATFORM_WXTIMELINE = "wxtimeline";
    public static final String PLATFORM_QQ = "qq";
    public static final String PLATFORM_QZONE = "qzone";

    private UMSocialService mController = UMServiceFactory.getUMSocialService(GlobalConstants.UMENG_DESCRIPTOR);

    private Activity activity;

    private List<String> platformList;

    private String shareId;
    private int shareType;

    private OnShareSuccessListener onShareSuccessListener;

    public void setOnShareSuccessListener(OnShareSuccessListener onShareSuccessListener) {
        this.onShareSuccessListener = onShareSuccessListener;
    }

    public void setShareParams(String shareId, int shareType) {
        this.shareId = shareId;
        this.shareType = shareType;
    }

    public YoungShareBoard(Activity activity) {
        super(activity);
        this.activity = activity;

        initView(activity);
    }

    public YoungShareBoard(Activity activity, String sharePlatform) {
        super(activity);
        this.activity = activity;

        platformList = new ArrayList<String>();

        String[] platformArray = sharePlatform.split(";");

        for(int i=0; i < platformArray.length; i++) {
            platformList.add(platformArray[i]);
        }

        initView(activity);
    }

    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.share_board, null);
        if (platformList != null) {
            if (platformList.contains(PLATFORM_SINA)) {
                ((View) rootView.findViewById(R.id.weibo).getParent()).setVisibility(View.VISIBLE);
            } else {
                ((View) rootView.findViewById(R.id.weibo).getParent()).setVisibility(View.GONE);
            }

            if (platformList.contains(PLATFORM_WXSESSION)) {
                ((View) rootView.findViewById(R.id.wechat).getParent()).setVisibility(View.VISIBLE);
            } else {
                ((View) rootView.findViewById(R.id.wechat).getParent()).setVisibility(View.GONE);
            }

            if (platformList.contains(PLATFORM_WXTIMELINE)) {
                ((View) rootView.findViewById(R.id.wechat_circle).getParent()).setVisibility(View.VISIBLE);
            } else {
                ((View) rootView.findViewById(R.id.wechat_circle).getParent()).setVisibility(View.GONE);
            }

            if (platformList.contains(PLATFORM_QQ)) {
                ((View) rootView.findViewById(R.id.qq).getParent()).setVisibility(View.VISIBLE);
            } else {
                ((View) rootView.findViewById(R.id.qq).getParent()).setVisibility(View.GONE);
            }
        }

        rootView.findViewById(R.id.weibo).setOnClickListener(this);
        rootView.findViewById(R.id.wechat).setOnClickListener(this);
        rootView.findViewById(R.id.wechat_circle).setOnClickListener(this);
        rootView.findViewById(R.id.qq).setOnClickListener(this);
        // rootView.findViewById(R.id.qzone).setOnClickListener(this);

        setContentView(rootView);

        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(LayoutParams.WRAP_CONTENT);

        setFocusable(true);

        setAnimationStyle(R.style.ShareBoard);

        setBackgroundDrawable(new BitmapDrawable());
        setTouchable(true);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.weibo:
                performShare(SHARE_MEDIA.SINA);
                break;
            case R.id.wechat:
                performShare(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.wechat_circle:
                performShare(SHARE_MEDIA.WEIXIN_CIRCLE);
                break;
            case R.id.qq:
                if (CommonUtil.isQQInstall(activity)) {
                    Log.d(TAG, "QQ : " + mController.hasShareImage());
                    performShare(SHARE_MEDIA.QQ);
                } else {
                    Toast.makeText(activity, "没有安装QQ哦~", Toast.LENGTH_SHORT).show();
                }
                break;
            /*case R.id.qzone:
                performShare(SHARE_MEDIA.QZONE);
                break;*/
            default:
                break;
        }
    }

    private void performShare(final SHARE_MEDIA platform) {
        mController.postShare(activity, platform, new SocializeListeners.SnsPostListener() {
            @Override
            public void onStart() {
                Log.d(TAG, "开始分享");
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int code, SocializeEntity socializeEntity) {
                String showText = platform.toString();

                if (code == StatusCode.ST_CODE_SUCCESSED) {
                    showText += "分享成功";

                    if (onShareSuccessListener != null && !StringUtil.isEmpty(shareId)) {
                        onShareSuccessListener.onShareSuccess(shareId, shareType);
                    }

                    if (activity instanceof HotTopicActivity) {
                        MobclickAgent.onEvent(activity, "hotTopicShareSuccessed");
                    } else if (activity instanceof LatestActivity) {
                        MobclickAgent.onEvent(activity, "activeShareSuccessed");
                    } else if (activity instanceof SpecialTopicActivity) {
                        MobclickAgent.onEvent(activity, "specialTopicSuccessed");
                    } else if (activity instanceof SearchDetailActivity) {
                        MobclickAgent.onEvent(activity, "themeSuccessed");
                    } else if (activity instanceof BannerH5Activity) {
                        MobclickAgent.onEvent(activity, "bannerH5Successed");
                    } else {
                        MobclickAgent.onEvent(activity, "docShareSuccessed");
                    }
                } else {
                    showText += "分享失败";
                    if (activity instanceof HotTopicActivity) {
                        MobclickAgent.onEvent(activity, "hotTopicShareCancelled");
                    } else if (activity instanceof LatestActivity) {
                        MobclickAgent.onEvent(activity, "activeShareCancelled");
                    } else if (activity instanceof SpecialTopicActivity) {
                        MobclickAgent.onEvent(activity, "specialTopicCancelled");
                    } else if (activity instanceof SearchDetailActivity) {
                        MobclickAgent.onEvent(activity, "themeCancelled");
                    } else if (activity instanceof BannerH5Activity) {
                        MobclickAgent.onEvent(activity, "bannerH5Cancelled");
                    } else {
                        MobclickAgent.onEvent(activity, "docShareCancelled");
                    }
                }

                Toast.makeText(activity, showText, Toast.LENGTH_SHORT).show();

                dismiss();
            }
        });
    }

    /**
     * 分享成功
     */
    public interface OnShareSuccessListener {
        void onShareSuccess(String shareId, int shareType);
    }
}
