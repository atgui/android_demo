package com.xcore.presenter;

import com.lzy.okgo.model.Response;
import com.xcore.base.BasePresent;
import com.xcore.data.bean.PlayerBean;
import com.xcore.data.bean.UpgradeBean;
import com.xcore.data.utils.AGCallback;
import com.xcore.presenter.view.UpgradeView;
import com.xcore.services.ApiFactory;

public class UpgradePresenter extends BasePresent<UpgradeView> {
    public void getUpgrade(){
        if(dialog!=null){
            dialog.show();
        }
        ApiFactory.getInstance().<UpgradeBean>getViplevel(new AGCallback<UpgradeBean>() {
            @Override
            public void onNext(UpgradeBean upgradeBean) {
                view.onUpgradeResult(upgradeBean);
                if(dialog!=null){
                    dialog.cancel();
                }
            }

            @Override
            public void onError(Response<UpgradeBean> response) {
                super.onError(response);
                if(dialog!=null){
                    dialog.cancel();
                }
            }
        });
    }

    //获取用户信息
    public void getUserInfo(){
        if(dialog!=null){
            dialog.show();
        }
        ApiFactory.getInstance().<PlayerBean>getUserInfo(new AGCallback<PlayerBean>() {
            @Override
            public void onNext(PlayerBean playerBean) {
                view.onResult(playerBean);
                if(dialog!=null){
                    dialog.cancel();
                }
            }

            @Override
            public void onError(Response<PlayerBean> response) {
                super.onError(response);
                if(dialog!=null){
                    dialog.cancel();
                }
            }
        });
    }

}
