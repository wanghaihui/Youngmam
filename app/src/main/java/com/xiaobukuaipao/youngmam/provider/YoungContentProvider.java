package com.xiaobukuaipao.youngmam.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.greenrobot.event.EventBus;
import com.xiaobukuaipao.youngmam.database.CookieTable;
import com.xiaobukuaipao.youngmam.database.LabelTable;
import com.xiaobukuaipao.youngmam.database.MamaTable;
import com.xiaobukuaipao.youngmam.database.MultiDatabaseHelper;
import com.xiaobukuaipao.youngmam.database.SearchTable;
import com.xiaobukuaipao.youngmam.domain.DbHelperEvent;

/**
 * Created by xiaobu1 on 15-5-12.
 */
public class YoungContentProvider extends ContentProvider {

    // Used for the UriMatcher
    private static final int COOKIE = 0x10;
    private static final int LABEL = 0x20;
    private static final int MAMA = 0x30;
    private static final int SEARCH = 0x40;

    private static final String AUTHORITY = "com.xiaobukuaipao.youngmam.provider";

    private static final String COOKIE_PATH = "cookie";
    public static final Uri COOKIE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + COOKIE_PATH);

    private static final String LABEL_PATH = "label";
    public static final Uri LABEL_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + LABEL_PATH);

    private static final String MAMA_PATH = "mama";
    public static final Uri MAMA_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + MAMA_PATH);

    private static final String SEARCH_PATH = "search";
    public static final Uri SEARCH_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + SEARCH_PATH);

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, COOKIE_PATH, COOKIE);
        sURIMatcher.addURI(AUTHORITY, LABEL_PATH, LABEL);
        sURIMatcher.addURI(AUTHORITY, MAMA_PATH, MAMA);
        sURIMatcher.addURI(AUTHORITY, SEARCH_PATH, SEARCH);
    }

    // ContentProvider必须实现的方法
    @Override
    public boolean onCreate() {
        // 最先执行
        return false;
    }

    // 查询语句
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // Using SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case COOKIE:
                // Check if the caller has requested a column which does not exists
                queryBuilder.setTables(CookieTable.TABLE_COOKIE);
                break;
            case LABEL:
                queryBuilder.setTables(LabelTable.TABLE_LABEL);
                break;
            case MAMA:
                queryBuilder.setTables(MamaTable.TABLE_MAMA);
                break;
            case SEARCH:
                queryBuilder.setTables(SearchTable.TABLE_SEARCH);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (MultiDatabaseHelper.youngDBHelper != null) {
            SQLiteDatabase db = MultiDatabaseHelper.youngDBHelper.getWritableDatabase();
            Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            // Make sure that potential listeners are getting notified
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            return cursor;
        } else {
            EventBus.getDefault().post(new DbHelperEvent(true));
            return null;
        }
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    // 插入语句
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);

        if (MultiDatabaseHelper.youngDBHelper != null) {
            SQLiteDatabase sqlDB = MultiDatabaseHelper.youngDBHelper.getWritableDatabase();
            long id = 0;

            switch (uriType) {
                case COOKIE:
                    id = sqlDB.insert(CookieTable.TABLE_COOKIE, null, values);
                    getContext().getContentResolver().notifyChange(uri, null);
                    return Uri.parse(COOKIE_PATH + "/" + id);
                case LABEL:
                    id = sqlDB.insert(LabelTable.TABLE_LABEL, null, values);
                    getContext().getContentResolver().notifyChange(uri, null);
                    return Uri.parse(LABEL_PATH + "/" + id);
                case MAMA:
                    id = sqlDB.insert(MamaTable.TABLE_MAMA, null, values);
                    getContext().getContentResolver().notifyChange(uri, null);
                    return Uri.parse(MAMA_PATH + "/" + id);
                case SEARCH:
                    id = sqlDB.insert(SearchTable.TABLE_SEARCH, null, values);
                    getContext().getContentResolver().notifyChange(uri, null);
                    return Uri.parse(SEARCH_PATH + "/" + id);
                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        } else {
            EventBus.getDefault().post(new DbHelperEvent(true));
            return null;
        }
    }

    // 删除语句
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);

        int rowsDeleted = 0;
        if (MultiDatabaseHelper.youngDBHelper != null) {
            SQLiteDatabase sqlDB = MultiDatabaseHelper.youngDBHelper.getWritableDatabase();

            switch (uriType) {
                case COOKIE:
                    rowsDeleted = sqlDB.delete(CookieTable.TABLE_COOKIE, selection, selectionArgs);
                    break;
                case LABEL:
                    rowsDeleted = sqlDB.delete(LabelTable.TABLE_LABEL, selection, selectionArgs);
                    break;
                case MAMA:
                    rowsDeleted = sqlDB.delete(MamaTable.TABLE_MAMA, selection, selectionArgs);
                    break;
                case SEARCH:
                    rowsDeleted = sqlDB.delete(SearchTable.TABLE_SEARCH, selection, selectionArgs);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        } else {
            EventBus.getDefault().post(new DbHelperEvent(true));
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    // 更新语句
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);

        int rowsUpdated = 0;

        if (MultiDatabaseHelper.youngDBHelper != null) {
            SQLiteDatabase sqlDB = MultiDatabaseHelper.youngDBHelper.getWritableDatabase();

            switch (uriType) {
                case COOKIE:
                    rowsUpdated = sqlDB.update(CookieTable.TABLE_COOKIE, values, selection, selectionArgs);
                    break;
                case LABEL:
                    rowsUpdated = sqlDB.update(LabelTable.TABLE_LABEL, values, selection, selectionArgs);
                    break;
                case MAMA:
                    rowsUpdated = sqlDB.update(MamaTable.TABLE_MAMA, values, selection, selectionArgs);
                    break;
                case SEARCH:
                    rowsUpdated = sqlDB.update(SearchTable.TABLE_SEARCH, values, selection, selectionArgs);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        } else {
            EventBus.getDefault().post(new DbHelperEvent(true));
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
