package com.xcore.utils;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.module.GlideModule;

public class MyGlideModule implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // 防止图片变绿，在有ALPHA通道的情况下
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);

        //设置磁盘缓存大小
        int size = 1000 * 1024 * 1024;
        String dirFolder = "img_cache";//MainApplicationContext.IMAGE_PATH;
        //设置磁盘缓存
        builder.setDiskCache(new ExternalCacheDiskCacheFactory(context,dirFolder, size));

        //builder.setDiskCache(new DiskLruCacheFactory(dirFolder, dirName,size));

    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        // 设置长时间读取和断线重连
//        OkHttpClient client =OkHttpUtils.getClient();
//                new OkHttpClient.Builder()
//                .connectTimeout(60, TimeUnit.SECONDS)
//                .readTimeout(50, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true).build();
//        glide.register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(client));
    }
}
