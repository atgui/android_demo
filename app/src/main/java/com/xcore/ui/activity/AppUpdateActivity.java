package com.xcore.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.common.BaseCommon;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.maning.updatelibrary.InstallUtils;
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.data.bean.VersionBean;
import com.xcore.utils.LogUtils;
import com.xcore.utils.Md5Util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class AppUpdateActivity extends Activity {
    private RelativeLayout proRelativeLayout;
    private ProgressBar progressBar;
    private Button btnInstall;
    private TextView txtPro;
    private Button btnUpdate;

    private String pathStr;
    private VersionBean versionBean;


    private Toast toast;
    private void show(String msg){
        if(toast==null){
            toast=Toast.makeText(this,msg,Toast.LENGTH_SHORT);
        }else{
            toast.setText(msg);
        }
        toast.show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_update);

        Intent intent= getIntent();
        String versionBeanStr=intent.getStringExtra("versionBean");
        versionBean=new Gson().fromJson(versionBeanStr,VersionBean.class);

        initViews(savedInstanceState);
        testApkSpeed();
    }

    private void initViews(Bundle savedInstanceState) {
        progressBar=findViewById(R.id.pro_progress);

        txtPro=findViewById(R.id.txtPro);
        proRelativeLayout=findViewById(R.id.proRelativeLayout);
        proRelativeLayout.setVisibility(View.GONE);

        TextView txtVersion=findViewById(R.id.txtVersion);
        txtVersion.setText("v"+versionBean.getData().getName());

        TextView txtRemark=findViewById(R.id.txtRemark);
        String mark=versionBean.getData().getRemark();
        if(TextUtils.isEmpty(mark)){
            mark="";
        }
        txtRemark.setText(Html.fromHtml(mark));

        btnUpdate=findViewById(R.id.btn_update);
        findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            btnUpdate.setVisibility(View.GONE);
            proRelativeLayout.setVisibility(View.VISIBLE);
            onDownApk();
            }
        });

        btnInstall=findViewById(R.id.btn_install);
        btnInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                installApk();
            }
        });
        btnInstall.setVisibility(View.GONE);
    }

    protected LoadingDailog dialog=null;
    //速度
    private void testApkSpeed(){
        List<String> apkList=BaseCommon.apkLists;
        if(apkList.size()<=0){
            return;
        }
        LoadingDailog.Builder loadBuilder;
        loadBuilder=new LoadingDailog.Builder(this)
                .setMessage("加载中...")
                .setCancelable(true)
                .setCancelOutside(true);
        dialog=loadBuilder.create();
        if(dialog!=null){
            dialog.show();
        }

        for(String url :apkList){
            toSpeed(url);
        }
    }
    private void toSpeed(final String url){
        OkGo.<String>get(url+"speed.txt").execute(new StringCallback() {
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
            if(dialog!=null){
                dialog.cancel();
            }
        }
    }

    //成功
    private void onFileSuccess(File file){
        //保存到本地  82599dd9f6bc8adedae1a0949a1da380
        String value=Md5Util.getMD5(file);//MD5Encoder.getFileMD5(file);
        //String xStr=Md5Util.getMD5(file);

        Log.e("TAG","下载好了:MD5::"+value);
        //校验md5 码
        if(!versionBean.getData().getMd5().equals(value)){
            Log.e("TAG","校验码不正确,可能被串改,请重新下载");
            return;
        }

        proRelativeLayout.setVisibility(View.GONE);
        btnInstall.setVisibility(View.VISIBLE);

        pathStr=file.getAbsolutePath();
        //安装
        installApk();
//        uninstallApk("");
    }

    //更新apk
    private void onDownApk(){
        String url= BaseCommon.APK_URL+versionBean.getData().getDownUrl();
        OkGo.<File>get(url)//
                .tag(this)//
                .execute(new FileCallback() {
                    @Override
                    public void onSuccess(Response<File> response) {
                        File file=response.body();
                        onFileSuccess(file);
                    }
                    @Override
                    public void downloadProgress(Progress progress) {
                        super.downloadProgress(progress);
//                        proRelativeLayout.setVisibility(View.VISIBLE);
                        int value= (int) (progress.currentSize*1.0/progress.totalSize*100);
                        if(value>0&&value<5){
                            value=5;
                        }
                        progressBar.setProgress(value);
                        txtPro.setText("已完成"+value+"%");
                    }

                    @Override
                    public void onError(Response<File> response) {
                        super.onError(response);
                        show("下载出错,请重新下载!");

                        progressBar.setProgress(0);
                        txtPro.setText("已下载0%");
                        proRelativeLayout.setVisibility(View.GONE);

                        btnInstall.setVisibility(View.GONE);
                        btnUpdate.setText("重新下载");
                        btnUpdate.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void installApk(){
        InstallUtils.installAPK(AppUpdateActivity.this, pathStr,
                new InstallUtils.InstallCallBack() {
                    @Override
                    public void onSuccess() {
                        Log.e("TAG","打开安装中...");
                        //Toast.makeText(AppUpdateActivity.this, "安装程序成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(Exception e) {
                        //tv_info.setText("安装失败:" + e.toString());
                        Log.e("TAG","安装失败");
                    }
                });
    }

    private long mExitTime;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                MainApplicationContext.finishAllActivity();
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(dialog!=null){
            dialog.dismiss();
        }
        dialog=null;
    }
}
