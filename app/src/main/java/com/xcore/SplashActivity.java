package com.xcore;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.common.BaseCommon;
import com.google.gson.Gson;
import com.xcore.base.MvpActivity;
import com.xcore.cache.CacheManager;
import com.xcore.data.bean.CdnBean;
import com.xcore.data.bean.GuestBean;
import com.xcore.data.bean.JsonDataBean;
import com.xcore.data.bean.RegisterBean;
import com.xcore.data.bean.TokenBean;
import com.xcore.data.bean.VersionBean;
import com.xcore.data.utils.DataUtils;
import com.xcore.presenter.SplashPresenter;
import com.xcore.presenter.view.SplashView;
import com.xcore.ui.activity.AppUpdateActivity;
import com.xcore.ui.activity.LockLoginActivity;
import com.xcore.ui.activity.LoginActivity;
import com.xcore.ui.activity.XMainActivity;
import com.xcore.utils.SystemUtils;

import java.util.List;


public class SplashActivity extends MvpActivity<SplashView,SplashPresenter> implements SplashView {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        TextView txtVersion= findViewById(R.id.txt_version);
        String v=SystemUtils.getVersion(this);
        txtVersion.setText(v);

        //初始化本地缓存信息
        CacheManager.getInstance().init();
    }

    @Override
    protected void initData() {
        presenter.getJson();
    }
        @Override
    public void onBack() {

    }
    @Override
    public SplashPresenter initPresenter() {
        return new SplashPresenter();
    }

    @Override
    public void onJsonResult(JsonDataBean jsonDataBean) {
        if(jsonDataBean==null){//直接到官网
            //从其他浏览器打开
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri content_url = Uri.parse("http://www.1av.co");
            intent.setData(content_url);
            startActivity(Intent.createChooser(intent, "请选择浏览器"));
            return;
        }

//        BaseCommon.apiList.add("http://192.168.8.217:81/");
        try{
            if(!TextUtils.isEmpty(jsonDataBean.getApi())){
                BaseCommon.apiList.add(jsonDataBean.getApi());
            }
        }catch (Exception e){}
        try{
            if(!TextUtils.isEmpty(jsonDataBean.getApi1())){
                BaseCommon.apiList.add(jsonDataBean.getApi1());
            }
        }catch (Exception e){}
        try{
            if(!TextUtils.isEmpty(jsonDataBean.getApi2())){
                BaseCommon.apiList.add(jsonDataBean.getApi2());
            }
        }catch (Exception e){}
        try{
            if(!TextUtils.isEmpty(jsonDataBean.getApi3())){
                BaseCommon.apiList.add(jsonDataBean.getApi3());
            }
        }catch (Exception e){}
        try{
            if(!TextUtils.isEmpty(jsonDataBean.getApi4())){
                BaseCommon.apiList.add(jsonDataBean.getApi4());
            }
        }catch (Exception e){}
        try{
            if(!TextUtils.isEmpty(jsonDataBean.getApi5())){
                BaseCommon.apiList.add(jsonDataBean.getApi5());
            }
        }catch (Exception e){}
        try{
            if(!TextUtils.isEmpty(jsonDataBean.getApi6())){
                BaseCommon.apiList.add(jsonDataBean.getApi6());
            }
        }catch (Exception e){}

        presenter.apiSpeed();
//        presenter.getSpeed();
//        presenter.getCdn();
    }

    @Override
    public void onCdnResult(CdnBean cdnBean) {
        List<CdnBean.CdnDataItem> apiCdnDataItems= cdnBean.getData().getApiList();
        //检测哪个 cdn 最快
//        if(apiCdnDataItems!=null&&apiCdnDataItems.size()>0){
//            BaseCommon.API_URL=apiCdnDataItems.get(0).getUrl();
//        }
        List<CdnBean.CdnDataItem> imgCdnDataItems= cdnBean.getData().getImageList();
        if(imgCdnDataItems!=null&&imgCdnDataItems.size()>0){
            for(CdnBean.CdnDataItem item:imgCdnDataItems){
                BaseCommon.resLists.add(item.getUrl());
            }
        }
        //BaseCommon.resLists.add("http://pic01.xxxlutubexxx1.com/");

        List<CdnBean.CdnDataItem> torrentCdnDataItems= cdnBean.getData().getTorrentList();
        if(torrentCdnDataItems!=null&&torrentCdnDataItems.size()>0){
            for(CdnBean.CdnDataItem item:torrentCdnDataItems){
                BaseCommon.videoLists.add(item.getUrl());
            }
        }

        List<CdnBean.CdnDataItem> httpCdnDataItems= cdnBean.getData().getHttpAccelerateList();
        if(httpCdnDataItems!=null&&httpCdnDataItems.size()>0){
            for(CdnBean.CdnDataItem item:httpCdnDataItems){
                BaseCommon.httpAccelerateLists.add(item.getUrl());
            }
        }

        List<CdnBean.CdnDataItem> apkCdnDataItems= cdnBean.getData().getApkDownList();
        if(apkCdnDataItems!=null&&apkCdnDataItems.size()>0){
            for(CdnBean.CdnDataItem item:apkCdnDataItems){
                BaseCommon.apkLists.add(item.getUrl());
            }
        }
        BaseCommon.testUrlMaps=cdnBean.getData().getTestUrlList();

        //先请求版本,没有更新的再测试速度
        presenter.getVersion();
        //先测试资源的速度
//        presenter.getResSpeed();
//        //得到cdn信息
//        presenter.getVersion();
    }

    @Override
    public void onVersionResult(VersionBean versionBean) {
        int code= SystemUtils.getVersionCode(this);
        long hearTimer=versionBean.getData().getHeartbeatTime();
        MainApplicationContext.HEAT_TIMER=hearTimer;
        if (versionBean.getData().getInsideVersion() > code && versionBean.getData().getRemark().indexOf("ag_debug") < 0) {
            //有更新
            Intent intent = new Intent(this, AppUpdateActivity.class);
            String versionBeanStr = new Gson().toJson(versionBean);
            intent.putExtra("versionBean", versionBeanStr);
            startActivity(intent);
            return;
        }else{//没有更新
            presenter.xxSpeed();
        }
    }

    @Override
    public void onGetGuest(GuestBean guestBean) {
        try {
            boolean t = guestBean.getData().isTourist();
            presenter.getLogin(guestBean.getData().getName(), guestBean.getData().getPassword(), true, t);
        }catch (Exception e){}
    }

    @Override
    public void getTokenResult(TokenBean tokenBean) {
        if(!TextUtils.isEmpty(tokenBean.getError())|| !TextUtils.isEmpty(tokenBean.getError_description())){
            toast("用户名或密码错误,请重新输入!!!");
//            dialog.cancel();

            //到登录去
            Intent intent=new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        //
//        MainApplicationContext.TOKEN=tokenBean.getAccess_token();
//        MainApplicationContext.initOkGo();

//        presenter.getHome();
       // onFinalResult();
        //presenter.getTags();
    }

    @Override
    public void getRegisterResult(RegisterBean registerBean) {
    }

    @Override
    public void onError(String msg) {
        //到登录
        gotoActivity(LoginActivity.class,true);
    }

    public void onFResult(){
        //这里开启手势判断
        Intent intent=new Intent(this,LockLoginActivity.class);
        intent.putExtra("lock_type","0");
        startActivity(intent);
//        Toast.makeText(SplashActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onFinalResult() {
        //初始化缓存
        CacheManager.getInstance().initDown();

        //打开一个activity 关闭当前的
        gotoActivity(XMainActivity.class,true);
        MainApplicationContext.removeActivity(this);
        finish();
    }

//    private boolean isToCdn=false;
//    private int apiIndex=0;
    @Override
    public void onGetSpeedResult(String apiPath) {
//        apiIndex++;
////        if(isToCdn==false) {
////            if(apiIndex<BaseCommon.apiList.size()-1&&apiPath.equals("")){
////                isToCdn=false;
////            }else {
////                isToCdn = true;
////            }
////            if(!apiPath.equals("")) {
////                BaseCommon.API_URL = apiPath;
////            }
////            if(isToCdn==true){
////                presenter.getCdn();
////            }
////        }
    }

//    private boolean isResCdn=false;
//    private int resIndex=0;
    /**
     * 得到资源最快的
     * @param resPath
     */
    @Override
    public void onGetResSpeedResult(String resPath) {
//        resIndex++;
//        if(isResCdn==false) {
//            if(resIndex<BaseCommon.resLists.size()-1&&resPath.equals("")){
//                isResCdn=false;
//            }else {
//                isResCdn = true;
//            }
//            if(!resPath.equals("")) {
//                BaseCommon.RES_URL = resPath;
//            }
//            if(isResCdn==true) {
//                presenter.getTorSpeed();
//            }
//        }
    }
//    private boolean isTorCnd=false;
////    private int torIndex=0;
    /**
     * 得到种子最快的
     * @param torPath
     */
    @Override
    public void onGetTorrentSpeedResult(String torPath) {
//        torIndex++;
//        if(isTorCnd==false) {
//            if(torIndex<BaseCommon.videoLists.size()-1&&torPath.equals("")){
//                isTorCnd=false;
//            }else {
//                isTorCnd = true;
//            }
//            if(!torPath.equals("")) {
//                BaseCommon.VIDEO_URL = torPath;
//            }
//            if(isTorCnd==true) {
//                //得到版本信息
//                presenter.getVersion();
//            }
//        }
    }

    @Override
    public void onSpeedFinalResult() {
        //没有大版本更新,检查热更   热更新暂时先不用了。。。
        //new HotServer().initServer();

        String accountStr=CacheManager.getInstance().getLocalHandler().get(DataUtils.SAVE_ID);
        if(accountStr.isEmpty()) {//没有游客账号,得到游客账号
            presenter.getGuest(SystemUtils.getVersion(this));
            return;
        }

        int splitIndex=accountStr.indexOf("|");
        String uname=accountStr.substring(0,splitIndex);
        int endIndex=accountStr.lastIndexOf("|");
//
        if(splitIndex<endIndex&&splitIndex+1<endIndex) {//后面有判断
            try {
                String upass = accountStr.substring(splitIndex + 1, endIndex);
                String guestStr = accountStr.substring(endIndex + 1);
                if (guestStr.equals("ok")) {//是游客 直接登录
                    presenter.getLogin(uname, upass, true, true);
                }else if(TextUtils.isEmpty(guestStr)){
                    presenter.getLogin(uname, upass, true, false);
                } else {
                    onFResult();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            onFResult();
        }
    }


}
