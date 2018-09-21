package com.xcore;

import android.text.TextUtils;
import android.util.Log;

import com.common.BaseCommon;
import com.lzy.okgo.model.HttpParams;
import com.xcore.data.bean.CdnBean;
import com.xcore.data.utils.AGCallback;
import com.xcore.services.ApiFactory;
import com.xcore.utils.LogUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Stack;

import javax.security.auth.login.LoginException;

/**
 * socket ip 端口测试
 */
public class JavaSocketTest {
    public void start(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000); // 休眠5秒
                    List<CdnBean.CdnDataItem> mps= BaseCommon.testUrlMaps;
                    if(mps!=null&&mps.size()>0) {
                        for (CdnBean.CdnDataItem item : mps) {
                            Thread.sleep(300);
                            toTest(item.getUrl(), item.getPort());
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void toTest(final String url, final Integer port){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String ipStr=getIP(getDomain(url));
                try {
                    if(TextUtils.isEmpty(ipStr)){
                        return;
                    }
                    connect(url,ipStr,port);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void connect(final String url,final String server, final int servPort){
        //1.创建一个Socket实例：构造函数向指定的远程主机和端口建立一个TCP连接
        //socket = new Socket(server, servPort);
        long startTime=System.currentTimeMillis();
        Socket socket = new Socket();

        int status=0;
        String error="";
        try {
            socket.setReceiveBufferSize(8192);
            socket.setSoTimeout(5000);// socket.setSoTimeout(2000);
            SocketAddress address = new InetSocketAddress(server, servPort);
            socket.connect(address, 5000);//1.判断ip、端口是否可连接
            status=1;
            Log.e("TAG1", "新建一个socket");
            Log.e("TAG1", "Connected to server... sending echo string");
        } catch (Exception e) {
            error=LogUtils.getException(e);
            if(TextUtils.isEmpty(error)){
                error="连接失败,错误消息null";
            }
        }
        finally {
            try{
                long endTime=System.currentTimeMillis();
                long eT=endTime-startTime;
                toSendServer(eT,server+":"+servPort,status,url,error);
                if(socket!=null) {
                    socket.close();
                    socket = null;
                }
            }catch (Exception ex){}
        }
        //2. 通过套接字的输入输出流进行通信：一个Socket连接实例包括一个InputStream和一个OutputStream，它们的用法同于其他Java输入输出流。
//        in = socket.getInputStream();
//        out = socket.getOutputStream();
//        isalreadyconnected=1;
        //connect1( server,  servPort) ;
    }

    private void toSendServer(long time,String ip,int status,String url,String error){
        HttpParams params = new HttpParams();
        params.put("sourceUrl", url);
        params.put("status", status);
        params.put("shortId", ip);
        params.put("key", String.valueOf(time));
        params.put("error", error);

        ApiFactory.getInstance().<String>toServerSocket(params, new AGCallback<String>() {
            @Override
            public void onNext(String s) {
                Log.e("TAG", "发送socket测试信息成功" + s);
            }
        });
    }
    /**
     * 获取url对应的域名
     *
     * @param url
     * @return
     */
    private String getDomain(String url) {
        String result = "";
        try {
            int j = 0, startIndex = 0, endIndex = 0;
            for (int i = 0; i < url.length(); i++) {
                if (url.charAt(i) == '/') {
                    j++;
                    if (j == 2)
                        startIndex = i;
                    else if (j == 3)
                        endIndex = i;
                }
            }
            result = url.substring(startIndex + 1, endIndex);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    private String getIP(String domain) {
        String ipAddress = "";
        InetAddress iAddress = null;
        try {
            iAddress = InetAddress.getByName(domain);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        if (iAddress == null)
            Log.i("xxx", "iAddress ==null");
        else {
            ipAddress = iAddress.getHostAddress();
        }
        return ipAddress;
    }
}
