package com.xiaobukuaipao.youngmam.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.umeng.socialize.sso.UMQQSsoHandler;
import com.xiaobukuaipao.youngmam.SplashActivity;

/**
 * Created by xiaobu1 on 15-7-7.
 */
public class CommonUtil {

    public static boolean isLogin(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
        if (sp.getLong(SplashActivity.UID, 0) > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否安装了QQ
     */
    public static boolean isQQInstall(Activity activity) {
        UMQQSsoHandler umqqSsoHandler = new UMQQSsoHandler(activity, GlobalConstants.QQ_APPID, GlobalConstants.QQ_APPKEY);
        umqqSsoHandler.addToSocialSDK();

        return umqqSsoHandler.isClientInstalled();
    }

}
