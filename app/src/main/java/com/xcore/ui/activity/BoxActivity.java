package com.xcore.ui.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.base.MvpActivity;
import com.xcore.data.bean.BoxBean;
import com.xcore.presenter.BoxPresenter;
import com.xcore.presenter.view.BoxView;

public class BoxActivity extends MvpActivity<BoxView,BoxPresenter> implements BoxView {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_box;
    }

    @Override
    public String getParamsStr() {
        return "宝箱页面";
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        View v=findViewById(R.id.toolbar_layout);
        Toolbar toolbar=v.findViewById(R.id.toolbar1);
        toolbar.setBackgroundColor(getResources().getColor(R.color.color_translucent));

        setEdit("");
        setTitle("宝箱福利");

        findViewById(R.id.btn_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOpen==false){
                    return;
                }
                presenter.getOpen();
            }
        });
        ImageView aniImage=findViewById(R.id.animal_box);
        aniImage.setBackgroundResource(R.drawable.receive_no);

        setIsOpen(false);
    }
    //时间转换
    private String formatTime(long totalTime) {
        long hour = 0;
        long minute = 0;
        long second = totalTime / 1000;
        if (totalTime <= 1000 && totalTime > 0) {
            second = 1;
        }
        if (second > 60) {
            minute = second / 60;
            second = second % 60;
        }
        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        // 转换时分秒 00:00:00
        String str="";
        if(hour>=10){
            str=hour+":";
        }else{
            str="0"+hour+":";
        }
        if(minute>=10){
            str+=minute+":";
        }else{
            str+="0"+minute+":";
        }
        if(second>=10){
            str+=second+"";
        }else{
            str+="0"+second+"";
        }
        return str;
    }
    @Override
    protected void initData() {
        presenter.getBoxTime();
    }

    private boolean isOpen=false;
    private void setIsOpen(boolean value){
        isOpen=value;
        TextView v=findViewById(R.id.btn_open);
        ImageView aniImage=findViewById(R.id.animal_box);
        if(isOpen==false){
            String infoTxt="<font size='28' color='#666666'>距宝箱开启时间还有</font><br/>" +
                    "<font size='32' color='#000000'>00:00:00</font>";
            v.setText(Html.fromHtml(infoTxt));
            v.setBackgroundResource(R.drawable.receive_btn_no);
        }else{
            v.setText("");
            v.setBackgroundResource(R.drawable.receive_btn_yes);
            aniImage.setBackgroundResource(R.drawable.box_animation);
        }
    }
//    弹窗显示
    private void show(int count){
        restart();
        Intent intent=new Intent(this, TipsPopupActivity.class);
        intent.putExtra("content","恭喜获得"+count+"次观影次数");
        startActivity(intent);
    }
    public void _slRunAnimation(final int data){
        ImageView aniImage=findViewById(R.id.animal_box);
        aniImage.setBackgroundResource(R.drawable.box_animation);
        AnimationDrawable animationDrawable = (AnimationDrawable) aniImage.getBackground();
        animationDrawable.start();

//        animationDrawable.isRunning();
        int duration = 0;
        for(int i=0;i<animationDrawable.getNumberOfFrames();i++){
            duration += animationDrawable.getDuration(i);
        }
        duration+=300;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                show(data);
            }
        },Long.valueOf(duration));

        TextView v=findViewById(R.id.btn_open);
        v.setBackgroundResource(R.drawable.receive_btn_no);
        //请求时间
        presenter.getBoxTime();
    }
    CountDownTimer timer;
    //时间器运行
    private void _slRun(int t){
        if(t<=0){
            setIsOpen(true);
            return;
        }

        if(timer!=null){
            timer.cancel();
        }
       timer=new CountDownTimer(Long.valueOf(t)*1000,1000){
            @SuppressWarnings("deprecation")
            @Override
            public void onTick(long millisUntilFinished) {
                String timeStr=formatTime(millisUntilFinished);
                String infoTxt="<font size='28' " +
                        "color='#666666'>距宝箱开启时间还有</font><br/>" +
                        "<font size='32' color='#000000'>"+timeStr+"</font>";
                ((TextView)findViewById(R.id.btn_open)).setText(Html.fromHtml(infoTxt));
            }
            @Override
            public void onFinish() {
                timer.cancel();
                timer=null;
                setIsOpen(true);
            }
        };
        timer.start();
    }

    private void restart(){
        if(timer==null){
            return;
        }
        ImageView aBox=findViewById(R.id.animal_box);
        aBox.setBackgroundResource(R.drawable.receive_no);
        setIsOpen(false);
    }

    @Override
    public BoxPresenter initPresenter() {
        return new BoxPresenter();
    }

    @Override
    public void onGetTimeResult(BoxBean boxBean) {
        Log.e("TAG",boxBean.toString());
        _slRun(boxBean.getData());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100&&resultCode==100){
            //restart();
        }
    }

    @Override
    public void onGetOpenBoxResult(BoxBean boxBean) {
        Log.e("TAG",boxBean.toString());
        _slRunAnimation(boxBean.getData());
    }

}
