package com.xcore;

import android.content.Context;
import android.support.v4.provider.DocumentFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 下载器
 * by 幻梦泽
 * 功能：支持Http和SAF两种下载方式，SAF：Storage Access Framework 安卓存储访问框架
 */
public abstract class SuperDownloadManager {

    private static String TAG = SuperDownloadManager.class.getSimpleName();
    private final ExecutorService singleThreadExecutor;
    private Context ctx;
    private static boolean stop = false;


    public SuperDownloadManager(Context ctx){
        this.ctx = ctx;
        singleThreadExecutor = Executors.newSingleThreadExecutor(); //单线程线程池
    }

    /**开始下载*/
    public void startDownload(final String url, final String fileDir, final String fileName, final boolean isSAF){

        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                InputStream is = null;
                OutputStream os = null;

                try {
                    //设置为非停止状态
                    stop = false;

                    //输出流
                    long oldLength = 0;
                    if(isSAF){ //SAF模式
                        DocumentFile targetDocument = SAFUtils.createFile(ctx, fileDir, fileName);
                        os = ctx.getContentResolver().openOutputStream(targetDocument.getUri(), "wa");//write append
                        oldLength = targetDocument.length();
                    }else{ //Http模式
                        File file = new File(fileDir+"/"+fileName);
                        os = new FileOutputStream(file);
                        oldLength = file.length();
                    }

                    //发送下载请求，获得Response响应
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(url).header("RANGE", "bytes=" + oldLength + "-").build();
                    Response response = client.newCall(request).execute();

                    //输入流
                    is = response.body().byteStream();

                    //参数的声明
                    int len; //每次写入的大小
                    byte[] buf = new byte[2*1024]; //字符buf大小
                    long totalSize = response.body().contentLength(); //文件总大小
                    long tempSize = oldLength; //大小的临时值
                    long sendSize = oldLength; //最近一次发送时的大小
                    long sendTime = System.currentTimeMillis(); //最近一次发送的时间
                    long diffTime;//时间差
                    long diffSize;//文件大小差

                    //读写数据
                    while((len = is.read(buf)) != -1 && !stop){

                        //写数据
                        os.write(buf, 0, len);

                        //大小差
                        tempSize = tempSize + len;
                        diffSize = tempSize - sendSize;

                        //时间差
                        long tempTime = System.currentTimeMillis();
                        diffTime = tempTime - sendTime;

                        //发送信息（指定时间或下载完成时）
                        if(diffTime >= 1000 || tempSize == totalSize){
                            sendTime = tempTime;
                            sendSize = tempSize;
                            onProgress(sendSize, totalSize, sendSize * 1.0f / totalSize, diffSize / diffTime);
                            if(tempSize == totalSize){
                                onFinish();
                            }
                        }
                    }
                    os.flush();
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**暂停下载*/
    public void stopDownload(){
        stop = true;
    }

    /**实时进度回调*/
    public abstract void onProgress(long currentSize, long totalSize, float progress, long networkSpeed);

    /**下载完成回调*/
    public abstract void onFinish();

}
