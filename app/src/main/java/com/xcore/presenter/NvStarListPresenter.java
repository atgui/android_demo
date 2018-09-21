package com.xcore.presenter;

import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xcore.base.BasePresent;
import com.xcore.data.bean.NvStarBean;
import com.xcore.data.utils.AGCallback;
import com.xcore.presenter.view.NvStarListView;
import com.xcore.services.ApiFactory;

public class NvStarListPresenter extends BasePresent<NvStarListView> {

    /**
     * 获取女星
     * @param pageIndex
     */
    public void getNvstars(int pageIndex,String sorttypeStr){
        HttpParams params=new HttpParams();
        params.put("PageIndex",pageIndex);
        params.put("sorttype",sorttypeStr);
        if(dialog!=null){
            dialog.show();
        }
        ApiFactory.getInstance().<NvStarBean>getNvStars(params, new AGCallback<NvStarBean>() {
            @Override
            public void onNext(NvStarBean nvStarListBean) {
            view.onResult(nvStarListBean);
            if(dialog!=null){
                dialog.cancel();
            }
            }

            @Override
            public void onError(Response<NvStarBean> response) {
                super.onError(response);
                if(dialog!=null){
                    dialog.cancel();
                }
            }
        });
    }

}
