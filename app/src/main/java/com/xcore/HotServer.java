package com.xcore;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.common.BaseCommon;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.xcore.data.bean.VersionBean;
import com.xcore.data.utils.AGCallback;
import com.xcore.services.ApiSystemFactory;
import com.xcore.tinker.TinkerManager;
import com.xcore.utils.LogUtils;
import com.xcore.utils.MD5Encoder;
import com.xcore.utils.Md5Util;
import com.xcore.utils.SystemUtils;

import java.io.File;
import java.util.List;

/**
 * 热更
 */
public class HotServer{
    private VersionBean versionBean;
    public int VERSION_CODE=7;//热更版本

    public void initServer() {
        Context context=MainApplicationContext.context;
        final int code=VERSION_CODE;
        final String name=SystemUtils.getVersion(context);
        Log.e("TAG","当前版本号:"+name+" ＣＯＤＥ："+code);
        //请求热更
        ApiSystemFactory.getInstance().<VersionBean>getHotUpdate(new AGCallback<VersionBean>() {
            @Override
            public void onNext(VersionBean versionBean1) {
            versionBean=versionBean1;
//            versionBean.getData().setInsideVersion(3);
//            versionBean.getData().setMd5("5dd1d9fcc12a39439fd3ec28cd9f4e38");

            Log.e("TAG","HOT::"+versionBean1.toString());
            if(versionBean.getData().getInsideVersion()>code
                    &&name.equals(versionBean.getData().getName())
                    &&versionBean.getData().getRemark().indexOf("ag_debug")<0){
                testApkSpeed();
                //onDownApk();
                return;
            }
            }
        });
    }

    protected LoadingDailog dialog=null;
    //速度
    private void testApkSpeed(){
        List<String> apkList=BaseCommon.apkLists;
        if(apkList.size()<=0){
            return;
        }

        for(String url :apkList){
            toSpeed(url);
        }
    }
    private void toSpeed(final String url){
        String vUrl=url+"speed.txt";
        Log.e("TAG",vUrl);
        OkGo.<String>get(vUrl).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                finalResult(url);
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                Log.e("TAG","测试地址错误");
                try {
                    String msg = response.getException().getMessage();
                    if (TextUtils.isEmpty(msg)) {
                        msg = "请求出错";
                    }
                    String bodyStr=response.body();
                    if(!TextUtils.isEmpty(bodyStr)){
                        msg+="|BODY="+bodyStr;
                    }
                    msg += "|API_URL=" + url;
                    msg += "|API_ERROR=API_测速";
                    LogUtils.apiRequestError(url, msg);
                }catch (Exception e){}
            }
        });
    }

    private boolean isLoad=false;
    private void finalResult(String url){
        if(isLoad==false){
            isLoad=true;
            BaseCommon.APK_URL=url;
            onDownApk();
        }
    }

    //更新apk
    private void onDownApk(){
        String path=MainApplicationContext.APK_HOT_PATH;
        File file=new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
        String url=BaseCommon.APK_URL+versionBean.getData().getDownUrl();
        OkGo.<File>get(url)//
                .tag(this)//
                .execute(new FileCallback(file.getAbsolutePath(),"hot_update.apk") {
                    @Override
                    public void onSuccess(Response<File> response) {
                        File file=response.body();
                        onFileSuccess(file);
                    }

                    @Override
                    public void downloadProgress(Progress progress) {
                        super.downloadProgress(progress);
                        int value= (int) (progress.currentSize*1.0/progress.totalSize*100);
                        Log.e("TAG","已完成" + value + "%");
                    }

                    @Override
                    public void onError(Response<File> response) {
                        super.onError(response);
                        Log.e("TAG","下载出错");
                    }
                });
    }
    //下载完成 加载热更
    private void onFileSuccess(File file){
        //校验md5
        String md5Str=Md5Util.getMD5(file);
        Log.e("TAG","本地MD5:"+md5Str);
//        String md5Str=MD5Encoder.getFileMD5(file);
        if(!md5Str.equals(versionBean.getData().getMd5())){
            Log.e("TAG","校验失败");
            //Toast.makeText(MainApplicationContext.context,"校验失败",Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("TAG","校验成功");
        //Toast.makeText(MainApplicationContext.context,"校验成功",Toast.LENGTH_SHORT).show();
        //加载热更
        TinkerManager.loadPatch(file.getAbsolutePath());
    }
}
