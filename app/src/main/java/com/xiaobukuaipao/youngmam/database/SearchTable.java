package com.xiaobukuaipao.youngmam.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by xiaobu1 on 15-5-26.
 */
public class SearchTable {

    // Database table
    public static final String TABLE_SEARCH = "search";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_WORD = "word";

    // 数据库更新时，表名后缀
    public static final String TEMP_SUFFIX = "_temp_suffix";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_SEARCH
            + "("
            + COLUMN_ID + " integer not null primary key autoincrement, "
            + COLUMN_WORD + " text"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // database.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCH);
        // onCreate(database);
        updateTable(database);
    }

    private static void updateTable(SQLiteDatabase database) {
        // 此时进行数据库的更新和升级
        database.beginTransaction();
        try {

            // rename the table
            String alterMessageTableSql = getAlterTableSql(TABLE_SEARCH);
            database.execSQL(alterMessageTableSql);

            // creat table
            // 升级时改成新表名
            database.execSQL(DATABASE_CREATE);
            // 记得用旧表名
            String selectColumns = getColumnNames(database, TABLE_SEARCH + TEMP_SUFFIX);

            if (selectColumns.endsWith(",")) {
                selectColumns = selectColumns.substring(0, selectColumns.length() - 1);
            }

            String updateMessagesSql = "insert into " + TABLE_SEARCH + " ("
                    + selectColumns + ") "
                    + "select " + selectColumns + "" + " " + " from " + TABLE_SEARCH
                    + TEMP_SUFFIX;
            database.execSQL(updateMessagesSql);


            // drop the oldtable
            String dropMessageTableSql = getDropTableSql(TABLE_SEARCH + TEMP_SUFFIX);
            database.execSQL(dropMessageTableSql);

            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    private static String getAlterTableSql(String oldTableName) {
        // ALTER TABLE来重新命名现有的表的基本语法如下：ALTER TABLE database_name.table_name RENAME TO new_table_name;
        return "alter table " + oldTableName + " rename to " + oldTableName + TEMP_SUFFIX;
    }

    private static String getDropTableSql(String tableName) {
        return "drop table if exists " + tableName;
    }

    // 获取升级前表中的字段
    private static String getColumnNames(SQLiteDatabase db, String tableName)
    {
        StringBuffer sbSelect = null;
        Cursor c = null;
        try {
            c = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);

            if (null != c) {
                int columnIndex = c.getColumnIndex("name");
                if (-1 == columnIndex) {
                    return null;
                }

                int index = 0;
                // 并标记最后一个不加逗号，
                // 由于index从0开始，所有这里不用加2,只加1
                int pos = c.getCount() + 1;
                // 字段总列数，增加2列需要加2
                sbSelect = new StringBuffer(c.getCount() + 2);
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    sbSelect.append(c.getString(columnIndex));
                    index++;
                    if (index < pos) {
                        sbSelect.append(",");
                    }
                }
            }
        } catch (Exception e) {

        } finally {
            if (null != c) {
                c.close();
            }
        }

        return sbSelect.toString();
    }

}
