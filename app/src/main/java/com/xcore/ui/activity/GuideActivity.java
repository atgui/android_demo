package com.xcore.ui.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.base.BaseActivity;
import com.xcore.ext.ImageViewExt;

import java.util.Arrays;
import java.util.List;

public class GuideActivity extends BaseActivity {
    private List<Integer> guides= Arrays.asList(R.drawable.guide_a,R.drawable.guide_b,R.drawable.guide_c,R.drawable.guide_d);
    ImageViewExt img;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_guide;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        img=findViewById(R.id.img_guide);
        current=0;
        int res=guides.get(current);
        img.setImageResource(res);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchImage();
            }
        });
    }

    @Override
    protected void initData() {

    }

    private int current;
    private void switchImage(){
        current++;
        if(current>=guides.size()){
            MainApplicationContext.getNotice();
            finish();
        }else{
            img.setImageResource(guides.get(current));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
