package cn.dolit.p2ptrans;

import android.util.Log;

public class Module {
    protected static final String TAG = "p2ptrans";
    /* Load library before object instantiation */
    static {
        try {
            System.loadLibrary("p2ptrans");
        } catch (UnsatisfiedLinkError ule) {
            Log.e(TAG, "Can't load p2ptrans library: " + ule);
        } catch (SecurityException se) {
            Log.e(TAG, "Encountered a security issue when loading p2ptrans library: " + se);
        }
    }
    // 启动P2PTrans,参数为："8999" or "127.0.0.1:8999"
    public native int run(String address);
    public native int setLogLevel(int level);
}