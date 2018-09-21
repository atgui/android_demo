package com.xcore.tinker;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.lzy.okgo.OkGo;
import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.loader.app.DefaultApplicationLike;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.xcore.MainApplicationContext;
import com.xcore.utils.CrashHandler;


@SuppressWarnings("unused")
@DefaultLifeCycle(
        application = "com.xcore.MainApplication",
        flags = ShareConstants.TINKER_ENABLE_ALL,
        loadVerifyFlag = false
)
public class CustomerTinkerLike extends DefaultApplicationLike {

    public CustomerTinkerLike(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag,
                                 long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }

//    @Override
//    public void onCreate() {
//        super.onCreate();
//        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//        StrictMode.setVmPolicy(builder.build());
//        builder.detectFileUriExposure();
//    }

    /**
     * install multiDex before install tinker
     * so we don't need to put the tinker lib classes in the main dex
     *
     * @param base
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);

        Log.e("TAG","初始化。。。");

        //you must install multiDex whatever tinker is installed!
        MultiDex.install(base);

        MainApplicationContext.application= getApplication();
        MainApplicationContext.context=base;
        MainApplicationContext.onCreate();

        TinkerManager.installTinker(this);

        //初始化异常信息
        CrashHandler.getInstance().init(base);

//        Recovery.getInstance()
//            .debug(true)
//            .recoverInBackground(false)
//            .recoverStack(true)
//            .mainPage(RunActivity.class)
//            .recoverEnabled(true)
//            .callback(new MyCrashCallback())
//            .silent(false, Recovery.SilentMode.RESTART)
//            //.skip(TestActivity.class)
//            .init(base);

//        initOkHttp();
    }

    //初始化 okgo
    private void initOkHttp(){
        OkGo.getInstance().init(getApplication());
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {
        getApplication().registerActivityLifecycleCallbacks(callback);
        Log.e("TAG","这个是怎么回调的。。。");
    }


}
