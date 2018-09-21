package com.xcore.cache;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

public class CustomPathDatabaseContext extends ContextWrapper {
    private String mDirPath;

    public CustomPathDatabaseContext(Context base, String dirPath) {
        super(base);
        this.mDirPath = dirPath;
    }

    @Override
    public File getDatabasePath(String name)
    {
        File result = new File(mDirPath + File.separator + name);

        if (!result.getParentFile().exists())
        {
            result.getParentFile().mkdirs();
        }

        return result;
    }

    /**
     * android2.x 调用
     * @param name
     * @param mode
     * @param factory
     * @return
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory)
    {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory);
    }

    /**
     * android4.x 调用
     * @param name
     * @param mode
     * @param factory
     * @param errorHandler
     * @return
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler){
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name).getAbsolutePath(), factory, errorHandler);
    }


}
