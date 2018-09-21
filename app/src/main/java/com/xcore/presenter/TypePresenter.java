package com.xcore.presenter;

import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xcore.base.BasePresent;
import com.xcore.data.bean.CategoriesBean;
import com.xcore.data.bean.TypeListBean;
import com.xcore.data.bean.TypeTabBean;
import com.xcore.data.utils.AGCallback;
import com.xcore.data.utils.DataUtils;
import com.xcore.presenter.view.ITypeView;
import com.xcore.services.ApiFactory;

public class TypePresenter extends BasePresent<ITypeView>{

    /**
     * 得到页面数据
     * （页面索引）PageIndex:1
         （页面大小）PageSize:20
         （排序分类）sorttype:1
         （种类）species:1
         （分类）categories:1
        请求：post
     */
    public void getViewData(int pageIndex,String sortType,String species,String categories,boolean showall){
        if(!checkNetwork()){
            return;
        }
        HttpParams params=new HttpParams();
        params.put("PageIndex",pageIndex);
//        params.put("PageSize",1);
        params.put("sorttype",sortType);
        params.put("species",species);
        params.put("categories",categories);
        params.put("showall",showall);
        if(dialog!=null) {
            dialog.show();
        }
        ApiFactory.getInstance().<TypeListBean>getTypeByData(params, new AGCallback<TypeListBean>() {
            @Override
            public void onNext(TypeListBean typeListBean) {
                if(dialog!=null) {
                    dialog.cancel();
                }
                view.onViewResult(typeListBean);
            }
            @Override
            public void onError(Response<TypeListBean> response) {
                super.onError(response);
                if(dialog!=null){
                    dialog.cancel();
                }
                view.onError("加载出错.");
            }
        });
    }

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
                typeTabBean.getData().getCategories().add(0,new CategoriesBean("","全部"));
                typeTabBean.getData().getSpecies().add(0,new CategoriesBean("","全部"));
//                        typeTabBean.getData().getSorttype().add(0,new CategoriesBean("","全部"));
                DataUtils.typeTabBean=typeTabBean;

                if(dialog!=null){
                    dialog.cancel();
                }
                view.onTagResult(typeTabBean);

            }
        });
    }
}
