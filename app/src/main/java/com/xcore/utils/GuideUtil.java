package com.xcore.utils;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.xcore.MainApplicationContext;
import com.xcore.cache.CacheManager;
import com.xcore.data.bean.CategoriesBean;
import com.xcore.data.bean.NoticeAlertBean;
import com.xcore.data.utils.AGCallback;
import com.xcore.services.ApiFactory;
import com.xcore.ui.activity.GuideActivity;
import com.xcore.ui.activity.XMainActivity;

public class GuideUtil {
    public static String GUIDE_KEY="guide_password";
    public static String GUIDE_VALUE="guide_yes";

    public static void show(Activity activity,View view){
        try {
            //判断是否要显示引导
            String value=CacheManager.getInstance().getLocalHandler().get(GUIDE_KEY);
            if(!TextUtils.isEmpty(value)){
                MainApplicationContext.getNotice();
                return;
            }

            Intent intent=new Intent(activity, GuideActivity.class);
            activity.startActivity(intent);
            CacheManager.getInstance().getLocalHandler().put(GUIDE_KEY, GUIDE_VALUE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
