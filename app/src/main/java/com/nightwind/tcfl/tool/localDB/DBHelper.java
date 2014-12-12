package com.nightwind.tcfl.tool.localDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wind on 2014/12/12.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tcfl.db";
    private static final String TABLE_NAME_USER = "user";
    private static final String CREATE_TABLE_USER = "CREATE TABLE \"user\" ( \"uid\" INTEGER(11) NOT NULL, \"username\" TEXT(255) NOT NULL, \"password\" TEXT(255) NOT NULL, \"salt\" TEXT(36) NOT NULL, \"level\" INTEGER(11) NOT NULL, \"email\" TEXT(255), \"tel\" TEXT(255), \"age\" INTEGER(11), \"sex\" INTEGER(1), \"work\" TEXT(255), \"info\" TEXT(255), \"edu\" INTEGER(255), \"school\" TEXT(255), PRIMARY KEY (\"uid\") );";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        //CursorFactory设置为null,使用默认值
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS person" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, age INTEGER, info TEXT)");
        db.execSQL(CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
    }

}


