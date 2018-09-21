package com.xcore.presenter;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import com.android.tu.loadingdialog.LoadingDailog;
import com.common.BaseCommon;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xcore.MainApplicationContext;
import com.xcore.base.BasePresent;
import com.xcore.cache.CacheManager;
import com.xcore.data.bean.CategoriesBean;
import com.xcore.data.bean.HomeBean;
import com.xcore.data.bean.RegisterBean;
import com.xcore.data.bean.TokenBean;
import com.xcore.data.bean.TypeTabBean;
import com.xcore.data.utils.AGCallback;
import com.xcore.data.utils.DataUtils;
import com.xcore.presenter.view.LoginView;
import com.xcore.services.ApiFactory;
import com.xcore.services.ApiSystemFactory;
import com.xcore.utils.SystemUtils;


public class LoginPresenter extends BasePresent<LoginView> {

    @Override
    public void attach(LoginView view) {
        Log.e("TAG","Login Attach...");
        this.view = view;
        dbHandler=CacheManager.getInstance().getDbHandler();
        Activity activity=MainApplicationContext.getLastActivity();
        if(activity==null){
            activity= (Activity) view;
        }
        LoadingDailog.Builder loadBuilder;loadBuilder=new LoadingDailog.Builder(activity)
                .setMessage("登录中...")
                .setCancelable(false)
                .setCancelOutside(false);

        dialog=loadBuilder.create();
    }

    //登录
    public void getLogin(String name,String pwd,boolean isRecode,boolean isGuest) {
        if(!checkNetwork()){
            return;
        }
        final boolean recodePwd=true;//全部记住账号密码
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
                CacheManager.getInstance().getLocalHandler().put(DataUtils.SAVE_ID, value);
//                if(recodePwd==true){//登录成功如有记住密码就保存到本地
//                    //保存到本地
//                    if(guestBoo==true) {
//                        CacheManager.getInstance().getLocalHandler().put(DataUtils.SAVE_ID, "" + username + "|" + password+"|true");
//                    }else{
//                        //不是游客
//                        CacheManager.getInstance().getLocalHandler().put(DataUtils.SAVE_ID, "" + username + "|" + password+"|false");
//                    }
//                }else{
//                    CacheManager.getInstance().getLocalHandler().remove(DataUtils.SAVE_ID);
//                }
                MainApplicationContext.isGuest=guestBoo;
                DataUtils.uname=username;
                DataUtils.pwd=password;
                tokenBean.setToken_time(System.currentTimeMillis());
                DataUtils.tokenBean=tokenBean;
                view.getTokenResult(tokenBean);
            }
            @Override
            public void onError(Response<TokenBean> response) {
                super.onError(response);
                if(dialog!=null){
                    dialog.cancel();
                }
            }
        },username,password);
    }
    //注册
    public void toRegister(final String name, String password, String versionCode, String shareCode){
        if(!checkNetwork()){
            return;
        }
        final String Name=name;
        final String Password=password;
        String ShareCode=shareCode;

        String Language= SystemUtils.getSystemLanguage();
        String SystemVersion=SystemUtils.getSystemVersion();
        String DeviceBrand=SystemUtils.getDeviceBrand();
        String AppDeviceCode=SystemUtils.getFingerprint();
        String Imei=SystemUtils.getM(MainApplicationContext.context);
        String SystemModel=SystemUtils.getSystemModel();
        String SystemInfo=SystemUtils.getDevice();

        String ClientVersion=versionCode;

        HttpParams params=new HttpParams();
        params.put("Name",Name);
        params.put("Password",Password);
        params.put("ShareCode",ShareCode);
        params.put("Language",Language);
        params.put("SystemVersion",SystemVersion);
        params.put("DeviceBrand",DeviceBrand);
        params.put("Imei",Imei);
        params.put("SystemModel",SystemModel);
        params.put("ClientVersion",ClientVersion);
        params.put("AppDeviceCode",AppDeviceCode);
        params.put("SystemInfo",SystemInfo);

        if(dialog!=null){
            dialog.show();
        }

        ApiSystemFactory.getInstance().<RegisterBean>toRegister(new AGCallback<RegisterBean>() {
            @Override
            public void onNext(RegisterBean registerBean) {
                registerBean.setUsername(Name);
                registerBean.setPassword(Password);
                view.getRegisterResult(registerBean);
                if(dialog!=null){
                    dialog.cancel();
                }
            }
            @Override
            public void onError(Response<RegisterBean> response) {
                RegisterBean registerBean=response.body();
                view.onError(registerBean.getMessage());
                if(dialog!=null){
                    dialog.cancel();
                }
            }
        },params);

    }
    //得到所有的类型标签
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
//                view.onFinalResult();
            }
            @Override
            public void onError(Response<TypeTabBean> response) {
                super.onError(response);
                getHome();
//                view.onFinalResult();
            }
        });
    }
    //得到首页数据
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
                if(dialog!=null){
                    dialog.cancel();
                }
                view.onFinalResult();
            }
        });
    }

}
