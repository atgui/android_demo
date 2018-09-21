package com.xcore.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xcore.R;
import com.xcore.base.MvpActivity;
import com.xcore.data.bean.RegisterBean;
import com.xcore.data.bean.TokenBean;
import com.xcore.presenter.LoginPresenter;
import com.xcore.presenter.view.LoginView;
import com.xcore.ui.touch.DrawableUtil;
import com.xcore.utils.SystemUtils;

public class RegisterActivity extends MvpActivity<LoginView,LoginPresenter> implements LoginView,View.OnClickListener {
    private boolean showPass=false;//是否显示密码
    private boolean showBox=true;//box 框是否显示
    EditText invitationCode;
    EditText loginPassword;
    EditText loginAccount;
    Button registerBtn;
    ImageView xyCheckbox;
    TextView userProtocol;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        xyCheckbox=findViewById(R.id.tyCheckbox);
        invitationCode=findViewById(R.id.invitationCode);//邀请码
        loginAccount=findViewById(R.id.loginAccount);
        loginPassword=findViewById(R.id.loginPassword);
        userProtocol=findViewById(R.id.user_protocol);

        registerBtn=findViewById(R.id.registerBtn);

        Drawable drawable=getResources().getDrawable(R.drawable.login_qcode);
        drawable.setBounds(5,0,65,60);
        invitationCode.setCompoundDrawables(drawable,null,null,null);

        Drawable drawable1=getResources().getDrawable(R.drawable.login_user);
        drawable1.setBounds(5,0,65,60);
        loginAccount.setCompoundDrawables(drawable1,null,null,null);

        Drawable drawable2=getResources().getDrawable(R.drawable.login_mima);
        drawable2.setBounds(5,0,65,60);
//        Drawable drawable5=loginPassword.getCompoundDrawables()[2];
        Drawable drawable5=getResources().getDrawable(R.drawable.login_look);
        drawable5.setBounds(5,0,80,45);
        loginPassword.setCompoundDrawables(drawable2,null,drawable5,null);

        new DrawableUtil(loginPassword, new DrawableUtil.OnDrawableListener() {
            @Override
            public void onLeft(View v, Drawable left) {
                Log.e("TAG","onLeft...");
            }
            @Override
            public void onRight(View v, Drawable right) {
                showPass=!showPass;
                if(showPass==true) {
                    HideReturnsTransformationMethod method = HideReturnsTransformationMethod.getInstance();
                    loginPassword.setTransformationMethod(method);
                }else{
                    TransformationMethod method =  PasswordTransformationMethod.getInstance();
                    loginPassword.setTransformationMethod(method);
                }
            }
        });
        showBox();
        registerBtn.setOnClickListener(this);
        xyCheckbox.setOnClickListener(this);
        userProtocol.setOnClickListener(this);

    }

    private void showBox(){
        if(showBox){
            xyCheckbox.setImageResource(R.drawable.login_switch_select);
        }else{
            xyCheckbox.setImageResource(R.drawable.login_switch);
        }
    }
    @Override
    protected void initData() {
    }

    @Override
    public void onBack() {

    }

    @Override
    public LoginPresenter initPresenter() {
        return new LoginPresenter();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.registerBtn:
                Log.e("TAG","注册");
                toRegister();
                break;
            case R.id.tyCheckbox:
                showBox=!showBox;
                showBox();
                break;
            case R.id.user_protocol://用户协议
                Intent intent=new Intent(this,ProtocolActivity.class);
                startActivity(intent);
                break;
        }
    }
    private void toRegister(){
        String uname=loginAccount.getText().toString().trim();
        String upass=loginPassword.getText().toString().trim();
        String invitationCodeStr=this.invitationCode.getText().toString().trim();//可为""
        if(uname.length()<=0){
            toast("用户名不能为空");
            return;
        }
        if(upass.length()<=0){
            toast("密码不能为空");
            return;
        }
        if(upass.length()<6){
            toast("密码最少6位字符");
            return;
        }
        if(TextUtils.isEmpty(invitationCodeStr)){
            showTips(uname,upass,invitationCodeStr);
            return;
        }
        toRegister(uname,upass,invitationCodeStr);
//        presenter.toRegister(uname,upass, SystemUtils.getVersion(this),invitationCodeStr);
    }
    private void toRegister(String uname,String upass,String codeStr){
        String versionName=SystemUtils.getVersion(this);
        presenter.toRegister(uname,upass,versionName,codeStr);
    }
    //弹窗提示
    private void showTips(final String uname, final String upass, final String codeStr){
        new AlertDialog.Builder(this)
                .setTitle("温馨提示")
                .setMessage("填写邀请码可获得更多播放特权哦,确定要放弃吗?")
                .setPositiveButton("放弃"
                    , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toRegister(uname,upass,codeStr);
                            dialog.dismiss();
                        }
                    })
                .setNegativeButton("去填写",
                    new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    @Override
    public void getTokenResult(TokenBean tokenBean) {
    }
    @Override
    public void onError(String msg) {
//        if(msg==null||(msg!=null&&msg.length()<=0)){
//            msg="注册失败!";
//        }
//        Intent intent=new Intent(this,TipsPopupActivity.class);
//        intent.putExtra("content",msg);
//        startActivity(intent);
    }

    @Override
    public void onFinalResult() {

    }

    @Override
    public void getRegisterResult(RegisterBean registerBean) {
        registerBtn.setEnabled(true);
        if(registerBean.getStatus()==200){//成功
            //拿到一个intent把需要返回的值放进去
            Intent intent = new Intent();
            intent.putExtra("uname", registerBean.getUsername());
            intent.putExtra("upass", registerBean.getPassword());
            /*
             * 调用setResult方法表示我将Intent对象返回给之前的那个Activity，这样就可以在onActivityResult方法中得到Intent对象，
             * 参数1：resultCode返回码，跳转之前的activity根据是这个resultCode，区分是哪一个activity返回的
             * 参数2：数据源
             */
            setResult(LoginActivity.SUCCESS_ID, intent);
            finish();//结束当前activity
        }else{
            toast(registerBean.getMessage());
        }
    }
}
