package com.xcore.data.utils;

import android.text.TextUtils;
import android.util.Log;

import com.android.tu.loadingdialog.LoadingDailog;
import com.google.gson.Gson;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.request.base.Request;
import com.xcore.MainApplicationContext;
import com.xcore.data.BaseBean;
import com.xcore.data.bean.JsonDataBean;
import com.xcore.data.bean.TokenBean;
import com.xcore.services.ApiFactory;
import com.xcore.services.ApiSystemFactory;
import com.xcore.utils.LogUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import okhttp3.Response;

public abstract class AGCallback<T> extends AbsCallback<T> {

    private Type type;
    private Class<T> clazz;

    public AGCallback() {
    }

    public AGCallback(Type type) {
        this.type = type;
    }

    public AGCallback(Class<T> clazz) {
        this.clazz = clazz;
    }

    private long startTime=0;
    private String requstUrl="";

    @Override
    public void onStart(Request<T, ? extends Request> request) {
        super.onStart(request);
        startTime=System.currentTimeMillis();
        String baseUrl=request.getBaseUrl();
        HttpHeaders headers= request.getHeaders();
        requstUrl=request.getUrl();
        //request.headers("Content-Type", "application/x-www-form-urlencoded");
        Log.e("TAG","请求API:"+requstUrl);

        // 主要用于在所有请求之前添加公共的请求头或请求参数
        // 例如登录授权的 token
        // 使用的设备信息
        // 可以随意添加,也可以什么都不传
        // 还可以在这里对所有的参数进行加密，均在这里实现
//        request.headers("header1", "HeaderValue1")//
//                .params("params1", "ParamsValue1")//
//                .params("token", "3215sdf13ad1f65asd4f3ads1f");
    }

    //成功的统一回调
    public abstract void onNext(T t);

    //设置统一回调方式
    @Override
    public void onSuccess(com.lzy.okgo.model.Response<T> response) {
        //得到数据判断状态码 status 200 成功  500 错误 401 token 过期
        try {
            T xT=response.body();
            if(xT instanceof String){
                onNext(xT);
                return;
            }else if(xT instanceof TokenBean){
                onNext(xT);
                return;
            }else if(xT instanceof JsonDataBean){
                onNext(xT);
                return;
            }
            BaseBean b= (BaseBean) response.body();
            if(b.getStatus()==200){
                onNext(xT);

                //成功后发送时间到服务器
                long endTime=System.currentTimeMillis();
                long eT=endTime-startTime;
                Log.e("TAG","请求接口："+requstUrl+"时间="+eT);

            }else if(b.getStatus()==500){
                Log.e("TAG",b.getMessage());
                onError(response);
                MainApplicationContext.showips(b.getMessage(),null,"");
            }else if(b.getStatus()==403){//TOKEN 失效,刷新
                Log.e("TAG",b.getMessage());
                ApiSystemFactory.getInstance().refreshToken(DataUtils.tokenBean);
                onError(response);
            }else if(b.getStatus()==501){//资源权限拒绝
                Log.e("TAG",b.getMessage());
                onError(response);
                MainApplicationContext.showips(b.getMessage(),null,"");
            }else if(b.getStatus()==510){//缓存次数不足 ?
                Log.e("TAG",b.getMessage());
                onError(response);
                MainApplicationContext.showips(b.getMessage(),null,"");
            }else if(b.getStatus()==402){//跳到登录
                Log.e("TAG",b.getMessage());
                onError(response);
                MainApplicationContext.showips(b.getMessage(),null,"toLogin");
            }else{
                Log.e("TAG",b.getMessage());
                onError(response);
                MainApplicationContext.showips(b.getMessage(),null,"");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onError(com.lzy.okgo.model.Response<T> response) {
        super.onError(response);
        //在这里上传错误信息
        Log.e("TAG","请求出错 。。。");
        if(requstUrl.contains("ARR")){//如果是提交错误的接口出错了就不要再请求了
            return;
        }
        try {
            String msg =response.getException().getMessage();// response.body();
            if (TextUtils.isEmpty(msg)) {
                msg = "请求出错";
            }
//            T bodyStr=response.body();
//            if(!TextUtils.isEmpty(bodyStr)){
//                msg+="|BODY="+bodyStr;
//            }
            msg += "|API_URL=" + requstUrl;
            msg += "|API_ERROR=API_接口";
            LogUtils.apiRequestError(requstUrl, msg);
        }catch (Exception e){}
    }

    /**
     * 该方法是子线程处理，不能做ui相关的工作
     * 主要作用是解析网络返回的 response 对象,生产onSuccess回调中需要的数据对象
     * 这里的解析工作不同的业务逻辑基本都不一样,所以需要自己实现,以下给出的时模板代码,实际使用根据需要修改
     */
    @Override
    public T convertResponse(Response response) throws Throwable {
        // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用
        // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用
        // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用

        Log.e("TAG", "数据转换中..." );

        //详细自定义的原理和文档，看这里： https://github.com/jeasonlzy/okhttp-OkGo/wiki/JsonCallback
        if (type == null) {
            if (clazz == null) {
                Type genType = getClass().getGenericSuperclass();
                type = ((ParameterizedType) genType).getActualTypeArguments()[0];
            } else {
                JsonConvert<T> convert = new JsonConvert<>(clazz);
                return convert.convertResponse(response);
            }
        }
        JsonConvert<T> convert = new JsonConvert<>(type);
        return convert.convertResponse(response);
    }

}