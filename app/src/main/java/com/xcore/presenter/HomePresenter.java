package com.xcore.presenter;

import android.app.Activity;

import com.android.tu.loadingdialog.LoadingDailog;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xcore.MainApplicationContext;
import com.xcore.base.BasePresent;
import com.xcore.data.bean.HomeBean;
import com.xcore.data.bean.NoticeAlertBean;
import com.xcore.data.bean.TypeListBean;
import com.xcore.data.utils.AGCallback;
import com.xcore.presenter.view.HomeView;
import com.xcore.services.ApiFactory;

public class HomePresenter extends BasePresent<HomeView> {
    private int pageSize=7;

    //得到首页数据
    public void getHomeData(){
        if(!checkNetwork()){
            return;
        }
        if(dialog!=null) {
            dialog.show();
        }
        ApiFactory.getInstance().<HomeBean>getHomeData(new AGCallback<HomeBean>() {
            @Override
            public void onNext(HomeBean homeBean) {
                if(dialog!=null) {
                    dialog.cancel();
                }
                view.onHomeResult(homeBean);
            }

            @Override
            public void onError(Response<HomeBean> response) {
                super.onError(response);
                if(dialog!=null){
                    dialog.cancel();
                }
            }
        });
    }
    //根据类型查询
    public void getTypeData(final int pageIndex, final HomeBean.HomeDataItem dataItem){
        if(!checkNetwork()){
            return;
        }
        HttpParams params=new HttpParams();
        params.put("PageIndex",pageIndex);
        params.put("PageSize",pageSize);
        params.put("sorttype","");
        params.put("species","");
        params.put("categories",dataItem.getShortId());
        if(dialog!=null) {
            dialog.show();
        }
        ApiFactory.getInstance().<TypeListBean>getTypeByData(params, new AGCallback<TypeListBean>() {
            @Override
            public void onNext(TypeListBean typeListBean) {
                HomeBean.HomeDataItem homeDataItem=new HomeBean.HomeDataItem();
                homeDataItem.setShortId(dataItem.getShortId());
                homeDataItem.setName(dataItem.getName());
                homeDataItem.setType(dataItem.getType());
                homeDataItem.setList(typeListBean.getList());

                if(dialog!=null) {
                    dialog.cancel();
                }
                view.onHomeTypeResult(homeDataItem);
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
    //类型标签
    public void getTypeTagData(final int pageIndex, final HomeBean.HomeDataItem dataItem){
        if(!checkNetwork()){
            return;
        }
        HttpParams params=new HttpParams();
        params.put("PageIndex",pageIndex);
        params.put("PageSize",6);
        params.put("sorttype",dataItem.getShortId());
        params.put("species","");
        params.put("categories","");
        if(dialog!=null){
            dialog.show();
        }
        ApiFactory.getInstance().<TypeListBean>getTypeByData(params, new AGCallback<TypeListBean>() {
            @Override
            public void onNext(TypeListBean typeListBean) {
                HomeBean.HomeDataItem homeDataItem=new HomeBean.HomeDataItem();
                homeDataItem.setShortId(dataItem.getShortId());
                homeDataItem.setName(dataItem.getName());
                homeDataItem.setType(dataItem.getType());
                homeDataItem.setList(typeListBean.getList());

                if(dialog!=null) {
                    dialog.cancel();
                }
                view.onHomeTypeResult(homeDataItem);
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
    //热播
    public void getTypeHot(final int pageIndex, final HomeBean.HomeDataItem dataItem){
        if(!checkNetwork()){
            return;
        }
        HttpParams params=new HttpParams();
        params.put("PageIndex",pageIndex);
        params.put("PageSize",pageSize);
        params.put("sorttype",dataItem.getShortId());
        params.put("species","");
        params.put("categories","");

        if(dialog!=null){
            dialog.show();
        }
        ApiFactory.getInstance().<TypeListBean>getTypeByData(params, new AGCallback<TypeListBean>() {
            @Override
            public void onNext(TypeListBean typeListBean) {
                HomeBean.HomeDataItem homeDataItem=new HomeBean.HomeDataItem();
                homeDataItem.setShortId(dataItem.getShortId());
                homeDataItem.setName(dataItem.getName());
                homeDataItem.setType(dataItem.getType());
                homeDataItem.setList(typeListBean.getList());

                if(dialog!=null) {
                    dialog.cancel();
                }
                view.onHomeTypeResult(homeDataItem);
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


    //根据标签查询
    public void getTagData(final int pageIndex, final HomeBean.HomeDataItem dataItem){
        if(!checkNetwork()){
            return;
        }
        HttpParams params=new HttpParams();
        params.put("PageIndex",pageIndex);
        params.put("key",dataItem.getName());
        params.put("PageSize",pageSize);
        if(dialog!=null){
            dialog.show();
        }
        ApiFactory.getInstance().<TypeListBean>getTag(params, new AGCallback<TypeListBean>() {
            @Override
            public void onNext(TypeListBean typeListBean) {
                HomeBean.HomeDataItem homeDataItem=new HomeBean.HomeDataItem();
                homeDataItem.setShortId(dataItem.getShortId());
                homeDataItem.setName(dataItem.getName());
                homeDataItem.setType(dataItem.getType());
                homeDataItem.setList(typeListBean.getList());

                if(dialog!=null) {
                    dialog.cancel();
                }
                view.onHomeTypeResult(homeDataItem);
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
