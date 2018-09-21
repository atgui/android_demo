package com.xcore.utils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class OkHttpUtils {
    private static OkHttpClient client;
    static int size = 1000 * 1024 * 1024;

    public static OkHttpClient getClient() {
//        String path= MainApplicationContext.IMAGE_PATH;
//        Log.e("TAG","OkHttp 缓存目录");
        if (client == null) {
            synchronized (OkHttpUtils.class) {
                if (client == null) {
                    client = new OkHttpClient.Builder()
                            .retryOnConnectionFailure(true)
                            .connectTimeout(60, TimeUnit.SECONDS)
                            .readTimeout(30,TimeUnit.SECONDS)
                            //设置缓存
                            //.cache(new Cache(new File(path),size))
                          .build();
                }
            }
        }
        return client;
    }
}
