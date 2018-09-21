package com.xcore.base;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.xcore.MainApplicationContext;
import com.xcore.cache.CacheManager;
import com.xcore.cache.DBHandler;
import com.xcore.data.bean.LikeBean;
import com.xcore.data.utils.AGCallback;
import com.xcore.services.ApiFactory;
import com.xcore.utils.NetWorkUtils;

public class BasePresent<T> {
    public T view;
    public DBHandler dbHandler;
    private String exitId="";

    protected LoadingDailog dialog;

    protected boolean checkNetwork(){
        boolean boo= NetWorkUtils.isNetWorkAvailable();
        if(!boo){
            Context context=MainApplicationContext.context;
            Toast.makeText(context,"请检查网络连接",Toast.LENGTH_SHORT).show();
        }
        return boo;
    }

    public void attach(T view){
        this.view = view;
        dbHandler=CacheManager.getInstance().getDbHandler();
        if(dialog!=null){
            return;
        }
        Activity activity=null;//MainApplicationContext.getLastActivity();
        if(view instanceof Fragment){
            activity=((Fragment)view).getActivity();
        }else{
            activity= (Activity) view;
        }
        if(activity==null){
            activity=MainApplicationContext.getLastActivity();
        }
        if(activity==null){
            return;
        }
        LoadingDailog.Builder loadBuilder;
        loadBuilder=new LoadingDailog.Builder(activity)
                .setMessage("加载中...")
                .setCancelable(true)
                .setCancelOutside(true);
        dialog=loadBuilder.create();
    }

    public void detach(){
        this.view = null;
        if(dialog!=null)
            dialog.dismiss();
        dialog=null;
    }

    /**
     * 进入页面
     */
    public void joinView(String className,String paramsStr){
        if(className.equals("com.xcore.ui.activity.LoginActivity")||
                className.equals("com.xcore.SplashActivity")||
                className.equals("com.xcore.ui.activity.LockLoginActivity")||
                className.equals("com.xcore.ui.activity.LockLoginActivity")||
                className.equals("com.xcore.ui.activity.RegisterActivity")){
            return;
        }
        ApiFactory.getInstance().<LikeBean>joinView(className,paramsStr, new AGCallback<LikeBean>() {
            @Override
            public void onNext(LikeBean o) {
                Log.e("TAG","进入页面返回.."+o.toString());
                exitId=o.getData();
            }
        });
    }

    public void outView(String className,String paramsSt){
        if(exitId.equals("")){
            return;
        }
        ApiFactory.getInstance().<String>outView(exitId, paramsSt,new AGCallback<String>() {
            @Override
            public void onNext(String o) {
                Log.e("TAG","退出页面返回.."+o);
            }
        });
    }

}
