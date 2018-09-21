package com.xcore.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xcore.R;
import com.xcore.cache.CacheManager;

public class TipsPopupActivity extends Activity {

    private String toLogin="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips_popup);

        final Intent intent=getIntent();
        String msg=intent.getStringExtra("content");
        String titleT=intent.getStringExtra("title");
        toLogin=intent.getStringExtra("toLogin");

        LinearLayout linearLayout=findViewById(R.id.btnLayout);
        LinearLayout linearLayout1=findViewById(R.id.btnLayout1);

        Button btnSure=findViewById(R.id.btn_sure);
        Button btnCancel=findViewById(R.id.btn_cancel);

        linearLayout.setVisibility(View.GONE);
        linearLayout1.setVisibility(View.GONE);
        if(toLogin!=null&&toLogin.equals("toLogin")){
            linearLayout1.setVisibility(View.VISIBLE);
        }else if(toLogin!=null&&toLogin.equals("toClear")){//清理空间
            linearLayout1.setVisibility(View.VISIBLE);
            btnSure.setText("立即清理");
            btnCancel.setText("暂不清理");
        }else if(toLogin!=null&&toLogin.equals("toFeedbackList")){
            linearLayout.setVisibility(View.VISIBLE);
            Button btnOk=findViewById(R.id.btn_ok);
            btnOk.setText("立即查看");
        }
        else{
            linearLayout.setVisibility(View.VISIBLE);
        }

        if(titleT!=null&&titleT.length()>0) {
            TextView txt_title = findViewById(R.id.title);
            txt_title.setText(titleT);
        }

        TextView txt_content=findViewById(R.id.content);
        txt_content.setText(msg);

        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toLogin!=null&&toLogin.equals("toFeedbackList")){
                    Intent intent2=new Intent(TipsPopupActivity.this,FeedbackRecodeActivity.class);
                    startActivity(intent2);
                    finish();
                }else {
                    setResult(100, null);
                    finish();
                }
            }
        });
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toLogin!=null&&toLogin.equals("toLogin")) {
                    Intent intent1 = new Intent(TipsPopupActivity.this, LoginActivity.class);
                    intent1.putExtra("toLogin", "comeLogin");
                    startActivity(intent1);
                    finish();
                }else if(toLogin!=null&&toLogin.equals("toClear")){
                    CacheManager.getInstance().getDownHandler().clearOtherDir();
                    finish();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            setResult(100,null);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
