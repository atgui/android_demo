package com.xcore.presenter;

import android.util.Log;

import com.common.BaseCommon;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xcore.base.BasePresent;
import com.xcore.data.utils.AGCallback;
import com.xcore.presenter.view.CollectView;
import com.xcore.services.ApiFactory;

public class CollectPresenter extends BasePresent<CollectView> {

    //更新服务器收藏
    public void deleteCollect(String shortId){
        if(!checkNetwork()){
            return;
        }
        HttpParams params=new HttpParams();
        params.put("shortId",shortId);
        ApiFactory.getInstance().<String>removeCollect(params, new AGCallback<String>() {
            @Override
            public void onNext(String s) {
                Log.e("TAG","取消收藏：："+s);
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
            }
        });
    }
}
