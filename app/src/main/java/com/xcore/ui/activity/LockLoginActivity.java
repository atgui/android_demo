package com.xcore.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.star.lockpattern.util.LockPatternUtil;
import com.star.lockpattern.widget.LockPatternView;
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.base.MvpActivity;
import com.xcore.cache.CacheManager;
import com.xcore.data.bean.RegisterBean;
import com.xcore.data.bean.TokenBean;
import com.xcore.data.utils.DataUtils;
import com.xcore.presenter.LoginPresenter;
import com.xcore.presenter.view.LoginView;
import com.xcore.utils.ACache;
import com.xcore.utils.Constant;

import java.util.List;

public class LockLoginActivity extends MvpActivity<LoginView,LoginPresenter> implements LoginView {

    LockPatternView lockPatternView;
    TextView messageTv;
    Button forgetGestureBtn;

    private ACache aCache;
    private static final long DELAYTIME = 600l;
    private byte[] gesturePassword;
    private String lockType;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_lock_login;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        Intent intent= getIntent();
        lockType=intent.getStringExtra("lock_type");

        lockPatternView=findViewById(R.id.lockPatternView);
        messageTv=findViewById(R.id.messageTv);
        forgetGestureBtn=findViewById(R.id.forgetGestureBtn);
        lockPatternView.setTactileFeedbackEnabled(true);

        forgetGestureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//忘记密码
                forgetGesturePasswrod();
            }
        });
        init();
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

    private void init() {
        aCache = ACache.get(LockLoginActivity.this);
        //得到当前用户的手势密码
        gesturePassword = aCache.getAsBinary(Constant.GESTURE_PASSWORD);
        lockPatternView.setOnPatternListener(patternListener);
        updateStatus(Status.DEFAULT);

        if(gesturePassword==null&&lockType.equals("0")){//要到登录界面
            Intent intent=new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
//            toLogin();
        }
    }

    private void toLogin(){
        String uInfo=CacheManager.getInstance().getLocalHandler().get(DataUtils.SAVE_ID);
        if(uInfo.isEmpty()){
            //把手势清除
            aCache.clear();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        int splitIndex=uInfo.indexOf("|");
        String uname=uInfo.substring(0,splitIndex);
        int endIndex=uInfo.lastIndexOf("|");
        String upass ="";
        boolean guestBoo=false;
        if(splitIndex<endIndex&&splitIndex+1<endIndex) {
            upass=uInfo.substring(splitIndex + 1, endIndex);
            String v=uInfo.substring(endIndex+1);
            guestBoo=v.equals("ok");
        }else{
            upass=uInfo.substring(splitIndex + 1);
        }
        presenter.getLogin(uname, upass, true, guestBoo);
    }

    private LockPatternView.OnPatternListener patternListener = new LockPatternView.OnPatternListener() {

        @Override
        public void onPatternStart() {
            lockPatternView.removePostClearPatternRunnable();
        }

        @Override
        public void onPatternComplete(List<LockPatternView.Cell> pattern) {
            if(pattern != null){
                if(LockPatternUtil.checkPattern(pattern, gesturePassword)) {
                    updateStatus(Status.CORRECT);
                } else {
                    updateStatus(Status.ERROR);
                }
            }
        }
    };

    /**
     * 更新状态
     * @param status
     */
    private void updateStatus(Status status) {
        messageTv.setText(status.strId);
        messageTv.setTextColor(getResources().getColor(status.colorId));
        switch (status) {
            case DEFAULT:
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                break;
            case ERROR:
                lockPatternView.setPattern(LockPatternView.DisplayMode.ERROR);
                lockPatternView.postClearPatternRunnable(DELAYTIME);
                break;
            case CORRECT:
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                loginGestureSuccess();
                break;
        }
    }

    /**
     * 手势登录成功（去首页）
     */
    private void loginGestureSuccess() {
        //Toast.makeText(GestureLoginActivity.this, "success", Toast.LENGTH_SHORT).show();
        if(lockType.equals("1")){//取消密码 手势输入成功
//            清除数据
            aCache.clear();
            setResult(100,null);
            finish();
            return;
        }
        toLogin();
    }

    /**
     * 忘记手势密码（弹窗提示到登录界面）
     */
    void forgetGesturePasswrod() {
        String uInfo=CacheManager.getInstance().getLocalHandler().get(DataUtils.SAVE_ID);
        if(lockType.equals("0")){
            //把密码清除掉
            //获取保存的数据
            if(!TextUtils.isEmpty(uInfo)){
                int splitIndex=uInfo.indexOf("|");
                String uname=uInfo.substring(0,splitIndex);
                CacheManager.getInstance().getLocalHandler().put(DataUtils.SAVE_ID,uname+"|");
            }

            //把手势清除
            aCache.clear();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else{//取消手势的时候点忘记密码
            Log.e("TAG","弹窗出来验证密码");
            Intent intent=new Intent(this,VerifyPassActivity.class);
            startActivityForResult(intent,99);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==99&&resultCode==99){//取消手势
            //把密码清除掉
            //获取保存的数据
            String uInfo=CacheManager.getInstance().getLocalHandler().get(DataUtils.SAVE_ID);
            if(uInfo!=null||!uInfo.equals("")){
                int splitIndex=uInfo.indexOf("|");
                String uname=uInfo.substring(0,splitIndex);
                CacheManager.getInstance().getLocalHandler().put(DataUtils.SAVE_ID,uname+"|");
            }
            aCache.clear();
            setResult(100);
            finish();
        }
    }

    @Override
    public void getTokenResult(TokenBean tokenBean) {
        //
        MainApplicationContext.TOKEN=tokenBean.getAccess_token();
        MainApplicationContext.initOkGo();

        presenter.getTags();
    }

    @Override
    public void getRegisterResult(RegisterBean registerBean) {

    }

    @Override
    public void onError(String msg) {
        //打开一个activity 关闭当前的
        gotoActivity(LoginActivity.class,true);
    }

    @Override
    public void onFinalResult() {
        //hideProgress();
        //初始化缓存
        CacheManager.getInstance().initDown();

        //打开一个activity 关闭当前的
        gotoActivity(XMainActivity.class,true);
        toast("登录成功");
    }

    private enum Status {
        //默认的状态
        DEFAULT(R.string.gesture_default, R.color.grey_a5a5a5),
        //密码输入错误
        ERROR(R.string.gesture_error, R.color.red_f4333c),
        //密码输入正确
        CORRECT(R.string.gesture_correct, R.color.grey_a5a5a5);

        private Status(int strId, int colorId) {
            this.strId = strId;
            this.colorId = colorId;
        }
        private int strId;
        private int colorId;
    }

}
