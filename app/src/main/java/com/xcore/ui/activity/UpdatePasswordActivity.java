package com.xcore.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xcore.R;
import com.xcore.base.BaseActivity;
import com.xcore.data.bean.SpeedDataBean;
import com.xcore.data.utils.AGCallback;
import com.xcore.services.ApiFactory;

public class UpdatePasswordActivity extends Activity {

    private Button btnSure;
    private Button btnCancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        btnSure=findViewById(R.id.btn_sure);
        btnCancel=findViewById(R.id.btn_cancel);

        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sentChange();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void sentChange(){
        EditText oldPass=findViewById(R.id.edit_oldPass);
        String oldStr=oldPass.getText().toString().trim();
        if(TextUtils.isEmpty(oldStr)){
            toast("请输入旧密码");
            return;
        }
        EditText newPass=findViewById(R.id.edit_newPass);
        String newStr=newPass.getText().toString().trim();
        if(TextUtils.isEmpty(newStr)){
            toast("请输入新密码");
            return;
        }
        EditText rePass=findViewById(R.id.edit_rePass);
        String reStr=rePass.getText().toString().trim();
        if(TextUtils.isEmpty(reStr)){
            toast("请确认新密码");
            return;
        }
        if(!newStr.equals(reStr)){
            toast("两次输入的新密码不一致,请重新输入!");
            return;
        }

        HttpParams params=new HttpParams();
        params.put("oldPwd",oldStr);
        params.put("newPwd",newStr);
        params.put("reNewPwd",newStr);

        ApiFactory.getInstance().<SpeedDataBean>updateUserPass(params, new AGCallback<SpeedDataBean>() {
            @Override
            public void onNext(SpeedDataBean speedDataBean) {
                toast("修改密码成功");
                finish();
            }
            @Override
            public void onError(Response<SpeedDataBean> response) {
                super.onError(response);
                toast("修改密码出错,稍后重试!");
            }
        });
    }

    Toast toast;

    protected void toast(String msg) {
        if(toast==null){
            toast=Toast.makeText(this,msg,Toast.LENGTH_SHORT);
        }else{
            toast.setText(msg);
        }
        toast.show();
    }

}
