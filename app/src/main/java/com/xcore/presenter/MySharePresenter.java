package com.xcore.presenter;

import com.lzy.okgo.model.Response;
import com.xcore.base.BasePresent;
import com.xcore.data.bean.MyShareBean;
import com.xcore.data.utils.AGCallback;
import com.xcore.presenter.view.MyShareView;
import com.xcore.services.ApiFactory;

public class MySharePresenter extends BasePresent<MyShareView>{
    //获取推广信息
    public void getShareList(int pageIndex){
        if(!checkNetwork()){
            return;
        }
        if(dialog!=null){
            dialog.show();
        }
        ApiFactory.getInstance().<MyShareBean>getMyShareList(pageIndex,new AGCallback<MyShareBean>(){
            @Override
            public void onNext(MyShareBean shareBean) {
                view.onShareResult(shareBean);
                if(dialog!=null){
                    dialog.cancel();
                }
            }

            @Override
            public void onError(Response<MyShareBean> response) {
                super.onError(response);
                if(dialog!=null){
                    dialog.cancel();
                }
            }
        });
    }
}
