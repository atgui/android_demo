package com.xcore.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import com.common.BaseCommon;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.maning.updatelibrary.InstallUtils;
import com.xcore.R;
import com.xcore.data.bean.VersionBean;
import java.io.File;
import java.util.List;

public class AppUpdateUtils {
    private Context context;
    private VersionBean.VersionData versionData;
    public boolean isNull=false;

    public AppUpdateUtils(Context context, String verStr){
        this.context=context;
//        private String name;
//        private int insideVersion;
//        private String remark;
//        private String md5;
//        private String downUrl;
//        private long heartbeatTime;
        try {
            versionData = new Gson().fromJson(verStr, VersionBean.VersionData.class);
            if(versionData==null){
                versionData=new VersionBean.VersionData();
                versionData.setName("");
                versionData.setDownUrl("");
            }
            tipsDownApk( "发现新版本" + versionData.getName() + ",马上更新或到官网(http://www.1av.co)下载最新版本!");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void tipsDownApk(String msg){
        // 这里的属性可以一直设置，因为每次设置后返回的是一个builder对象
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // 设置提示框的标题
        builder.setTitle("版本升级").
                setIcon(R.drawable.ic_launcher). // 设置提示框的图标
                setMessage(msg).// 设置要显示的信息
                setPositiveButton("确定", new DialogInterface.OnClickListener() {// 设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downApk();//下载最新的版本程序
            }
        });//.setNegativeButton("取消", null);//设置取消按钮,null是什么都不做，并关闭对话框
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        // 显示对话框
        alertDialog.show();
    }

    private void downApk(){
        final ProgressDialog progressDialog=new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("正在下载新版本");
        progressDialog.setCancelable(false);//不能手动取消下载进度对话框
        progressDialog.show();

        List<String> apks= BaseCommon.apkLists;
        int num =(int)(Math.random() * apks.size());
        String apkUrl=apks.get(num);
        downApk(progressDialog,apkUrl);
    }
    private void downApk(final ProgressDialog progressDialog,String apkUrl){
        OkGo.<File>get(apkUrl+versionData.getDownUrl())//
                .tag(context)//
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
                        progressDialog.setProgress(value);
                    }
                    @Override
                    public void onError(Response<File> response) {
                        super.onError(response);
                        tipsDownApk("下载出错,如重试无法更新请到官网(http://www.1av.co)下载最新版本!");
                    }
                });
    }

    //成功
    private void onFileSuccess(File file){
        //保存到本地  82599dd9f6bc8adedae1a0949a1da380
        String value= Md5Util.getMD5(file);//MD5Encoder.getFileMD5(file);
        //String xStr=Md5Util.getMD5(file);
        Log.e("TAG","下载好了:MD5::"+value);
        //校验md5 码
        if(!versionData.getMd5().equals(value)){
            Log.e("TAG","校验码不正确,可能被串改,请重新下载");
            tipsDownApk("下载出错,重新下载!!!");
            return;
        }
        String pathStr=file.getAbsolutePath();
//        //安装
        installApk(pathStr);
    }
    private void installApk(String pathStr){
        InstallUtils.installAPK(context, pathStr,
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

}
