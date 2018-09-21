package com.xcore.cache;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.xcore.MainApplicationContext;

import java.io.File;
import java.io.IOException;

public class DBManager {
    private static DBManager manager;
    private MySQLiteOpenHelper mySQLiteOpenHelper;
    private SQLiteDatabase db;

    /**
     * 私有化构造器
     */
    private DBManager() {
        //创建数据库
        mySQLiteOpenHelper = MySQLiteOpenHelper.getInstance(MainApplicationContext.context);
        if (db == null) {
            try {
                db = mySQLiteOpenHelper.getWritableDatabase();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 单例DbManager类
     *
     * @return 返回DbManager对象
     */
    public static DBManager newInstances() {
        if (manager == null) {
            manager = new DBManager();
        }
        return manager;
    }

    /**
     * 获取数据库的对象
     *
     * @return 返回SQLiteDatabase数据库的对象
     */
    public SQLiteDatabase getDataBase() {
        return db;
    }
}
