package com.xcore.presenter;

import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xcore.base.BasePresent;
import com.xcore.data.bean.LikeBean;
import com.xcore.data.bean.PathBean;
import com.xcore.data.bean.TypeListBean;
import com.xcore.data.utils.AGCallback;
import com.xcore.presenter.view.FindView;
import com.xcore.services.ApiFactory;

public class FindPresenter extends BasePresent<FindView> {
    public void getFind(final String shortId, final int pageIndex){
        if(!checkNetwork()){
            return;
        }
        if(dialog!=null){
            dialog.show();
        }
        HttpParams params=new HttpParams();
        params.put("PageIndex",pageIndex);
        params.put("shortId",shortId);

        ApiFactory.getInstance().<TypeListBean>getFinds(params, new AGCallback<TypeListBean>() {
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
    //得到下载路径
    public void getPath(String shortId){
        if(!checkNetwork()){
            return;
        }
        if(dialog!=null){
            dialog.show();
        }
        HttpParams params=new HttpParams();
        params.put("shortId",shortId);
        ApiFactory.getInstance().<LikeBean>getDownPath(params, new AGCallback<LikeBean>() {
            @Override
            public void onNext(LikeBean likeBean) {
                view.onPathResult(likeBean);
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

}
