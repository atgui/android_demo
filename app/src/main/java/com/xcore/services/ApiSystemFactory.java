package com.xcore.services;

import android.util.Base64;
import android.util.Log;
import com.common.BaseCommon;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.xcore.MainApplicationContext;
import com.xcore.data.bean.JsonDataBean;
import com.xcore.data.bean.SpeedDataBean;
import com.xcore.data.bean.TokenBean;
import com.xcore.data.utils.AGCallback;
import com.xcore.data.utils.DataUtils;


public class ApiSystemFactory {
    private SystemApiService apiService=new SystemApiService();

    private ApiSystemFactory(){}

    private static ApiSystemFactory instance;
    public static ApiSystemFactory getInstance(){
        if(instance==null){
            instance=new ApiSystemFactory();
        }
        return instance;
    }

    /**
     * 速度测试
     * @param apiUrl
     * @param callback
     */
    public void getSpeed(String apiUrl,AGCallback<SpeedDataBean> callback){
        String url=apiUrl+"api/v1/system/GetSpeed";
        apiService.<SpeedDataBean>get(url,callback);
    }

    /**
     * 测试速度
     * @param url
     * @param callback
     */
    public void getTestSpeed(String url,AGCallback<String> callback){
        apiService.<String>get(url,callback);
    }

    /**
     * 得到json
     */
    public void getJson(String jsonUrl, final AGCallback<JsonDataBean> callback){
        String url=jsonUrl+"data.json";
        apiService.get(url,callback);
    }

    /**
     * 刷新 TOKEN
     * @param tokenBean
     */
    public void refreshToken(TokenBean tokenBean){
        String url= BaseCommon.API_URL+"api/token";
        String basic= Base64.encodeToString("NEWONEAV:ADMIN".getBytes(),Base64.DEFAULT).trim();

        HttpHeaders headers=new HttpHeaders();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Basic "+basic);

        HttpParams params=new HttpParams();
        params.put("grant_type","refresh_token");
        params.put("refresh_token",tokenBean.getRefresh_token());

        apiService.<TokenBean>post(url, new AGCallback<TokenBean>() {
            @Override
            public void onNext(TokenBean tokenBean) {
                TokenBean tokenBean1= DataUtils.tokenBean;
                tokenBean1.setExpires_in(tokenBean.getExpires_in());
                tokenBean1.setAccess_token(tokenBean.getAccess_token());
                if(tokenBean.getRefresh_token()!=null){
                    tokenBean1.setRefresh_token(tokenBean.getRefresh_token());
                }
//                tokenBean1.setExpires_in(60);
                tokenBean1.setToken_time(System.currentTimeMillis());
                MainApplicationContext.TOKEN=tokenBean1.getAccess_token();

                //重置全局请求头
                HttpHeaders headers=new HttpHeaders();
                headers.put("Cache-Control","no-cache");
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("Authorization","Bearer "+tokenBean1.getAccess_token());
                OkGo.getInstance().addCommonHeaders(headers);
            }
        }, params, headers);
    }
    //得到token
    public <T> void getToken(AGCallback<T> callback, String... args) {
        String url=BaseCommon.API_URL+"api/token";
        HttpParams params=new HttpParams();
        String basic= Base64.encodeToString("NEWONEAV:ADMIN".getBytes(),Base64.DEFAULT).trim();
        params.put("grant_type","password");
        params.put("username",args[0]);
        params.put("password",args[1]);

        HttpHeaders headers=new HttpHeaders();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Basic "+basic);

        apiService.post(url,callback,params,headers);
    }
    //得到版本信息
    public <T> void getVersion(AGCallback<T> callback) {
        String url= BaseCommon.API_URL+"api/v1/system/getVersion";
        apiService.<T>get(url,callback);
    }
    //得到CDN
    public <T> void getCdn(AGCallback<T> callback) {
        String url= BaseCommon.API_URL+"api/v1/system/getCdn";
        apiService.<T>get(url,callback);
    }
    //注册
    public <T> void toRegister(AGCallback<T> callback,HttpParams params){
        String url= BaseCommon.API_URL+"api/v1/appuser/registered";
        apiService.<T>post(url,callback,params,null);
    }
    //检查热更
    public <T> void getHotUpdate(AGCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/system/getHotUpdate";
        apiService.<T>get(url,callback);
        Log.e("TAg","请求热更");
    }

    //获取游客信息
    public <T> void getGuest(HttpParams params,AGCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/appuser/touristsLanding";
        apiService.<T>post(url,callback,params,null);
    }

}
