package com.xcore.ui.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xcore.R;
import com.xcore.data.bean.SpeedDataBean;
import com.xcore.data.utils.AGCallback;
import com.xcore.services.ApiFactory;

public class UpgradeUserInfoActivity extends Activity {

    EditText editNickname;
    Button btnSure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade_user_info);

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
                toSave();
            }
        });
    }

    //保存
    private void toSave(){
        String nickNameStr=editNickname.getText().toString();
        nickNameStr=nickNameStr.trim();
        if(TextUtils.isEmpty(nickNameStr)){
            Toast.makeText(UpgradeUserInfoActivity.this,"昵称必须输入",Toast.LENGTH_SHORT).show();
            return;
        }
        HttpParams params=new HttpParams();
        params.put("NickName",nickNameStr);
        ApiFactory.getInstance().<SpeedDataBean>updateUserInfo(params, new AGCallback<SpeedDataBean>() {
            @Override
            public void onNext(SpeedDataBean speedDataBean) {
                toast("修改昵称成功");
            }

            @Override
            public void onError(Response<SpeedDataBean> response) {
                super.onError(response);
                toast("修改昵称失败");
            }
        });
    }

    private void toast(String msg){
        Toast.makeText(UpgradeUserInfoActivity.this,msg,Toast.LENGTH_SHORT).show();
        finish();
    }
}
