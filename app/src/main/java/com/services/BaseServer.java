package com.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public abstract class BaseServer extends Service {
    public static String TAG="SERVICE";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.initServer();
    }

    public abstract void initServer();
}
