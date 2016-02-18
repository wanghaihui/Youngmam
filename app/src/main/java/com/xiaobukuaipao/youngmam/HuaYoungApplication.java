package com.xiaobukuaipao.youngmam;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.DisplayMetrics;
import android.util.Log;

import com.baidu.frontia.FrontiaApplication;
import com.xiaobukuaipao.youngmam.database.CookieTable;
import com.xiaobukuaipao.youngmam.font.TypefaceManager;
import com.xiaobukuaipao.youngmam.manager.YoungDatabaseManager;
import com.xiaobukuaipao.youngmam.notification.YoungNotificationManager;
import com.xiaobukuaipao.youngmam.provider.YoungContentProvider;
import com.xiaobukuaipao.youngmam.utils.GlobalConstants;

import java.util.Map;

/**
 * Created by xiaobu1 on 15-4-22.
 */
public class HuaYoungApplication extends FrontiaApplication {
    private static final String TAG = HuaYoungActivity.class.getSimpleName();

    private static HuaYoungApplication instance;

    // 全局变量--Cookie的T票
    public static String mCookie_T = null;

    // 异常捕获
    private boolean isNeedCaughtExeption = true;
    private HuaYoungUncaughtExceptionHandler uncaughtExceptionHandler;
    private String packgeName;
    private PendingIntent restartIntent;

    // 显示矩阵
    private DisplayMetrics displayMetrics = null;

    public HuaYoungApplication() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化
        init();
    }

    public void init() {
        instance = this;

        Context context = getApplicationContext();

        // 初始化字体--已经初始化
        TypefaceManager.initialize(this, R.xml.fonts);

        // Notification
        YoungNotificationManager.getInstance().setContext(context);
        // 数据库管理器
        YoungDatabaseManager.getInstance().setContext(context);

        packgeName = getPackageName();

        if (isNeedCaughtExeption) {
            // cauchException();
        }
    }

    public static HuaYoungApplication getInstance() {
        if (instance != null && instance instanceof HuaYoungApplication) {
            return (HuaYoungApplication) instance;
        } else {
            instance = new HuaYoungApplication();
            instance.onCreate();
            return (HuaYoungApplication) instance;
        }
    }

    /**
     * Adds session cookie to headers if exists.
     *
     * @param headers
     */
    public final void addCookie(Map<String, String> headers) {

        String[] projection = { CookieTable.COLUMN_UID, CookieTable.COLUMN_P, CookieTable.COLUMN_LOGIN_TYPE };

        Cursor cursor = getApplicationContext().getContentResolver().query(YoungContentProvider.COOKIE_CONTENT_URI, projection, null,
                null, null);
        if (cursor != null && cursor.moveToFirst()) {

            Long uid = (long) cursor.getInt(cursor.getColumnIndex(CookieTable.COLUMN_UID));
            String p = cursor.getString(cursor.getColumnIndex(CookieTable.COLUMN_P));
            String loginType = cursor.getString(cursor.getColumnIndex(CookieTable.COLUMN_LOGIN_TYPE));
            StringBuilder builder = new StringBuilder();

            builder.append("uid=");
            builder.append(uid);
            builder.append("\n");

            builder.append("p=");
            builder.append(p);
            builder.append("\n");

            if (mCookie_T != null) {
                builder.append("t=");
                builder.append(mCookie_T);
                builder.append("\n");
            }

            builder.append("login_type=");
            builder.append(loginType);

            Log.d(TAG, "cookie : " + builder.toString());
            headers.put(GlobalConstants.COOKIE_KEY, builder.toString());

            // always close the cursor
            cursor.close();
        }

    }

    /**
     * 获得Cookie
     */
    public String getCookieValue() {
        String[] projection = { CookieTable.COLUMN_UID, CookieTable.COLUMN_P, CookieTable.COLUMN_LOGIN_TYPE };

        Cursor cursor = getApplicationContext().getContentResolver().query(YoungContentProvider.COOKIE_CONTENT_URI, projection, null,
                null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Long uid = (long) cursor.getInt(cursor.getColumnIndex(CookieTable.COLUMN_UID));
            StringBuilder builder = new StringBuilder();

            builder.append("uid=");
            builder.append(uid);
            builder.append(";");

            // always close the cursor
            cursor.close();

            return builder.toString();
        }

        return null;
    }

    private void cauchException() {
        Intent intent = new Intent();
        // 参数1：包名
        // 参数2：程序入口的activity
        intent.setClassName(packgeName, packgeName + ".SplashActivity");
        restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);

        // 程序崩溃时触发线程
        uncaughtExceptionHandler = new HuaYoungUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
    }

    // 创建服务用于捕获崩溃异常
    private class HuaYoungUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {

            // 1秒钟后重启应用
            AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);

            // 关闭当前应用
            AppActivityManager.getInstance().popAllActivity();
            finishProcess();
        }
    }

    public void finishProcess() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public float getScreenDensity() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(getResources().getDisplayMetrics());
        }
        return this.displayMetrics.density;
    }

    public int getScreenHeight() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(getResources().getDisplayMetrics());
        }
        return this.displayMetrics.heightPixels;
    }

    public int getScreenWidth() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(getResources().getDisplayMetrics());
        }
        return this.displayMetrics.widthPixels;
    }

    public void setDisplayMetrics(DisplayMetrics DisplayMetrics) {
        this.displayMetrics = DisplayMetrics;
    }

    public int dp2px(float f)
    {
        return (int)(0.5F + f * getScreenDensity());
    }

    public int px2dp(float pxValue) {
        return (int) (pxValue / getScreenDensity() + 0.5f);
    }

    // 获取应用的data/data/....File目录
    public String getFilesDirPath() {
        return getFilesDir().getAbsolutePath();
    }

    // 获取应用的data/data/....Cache目录
    public String getCacheDirPath() {
        return getCacheDir().getAbsolutePath();
    }
}
