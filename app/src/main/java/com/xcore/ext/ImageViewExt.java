package com.xcore.ext;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;
import com.bumptech.glide.request.target.Target;
import com.xcore.R;
import com.xcore.utils.CacheFactory;

@SuppressLint("AppCompatCustomView")
public class ImageViewExt extends android.widget.ImageView {
    public ImageViewExt(Context context) {
        super(context);
    }

    public ImageViewExt(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewExt(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ImageViewExt(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    //加载图片
    public void loadUrl(String url){
//        final ViewPropertyAnimation.Animator animationObject = new ViewPropertyAnimation.Animator() {
//            @Override
//            public void animate(View view) {
//                view.setAlpha( 0f );
//                ObjectAnimator fadeAnim = ObjectAnimator.ofFloat( view, "alpha", 0f, 1f );
//                fadeAnim.setDuration( 500 );
//                fadeAnim.start();
//            }
//        };
//        if(url==null||(url!=null&&url.equals("null"))){
//            return;
//        }
        CacheFactory.getInstance().getImage(getContext(),this,url);
//        Log.e("TAG","加载图片地址:"+url);
//        Glide.with(getContext())
//                .load(url)
//                .asBitmap()
//                .placeholder(R.drawable.network_load)
//                .skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.RESULT)//不使用缓存
//                .listener(new RequestListener<String, Bitmap>() {
//                    @Override
//                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
//                        //e.printStackTrace();
//                        String msg=e.getLocalizedMessage();
//                        Log.e("TAG","图片加载出错:"+msg);
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        return false;
//                    }
//                })
//                .animate(animationObject)
//                .into(this);
    }

}
