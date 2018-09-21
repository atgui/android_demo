package com.xcore.utils;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;
import com.bumptech.glide.request.target.Target;
import com.xcore.R;

import java.io.File;
import java.util.Locale;

/**
 * 图片缓存类  请求全部从这里发出去
 */
public class CacheFactory{
    private CacheFactory(){}
    private static CacheFactory instance;
    public static CacheFactory getInstance(){
        if(instance==null){
            instance=new CacheFactory();
        }
        return instance;
    }

    //加载图片
    public void getImage(final Context context,final ImageView pic,final String url){
        final ViewPropertyAnimation.Animator animationObject = new ViewPropertyAnimation.Animator() {
            @Override
            public void animate(View view) {
                view.setAlpha( 0f );
                ObjectAnimator fadeAnim = ObjectAnimator.ofFloat( view, "alpha", 0f, 1f );
                fadeAnim.setDuration( 100 );
                fadeAnim.start();
            }
        };
        if(url==null||(url!=null&&url.equals("null"))){
            return;
        }
        final long startTime=System.currentTimeMillis();
        //Log.e("TAG","加载路径:"+url);
        Glide.with(pic.getContext())
            .load(url)
            .asBitmap()
            .placeholder(R.drawable.network_load)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.RESULT)//是否使用缓存
            .listener(new RequestListener<String, Bitmap>() {
                @Override
                public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                    //e.printStackTrace();
                    String msg=LogUtils.getException(e);// e.getLocalizedMessage();
                    //Log.e("TAG","图片加载出错:"+model+":::::"+msg);
                    long endTime=System.currentTimeMillis();
                    long eT=endTime-startTime;
                    //上传图片错误日志
                    try {
                        if (msg != null && msg.length() > 0) {
                            LogUtils.imageUp(msg,model,eT);
                        }
                    }catch (Exception e1){
                        //e.printStackTrace();
                    }
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    return false;
                }
            })
            .animate(animationObject)
            .fitCenter()
            .into(pic);
    }
}
