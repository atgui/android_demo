package com.xcore.presenter;

import android.app.Activity;

import com.android.tu.loadingdialog.LoadingDailog;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xcore.base.BasePresent;
import com.xcore.data.bean.SpeedDataBean;
import com.xcore.data.utils.AGCallback;
import com.xcore.presenter.view.UpdateUserView;
import com.xcore.services.ApiFactory;

public class UpdateUserPresenter extends BasePresent<UpdateUserView> {

    /**
     * 修改用户头像
     */
    public void upload(String base64Str){
        if(!checkNetwork()){
            return;
        }
        if(dialog==null){
            LoadingDailog.Builder loadBuilder;
            loadBuilder=new LoadingDailog.Builder((Activity)view)
                    .setMessage("加载中...")
                    .setCancelable(false)
                    .setCancelOutside(false);
            dialog=loadBuilder.create();
        }
        dialog.show();
        ApiFactory.getInstance().<SpeedDataBean>toUploadSingle(base64Str, new AGCallback<SpeedDataBean>() {
            @Override
            public void onNext(SpeedDataBean speedDataBean) {
                view.onUploadResult(speedDataBean);
                //真正的修改头像
                updateHead(speedDataBean.getData());
            }

            @Override
            public void onError(Response<SpeedDataBean> response) {
                super.onError(response);
                view.onError("上传头像失败,请稍后重试!");
                //上传出错
                if(dialog!=null){
                    dialog.cancel();
                }
            }
        });
    }

    /**
     * 修改头像
     * @param headStr:头像路径
     */
    private void updateHead(String headStr){

        HttpParams params=new HttpParams();
        params.put("Image",headStr);

        ApiFactory.getInstance().<SpeedDataBean>updateUserInfo(params, new AGCallback<SpeedDataBean>() {
            @Override
            public void onNext(SpeedDataBean speedDataBean) {
                view.onUpdateResult();
            }

            @Override
            public void onError(Response<SpeedDataBean> response) {
                super.onError(response);
                view.onError("修改头像失败");
            }
        });
    }

}
