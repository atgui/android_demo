package com.xcore.presenter;

import com.common.BaseCommon;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xcore.base.BasePresent;
import com.xcore.data.bean.TypeListBean;
import com.xcore.data.bean.TypeTabBean;
import com.xcore.data.utils.AGCallback;
import com.xcore.presenter.view.LastUpdatedView;
import com.xcore.services.ApiFactory;

public class LastUpdatedPresenter extends BasePresent<LastUpdatedView> {

    //得到所有的分类
    public void getTags(){
        if(!checkNetwork()){
            return;
        }
        if(dialog!=null){
            dialog.show();
        }
        ApiFactory.getInstance().<TypeTabBean>getTags(new AGCallback<TypeTabBean>() {
            @Override
            public void onNext(TypeTabBean typeTabBean) {
                view.onTagResult(typeTabBean);
                if(dialog!=null){
                    dialog.cancel();
                }
            }

            @Override
            public void onError(Response<TypeTabBean> response) {
                super.onError(response);
                if(dialog!=null){
                    dialog.cancel();
                }
            }
        });
    }

    //得到数据
    public void getData(int pageIndex,String typeId,String sortId,String specie){
        if(!checkNetwork()){
            return;
        }
        if(dialog!=null){
            dialog.show();
        }
        HttpParams params=new HttpParams();
        params.put("PageIndex",pageIndex);
////        params.put("PageSize",5);
        params.put("sorttype",sortId);
        params.put("species",specie);
        params.put("categories",typeId);
        params.put("showall",false);

        ApiFactory.getInstance().<TypeListBean>getTypeByData(params, new AGCallback<TypeListBean>() {
            @Override
            public void onNext(TypeListBean typeListBean) {
                view.onDataResult(typeListBean);
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
