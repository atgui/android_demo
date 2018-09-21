package com.xcore.cache;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.xcore.MainApplicationContext;

import java.io.File;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    private static  MySQLiteOpenHelper helper;

    //构造器,传入四个参数Context对象，数据库名字name，操作数据库的Cursor对象，版本号version。
    private MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(new CustomPathDatabaseContext(context, getDirPath()), name, factory, version);
    }

    /**
     * 获取db文件在sd卡的路径
     * @return
     */
    private static String getDirPath(){
        String path=MainApplicationContext.DATABASE_PATH;
        File file=new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
        return path;
    }

    //为了简化构造器的使用，我们自定义一个构造器
    private MySQLiteOpenHelper(Context context, String name) {
        this(context, name, null, 12);//传入Context和数据库的名称，调用上面那个构造器
    }
    //将自定义的数据库创建类单例。
    public static synchronized  MySQLiteOpenHelper getInstance(Context context) {
        if(helper==null){
            helper = new MySQLiteOpenHelper(context, "xcore_db.db");//数据库名称为create_db。
        }
        return  helper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //在创建数据库时，初始化创建数据库中包含的数据库表。
        //缓存记录表、观看记录、播放记录
        String cSql="create table if not exists "
                +TableConfig.Cache.TABLE_CACHE+"("
                +TableConfig.Cache.CACHE_ID+" integer not null primary key autoincrement,"
                +TableConfig.Cache.CACHE_SHORT_ID+ " verchar(255),"
                +TableConfig.Cache.CACHE_TITLE+ " verchar(255),"
                +TableConfig.Cache.CACHE_COVER+ " verchar(255),"
                +TableConfig.Cache.CACHE_TIME+ " verchar(255) ,"
                +TableConfig.Cache.CACHE_YEAR+ " verchar(255),"
                +TableConfig.Cache.CACHE_DESC+ " verchar(255), "
                +TableConfig.Cache.CACHE_ACTOR+ " verchar(255),"
                +TableConfig.Cache.CACHE_P_STAR+ " verchar(255),"
                +TableConfig.Cache.CACHE_UPDATE_TIME+" verchar(255),"
                +TableConfig.Cache.CACHE_TAGS+" verchar(255),"
                +TableConfig.Cache.CACHE_ACTORS+" verchar(255),"
                +TableConfig.Cache.CACHE_TYPE+" verchar(255),"
                +TableConfig.Cache.CACHE_PLAY_COUNT+" verchar(255),"
                +TableConfig.Cache.CACHE_DELETE+" verchar(10),"
                +TableConfig.Cache.CACHE_PLAY_POSITION+" verchar(255)"
                +")";
        sqLiteDatabase.execSQL(cSql);

        String dSql="create table if not exists "
                +TableConfig.Dictionary.TABLE_DIC+"("
                +TableConfig.Dictionary.DIC_ID+" integer not null primary key autoincrement,"
                +TableConfig.Dictionary.DIC_NAME+ " verchar(255),"
                +TableConfig.Dictionary.DIC_UPDATE_TIME+ " verchar(255),"
                +TableConfig.Dictionary.DIC_DELETE+ " verchar(10)"
                +")";
        sqLiteDatabase.execSQL(dSql);

        String downSql="create table if not exists "
                +TableConfig.DOWN.TABLE_DOWN+"("
                +TableConfig.DOWN.DOWN_ID+" integer not null primary key autoincrement,"
                +TableConfig.DOWN.DOWN_STREAM_ID+ " verchar(255),"
                +TableConfig.DOWN.DOWN_NAME+ " verchar(255),"
                +TableConfig.DOWN.DOWN_URL+ " verchar(255),"
                +TableConfig.DOWN.DOWN_CONVER+ " verchar(255),"
                +TableConfig.DOWN.DOWN_UPDATE_TIME+ " verchar(255),"
                +TableConfig.DOWN.DOWN_TOTAL_SIZE+ " verchar(255),"
                +TableConfig.DOWN.DOWN_PERCENT+ " verchar(255),"
                +TableConfig.DOWN.DOWN_DOWNSIZE+ " verchar(255),"
                +TableConfig.DOWN.DOWN_SHORT_ID+ " verchar(255),"
                +TableConfig.DOWN.DOWN_DELETE+ " verchar(10)"
                +")";
        sqLiteDatabase.execSQL(downSql);
        Log.e("TAG","创建表");
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //用于升级数据库，只需要在创建本类对象时传入一个比之前创建传入的version大的数即可。

        String DROP_TABLE_CACHE = "DROP TABLE IF EXISTS "+TableConfig.Cache.TABLE_CACHE;
        sqLiteDatabase.execSQL(DROP_TABLE_CACHE);

        String DROP_TABLE_DIC = "DROP TABLE IF EXISTS "+TableConfig.Dictionary.TABLE_DIC;
        sqLiteDatabase.execSQL(DROP_TABLE_DIC);

        String DROP_TABLE_DOWN = "DROP TABLE IF EXISTS "+TableConfig.DOWN.TABLE_DOWN;
        sqLiteDatabase.execSQL(DROP_TABLE_DOWN);
        Log.e("TAG","删除表 。");

        onCreate(sqLiteDatabase);
    }
}
