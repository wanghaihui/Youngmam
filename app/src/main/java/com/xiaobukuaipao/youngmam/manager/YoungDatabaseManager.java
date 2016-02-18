package com.xiaobukuaipao.youngmam.manager;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.xiaobukuaipao.youngmam.database.CookieTable;
import com.xiaobukuaipao.youngmam.provider.YoungContentProvider;

/**
 * Created by xiaobu1 on 15-5-27.
 */
public class YoungDatabaseManager {
    private static final String TAG = YoungDatabaseManager.class.getSimpleName();

    private Context context;

    private static final YoungDatabaseManager dbManager = new YoungDatabaseManager();

    private YoungDatabaseManager() {

    }

    public static YoungDatabaseManager getInstance() {
        return dbManager;
    }

    public void setContext(Context context) {
        if (context == null) {
            throw new RuntimeException("context is null");
        }

        this.context = context;
    }

    /**
     * 首先判断是否存在Cookie
     */
    public synchronized boolean isExistCookie() {
        Cursor cursor = context.getContentResolver().query(YoungContentProvider.COOKIE_CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        return false;
    }

    /**
     * 清除Cookie
     */
    public synchronized void clearCookie() {
        Cursor cursor = context.getContentResolver().query(YoungContentProvider.COOKIE_CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            context.getContentResolver().delete(YoungContentProvider.COOKIE_CONTENT_URI, null, null);
            cursor.close();
        }
    }

    /**
     * 读取此用户的login type
     */
    public synchronized String getLoginType() {
        String loginType;
        Cursor cursor = context.getContentResolver().query(YoungContentProvider.COOKIE_CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            loginType = cursor.getString(cursor.getColumnIndex(CookieTable.COLUMN_LOGIN_TYPE));
            return loginType;
        }

        Log.d(TAG, "cursor is null");
        return null;
    }

    /**
     *  查看是否有搜索记录
     */
    public synchronized boolean isExistSearchHistory() {
        Cursor cursor = context.getContentResolver().query(YoungContentProvider.SEARCH_CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        return false;
    }

    /**
     * 清空搜索记录
     */
    public synchronized void clearSearchDatabase() {
        context.getContentResolver().delete(YoungContentProvider.SEARCH_CONTENT_URI, null, null);
    }
}
