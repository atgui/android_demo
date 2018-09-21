package com.xcore.cache;

import android.content.Context;

import com.lzy.okgo.cache.CacheMode;
import com.xcore.MainApplicationContext;
import com.xcore.cache.beans.CacheBean;
import com.xcore.cache.beans.CacheCountBean;
import com.xcore.cache.beans.CacheType;
import com.xcore.cache.beans.DictionaryBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库缓存
 */
public class DBHandler {
    private Context context= MainApplicationContext.context;
    TableOperate operate;

    public DBHandler(){
        operate= new TableOperate();//创建数据库操作类
    }

    /**
     * 添加缓存记录
     * @param cache
     */
    public void insertCache(CacheBean cache){
        ArrayList<CacheBean> list = operate.query(TableConfig.Cache.TABLE_CACHE, CacheBean.class,
                new String[]{TableConfig.UPDATE_COLUM,TableConfig.Cache.CACHE_TYPE},
                new String[]{cache.getShortId(),cache.gettType()});
        if(list!=null&&list.size()>0){//更新
            this.update(cache);
        }else{
            operate.insert(TableConfig.Cache.TABLE_CACHE,cache);
        }
    }

    /**
     * 更新
     * @param cacheBean
     */
    public void update(CacheBean cacheBean){
        String sql="update "+TableConfig.Cache.TABLE_CACHE+" set updateTime=?,playPosition=?,"
                +TableConfig.Cache.CACHE_DELETE+"=? where shortId=? and "+TableConfig.Cache.CACHE_TYPE+"=?";
        operate.uptate(sql,System.currentTimeMillis()+"",cacheBean.getPlayPosition()+"",cacheBean.gettDelete(),
                cacheBean.getShortId(),cacheBean.gettType());
    }

    /**
     * 删除数据
     * @param o 要删除的数据
     */
    public void delete(CacheBean o){
//        String tableName=getTableName(tableType);
//        operate.delete(TableConfig.Cache.TABLE_CACHE,TableConfig.UPDATE_COLUM,o.getShortId());
        String sql="update "+TableConfig.Cache.TABLE_CACHE+" set updateTime=?,"
                +TableConfig.Cache.CACHE_DELETE+"=0 where "+TableConfig.Cache.CACHE_SHORT_ID+"=? and "+TableConfig.Cache.CACHE_TYPE+"=?";
        operate.uptate(sql,o.getShortId(),o.gettType());
    }

    /**
     * 得到缓存记录
     * @return List<Cache>
     */
    public List<CacheBean> query(int pageIndex,String type){
        return operate.query(TableConfig.Cache.TABLE_CACHE,CacheBean.class,pageIndex,type,10);
    }

    /**
     * 得到一条数据
     * @param o 查询数据
     * @return
     */
    public CacheBean query(CacheBean o){
        if(o==null){
            return null;
        }
        List<CacheBean> collects= operate.query(TableConfig.Cache.TABLE_CACHE, CacheBean.class,
                new String[]{TableConfig.UPDATE_COLUM,TableConfig.Cache.CACHE_TYPE},
                new String[]{o.getShortId(),o.gettType()});
        if(collects!=null&&collects.size()>0){
            return collects.get(0);
        }
        return null;
    }
    //时间查询
    public List<CacheBean> getQuery(String type,String date1,String date2,int pageIndex,int pageSize){
        List<CacheBean>  cacheBeans=operate.query(type,CacheBean.class,date1,date2,pageIndex,pageSize);
        return cacheBeans;
    }

    /**
     * 得到缓存的总数
     * @return
     */
    public List<CacheCountBean> getCacheCount(){
        return operate.queryCacheCount(CacheCountBean.class);
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// 下载缓存 /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////

    //添加到缓存
    public void insertDown(CacheModel cacheModel){
        List<CacheModel> cacheModels=operate.query(TableConfig.DOWN.TABLE_DOWN,
                CacheModel.class,new String[]{TableConfig.DOWN.DOWN_STREAM_ID},new String[]{cacheModel.getStreamId()});
        if(cacheModels.size()>0){
            //更新
            update(cacheModel);
        }else{
            operate.insert(TableConfig.DOWN.TABLE_DOWN,cacheModel);
        }
    }
    //更新
    public void update(CacheModel cacheModel){
        String sql="update "+TableConfig.DOWN.TABLE_DOWN+" set "
                +TableConfig.DOWN.DOWN_UPDATE_TIME+"=?,"
                +TableConfig.DOWN.DOWN_DELETE+"=?,"+
                TableConfig.DOWN.DOWN_CONVER+"=?,"
                +TableConfig.DOWN.DOWN_TOTAL_SIZE+"=?,"
                +TableConfig.DOWN.DOWN_DOWNSIZE+"=?,"
                +TableConfig.DOWN.DOWN_PERCENT+"=?"+
                " where streamId=?";

        operate.uptate(sql,System.currentTimeMillis()+"",
                "1",
                cacheModel.getConver(),
                cacheModel.getTotalSize(),
                cacheModel.getDownSize(),
                cacheModel.getPercent(),
                cacheModel.getStreamId());
    }

    //得到所有的下载信息
    public List<CacheModel> getDowns(){
        List<CacheModel> cacheModels= operate.query(TableConfig.DOWN.TABLE_DOWN,CacheModel.class,new String[]{},new String[]{});
        return cacheModels;
    }

    //删除
    public void delteDown(CacheModel cacheModel){
        operate.delete(TableConfig.DOWN.TABLE_DOWN,TableConfig.DOWN.DOWN_STREAM_ID,cacheModel.getStreamId());
    }

    /**
     * 得到下载的总数
     * @return
     */
    public List<CacheCountBean> getDownCount(){
        List<CacheCountBean> cacheCountBeans= operate.queryDownCount(CacheCountBean.class);
        if(cacheCountBeans!=null&&cacheCountBeans.size()>0){
            cacheCountBeans.get(0).vt=CacheType.CACHE_DOWN;
        }
        return cacheCountBeans;
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// 搜索历史 /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////

    //插入关健字
    public void insertDic(DictionaryBean dictionaryBean){
        ArrayList<DictionaryBean> list = operate.query(TableConfig.Dictionary.TABLE_DIC, DictionaryBean.class,
                new String[]{TableConfig.Dictionary.DIC_NAME},
                new String[]{dictionaryBean.getDicName()});
        if(list!=null&&list.size()>0){//更新
            this.update(dictionaryBean);
        }else{
            operate.insert(TableConfig.Dictionary.TABLE_DIC,dictionaryBean);
        }
    }
    //得到关健字信息
    public DictionaryBean getDic(DictionaryBean o){
        if(o==null){
            return null;
        }
        List<DictionaryBean> dics= operate.query(TableConfig.Dictionary.TABLE_DIC, DictionaryBean.class,
                new String[]{TableConfig.Dictionary.DIC_NAME},
                new String[]{o.getDicName()});
        if(dics!=null&&dics.size()>0){
            return dics.get(0);
        }
        return null;
    }
    public void update(DictionaryBean dictionaryBean){
        String sql="update "+TableConfig.Dictionary.TABLE_DIC+" set dicUpdateTime=?,"
                +TableConfig.Dictionary.DIC_DELETE+"=? where dicName=?";
        operate.uptate(sql,System.currentTimeMillis()+"",dictionaryBean.getDicDelete(),
                dictionaryBean.getDicName());
    }
    /**
     * 得到历史搜索记录
     * @return List<Cache>
     */
    public List<DictionaryBean> query(int pageIndex){
        return operate.query(TableConfig.Dictionary.TABLE_DIC,DictionaryBean.class,pageIndex);
    }

}
