package com.xcore.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xcore.R;
import com.xcore.cache.CacheManager;
import com.xcore.data.utils.DataUtils;

public class VerifyPassActivity extends Activity {

    private EditText editText;
    private Button cancelBtn;
    private Button okBtn;
    private TextView txtName;
    private String ppass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_verify_pass);

        txtName=findViewById(R.id.txt_name);
        editText=findViewById(R.id.edit_pass);
        okBtn=findViewById(R.id.btn_ok);
        cancelBtn=findViewById(R.id.btn_cancel);

        String uInfo= CacheManager.getInstance().getLocalHandler().get(DataUtils.SAVE_ID);
        if(uInfo!=null||!uInfo.equals("")){
            int splitIndex=uInfo.indexOf("|");
            String uname=uInfo.substring(0,splitIndex);
            txtName.setText(uname);

            ppass=uInfo.substring(splitIndex+1);
        }
        okBtn.setBackgroundResource(R.drawable.button_normal_grey_raduis_5);
        okBtn.setEnabled(false);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String value=s.toString().trim();
                if(value.length()>=3){
                    okBtn.setBackgroundResource(R.drawable.button_normal_yello_raduis_5);
                    okBtn.setEnabled(true);
                }else{
                    okBtn.setBackgroundResource(R.drawable.button_normal_grey_raduis_5);
                    okBtn.setEnabled(false);
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOk();
            }
        });
    }

    //
    void clickOk(){
        String pTxt=editText.getText().toString().trim();
        if(pTxt.equals(ppass)){
            setResult(99);
            finish();
        }else{
            Toast toast=Toast.makeText(this,"输入的密码不正确,请重新输入!!!",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }
}
