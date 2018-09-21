package com.xcore.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.xcore.MainApplicationContext;

public class NetWorkUtils {
    /*
     *  判断网络是否可用
     */
    public static boolean isNetWorkAvailable() {
        Context context= MainApplicationContext.context;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
