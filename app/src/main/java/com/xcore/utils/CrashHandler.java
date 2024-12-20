package com.xcore.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;

import com.xcore.MainApplicationContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 异常退出
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = CrashHandler.class.getSimpleName();

    private static final String SINGLE_RETURN = "\n";
    private static final String SINGLE_LINE = "--------------------------------";

    private static CrashHandler mCrashHandler;
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private StringBuffer mErrorLogBuffer = new StringBuffer();

    /**
     * 获取CrashHandler实例，单例模式。
     *
     * @return 返回CrashHandler实例
     */
    public static CrashHandler getInstance() {
        if (mCrashHandler == null) {
            synchronized (CrashHandler.class) {
                if (mCrashHandler == null) {
                    mCrashHandler = new CrashHandler();
                }
            }
        }

        return mCrashHandler;
    }

    public void init(Context context) {
        mContext = context;
        // 获取系统默认的uncaughtException处理类实例
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置成我们处理uncaughtException的类
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.d(TAG, "uncaughtException:" + ex);
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理异常就由系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            MainApplicationContext.restart();
           // MainApplicationContext.finishAllActivity();
        }
    }

    //处理异常事件
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
            Looper.prepare();
            try {
//                    Toast.makeText(mContext, "请重新重启!", Toast.LENGTH_SHORT).show();
            }catch (Exception e){

            }
            Looper.loop();
            }
        }).start();
        // 收集设备参数信息
        collectDeviceInfo(mContext);
        // 收集错误日志
        collectCrashInfo(ex);
        //TODO: 这里可以加一个网络的请求，发送错误log给后台
        sendErrorLog();
        // 保存错误日志
        saveErrorLog();
        return true;
    }
    //发送请求到服务器
    private void sendErrorLog(){
        String errorStr=mErrorLogBuffer.toString();
        LogUtils.crashUp(errorStr);
    }
    //保存日志到/mnt/sdcard/AppLog/目录下，文件名已时间yyyy-MM-dd_hh-mm-ss.log的形式保存
    private void saveErrorLog() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String format = sdf.format(new Date());
            format += ".log";
            String path = Environment.getExternalStorageDirectory().getPath()+"/AppLog/";
            File file = new File(path);
            if (!file.exists()){
                file.mkdirs();
            }

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(path+format,true);
                fos.write(mErrorLogBuffer.toString().getBytes());
                fos.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                        fos = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    //收集错误信息
    private void collectCrashInfo(Throwable ex) {
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);

        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }

        String result = info.toString();
        printWriter.close();

        //将错误信息加入mErrorLogBuffer中
        append("", result);
        mErrorLogBuffer.append(SINGLE_LINE + SINGLE_RETURN);

        Log.d(TAG, "saveCrashInfo2File:" + mErrorLogBuffer.toString());
    }

    //收集应用和设备信息
    private void collectDeviceInfo(Context context) {
        //每次使用前，清掉mErrorLogBuffer里的内容
        mErrorLogBuffer.setLength(0);
        mErrorLogBuffer.append(SINGLE_RETURN + SINGLE_LINE + SINGLE_RETURN);

        //获取应用的信息
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                append("versionCode", pi.versionCode);
                append("versionName", pi.versionName);
                append("packageName", pi.packageName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mErrorLogBuffer.append(SINGLE_LINE + SINGLE_RETURN);

        //获取设备的信息
        Field[] fields = Build.class.getDeclaredFields();
        getDeviceInfoByReflection(fields);

        fields = Build.VERSION.class.getDeclaredFields();
        getDeviceInfoByReflection(fields);

        mErrorLogBuffer.append(SINGLE_LINE + SINGLE_RETURN);
    }

    //获取设备的信息通过反射方式
    private void getDeviceInfoByReflection(Field[] fields) {
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                append(field.getName(), field.get(null));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    //mErrorLogBuffer添加友好的log信息
    private void append(String key, Object value) {
        mErrorLogBuffer.append("" + key + ":" + value + SINGLE_RETURN);
    }
}
