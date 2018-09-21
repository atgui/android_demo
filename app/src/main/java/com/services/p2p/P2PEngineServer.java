package com.services.p2p;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.common.BaseCommon;
import com.services.BaseServer;
import com.xcore.MainApplicationContext;
import com.xcore.P2PTransActivity;

import java.io.File;

import cn.dolit.p2ptrans.P2PTrans;

/**
 * 启动 p2p 服务
 */
public class P2PEngineServer extends BaseServer {

//    @Override
//    public void onCreate() {
//        super.onCreate();
//        startForeground(1,new Notification());
//    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new P2PBander();
    }

    public class P2PBander extends Binder{
        public P2PEngineServer getService(){
            return P2PEngineServer.this;
        }
    }

    @Override
    public void initServer() {
        final Handler runHandle = new Handler(){
            @Override
            public void handleMessage( Message msg) {
            super.handleMessage(msg);
            if (msg.what == 2301) {
                Log.e("TAG","播放服务启动失败,请关闭后重新打开app");
                Toast.makeText(MainApplicationContext.context,"播放服务启动失败,请关闭后重新打开app",
                        Toast.LENGTH_SHORT).show();
                MainApplicationContext.isStartServer=false;
            } else if (msg.what == 2300) {
                MainApplicationContext.serverSuccess=true;
                Log.e("TAG","服务器启动成功");
                //Toast.makeText(MainApplicationContext.context,"启动服务成功",Toast.LENGTH_SHORT).show();
            }
            }
        };
        //启动服务
        new Thread( new Runnable(){
            public void run()
            {
                int i = 0;
                for( ; i < 10; i++)
                {
                    int ret = P2PTrans.run( "", BaseCommon.P2P_SERVER_PORT );
                    Log.e("TAG", "RunServer ret:" + ret);
                    if ( ret != 0){
                        BaseCommon.P2P_SERVER_PORT++;
                    }
                    else
                    {
                        Message msg = new Message();
                        runHandle.sendMessage( msg);
                        break;
                    }
                }
                if ( i == 10){
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("error","启动服务器失败");
                    msg.setData(data);
                    runHandle.sendMessage( msg);
                }
            }
        }).start();
        //createDir();
        //创建缓存目录
        new AsyncTask<Void, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                // 设置下载目录
                File file = new File(MainApplicationContext.SD_PATH);
                if (!file.exists()) {
                    try {
                        //按照指定的路径创建文件夹
                        file.mkdirs();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        return false;
                    }
                }

                if (!file.exists()) {
                    return false;
                }

                for (int i = 0; i < 10; i++) {
                    SystemClock.sleep(1000);
                    if (!P2PTrans.testStream(BaseCommon.P2P_SERVER_PORT)) {
                        continue;
                    }
                    if (!P2PTrans.enableStream(BaseCommon.P2P_SERVER_PORT, file.getPath(), file.getPath(), "8aaf88937afbd0826b2c6c3c5d4e01b3")) {
                        continue;
                    }
                    return true;
                } // end of for
                return false;
            }

            @Override
            protected void onPostExecute(Boolean succeed) {
                if (succeed == true) {
                    runHandle.sendEmptyMessage(2300);
                } else {
                    runHandle.sendEmptyMessage(2301);
                }
            }
        }.execute();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
