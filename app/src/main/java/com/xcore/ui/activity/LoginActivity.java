package com.xcore.ui.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.base.MvpActivity;
import com.xcore.cache.CacheManager;
import com.xcore.data.bean.RegisterBean;
import com.xcore.data.bean.TokenBean;
import com.xcore.data.utils.DataUtils;
import com.xcore.presenter.LoginPresenter;
import com.xcore.presenter.view.LoginView;
import com.xcore.utils.AppUpdateUtils;
import com.xcore.utils.Md5Util;

import java.io.File;

public class LoginActivity extends MvpActivity<LoginView,LoginPresenter> implements LoginView,View.OnTouchListener,View.OnClickListener {
    public static int RECODE_ID=8;
    public static int SUCCESS_ID=88;

    private boolean isShowPass=false;
    EditText loginPassword;
    EditText loginAccount;
    CheckBox recodePass;
    Button registerBtn;

    Button btnLogin;

    private String toLogin="";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        Log.e("TAG","initeView Login..");

        loginPassword= findViewById(R.id.edit_loginPassword);
        loginAccount= findViewById(R.id.edit_loginAccount);
        recodePass=findViewById(R.id.recodePass);
        registerBtn=findViewById(R.id.btn_loginRegister);
        btnLogin=findViewById(R.id.btn_login);

        Drawable drawable=getResources().getDrawable(R.drawable.login_user);
        drawable.setBounds(5,0,65,60);
        loginAccount.setCompoundDrawables(drawable,null,null,null);

        Drawable drawable1=getResources().getDrawable(R.drawable.login_mima);
        drawable1.setBounds(5,0,65,60);

        Drawable drawable5=getResources().getDrawable(R.drawable.login_look);
        drawable5.setBounds(5,0,80,45);
//        Drawable drawable5=loginPassword.getCompoundDrawables()[2];
        loginPassword.setCompoundDrawables(drawable1,null,drawable5,null);

        //点击登录
        btnLogin.setOnClickListener(this);
        //点击注册
        registerBtn.setOnClickListener(this);
        //显示隐藏密码
        loginPassword.setOnTouchListener(this);
        //记住密码
        recodePass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true){
                    recodePass.setButtonDrawable(R.drawable.login_switch_select);
                }else{
                    recodePass.setButtonDrawable(R.drawable.login_switch);
                }
            }
        });
        recodePass.setVisibility(View.GONE);

        Intent intent=getIntent();
        toLogin=intent.getStringExtra("toLogin");
        if(toLogin!=null&&toLogin.equals("comeLogin")){
            return;
        }
        //获取保存的数据
        String uInfo=CacheManager.getInstance().getLocalHandler().get(DataUtils.SAVE_ID);
        if(uInfo==null||uInfo.equals("")){
            return;
        }
        String[] sList=uInfo.split("|");

        int splitIndex=uInfo.indexOf("|");
        String uname=uInfo.substring(0,splitIndex);

        int endIndex=uInfo.lastIndexOf("|");
        String upass="";
        if(splitIndex<endIndex&&splitIndex+1<endIndex) {
            upass=uInfo.substring(splitIndex + 1, endIndex);
        }else{
            upass=uInfo.substring(splitIndex+1);
        }

        loginAccount.setText(uname);
        loginPassword.setText(upass);
        recodePass.setChecked(true);
        //自动登录
//        presenter.getLogin(uArr[0],uArr[1],recodePass.isChecked());
        MainApplicationContext.removeActivityBy(this);
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
    public void getTokenResult(TokenBean tokenBean) {
        //有新版本
        if(!TextUtils.isEmpty(tokenBean.getError_description())&&"405".equals(tokenBean.getError_description())){
            String errorStr=tokenBean.getError();
            new AppUpdateUtils(this,errorStr);
            return;
        }

        if(!TextUtils.isEmpty(tokenBean.getError())|| !TextUtils.isEmpty(tokenBean.getError_description())){
            toast("用户名或密码错误,请重新输入!!!");
//            dialog.cancel();
            return;
        }

        //
        MainApplicationContext.TOKEN=tokenBean.getAccess_token();
        MainApplicationContext.initOkGo();

        if(toLogin!=null&&toLogin.equals("comeLogin")){
            finish();
        }else {
            presenter.getTags();
        }
    }

    @Override
    public void onFinalResult(){
        //禁用
        btnLogin.setEnabled(false);
        registerBtn.setEnabled(false);
//        dialog.cancel();
        //初始化缓存
        CacheManager.getInstance().initDown();

        //打开一个activity 关闭当前的
        gotoActivity(XMainActivity.class,true);
//        Intent intent=new Intent(this,XMainActivity.class);
//        startActivity(intent);
        //toast("登录成功");
        MainApplicationContext.removeActivity(this);
        finish();
    }

    private long mExitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK&&toLogin==null) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                MainApplicationContext.finishAllActivity();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void getRegisterResult(RegisterBean registerBean) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_loginRegister://注册 //有返回的跳转 把注册的用户名和密码传回来
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
//                startActivity(intent);
                startActivityForResult(intent,RECODE_ID);
                break;
            case R.id.btn_login:
                toLogin();
                break;
        }
    }

    public void toLogin(){
        //判断用户名密码
        String uname=loginAccount.getText().toString().trim();
        String upass=loginPassword.getText().toString().trim();
        if(uname.length()<=0){
            toast("用户名不能为空");
            return;
        }
        if(upass.length()<=0){
            toast("密码不能为空");
            return;
        }
        //判断密码规则
        presenter.getLogin(uname,upass,true,false);
    }

    @Override
    public void onError(String msg) {
//        dialog.cancel();
        btnLogin.setEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 判断请求码和返回码是不是正确的，这两个码都是我们自己设置的
        if (requestCode == RECODE_ID && resultCode == SUCCESS_ID) {
            String name = data.getStringExtra("uname");// 拿到返回过来的输入的账号
            String pwd = data.getStringExtra("upass");// 拿到返回过来的输入的账号
            // 把得到的数据显示到输入框内 注册的不用记住密码

            loginAccount.setText(name);
            loginPassword.setText(pwd);
            toLogin();
            //presenter.getLogin(name,pwd,false);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // et.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
        Drawable drawable = loginPassword.getCompoundDrawables()[2];
        //如果右边没有图片，不再处理
        if (drawable == null)
            return false;
        //如果不是按下事件，不再处理
        if (event.getAction() != MotionEvent.ACTION_UP)
            return false;
        if (event.getX() > loginPassword.getWidth()
                - loginPassword.getPaddingRight()
                - drawable.getIntrinsicWidth()){
            if(isShowPass==false) {
                HideReturnsTransformationMethod method = HideReturnsTransformationMethod.getInstance();
                loginPassword.setTransformationMethod(method);
                isShowPass=true;
            }else {
                TransformationMethod method =  PasswordTransformationMethod.getInstance();
                loginPassword.setTransformationMethod(method);
                isShowPass=false;
            }
        }
        return false;
    }

}
