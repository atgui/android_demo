package com.xcore.cache;
import android.util.Log;

import com.common.BaseCommon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.dolit.p2ptrans.P2PTrans;


/**
 * 缓存管理
 */
public class CacheManager {
    public static String PLAY_IS_CACHE="play_is_cache";//播放中的时候是否缓存
    public static String IDLE_IS_CACHE="idle_is_cache";//空闲的时候是否缓存


    private CacheManager(){}
    private static CacheManager instance;
    public static CacheManager getInstance(){
        if(instance==null){
            instance=new CacheManager();
        }
        return instance;
    }
    private LocalHandler localHandler;
    private DBHandler dbHandler;
    private DownHandler downHandler;

    public DBHandler getDbHandler() {
        return dbHandler;
    }

    public LocalHandler getLocalHandler() {
        return localHandler;
    }

    public DownHandler getDownHandler(){
        return this.downHandler;
    }

    /**
     * 初始化缓存数据
     */
    public void init(){
        //初始化缓存数据
        localHandler=new LocalHandler();
        dbHandler=new DBHandler();
        downHandler=new DownHandler(dbHandler);
    }

    public void initDown(){
        this.downHandler.initDown();
    }

    /**
     * 下载
     * @param downModel
     */
    public void downByUrl(DownModel downModel){
        this.downHandler.downByUrl(downModel);
    }

    /**
     * 得到下载的map
     * @return
     */
    public Map<String,CacheModel> getDataMap(){
        return this.downHandler.getDataMap();
    }

    /**
     * 得到下载的list
     * @return
     */
    public List<CacheModel> getDataList(){
        Map<String,CacheModel> mapData= this.downHandler.getDataMap();
        List<CacheModel> list=new ArrayList<CacheModel>();
        for (Map.Entry<String, CacheModel> entry : mapData.entrySet()) {
            CacheModel cacheModel = entry.getValue();
            list.add(cacheModel);
        }
        return list;
    }

    /**
     * 停止任务
     * @param cacheModel
     */
    public void stopByUrl(CacheModel cacheModel,boolean isDelete){
        this.downHandler.stopByUrl(cacheModel,isDelete);
    }
    /**
     * 是否有任务正在下载
     * @return
     */
    public boolean isRunning(){
        return this.downHandler.isRunning();
    }

    /**
     * 清除缓存
     * @param cacheModel
     * @return
     */
    public void clearDir(List<CacheModel> cacheModel){
        this.downHandler.clearDir(cacheModel);
    }
}
