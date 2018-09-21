package com.xcore.services;

import android.content.Context;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.request.PostRequest;
import com.xcore.MainApplicationContext;
import com.xcore.data.utils.AGCallback;
import com.xcore.utils.SystemUtils;

/**
 * 请求统一从这里发出去
 */
public class SystemApiService {
    public <T>void get(String url, AGCallback<T> callback){
        int index=url.indexOf("?");
        Context context=MainApplicationContext.context;
        String v= SystemUtils.getVersion(context);
        if(index>-1){
            url+="&version="+v;
        }else{
            url+="?version="+v;
        }
        OkGo.<T>get(url).execute(callback);
    }

    public <T>void post(String url, final AGCallback<T> callback, HttpParams params, HttpHeaders headers){
        PostRequest<T> postRequest= OkGo.<T>post(url);
        if(headers!=null){
            postRequest.headers(headers);
        }
        Context context=MainApplicationContext.context;
        String v=SystemUtils.getVersion(context);
        if(params==null){
            params=new HttpParams();
        }
        if(params!=null){
            params.put("version",v);
            postRequest.params(params);
        }
        postRequest.execute(callback);
    }
}
