package com.xcore;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.common.BaseCommon;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.services.p2p.P2PEngineServer;
import com.xcore.data.bean.NoticeAlertBean;
import com.xcore.data.utils.AGCallback;
import com.xcore.services.ApiFactory;
import com.xcore.ui.activity.NoticeTipsPopupActivity;
import com.xcore.ui.activity.TipsPopupActivity;
import com.xcore.ui.activity.XMainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import cn.dolit.DLBT.DLBT_KERNEL_START_PARAM;
import cn.dolit.DLBT.DolitBT;
import cn.dolit.p2ptrans.P2PTrans;
import okhttp3.OkHttpClient;


public class MainApplicationContext {
	public static String TOKEN="";

	public static Context context=null;
	public static Application application=null;

	public static String SD_PATH="";//缓存路径
	public static String APK_HOT_PATH="";//热更路径
	public static String DATABASE_PATH="";//本地数据路径
	public static String IMAGE_PATH="";//图片缓存路径


	public static String DOLITBT_PASS="1234";//key-pass  默认
	public static String DOLITBT_PRIVETE="";//是否私有  1 :私有  "":不是私有

	public static DolitBT mDolitBT = null;
	private static boolean mBInit = false;

	public static boolean serverSuccess=false;

	public static boolean isGuest=true;//是不是游客 true 是游客


	public static boolean IS_PLAYING_TO_CACHE=false;//播放的时候是否缓存
	public static boolean IS_IDLE_CACHE=false;//是否开启空闲缓存
	public static long IDLE_TIMER=300000;//停止多少时间后开始空闲下载

	public static long HEAT_TIMER=-1;//心跳时间  -1:不开启 >-1 开启时间


	protected static final String TAG = "DolitBTDemoApp/";

	//请输入点量软件正版验证码密钥1 和密钥2 -- 需要购买正版后点量软件提供。请访问http://www.dolit.cn联系购买
//	public static final String strDolitParseKey1 = "dolitBTSample";
//	public static final String strDolitParseKey2 = "PoweredBy-www.Dolit.cn";
	public static final String strDolitParseKey1 = "dolitBT";
	public static final String strDolitParseKey2 = "PoweredBy-www.Dolit.cn";

	//请输入正版验证码密钥1 和密钥2
	//public static final String strDolitParseKey1 = "McsCtjV1VEushEGv";
	//public static final String strDolitParseKey2 = "cy6CkbsG82KM03tc";

	// USB root
	//public static String m_usbRoot = "";

    public static void onCreate() {
		//缓存目录
    	SD_PATH=context.getExternalCacheDir()+"/videos/";
		DATABASE_PATH=SD_PATH.replace("cache/videos/","")+"files/data/";
		IMAGE_PATH=SD_PATH.replace("videos/","image_http_cache/");
//		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//			SD_PATH=Environment.getExternalStorageDirectory().getPath()+"/qxDownloads/";
//		}

		APK_HOT_PATH=context.getExternalCacheDir()+"/hot_apk/";

	    initDolitBT();   //不能开始就启动bt了，因为还没设置好外部存储的权限，里面启动就要操作IO的
//		String packagePath = context.getCacheDir()+"/test.torrent";
//		try {
//			InputStream is = context.getResources().getAssets().open("test.torrent");
//			FileOutputStream fos = new FileOutputStream(new File(packagePath));
//			byte[] buffer = new byte[1024];
//			while (true) {
//				int len = is.read(buffer);
//				if (len == -1) {
//					break;
//				}
//				fos.write(buffer, 0, len);
//			}
//			is.close();
//			fos.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		initRefresh();
	}
	public static boolean isLoading=false;
	public static boolean isStartServer=false;
	public static void startP2PServer(){
	    if(isStartServer==false&&isLoading==false){
			Toast.makeText(context,"视频服务启动中,请稍后...",
					Toast.LENGTH_SHORT).show();
			isLoading=true;
            initServer();
        }
	}


    /**
     * 使用解析sdk前需先初始化DolitSiteParser.
     * DolitSiteParser是全局的，可为所有其它地方共用，它需要在第一次被解析之前初始化，也可以放到程序初始化时初始化一次，
     * 只要有需要用到解析，就不应该销毁DolitSiteParser对象。
     * 
     * 在初始化时需要传入正版Key，如果还没有请联系点量软件获取
     */
	public static void initDolitBT(){
        if (mDolitBT == null)
        {
        	mDolitBT = new DolitBT(context);

        	int ret = mDolitBT.InitAPIKey(strDolitParseKey1, strDolitParseKey2);
        	if (ret == -4)
        	{
        		Log.e(TAG, "InitAPIKey Error, please check your apk key!");
        		Toast.makeText(context,
                        "您的Keystore和包名未被授权，请联系官方获取正确的授权Key!", Toast.LENGTH_LONG).show();
        		//return;
        	}
        	else if (ret != 0)
            {
        		Log.e(TAG, "InitAPIKey failed, please check your env!");
                Toast.makeText(context,
                        "组件模块 初始化错误!,ret:" + ret, Toast.LENGTH_LONG).show();
                //return;
            }
        }
    	// 启动bt
    	if(!mBInit)
    	{
        	DLBT_KERNEL_START_PARAM param = new DLBT_KERNEL_START_PARAM();
        	param.startPort = 9010;
        	param.endPort = 9020;
            param.bVODMode = 1;
        	mDolitBT.DLBT_Startup(param, "BaiYinBY-DLBT", false, "{37CD31CA-FBF8-406f-9901-06C0FF049DBA}");
        	mBInit = true;
    	}
	}

    /**
     * 启动服务
     */
    public static void initServer() {
        final Handler runHandle = new Handler(){
            @Override
            public void handleMessage( Message msg) {
                super.handleMessage(msg);
                if (msg.what == 2301) {
                	isLoading=false;
                    Log.e("TAG","播放服务启动失败,请关闭后重新打开app");
                    Toast.makeText(context,"播放服务启动失败,请关闭后重新打开app",
                            Toast.LENGTH_SHORT).show();
                    isStartServer=false;
                } else if (msg.what == 2300) {
                    serverSuccess=true;
                    Log.e("TAG","服务器启动成功");
					Toast.makeText(context,"服务启动成功",
							Toast.LENGTH_SHORT).show();
                    //Toast.makeText(MainApplicationContext.context,"启动服务成功",Toast.LENGTH_SHORT).show();
                }
            }
        };
        //启动服务
        new Thread( new Runnable(){
            public void run()
            {
                int i = 0;
                for( ; i < 10; i++)
                {
                    int ret = P2PTrans.run( "", BaseCommon.P2P_SERVER_PORT );
                    Log.e("TAG", "RunServer ret:" + ret);
                    if ( ret != 0){
                        BaseCommon.P2P_SERVER_PORT++;
                    }
                    else
                    {
                        Message msg = new Message();
                        runHandle.sendMessage( msg);
                        break;
                    }
                }
                if ( i == 10){
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("error","启动服务器失败");
                    msg.setData(data);
                    runHandle.sendMessage( msg);
                }
            }
        }).start();
        //createDir();
        //创建缓存目录
        new AsyncTask<Void, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                // 设置下载目录
                File file = new File(SD_PATH);
                if (!file.exists()) {
                    try {
                        //按照指定的路径创建文件夹
                        file.mkdirs();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        return false;
                    }
                }

                if (!file.exists()) {
                    return false;
                }

                for (int i = 0; i < 10; i++) {
                    SystemClock.sleep(1000);
                    if (!P2PTrans.testStream(BaseCommon.P2P_SERVER_PORT)) {
                        continue;
                    }
                    if (!P2PTrans.enableStream(BaseCommon.P2P_SERVER_PORT, file.getPath(), file.getPath(), "8aaf88937afbd0826b2c6c3c5d4e01b3")) {
                        continue;
                    }
                    return true;
                } // end of for
                return false;
            }

            @Override
            protected void onPostExecute(Boolean succeed) {
                if (succeed == true) {
                    runHandle.sendEmptyMessage(2300);
                } else {
                    runHandle.sendEmptyMessage(2301);
                }
            }
        }.execute();
    }

	/**
	 *	设置全局的 Refresh 刷新样式
	 */
	public static void initRefresh(){
		//设置全局的Header构建器
		SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
			@Override
			public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
				layout.setPrimaryColorsId(R.color.title_color, android.R.color.white);//全局设置主题颜色
				ClassicsHeader classicsHeader= new ClassicsHeader(context);
				//classicsHeader.setLastUpdateText("");
				//classicsHeader.REFRESH_HEADER_LOADING="这波什么操作呢";
				return classicsHeader;//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
//				return new BezierRadarHeader(context);
//				StoreHouseHeader storeHouseHeader= new StoreHouseHeader(context);
//				return new TaurusHeader(context);
			}
		});
		//设置全局的Footer构建器
		SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
			@Override
			public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
				//指定为经典Footer，默认是 BallPulseFooter
//				return new ClassicsFooter(context).setDrawableSize(20);
				return new BallPulseFooter(context);
			}
		});
	}

	//////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////// ACTIVITY //////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////

	public static List<Activity> activitys=new ArrayList<Activity>();

	public static Activity getLastActivity(){
		if(activitys.size()>0) {
			return activitys.get(activitys.size() - 1);
		}
		return null;
	}

	public static void addActivity(Activity aty) {
		activitys.add(aty);
	}
	public static void removeActivity(Activity aty) {
		activitys.remove(aty);
	}


	/**
	 * 删除除了 activity 的其他所有activity
	 * @param activity
	 */
	public static void removeActivityBy(Activity activity){
		for(Activity activity1:activitys){
			if(activity1==activity){
				continue;
			}
			activity1.finish();
		}
		activitys.clear();
		activitys.add(activity);
	}

	/**
	 * 结束所有Activity 重启应用
	 */
	public static void finishAllActivity() {
//		final Intent intent = context.getPackageManager()
//				.getLaunchIntentForPackage(context.getPackageName());
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		context.startActivity(intent);
//        Intent mStartActivity = new Intent(context, RunActivity.class);
//        int mPendingIntentId = 123456;
//        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
//        AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
//        System.exit(0);


//		context.stopService(new Intent(context,P2PEngineServer.class));
		for (int i = 0, size = activitys.size(); i < size; i++) {
			if (null != activitys.get(i)) {
				activitys.get(i).finish();
			}
		}
		activitys.clear();
		noticeShowBoo=false;
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
//		android.os.Process.killProcess(android.os.Process.myPid());
//		System.exit(0);
	}

	public static void restart(){
//        final Intent intent = context.getPackageManager()
//                .getLaunchIntentForPackage(context.getPackageName());
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        context.startActivity(intent);

//        Intent mStartActivity = new Intent(context, RunActivity.class);
//        int mPendingIntentId = 123456;
//        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
//        AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//        mgr.set(AlarmManager.RTC, 100, mPendingIntent);
//        System.exit(0);


//		AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//		Intent intent = new Intent(context, RunActivity.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.putExtra("crash", true);
//		PendingIntent restartIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, restartIntent); // 1秒钟后重启应用

		noticeShowBoo=false;
		finishAllActivity();
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
//		System.gc();
    }


	//初始化全局 OKGO
	public static void initOkGo() {
		Log.e("TAG","初始 okGo。。。");
		//---------这里给出的是示例代码,告诉你可以这么传,实际使用的时候,根据需要传,不需要就不传-------------//
//        HttpHeaders headers = new HttpHeaders();
//        headers.put("commonHeaderKey1", "commonHeaderValue1");    //header不支持中文，不允许有特殊字符
//        headers.put("commonHeaderKey2", "commonHeaderValue2");
//        HttpParams params = new HttpParams();
//        params.put("commonParamsKey1", "commonParamsValue1");     //param支持中文,直接传,不要自己编码
//        params.put("commonParamsKey2", "这里支持中文参数");
		//----------------------------------------------------------------------------------------//

		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		//log相关
		HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
		loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);        //log打印级别，决定了log显示的详细程度
		loggingInterceptor.setColorLevel(Level.INFO);                               //log颜色级别，决定了log在控制台显示的颜色
		builder.addInterceptor(loggingInterceptor);                                 //添加OkGo默认debug日志
		//第三方的开源库，使用通知显示当前请求的log，不过在做文件下载的时候，这个库好像有问题，对文件判断不准确
		//builder.addInterceptor(new ChuckInterceptor(this));

		//超时时间设置，默认60秒
		builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);      //全局的读取超时时间
		builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);     //全局的写入超时时间
		builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);   //全局的连接超时时间

		//自动管理cookie（或者叫session的保持），以下几种任选其一就行
		//builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));            //使用sp保持cookie，如果cookie不过期，则一直有效
//        builder.cookieJar(new CookieJarImpl(new DBCookieStore(getApplication().getApplicationContext())));              //使用数据库保持cookie，如果cookie不过期，则一直有效
		builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));            //使用内存保持cookie，app退出后，cookie消失

//        //https相关设置，以下几种方案根据需要自己设置
//        //方法一：信任所有证书,不安全有风险
//        HttpsUtils.SSLParams sslParams1 = HttpsUtils.getSslSocketFactory();
//        //方法二：自定义信任规则，校验服务端证书
//        HttpsUtils.SSLParams sslParams2 = HttpsUtils.getSslSocketFactory(new SafeTrustManager());
//        //方法三：使用预埋证书，校验服务端证书（自签名证书）
//        //HttpsUtils.SSLParams sslParams3 = HttpsUtils.getSslSocketFactory(getAssets().open("srca.cer"));
//        //方法四：使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
//        //HttpsUtils.SSLParams sslParams4 = HttpsUtils.getSslSocketFactory(getAssets().open("xxx.bks"), "123456", getAssets().open("yyy.cer"));
//        builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager);
//        //配置https的域名匹配规则，详细看demo的初始化介绍，不需要就不要加入，使用不当会导致https握手失败
//        builder.hostnameVerifier(new SafeHostnameVerifier());

		HttpHeaders headers=new HttpHeaders();
		headers.put("Cache-Control","no-cache");
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		headers.put("Authorization","Bearer "+TOKEN);

		// 其他统一的配置
		// 详细说明看GitHub文档：https://github.com/jeasonlzy/
		OkGo.getInstance().init(application)                           //必须调用初始化
				.setOkHttpClient(builder.build())               //必须设置OkHttpClient
				.setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
				.setCacheTime(-1)   //全局统一缓存时间，默认永不过期，可以不传
				.setRetryCount(0)                     //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
				.addCommonHeaders(headers);            //全局公共头
//                .addCommonParams(params);                       //全局公共参数
	}

	//状态栏隐藏
	public static void onWindowFocusChanged(boolean hasFocus,Activity activity){
		//设置隐藏状态栏
		if (hasFocus && Build.VERSION.SDK_INT >= 19) {
			View decorView = activity.getWindow().getDecorView();
			decorView.setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
							| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_FULLSCREEN
							| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		}
//        StatusBarUtil.setTransparent(activity);
//		StatusBarUtil.setTranslucent(activity,20);

	}

	//弹窗
	public static void showips(String msg,Context context1,String toLogin){
		if(context1==null){
			Activity activity= getLastActivity();
			context1= activity;
			if(activity==null){
				context1=context;
			}
		}
		Intent intent=new Intent(context1,TipsPopupActivity.class);
		intent.putExtra("content",msg);
		intent.putExtra("toLogin",toLogin);
		context1.startActivity(intent);
	}

	//公告弹窗
	public static void showips(String title,String msg,Context context1){
		if(context1==null){
			Activity activity= getLastActivity();
			context1= activity;
			if(context1==null){
				context1=context;
			}
		}
		Intent intent=new Intent(context1,NoticeTipsPopupActivity.class);
		intent.putExtra("content",msg);
		intent.putExtra("title",title);
		context1.startActivity(intent);
	}
	public static boolean noticeShowBoo=false;
	public static void getNotice(){
		if(noticeShowBoo){
			return;
		}
		ApiFactory.getInstance().<NoticeAlertBean>getNoticeAlert(new AGCallback<NoticeAlertBean>() {
			@Override
			public void onNext(NoticeAlertBean noticeAlertBean) {
			noticeShowBoo=true;
			NoticeAlertBean.NoticeItemData noticeItemData= noticeAlertBean.getData();
			showips(noticeItemData.getTitle(),noticeItemData.getText(),null);
			}
		});
	}

}
