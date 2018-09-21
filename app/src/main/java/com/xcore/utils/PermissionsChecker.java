package com.xcore.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class PermissionsChecker {
    private static final String TAG = "QVC_PermissionsChecker";
    private final Context mContext;

    public PermissionsChecker(Context context) {
        mContext = context.getApplicationContext();
    }

    // 判断权限集合
    public boolean lacksPermissions(String... permissions) {
        for (String permission : permissions) {
            if (lacksPermission(permission)) {
                Log.d(TAG,"is checking permission: " + permission);
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
                    if (permission.equals(Manifest.permission.WRITE_SETTINGS)
                            && Settings.System.canWrite(mContext)) {
                        continue;
                    }
                }
                return true;
            }
        }
        return false;
    }

    // 判断是否缺少权限
    private boolean lacksPermission(String permission) {
        return ContextCompat.checkSelfPermission(mContext, permission) ==
                PackageManager.PERMISSION_DENIED;
    }
}