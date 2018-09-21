package com.xcore.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.common.BaseCommon;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xcore.base.BasePresent;
import com.xcore.cache.CacheManager;
import com.xcore.cache.beans.CacheBean;
import com.xcore.cache.beans.CacheType;
import com.xcore.data.bean.CategoriesBean;
import com.xcore.data.bean.DetailBean;
import com.xcore.data.bean.LikeBean;
import com.xcore.data.bean.MovieBean;
import com.xcore.data.bean.NvStar;
import com.xcore.data.bean.PathBean;
import com.xcore.data.utils.AGCallback;
import com.xcore.presenter.view.DetailView;
import com.xcore.services.ApiFactory;
import com.xcore.ui.enums.DetailTouchType;
import com.xcore.utils.LogUtils;

import java.util.List;

public class DetailPresenter extends BasePresent<DetailView> {
    /**
     * 得到详情
     * @param shortId
     */
    public void getDetail(String shortId){
        if(!checkNetwork()){
            return;
        }
        if(dialog!=null){
            dialog.show();
        }
        HttpParams params=new HttpParams();
        params.put("shortId",shortId);
        ApiFactory.getInstance().<DetailBean>getMovieDetail(params, new AGCallback<DetailBean>() {
            @Override
            public void onNext(DetailBean detailBean) {
                view.onDetailResult(detailBean);
                if(dialog!=null){
                    dialog.cancel();
                }
            }

            @Override
            public void onError(Response<DetailBean> response) {
                super.onError(response);
                if(dialog!=null){
                    dialog.cancel();
                }

            }
        });
    }
    /**
     * 得到播放信息
     * @param shortId
     */
    public void getPlay(String shortId,String key){
        if(!checkNetwork()){
            return;
        }
        HttpParams params=new HttpParams();
        params.put("shortId",shortId);
        params.put("key",key);
        if(dialog!=null){
            dialog.show();
        }
        ApiFactory.getInstance().<PathBean>getPlayPath(params, new AGCallback<PathBean>() {
            @Override
            public void onNext(PathBean pathBean) {
                view.onGetPathResult(pathBean);
                if(dialog!=null){
                    dialog.cancel();
                }
            }

            @Override
            public void onError(Response<PathBean> response) {
                super.onError(response);
                if(dialog!=null){
                    dialog.cancel();
                }
            }
        });
    }

    /**
     * 更新收藏
     * @param movieBean
     */
    public void updateCollect(MovieBean movieBean, DetailTouchType touchType,int position){
        if(movieBean==null){
            return;
        }
        CacheBean cacheBean=new CacheBean();
        if(touchType==DetailTouchType.TOUCH_COLLECT) {
            if (movieBean.isCollect == true) {
                cacheBean.settDelete("0");
            } else {
                cacheBean.settDelete("1");
            }
        }else{
            if(movieBean.isCollect==true) {
                cacheBean.settDelete("1");
            }else {
                cacheBean.settDelete("0");
            }
        }
        cacheBean.setShortId(movieBean.getShortId());
        cacheBean.setpStar(String.valueOf(movieBean.getAvgRating()));
        cacheBean.setPlayCount(String.valueOf(movieBean.getPlayCount()));
        cacheBean.settYear(movieBean.getYear());
        cacheBean.setTitle(movieBean.getTitle());
        cacheBean.settDesc(movieBean.getDesc());
        cacheBean.setTime(String.valueOf(movieBean.getDuration()));
        cacheBean.setActor(movieBean.getActor());

        List<CategoriesBean> tagList= movieBean.getTagsList();
        Gson gson= new Gson();
        String tags=gson.toJson(tagList);
        List<NvStar> nvStars= movieBean.getActorList();
        String actors=gson.toJson(nvStars);

        cacheBean.setTags(tags);
        cacheBean.setActors(actors);
        cacheBean.setCover(movieBean.getCover());
        if(touchType==DetailTouchType.TOUCH_COLLECT) {
            cacheBean.settType(CacheType.CACHE_COLLECT);
        }else if(touchType==DetailTouchType.TOUCH_RECODE) {
            cacheBean.settType(CacheType.CACHE_RECODE);
            cacheBean.settDelete("1");
        }else if(touchType==DetailTouchType.TOUCH_CACHE) {
            cacheBean.settType(CacheType.CACHE_DOWN);
        }
        cacheBean.setPlayPosition(String.valueOf(position));
        cacheBean.setUpdateTime(String.valueOf(System.currentTimeMillis()));

        Log.e("TAG","更新"+cacheBean.getShortId()+" 进度:"+cacheBean.getPlayPosition());
        Log.e("TAG",cacheBean.toString());
        CacheManager.getInstance().getDbHandler().insertCache(cacheBean);
        if(touchType==DetailTouchType.TOUCH_COLLECT){
            view.onUpdateCollect();
            updateCollect(cacheBean.getShortId(),cacheBean.gettDelete().equals("1"));
        }

    }
    //更新服务器收藏
    private void updateCollect(String shortId,boolean isCollect){
        if(!checkNetwork()){
            return;
        }
        HttpParams params=new HttpParams();
        params.put("shortId",shortId);
        //dialog.show();
        if(isCollect){
            ApiFactory.getInstance().<LikeBean>addCollect(params, new AGCallback<LikeBean>() {
                @Override
                public void onNext(LikeBean s) {
                    Log.e("TAG","添加收藏：："+s);
                }
            });
        }else{//取消收藏
            ApiFactory.getInstance().<LikeBean>removeCollect(params, new AGCallback<LikeBean>() {
                @Override
                public void onNext(LikeBean s) {
                    Log.e("TAG","取消收藏：："+s);
                }
            });
        }
    }

    //得到缓存路径
    public void getCachePath(final String shortId){
        if(!checkNetwork()){
            return;
        }
        HttpParams params=new HttpParams();
        params.put("shortId",shortId);
        if(dialog!=null){
            dialog.show();
        }
        ApiFactory.getInstance().<LikeBean>getDownPath(params, new AGCallback<LikeBean>() {
            @Override
            public void onNext(LikeBean likeBean) {
                view.onCacheResult(likeBean);
                if(dialog!=null){
                    dialog.cancel();
                }
            }

            @Override
            public void onError(Response<LikeBean> response) {
                super.onError(response);
                if(dialog!=null){
                    dialog.cancel();
                }
                LikeBean likeBean=response.body();
                view.onError(likeBean.getMessage());
            }
        });
    }
    //喜欢或踩
    public void getLike(final String shortId, final int type){
        if(!checkNetwork()){
            return;
        }
        HttpParams params=new HttpParams();
        params.put("shortId",shortId);
        params.put("status",type);
        if(dialog!=null){
            dialog.show();
        }
        ApiFactory.getInstance().<LikeBean>getLikeOrNo(params, new AGCallback<LikeBean>() {
            @Override
            public void onNext(LikeBean likeBean) {
                view.onLikeResult(likeBean,type);
                if(dialog!=null){
                    dialog.cancel();
                }
            }

            @Override
            public void onError(Response<LikeBean> response) {
                super.onError(response);
                if(dialog!=null){
                    dialog.cancel();
                }
            }
        });
    }

    private String sId="";
    //发送播放信息
    public void toPlayInfo(String id,double avgSpeed,long d,long currentPos){
        HttpParams params=new HttpParams();
        params.put("key",avgSpeed);
        params.put("duration",d);
        params.put("pos",currentPos);//当前播放
        if("".equals(sId)||sId==null||sId.length()<=0) {
            params.put("shortId", id);
            ApiFactory.getInstance().<String>toPlayInfo(params, new AGCallback<String>() {
                @Override
                public void onNext(String s) {
                    Log.e("TAG", "进度上传：" + s);
                    try {
                        JsonObject o = new Gson().fromJson(s, JsonObject.class);
                        JsonElement keyStr = o.get("data");
                        String key = keyStr.getAsString();
                        Log.e("TAG", "得到key:" + key);
                        sId = key;
                    }catch (Exception e){
                        Log.e("TAG", "转换得到key 出错。。");
                    }
                }
            });
        }else{
            params.put("shortId", sId);
            ApiFactory.getInstance().<String>toUpdatePlayInfo(params, new AGCallback<String>() {
                @Override
                public void onNext(String s) {
                    Log.e("TAG", "进度上传：" + s);
                }
            });
        }
    }

}
