package com.xcore.presenter;

import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xcore.base.BasePresent;
import com.xcore.data.bean.MakeLtdTabBean;
import com.xcore.data.bean.TagBean;
import com.xcore.data.bean.TypeListBean;
import com.xcore.data.utils.AGCallback;
import com.xcore.presenter.view.MakeLtdView;
import com.xcore.services.ApiFactory;

public class MakeLtdPresenter extends BasePresent<MakeLtdView> {

    /**
     * 得到标签信息
     */
    public void getTags(String shortId){

        HttpParams params=new HttpParams();
        params.put("shortId",shortId);

        ApiFactory.getInstance().<MakeLtdTabBean>getCompanyTags(params, new AGCallback<MakeLtdTabBean>() {
            @Override
            public void onNext(MakeLtdTabBean makeLtdTabBean) {
                view.onTags(makeLtdTabBean);
            }
        });
    }

    /**
     * 得到信息
     * @param pageIndex
     * @param sortTypeStr
     * @param movieCompany
     */
    public void getMakeLtds(int pageIndex,String sortTypeStr,String tag,String movieCompany,String q){
        if(!checkNetwork()){
            return;
        }
        HttpParams params=new HttpParams();
        params.put("PageIndex",pageIndex);
        params.put("sorttype",sortTypeStr);
        params.put("movieCompany",movieCompany);
        params.put("tags",tag);
        params.put("quality",q);
        params.put("showall",false);
        if(dialog!=null) {
            dialog.show();
        }
        ApiFactory.getInstance().<TypeListBean>getCompanyInfos(params, new AGCallback<TypeListBean>() {
            @Override
            public void onNext(TypeListBean typeListBean) {
                if(dialog!=null) {
                    dialog.cancel();
                }
                view.onResult(typeListBean);
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
