package com.xcore.presenter;

import android.os.Handler;
import android.os.Message;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xcore.base.BasePresent;
import com.xcore.cache.CacheManager;
import com.xcore.cache.DBHandler;
import com.xcore.cache.beans.DictionaryBean;
import com.xcore.data.bean.SearchBean;
import com.xcore.data.bean.TypeListBean;
import com.xcore.data.utils.AGCallback;
import com.xcore.presenter.view.SearchView;
import com.xcore.services.ApiFactory;

import java.util.List;

public class SearchPresenter extends BasePresent<SearchView> {
    final Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                List<DictionaryBean> dics= (List<DictionaryBean>) msg.obj;
                view.getHostoryDictionary(dics);
            }
        }
    };
    //删除
    public void delteKey(String key){
        DictionaryBean dictionaryBean=new DictionaryBean();
        dictionaryBean.setDicDelete("0");
        dictionaryBean.setDicName(key);
        dictionaryBean.setDicUpdateTime(System.currentTimeMillis()+"");

        CacheManager.getInstance().getDbHandler().update(dictionaryBean);
    }

    //添加关健字
    public void addDicKey(String key){
        DictionaryBean dictionaryBean=new DictionaryBean();
        dictionaryBean.setDicDelete("1");
        dictionaryBean.setDicName(key);
        dictionaryBean.setDicUpdateTime(System.currentTimeMillis()+"");

        DBHandler dbHandler= CacheManager.getInstance().getDbHandler();
        dbHandler.insertDic(dictionaryBean);

        getHDicKey();
    }

    //得到热门
    public void getHotDicKey(){
        if(!checkNetwork()){
            return;
        }
        if(dialog!=null){
            dialog.show();
        }
        ApiFactory.getInstance().<SearchBean>getHotDic(new AGCallback<SearchBean>() {
            @Override
            public void onNext(SearchBean searchBean) {
                view.getHotDictionary(searchBean);
                if(dialog!=null){
                    dialog.cancel();
                }
            }

            @Override
            public void onError(Response<SearchBean> response) {
                super.onError(response);
                if(dialog!=null){
                    dialog.cancel();
                }
            }
        });
    }
    //得到历史
    public void getHDicKey(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<DictionaryBean> dictionaryBeanList= CacheManager.getInstance().getDbHandler().query(1);
                Message msg=new Message();
                msg.obj=dictionaryBeanList;
                msg.what=1;
                handler.sendMessage(msg);
            }
        }).start();
    }
    //搜索
    public void getSearchResult(final int pageIndex, final String tag){
        if(!checkNetwork()){
            return;
        }
        HttpParams params=new HttpParams();
        params.put("key",tag);
        params.put("PageIndex",pageIndex);
        if(dialog!=null){
            dialog.show();
        }
        ApiFactory.getInstance().<TypeListBean>getSearchByKey(params, new AGCallback<TypeListBean>() {
            @Override
            public void onNext(TypeListBean typeListBean) {
                view.onResult(typeListBean);
                if(dialog!=null){
                    dialog.cancel();
                }
            }

            @Override
            public void onError(Response<TypeListBean> response) {
                super.onError(response);
                if(dialog!=null){
                    dialog.cancel();
                }
            }
        });
    }

}
