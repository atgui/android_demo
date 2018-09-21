package com.xcore.cache;

import android.content.Context;
import android.content.SharedPreferences;

import com.xcore.MainApplicationContext;

/**
 * 本地缓存
 */
public class LocalHandler {
    private Context context= MainApplicationContext.context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    public LocalHandler(){
        sharedPreferences=context.getSharedPreferences("local_database",Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }

    /**
     * 保存数据
     * @param key
     * @param value
     */
    public void put(String key,String value){
        editor.putString(key,value);
        editor.commit();
    }

    /**
     * 取出数据
     * @param key
     * @return
     */
    public String get(String key){
        return sharedPreferences.getString(key,"");
    }

    /**
     * 删除数据
     * @param key
     */
    public void remove(String key){
        editor.remove(key);
        editor.commit();
    }

}
