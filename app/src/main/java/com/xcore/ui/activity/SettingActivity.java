package com.xcore.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.tu.loadingdialog.LoadingDailog;
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.base.BaseActivity;
import com.xcore.cache.CacheManager;
import com.xcore.utils.ACache;
import com.xcore.utils.Constant;
import com.xcore.utils.DateUtils;
import com.xcore.utils.SystemUtils;
public class SettingActivity extends BaseActivity implements View.OnClickListener{
    private boolean guestPass;
    private ACache aCache;
    private byte[] gesturePassword;

    private ImageView imgCheckBox;

    ImageView playCacheImage;
    ImageView idleCacheImage;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("设置");
        setEdit("");

//        TextView onlyId=findViewById(R.id.only_id);
//        String imie=SystemUtils.getM(this);
//
//        String onlId=SystemUtils.getFingerprint();
//        onlyId.setText("IMIE:"+imie+":::ID:"+onlId);


        imgCheckBox=findViewById(R.id.img_checkbox);
        TextView txtVersion=findViewById(R.id.txt_current_version);
        String vStr="当前版本:"+SystemUtils.getVersion(this);
        txtVersion.setText(vStr);

        findViewById(R.id.set_pass_layout).setOnClickListener(this);
        findViewById(R.id.set_user_protocol).setOnClickListener(this);
        findViewById(R.id.btn_toLogin).setOnClickListener(this);

        findViewById(R.id.set_clear_speace).setOnClickListener(this);
        findViewById(R.id.set_clear_speace).setVisibility(View.GONE);

        findViewById(R.id.set_camera).setOnClickListener(this);
        findViewById(R.id.set_nickname).setOnClickListener(this);
        findViewById(R.id.set_pass_login).setOnClickListener(this);


        playCacheImage=findViewById(R.id.img_play_cache);
        playCacheImage.setOnClickListener(this);

        idleCacheImage=findViewById(R.id.img_idle_cache);
        idleCacheImage.setOnClickListener(this);

        long time=MainApplicationContext.IDLE_TIMER;
        TextView idleTxt=findViewById(R.id.txt_idle_info);

        String missValue=DateUtils.getMiss(time);
        idleTxt.setText("空闲时自动下载("+missValue+"无操作)");

        initCheckStatus();
        updatePlayCacheStatus();

        updateIdleCacheStatus();
    }
    //启动相机
    private void testCamera(){
        if(MainApplicationContext.isGuest){
            MainApplicationContext.showips("未登录",SettingActivity.this,"toLogin");
            return;
        }
        Intent intent=new Intent(this,UpdateUserHeadActivity.class);
        startActivity(intent);
    }
    //修改用户名
    private void testNickname(){
        if(MainApplicationContext.isGuest){
            MainApplicationContext.showips("未登录",SettingActivity.this,"toLogin");
            return;
        }
        Intent intent=new Intent(this,UpgradeUserInfoActivity.class);
        startActivity(intent);
    }

    @Override
    protected void initData() {
    }

    //设置手势状态
    private void initCheckStatus(){
        aCache = ACache.get(SettingActivity.this);
        //得到当前用户的手势密码
        gesturePassword = aCache.getAsBinary(Constant.GESTURE_PASSWORD);
        guestPass=gesturePassword!=null;
        if(guestPass){//有密码了 打开
            imgCheckBox.setImageResource(R.drawable.checkbox_selected);
        }else{//没有密码
            imgCheckBox.setImageResource(R.drawable.checkbox_select);
        }
    }

    private void updatePlayCacheStatus(){
        boolean boo=MainApplicationContext.IS_PLAYING_TO_CACHE;
        int res=-1;
        if(boo==true){
            res=R.drawable.checkbox_selected;
            CacheManager.getInstance().getLocalHandler().put(CacheManager.PLAY_IS_CACHE,"xxxxxxxx");
        }else{
            res=R.drawable.checkbox_select;
            CacheManager.getInstance().getLocalHandler().remove(CacheManager.PLAY_IS_CACHE);
        }
        playCacheImage.setImageResource(res);
    }

    private void updateIdleCacheStatus(){
        boolean boo=MainApplicationContext.IS_IDLE_CACHE;
        int res=-1;
        if(boo==true){
            res=R.drawable.checkbox_selected;
            CacheManager.getInstance().getLocalHandler().put(CacheManager.IDLE_IS_CACHE,"xxxxxxxx");
        }else{
            res=R.drawable.checkbox_select;
            CacheManager.getInstance().getLocalHandler().remove(CacheManager.IDLE_IS_CACHE);
        }
        idleCacheImage.setImageResource(res);
    }

    @Override
    public void onClick(View v) {
        Intent intent=null;
        switch (v.getId()){
            case R.id.set_camera:
                testCamera();
                break;
            case R.id.set_nickname:
                testNickname();
                break;
            case R.id.set_pass_layout:
                if(MainApplicationContext.isGuest){
                    MainApplicationContext.showips("未登录",SettingActivity.this,"toLogin");
                    return;
                }
                Intent intent1=null;
                if(guestPass) {//有密码 要取消
                    intent1 = new Intent(SettingActivity.this, LockLoginActivity.class);
                    intent1.putExtra("lock_type","1");
                    startActivityForResult(intent1,100);
                }else{
                    //到设置手势去
                    intent1=new Intent(SettingActivity.this,LockSettingActivity.class);
                    startActivityForResult(intent1,100);
                }
                break;
            case R.id.set_pass_login:
                if(MainApplicationContext.isGuest){
                    MainApplicationContext.showips("未登录",SettingActivity.this,"toLogin");
                    return;
                }
                intent=new Intent(SettingActivity.this,UpdatePasswordActivity.class);
                break;
            case R.id.set_user_protocol:
                intent=new Intent(SettingActivity.this,ProtocolActivity.class);
                break;
            case R.id.btn_toLogin:
                intent=new Intent(SettingActivity.this,LoginActivity.class);
                break;
            case R.id.set_clear_speace:
                LoadingDailog.Builder loadBuilder;loadBuilder=new LoadingDailog.Builder(this)
                    .setMessage("清理中...")
                    .setCancelable(true)
                    .setCancelOutside(true);
                LoadingDailog dialog=loadBuilder.create();
                dialog.show();
                CacheManager.getInstance().getDownHandler().clearOtherDir();
                dialog.cancel();
                if(dialog!=null){
                    dialog.dismiss();
                }
                dialog=null;
                break;
            case R.id.img_play_cache:
                boolean boo=MainApplicationContext.IS_PLAYING_TO_CACHE;
                MainApplicationContext.IS_PLAYING_TO_CACHE=!boo;
                updatePlayCacheStatus();
                break;
            case R.id.img_idle_cache:
                boolean boo1=MainApplicationContext.IS_IDLE_CACHE;
                MainApplicationContext.IS_IDLE_CACHE=!boo1;
                updateIdleCacheStatus();
                break;
        }
        if(intent!=null){
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100&&resultCode==100){
            //设置状态
            initCheckStatus();
            return;
        }
    }


    private void setWindowAlpha(float alpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = alpha;
        getWindow().setAttributes(lp);
    }

}
