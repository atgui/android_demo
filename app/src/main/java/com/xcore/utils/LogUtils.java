package com.xcore.utils;

import android.util.Log;

import com.common.BaseCommon;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xcore.data.bean.LikeBean;
import com.xcore.data.bean.SpeedDataBean;
import com.xcore.data.utils.AGCallback;
import com.xcore.services.ApiFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * 日志上报
 */
public class LogUtils {
    //上报类型
    private static String IMAGE_TIME="IMAGE_TIME";
    private static String API_TIME="API_TIME";

    public static String IMAGE_ERROR="IMAGE_ERROR";
    public static String API_ERROR="API_ERROR";

    //图片出错上报
    public static void imageUp(String msg,String model,long eT){
        Log.e("TAG","图片错误信息:"+model);

        HttpParams params=new HttpParams();
        params.put("ResponseCode",404);
        params.put("RequestAddress",model);
        params.put("ResponseTime",eT);
        params.put("RequestMethod","GET");
        params.put("RequestParameter",model+"::"+msg);
        ApiFactory.getInstance().<SpeedDataBean>toError(params, new AGCallback<SpeedDataBean>() {
            @Override
            public void onNext(SpeedDataBean speedDataBean) {
                Log.e("TAG","上传图片日志:"+speedDataBean.toString());
            }
        });
    }
    //API 请求出错上报
    public static void apiRequestError(String model,String msg){
        Log.e("TAG","API请求出错:"+model);

        HttpParams params=new HttpParams();
        params.put("ResponseCode",404);
        params.put("RequestAddress",model);
        params.put("ResponseTime",0);
        params.put("RequestMethod","GET");
        params.put("RequestParameter",msg);
//        String vStr=SystemUtils.getFingerprint();
//        params.put("key_code",vStr);
        ApiFactory.getInstance().<SpeedDataBean>toError(params, new AGCallback<SpeedDataBean>() {
            @Override
            public void onNext(SpeedDataBean speedDataBean) {
                Log.e("TAG","API日志上传:"+speedDataBean.toString());
            }
        });
    }
    //视频加载出错上报
    public static void videoErrorUp(String model,String msg,int statusCode){
        Log.e("TAG","视频错误信息:"+model);

        HttpParams params=new HttpParams();
        params.put("ResponseCode",statusCode);
        params.put("RequestAddress",model);
        params.put("ResponseTime",0);
        params.put("RequestMethod","GET");
        params.put("RequestParameter",msg);
        ApiFactory.getInstance().<SpeedDataBean>toError(params, new AGCallback<SpeedDataBean>() {
            @Override
            public void onNext(SpeedDataBean speedDataBean) {
                Log.e("TAG","上传视频日志:"+speedDataBean.toString());
            }
        });
    }

    //视频加载出错上报
    public static void videoSubmitErrorUp(String model,String msg){
        Log.e("TAG","视频错误信息:"+model);

        HttpParams params=new HttpParams();
        params.put("ResponseCode",10001);
        params.put("RequestAddress",model);
        params.put("ResponseTime",0);
        params.put("RequestMethod","GET");
        params.put("RequestParameter",msg);
        ApiFactory.getInstance().<SpeedDataBean>toError(params, new AGCallback<SpeedDataBean>() {
            @Override
            public void onNext(SpeedDataBean speedDataBean) {
                Log.e("TAG","上传视频日志:"+speedDataBean.toString());
            }
        });
    }

    //程序异常出错上报
    public static void crashUp(final String msg){
        ApiFactory.getInstance().<LikeBean>toCrashLog(msg, new AGCallback<LikeBean>() {
            @Override
            public void onNext(LikeBean likeBean) {
                Log.e("TAG","上传日志:"+likeBean.toString());
            }
        });
        Log.e("TAG",msg);
    }

    /**
     * 得到具体的错误消息
     * @param ex
     * @return
     */
    public static String getException(Exception ex) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream pout = new PrintStream(out);
        ex.printStackTrace(pout);
        String ret =ex.getMessage();
        try {
            ret=new String(out.toByteArray());
            pout.close();
            out.close();
        } catch (Exception e) {
        }
        return ret;
    }
}
