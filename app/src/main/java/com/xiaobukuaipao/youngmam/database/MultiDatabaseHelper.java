package com.xiaobukuaipao.youngmam.database;

import android.content.Context;
import android.util.Log;

import com.xiaobukuaipao.youngmam.utils.StringUtil;

/**
 * Created by xiaobu1 on 15-5-27.
 * 多用户数据库帮助类
 */
public class MultiDatabaseHelper {
    private static final String TAG = MultiDatabaseHelper.class.getSimpleName();

    public static final String DATABASE_NAME_PREFIX = "youngmam_";
    public static final String DATABASE_NAME_SUFFIX = ".db";

    public static YoungDBHelper youngDBHelper = null;

    private static final MultiDatabaseHelper instance = new MultiDatabaseHelper();

    private MultiDatabaseHelper() {

    }

    public static MultiDatabaseHelper getInstance() {
        return instance;
    }

    /**
     * 创建或者打开数据库
     */
    public void createOrOpenDatabase(Context context, String uid) {
        if (!StringUtil.isEmpty(uid)) {
            // 此时,存在用户的uid
            StringBuilder sb = new StringBuilder();
            sb.append(DATABASE_NAME_PREFIX);
            sb.append(uid);
            sb.append(DATABASE_NAME_SUFFIX);

            if (youngDBHelper == null) {
                Log.d(TAG, "uid : " + uid );
                youngDBHelper = new YoungDBHelper(context, sb.toString());
            }

        }
    }

    /**
     * 当用户退出登录的时候,关闭数据库
     */
    public void closeDatabase() {
        if (youngDBHelper != null) {
            youngDBHelper.close();
            youngDBHelper = null;
        }
    }
}
