package com.xcore.utils;

import android.text.TextUtils;
import android.util.Log;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.List;

public class TJave {
    private List<String> urlList;
    private INextJave iNextJave;
    private int currentIndex=0;
    private boolean isOk=false;

    public TJave(List<String> urls, INextJave nextJave){
        this.urlList=urls;
        this.iNextJave=nextJave;
    }
    //返回时下一步操作
    public void next(String path){
        Log.e("TAG",path);
        this.currentIndex++;
        if(!isEmpty(path)&&isOk==false){//有地址,请求成功了
            isOk=true;
            if(iNextJave!=null){
                iNextJave.onNext(path);
            }
        }else{
            if(isEmpty(path)&&isOk==false&&this.currentIndex>=this.urlList.size()-1){
                if(iNextJave!=null){
                    iNextJave.onNext(path);
                }
            }
        }
    }
    //开始请求网络
    public void start(){
        if(urlList.size()<=0){
            this.next("");
            return;
        }
        for(String url:urlList){
            tSpeed(url);
        }
    }
    //测速
    private void tSpeed(final String pathUrl){
        OkGo.<String>get(pathUrl).retryCount(0).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                next(pathUrl);
            }
            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                try {
                    String msg = response.getException().getMessage();//.message();
                    if (TextUtils.isEmpty(msg)) {
                        msg = "请求出错";
                    }
                    String bodyStr=response.body();
                    if(!TextUtils.isEmpty(bodyStr)){
                        msg+="|BODY="+bodyStr;
                    }
                    msg += "|API_URL=" + pathUrl;
                    msg += "|API_ERROR=API_测速";
                    Log.e("TAG",msg);
                    LogUtils.apiRequestError(pathUrl, msg);
                }catch (Exception e){}
                finally {
                    next("");
                }
            }
        });
    }

    private boolean isEmpty(String msg){
        return msg==null||msg.length()<=0;
    }
    public interface INextJave{
        void onNext(String path);
    }

}
