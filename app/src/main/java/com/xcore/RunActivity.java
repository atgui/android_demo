package com.xcore;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.xcore.utils.DensityUtil;
import com.xcore.utils.PermissionsChecker;

public class RunActivity extends Activity {

    private static final String TAG = "RunActivity";

    final private static int PERMISSIONS_CODE = 29; // 请求码

    static final String[] PERMISSIONS = new String[]{
//            Manifest.permission.INTERNET,
//            Manifest.permission.RECORD_AUDIO,
//            Manifest.permission.ACCESS_WIFI_STATE,
////            Manifest.permission.ACCESS_NETWORK_STATE,
//            Manifest.permission.CHANGE_NETWORK_STATE,
//            Manifest.permission.READ_PHONE_STATE,
//            Manifest.permission.READ_CONTACTS,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_SETTINGS,
//            Manifest.permission.ACCESS_FINE_LOCATION
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
//                Manifest.permission.READ_LOGS,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
//                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
            Manifest.permission.CAMERA,
            Manifest.permission.WAKE_LOCK
//            Manifest.permission.STATUS_BAR
//            Manifest.permission.REQUEST_INSTALL_PACKAGES
//            Manifest.permission.WRITE_SETTINGS,
//                Manifest.permission.INTERNET
    };

    private PermissionsChecker permissionsChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int v=DensityUtil.px2dip(this,203);
        Log.e("TAG","PX 转成dp:"+v);

        int x=DensityUtil.dip2px(this,180);
        Log.e("TAG","Dp to px:"+x);

        permissionsChecker = new PermissionsChecker(this);
    }

    @Override
    protected void onResume() {
        if (permissionsChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        } else {
            showMainActivity();
            finish();
        }
        super.onResume();
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, PERMISSIONS_CODE, PERMISSIONS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == PERMISSIONS_CODE &&
                resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
        } else {
            showMainActivity();
        }
        finish();
    }

    private void showMainActivity() {
        toMain();
    }
    private void toMain(){
        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
    }



}