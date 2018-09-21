package com.xcore.tinker;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.app.ApplicationLike;

public class TinkerManager {
    private static ApplicationLike mAppLike;
    private static boolean isInstalled=false;

    public static void installTinker(ApplicationLike applicationLike){
        mAppLike=applicationLike;
        if(isInstalled){
            return;
        }
        TinkerInstaller.install(mAppLike);
        isInstalled=true;
    }

    public static void loadPatch(String path){
        Context context=getApplicationContext();
        if(Tinker.isTinkerInstalled()){
            //Toast.makeText(context,"开始patch",Toast.LENGTH_LONG).show();
            TinkerInstaller.onReceiveUpgradePatch(context,path);
        }else{
            //Toast.makeText(context,"没有初始化Tinker",Toast.LENGTH_LONG).show();
            Log.e("TAG","没有初始化Tinker");
        }
    }
    private static Context getApplicationContext(){
        if(mAppLike==null){
            return null;
        }
        return mAppLike.getApplication().getApplicationContext();
    }
}
