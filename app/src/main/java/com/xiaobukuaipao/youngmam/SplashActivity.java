package com.xiaobukuaipao.youngmam;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;
import com.xiaobukuaipao.youngmam.cache.YoungCache;
import com.xiaobukuaipao.youngmam.database.CookieTable;
import com.xiaobukuaipao.youngmam.database.MultiDatabaseHelper;
import com.xiaobukuaipao.youngmam.http.HttpResult;
import com.xiaobukuaipao.youngmam.http.IEventLogic;
import com.xiaobukuaipao.youngmam.http.YoungEventLogic;
import com.xiaobukuaipao.youngmam.manager.YoungDatabaseManager;
import com.xiaobukuaipao.youngmam.provider.YoungContentProvider;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;
import com.xiaobukuaipao.youngmam.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiaobu1 on 15-5-27.
 * 多用户系统,普通的Activity被回收后都要走Splash重新登录,小登录更新T票
 */
public class SplashActivity extends FragmentActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();

    public static final String YOUNGMAM_UID = "youngmam_uid";
    public static final String UID = "uid";

    // 最小显示的时间
    private static final int SHOW_MIN_TIME = 1000;
    // 开始时间
    private long mStartTime;

    // Activity是否已销毁
    private boolean isDestroyed;
    /**
     * 网络逻辑
     */
    protected YoungEventLogic mEventLogic;

    private Handler mHandler = new Handler();

    /** 基类Toast */
    private Toast mToast;

    protected boolean isPaused;

    private Runnable registerRunnable = new Runnable() {
        @Override
        public void run() {
            SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, MODE_PRIVATE);
            if (sp.getBoolean(GuideActivity.LOGIN_FIRST, false)) {
                Intent intent = new Intent(SplashActivity.this, HuaYoungActivity.class);
                startActivity(intent);
                finish();
            } else {
                // 跳到登录页
                Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
                startActivity(intent);
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        // getWindow().getDecorView().findViewById(android.R.id.content).setBackgroundResource(R.mipmap.splash);

        mStartTime = System.currentTimeMillis();

        mEventLogic = new YoungEventLogic(this);

        // 处理Push Notification

        // uid会单独保存在一个SharedPreferences中
        SharedPreferences sp = getSharedPreferences(YOUNGMAM_UID, MODE_PRIVATE);
        if (sp.getLong(UID, 0) > 0) {
            MultiDatabaseHelper.getInstance().createOrOpenDatabase(getApplicationContext(), String.valueOf(sp.getLong(UID, 0)));

            // 首先判断是否存在Cookie
            if (YoungDatabaseManager.getInstance().isExistCookie()) {
                // 此时,进行小登录
                mEventLogic.tlogin();
            } else {
                // 此时,不存在Cookie
                long loadingTime = System.currentTimeMillis() - mStartTime;
                if (loadingTime < SHOW_MIN_TIME) {
                    mHandler.postDelayed(registerRunnable, SHOW_MIN_TIME - loadingTime);
                } else {
                    mHandler.post(registerRunnable);
                }
            }

        } else {
            Log.d(TAG, "uid不存在");
            // 此时,不存在Cookie
            long loadingTime = System.currentTimeMillis() - mStartTime;
            if (loadingTime < SHOW_MIN_TIME) {
                mHandler.postDelayed(registerRunnable, SHOW_MIN_TIME - loadingTime);
            } else {
                mHandler.post(registerRunnable);
            }
        }

    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        isPaused = true;
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

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

        // 这个地方要进行网络请求返回失败后的情况

        if (!isDestroyed && !isFinishing()) {

            switch (msg.what) {
                case R.id.tlogin:

                    if (checkResponse(msg)) {
                        HttpResult httpResult = (HttpResult) msg.obj;

                        JSONObject jsonObject = JSONObject.parseObject(httpResult.getData());
                        int status = jsonObject.getInteger(GlobalConstants.JSON_STATUS);
                        String message = jsonObject.getString(GlobalConstants.JSON_MSG);

                        if (status == 0) {
                            // 此时说明登录成功,这时候会下发Cookie
                            checkCookie(httpResult.getHeaders());
                            // 此时,Cookie存入数据库,进入下一个页面
                            Intent intent = new Intent(SplashActivity.this, HuaYoungActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (status == 12) {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        } else if (status == 13) {
                            // P票校验失败--此时重新登录
                            // 踢出系统,重新登录,执行登出操作
                            logout();
                            /*Intent intent = new Intent(SplashActivity.this, RegisterAndLoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();*/
                        }
                    } else {
                        Intent intent = new Intent(SplashActivity.this, HuaYoungActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    break;

                case R.id.passport_logout:
                    // 清除上个用户的我的发布,我的喜欢,我的评论,我的通知缓存
                    YoungCache.get(this).remove(YoungCache.CACHE_MINE_PUBLISH);
                    YoungCache.get(this).remove(YoungCache.CACHE_MINE_LIKE);
                    YoungCache.get(this).remove(YoungCache.CACHE_MINE_COMMENT);
                    YoungCache.get(this).remove(YoungCache.CACHE_MINE_NOTIFY);

                    // 清除Cookie
                    YoungDatabaseManager.getInstance().clearCookie();

                    // 关闭所有数据库
                    MultiDatabaseHelper.getInstance().closeDatabase();

                    // 清除uid
                    SharedPreferences sp = getSharedPreferences(SplashActivity.YOUNGMAM_UID, Context.MODE_PRIVATE);
                    sp.edit().putLong(SplashActivity.UID, Long.valueOf(0)).commit();
                    sp.edit().putBoolean(SettingActivity.PUSH_STATE, true).commit();
                    sp.edit().putString(RegisterAndLoginActivity.MOBILE, "").commit();

                    // 关闭所有Activity
                    // AppActivityManager.getInstance().popAllActivity();

                    // 跳到登录页
                    Intent intent = new Intent(SplashActivity.this, HuaYoungActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(GlobalConstants.JSON_PAGE, 3);
                    startActivity(intent);

                    Toast.makeText(this, "您长时间未登录或者在别处登录,请重新登录...", Toast.LENGTH_SHORT).show();

                    finish();

                    break;
                default:
                    Intent intentDefault = new Intent(SplashActivity.this, HuaYoungActivity.class);
                    startActivity(intentDefault);
                    finish();
                    break;
            }
        }

    }

    /**
     * 退出登录
     */
    private void logout() {
        mEventLogic.logout();
    }


    /**
     * 检测Cookie
     * @param headers
     */
    private void checkCookie(Map<String, String> headers) {

        if (headers.containsKey(GlobalConstants.SET_COOKIE_KEY)) {

            String cookie = headers.get(GlobalConstants.SET_COOKIE_KEY);

            Map<String, String> customHeaders = new HashMap<String, String>();

            String[] split = cookie.split("\n");

            // 四行--四个Cookie行--解析Cookie
            for (String sc : split) {
                Log.d(TAG, " sc : " + sc);

                if (cookie.length() > 0) {
                    String[] splitCookie = sc.split(";");
                    for (int i = 0; i < splitCookie.length; i++) {
                        String[] entry = splitCookie[i].split("=");
                        if (entry.length == 1) {
                            // 当只有key时，value为null
                            customHeaders.put(entry[0], null);
                        } else {
                            customHeaders.put(entry[0], entry[1]);
                        }
                    }
                }
            }

            HuaYoungApplication.mCookie_T = customHeaders.get(GlobalConstants.COOKIE_T);

            // 将Cookie插入到数据库中
            insertToDatabase(Long.valueOf(customHeaders
                            .get(GlobalConstants.COOKIE_UID)), customHeaders.get(GlobalConstants.COOKIE_P),
                    customHeaders.get(GlobalConstants.COOKIE_LOGIN_TYPE));
        }
    }

    public synchronized void insertToDatabase(Long uid, String p,
                                              String login_type) {

        ContentValues values = new ContentValues();
        values.put(CookieTable.COLUMN_UID, uid);
        values.put(CookieTable.COLUMN_P, p);
        if (!StringUtil.isEmpty(login_type) && !login_type.equals("null")) {

            values.put(CookieTable.COLUMN_LOGIN_TYPE, login_type);
        } else {
            Log.d(TAG, "login type is null");
            if (!StringUtil.isEmpty(login_type)) {
                Log.d(TAG, "login type : " + login_type);
            }
        }

        // 此时先清空一下数据库，防止数据库已经存在Cookie
        /*Cursor cursor = getContentResolver().query(YoungContentProvider.COOKIE_CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            getContentResolver().delete(YoungContentProvider.COOKIE_CONTENT_URI, null, null);
            cursor.close();
        }*/

        // 插入数据库
        getContentResolver().update(YoungContentProvider.COOKIE_CONTENT_URI, values, null, null);

    }


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

}
