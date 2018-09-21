package com.xcore.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xcore.R;
import com.xcore.data.bean.SpeedDataBean;
import com.xcore.data.utils.AGCallback;
import com.xcore.services.ApiFactory;
import com.xcore.utils.LogUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VideoSubmitErrorActivity extends Activity {

    EditText editNickname;
    Button btnSure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_submit_error);

        Intent intent=getIntent();
        final String shortId=intent.getStringExtra("shortId");
        final String url=intent.getStringExtra("playUrl");

        editNickname= findViewById(R.id.edit_nickname);
        btnSure=findViewById(R.id.btn_sure);

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSave(shortId,url);
            }
        });

        for(Integer i:resList){
            ImageView img=findViewById(i);
            imgList.add(img);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tapImage(v);
                }
            });
        }
    }
    private int currentIndex=-1;
    private void tapImage(View v){
        int ix=-1;
        for(ImageView img:imgList){
            ix++;
            if(v==img){
                img.setImageResource(R.drawable.radio_checked);
                currentIndex=ix;
            }else{
                img.setImageResource(R.drawable.radio_check);
            }
        }
    }

    private List<Integer> resList= Arrays.asList(R.id.radioBtn0,R.id.radioBtn1,R.id.radioBtn2);
    private List<String> resStr=Arrays.asList("无法播放","播放慢","其他");
    private List<ImageView> imgList=new ArrayList<>();

    //保存
    private void toSave(String sId,String url){
        if(currentIndex==-1){
            Toast.makeText(VideoSubmitErrorActivity.this,"请选择一个类型",Toast.LENGTH_SHORT).show();
            return;
        }
        String content=editNickname.getText().toString();
        content=content.trim();
        if(TextUtils.isEmpty(content)){
            Toast.makeText(VideoSubmitErrorActivity.this,"请输入上报错误内容",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(url)){
            url="";
        }
        if(TextUtils.isEmpty(sId)){
            sId="";
        }
        String typeStr="其他";
        try{
            typeStr=resStr.get(currentIndex);
        }catch (Exception e){}

        String msg="SOURCE_ID="+sId+"|URL="+url+"|ERROR_CONTENT="+content+"TYPE="+typeStr+"|INFO_ID=人工提交";

        HttpParams params=new HttpParams();
        params.put("ResponseCode",10001);
        params.put("RequestAddress",url);
        params.put("ResponseTime",0);
        params.put("RequestMethod","POST");
        params.put("RequestParameter",msg);
        ApiFactory.getInstance().<SpeedDataBean>toError(params, new AGCallback<SpeedDataBean>() {
            @Override
            public void onNext(SpeedDataBean speedDataBean) {
                Toast.makeText(VideoSubmitErrorActivity.this,"已上报,感谢您的反馈!",Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(Response<SpeedDataBean> response) {
                super.onError(response);
                Toast.makeText(VideoSubmitErrorActivity.this,"服务器忙,稍后重试!",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void toast(String msg){
        Toast.makeText(VideoSubmitErrorActivity.this,msg,Toast.LENGTH_SHORT).show();
        finish();
    }
}
