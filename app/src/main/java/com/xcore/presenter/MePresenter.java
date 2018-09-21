package com.xcore.presenter;

import com.lzy.okgo.model.Response;
import com.xcore.base.BasePresent;
import com.xcore.data.bean.PlayerBean;
import com.xcore.data.utils.AGCallback;
import com.xcore.data.utils.DataUtils;
import com.xcore.presenter.view.MeView;
import com.xcore.services.ApiFactory;

public class MePresenter extends BasePresent<MeView> {

    //获取用户信息
    public void getUserInfo(){
        if(!checkNetwork()){
            return;
        }
//        if(dialog!=null){
//            dialog.show();
//        }
        ApiFactory.getInstance().<PlayerBean>getUserInfo(new AGCallback<PlayerBean>() {
            @Override
            public void onNext(PlayerBean playerBean) {
                DataUtils.playerBean=playerBean;
                view.onResult(playerBean);
//                if(dialog!=null){
//                    dialog.cancel();
//                }
            }

            @Override
            public void onError(Response<PlayerBean> response) {
                super.onError(response);
//                if(dialog!=null){
//                    dialog.cancel();
//                }
            }
        });
    }

}
