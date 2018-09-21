package com.xcore.presenter;

import com.lzy.okgo.model.Response;
import com.xcore.base.BasePresent;
import com.xcore.data.bean.BoxBean;
import com.xcore.data.utils.AGCallback;
import com.xcore.presenter.view.BoxView;
import com.xcore.services.ApiFactory;

public class BoxPresenter extends BasePresent<BoxView> {
    //获取打开时间
    public void getBoxTime(){
        if(!checkNetwork()){
            return;
        }
        dialog.show();
        ApiFactory.getInstance().<BoxBean>getBoxTime(new AGCallback<BoxBean>() {
            @Override
            public void onNext(BoxBean boxBean) {
                view.onGetTimeResult(boxBean);
                dialog.cancel();
            }

            @Override
            public void onError(Response<BoxBean> response) {
                super.onError(response);
                if(dialog!=null){
                    dialog.cancel();
                }
            }
        });
    }
    //打开
    public void getOpen(){
        if(!checkNetwork()){
            return;
        }
        dialog.show();
        ApiFactory.getInstance().<BoxBean>getOpenBox(new AGCallback<BoxBean>() {
            @Override
            public void onNext(BoxBean boxBean) {
                view.onGetOpenBoxResult(boxBean);
                dialog.cancel();
            }

            @Override
            public void onError(Response<BoxBean> response) {
                super.onError(response);
                if(dialog!=null){
                    dialog.cancel();
                }
            }
        });
    }

}
