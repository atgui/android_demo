package com.xcore;

import com.xcore.data.utils.AGCallback;
import com.xcore.services.ApiFactory;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 心跳
 */
public class HeartTest {
    private Timer heartTimer;

    public void start(){
        //心跳
        long hTimer=MainApplicationContext.HEAT_TIMER;
        if(hTimer>0){
            heartTimer=new Timer();
            heartTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    toHeart();
                }
            },5000,hTimer);
        }
    }
    //心跳
    private void toHeart(){
        ApiFactory.getInstance().<String>toHeart(new AGCallback<String>() {
            @Override
            public void onNext(String s) {
                //Log.e("TAG","心跳:"+s);
            }
        });
    }
    public void destroy(){
        if(heartTimer!=null){
            heartTimer.cancel();
        }
        heartTimer=null;
    }
}
