package com.xcore.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.common.BaseCommon;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xcore.MainApplicationContext;
import com.xcore.base.BasePresent;
import com.xcore.cache.CacheManager;
import com.xcore.data.bean.CategoriesBean;
import com.xcore.data.bean.CdnBean;
import com.xcore.data.bean.GuestBean;
import com.xcore.data.bean.HomeBean;
import com.xcore.data.bean.JsonDataBean;
import com.xcore.data.bean.RegisterBean;
import com.xcore.data.bean.SpeedDataBean;
import com.xcore.data.bean.TokenBean;
import com.xcore.data.bean.TypeTabBean;
import com.xcore.data.bean.VersionBean;
import com.xcore.data.utils.AGCallback;
import com.xcore.data.utils.DataUtils;
import com.xcore.presenter.view.SplashView;
import com.xcore.services.ApiFactory;
import com.xcore.services.ApiSystemFactory;
import com.xcore.utils.LogUtils;
import com.xcore.utils.SystemUtils;
import com.xcore.utils.TJave;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SplashPresenter extends BasePresent<SplashView>{

    private int jsonIndex=0;
    List<TJave> tJaves=new ArrayList<>();//pic、tor 测试
    List<TJave> apiJaves=new ArrayList<>();//api 测试

    /**
     * 请求 json
     */
    public void getJson(){
        if(!checkNetwork()){
            return;
        }
        final String jsonUrl=BaseCommon.jsonList.get(jsonIndex);
        jsonIndex++;
        ApiSystemFactory.getInstance().getJson(jsonUrl, new AGCallback<JsonDataBean>() {
            @Override
            public void onNext(JsonDataBean jsonDataBean) {
                view.onJsonResult(jsonDataBean);
            }
            @Override
            public void onError(Response<JsonDataBean> response) {
                super.onError(response);
                Log.e("TAG","请求JSON出错");
                    if(jsonIndex<BaseCommon.jsonList.size()){
                    getJson();
                }else{
                    view.onJsonResult(null);
                }
            }
        });
    }

    //api 速度测试
    public void apiSpeed(){
        apiJaves.add(new TJave(BaseCommon.apiList, new TJave.INextJave() {
            @Override
            public void onNext(String path) {
                if(path!=null&&path.length()>0){
                    BaseCommon.API_URL=path;
                    TJave tJave= apiJaves.remove(0);
                    tJave.start();
                }
            }
        }));
        apiJaves.add(new TJave(new ArrayList<String>(), new TJave.INextJave() {
            @Override
            public void onNext(String path) {
                //view.onGetSpeedResult("");
                getCdn();
            }
        }));
        TJave tJave= apiJaves.remove(0);
        tJave.start();
    }

    //pic、tor 速度测试
    public void xxSpeed(){
        tJaves.add(new TJave(BaseCommon.resLists, new TJave.INextJave() {
            @Override
            public void onNext(String path) {
                if(path!=null&&path.length()>0){
                    BaseCommon.RES_URL=path;
                    TJave tJave= tJaves.remove(0);
                    tJave.start();
                }
            }
        }));
        tJaves.add(new TJave(BaseCommon.videoLists, new TJave.INextJave() {
            @Override
            public void onNext(String path) {
                if(path!=null&&path.length()>0){
                    BaseCommon.VIDEO_URL=path;
                    TJave tJave= tJaves.remove(0);
                    tJave.start();
                }
            }
        }));
        tJaves.add(new TJave(new ArrayList<String>(), new TJave.INextJave() {
            @Override
            public void onNext(String path) {
                view.onSpeedFinalResult();
            }
        }));
        TJave tJave=tJaves.remove(0);
        tJave.start();
    }

    /**
     * 请求版本
     */
    public void getVersion(){
        if(!checkNetwork()){
            return;
        }
        Log.e("TAG","请求版本.");

        ApiSystemFactory.getInstance().<VersionBean>getVersion(new AGCallback<VersionBean>() {
            @Override
            public void onNext(VersionBean versionBean) {
                Log.e("TAG","版本返回."+versionBean.toString());
                view.onVersionResult(versionBean);
            }

            @Override
            public void onError(Response<VersionBean> response) {
                super.onError(response);
                Log.e("TAG","请求版本出错");
            }
        });
    }

    /**
     * 请求cdn
     */
    public void getCdn(){
        if(!checkNetwork()){
            return;
        }
        ApiSystemFactory.getInstance().<CdnBean>getCdn(new AGCallback<CdnBean>() {
            @Override
            public void onNext(CdnBean cdnBean) {
                Log.e("TAG","CDN 结果返回了"+cdnBean.toString());
                view.onCdnResult(cdnBean);
            }

            @Override
            public void onError(Response<CdnBean> response) {
                super.onError(response);
                Log.e("TAG","请求CDN出错");
            }
        });
    }

    /**
     * 获取游客账号信息
     * @param versionCode
     */
    public void getGuest(String versionCode){
        String ShareCode="";

        String Language= SystemUtils.getSystemLanguage();
        String SystemVersion=SystemUtils.getSystemVersion();
        String DeviceBrand=SystemUtils.getDeviceBrand();
        String AppDeviceCode=SystemUtils.getFingerprint();
        String SystemModel=SystemUtils.getSystemModel();
        String Imei=SystemUtils.getM(MainApplicationContext.context);
        String SystemInfo=SystemUtils.getDevice();

        String ClientVersion=versionCode;

        HttpParams params=new HttpParams();
//        params.put("Name","");
//        params.put("Password","");
        params.put("ShareCode",ShareCode);
        params.put("Language",Language);
        params.put("SystemVersion",SystemVersion);
        params.put("DeviceBrand",DeviceBrand);
        params.put("Imei",Imei);
        params.put("SystemModel",SystemModel);
        params.put("ClientVersion",ClientVersion);
        params.put("AppDeviceCode",AppDeviceCode);
        params.put("SystemInfo",SystemInfo);

        ApiSystemFactory.getInstance().<GuestBean>getGuest(params, new AGCallback<GuestBean>() {
            @Override
            public void onNext(GuestBean guestBean) {
                view.onGetGuest(guestBean);
            }

            @Override
            public void onError(Response<GuestBean> response) {
                super.onError(response);
                Log.e("TAG","请求游客出错!!");
                view.onError("");
            }
        });
    }

    /**
     * 登录
     * @param name
     * @param pwd
     * @param isRecode
     * @param isGuest
     */
    public void getLogin(String name,String pwd,boolean isRecode,boolean isGuest) {
        if(!checkNetwork()){
            return;
        }
        final boolean recodePwd=isRecode;
        final String username=name;
        final String password=pwd;
        final boolean guestBoo=isGuest;
        if(dialog!=null) {
            dialog.show();
        }

        ApiSystemFactory.getInstance().<TokenBean>getToken(new AGCallback<TokenBean>() {
            @Override
            public void onNext(TokenBean tokenBean) {
                if(!TextUtils.isEmpty(tokenBean.getError())|| !TextUtils.isEmpty(tokenBean.getError_description())){
                    view.getTokenResult(tokenBean);
                    if(dialog!=null){
                        dialog.cancel();
                    }
                    return;
                }
                String guestStr="no";
                if(guestBoo==true){
                    guestStr="ok";
                }
                String value=username + "|" + password+"|"+guestStr;
                CacheManager.getInstance().getLocalHandler().put(DataUtils.SAVE_ID, "" + value);
//                if(recodePwd==true){//登录成功如有记住密码就保存到本地
//                    //保存到本地
//
//                }else{
//                    CacheManager.getInstance().getLocalHandler().remove(DataUtils.SAVE_ID);
//                }
                MainApplicationContext.isGuest=guestBoo;
                DataUtils.uname=username;
                DataUtils.pwd=password;
                tokenBean.setToken_time(System.currentTimeMillis());
                DataUtils.tokenBean=tokenBean;

                if(!TextUtils.isEmpty(tokenBean.getError())|| !TextUtils.isEmpty(tokenBean.getError_description())){
                    view.getTokenResult(tokenBean);
                }else{
                    MainApplicationContext.TOKEN=tokenBean.getAccess_token();
                    MainApplicationContext.initOkGo();

                    getTags();
                }

            }
            @Override
            public void onError(Response<TokenBean> response) {
                super.onError(response);
                Log.e("TAG","登录出错");
                if(dialog!=null){
                    dialog.cancel();
                }
                view.onError("");
            }
        },username,password);
    }

    /**
     * 得到所有的类型标签
     */
    public void getTags(){
        if(!checkNetwork()){
            return;
        }
        ApiFactory.getInstance().<TypeTabBean>getTags(new AGCallback<TypeTabBean>() {
            @Override
            public void onNext(TypeTabBean typeTabBean) {
                typeTabBean.getData().getCategories().add(0,new CategoriesBean("","全部"));
                typeTabBean.getData().getSpecies().add(0,new CategoriesBean("","全部"));
//                        typeTabBean.getData().getSorttype().add(0,new CategoriesBean("","全部"));
                DataUtils.typeTabBean=typeTabBean;
                getHome();
//                if(dialog!=null){
//                    dialog.cancel();
//                }
//                view.onFinalResult();
            }
            @Override
            public void onError(Response<TypeTabBean> response) {
                super.onError(response);
                Log.e("TAG","请求标签出错!");
//                getHome();
                if(dialog!=null){
                    dialog.cancel();
                }
                view.onFinalResult();
            }
        });
    }

    /**
     * 得到首页数据
     */
    public void getHome(){
        if(!checkNetwork()){
            return;
        }
        ApiFactory.getInstance().<HomeBean>getHomeData(new AGCallback<HomeBean>() {
            @Override
            public void onNext(HomeBean homeBean) {
                DataUtils.homeBean=homeBean;
                if(dialog!=null){
                    dialog.cancel();
                }
                view.onFinalResult();
            }
            @Override
            public void onError(Response<HomeBean> response) {
                super.onError(response);
                Log.e("TAG","请求HOME出错!");
                view.onFinalResult();
                if(dialog!=null){
                    dialog.cancel();
                }
            }
        });
    }

}
