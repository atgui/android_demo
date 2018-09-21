package com.common;

import com.xcore.data.bean.CdnBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseCommon {
    //服务端口
    public static int P2P_SERVER_PORT=8777;

    public static List<String> jsonList=
            Arrays.asList("http://api01.xxxlutubexxx.com/",
                    "https://bcdn.bvgqp.com/",
                    "http://tor01.xxxlutubexxx.com/",
                    "http://tor02.xxxlutubexxx.com/",
                    "http://pic01.xxxlutubexxx.com/",
                    "http://pic02.xxxlutubexxx.com/"
                    );

    public static List<String> apiList=new ArrayList<>();

    //API请求路径
    public static String API_URL="http://api01.xxxlutubexxx.com/";

    public static List<String> videoLists=new ArrayList<>();
    //初始路径
    public static String VIDEO_URL="http://tor01.xxxlutubexxx.com/";

    public static List<String> resLists=new ArrayList<>();
    //资源路径
    public static String RES_URL="http://pic01.xxxlutubexxx.com/";

    public static List<String> apkLists=new ArrayList<>();
    //apk 更新路径
    public static String APK_URL="https://bcdn.bvgqp.com/";
    //测试地址
    public static List<CdnBean.CdnDataItem> testUrlMaps=new ArrayList<>();
    /**
     * 加速 ?
     */
    public static List<String> httpAccelerateLists=new ArrayList<>();
}
