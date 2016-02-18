package com.xiaobukuaipao.youngmam.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by xiaobu1 on 15-5-12.
 */
public class YoungDBHelper extends SQLiteOpenHelper {

    // 数据库版本号
    private static final int DATABASE_VERSION = 3;

    public YoungDBHelper(Context context, String dName) {
        super(context, dName, null, DATABASE_VERSION);
    }

    /**
     * 构造函数
     * @param context 上下文
     * @param name 数据库名字
     * @param factory 可选的数据库游标工厂类，当查询(query)被提交时，该对象会被调用来实例化一个游标
     * @param version 数据库版本
     */
    public YoungDBHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        CookieTable.onCreate(database);
        LabelTable.onCreate(database);
        MamaTable.onCreate(database);
        SearchTable.onCreate(database);
    }

    // Method is called during an upgrade of the database
    // e.g. if you increase the database version
    // 数据库的版本是一个整型值，在创建SQLiteOpenHelper时，会传入该数据库的版本
    // 如果传入的数据库版本号比数据库文件中存储的版本号大的话
    // 那么SQLiteOpenHelper#onUpgrade()方法就会被调用，我们的升级应该在该方法中完成
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        CookieTable.onUpgrade(database, oldVersion, newVersion);
        LabelTable.onUpgrade(database, oldVersion, newVersion);
        MamaTable.onUpgrade(database, oldVersion, newVersion);
        SearchTable.onUpgrade(database, oldVersion, newVersion);
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
