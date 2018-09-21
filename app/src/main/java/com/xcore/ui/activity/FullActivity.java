package com.xcore.ui.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.ViviTV.player.widget.DolitBaseMediaPlayer;
import android.media.ViviTV.player.widget.DolitVideoView;
import android.media.ViviTV.player.widget.MediaController;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.common.BaseCommon;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.wang.avi.AVLoadingIndicatorView;
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.base.BaseActivity;
import com.xcore.cache.CacheManager;
import com.xcore.cache.CacheModel;
import com.xcore.cache.DownHandler;

import java.util.Locale;
import java.util.Timer;
import cn.dolit.p2ptrans.P2PTrans;
import tv.danmaku.ijk.media.player.IMediaPlayer;

public class FullActivity extends BaseActivity implements MediaController.IMediaControllerCallback,
        View.OnClickListener,MediaController.OnShownListener,
        MediaController.OnHiddenListener,MediaController.OnSelectQualityListener,DolitBaseMediaPlayer.OnInfoListener {
    private MediaController mMediaController;

    private DolitVideoView player;
    private RelativeLayout rlMain;
    private Timer timer;
//    private OkHttpClient httpClient = new OkHttpClient.Builder().build();
    private boolean isPaused = false;
//    private String streamInfoUrl = "";
    private int playSeconds = 0;
    private ImageButton ibPlayPauseIndicator;

    private String url;
    private String playUrl;

    private RelativeLayout titleLayout;
    private TextView titleTxt;


    private AVLoadingIndicatorView aviLoading;
    private RelativeLayout aviLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initWindowFeature();
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_full;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        rlMain = findViewById(R.id.activity_video);
//        tvInfo = findViewById(R.id.tv_info);
        titleLayout=findViewById(R.id.titleLayout);
        titleTxt=findViewById(R.id.titleTxt);

        aviLayout=findViewById(R.id.aviLayout);
        aviLoading=findViewById(R.id.avi);
        aviLayout.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        url=intent.getStringExtra("url");
        playSeconds=intent.getIntExtra("position",0);
        String title=intent.getStringExtra("title");
        titleTxt.setText(title);

//        toast("position:"+position);

//        streamInfoUrl = intent.getStringExtra("stream_info_url");

        ibPlayPauseIndicator=findViewById(R.id.ib_play_pause_indicator);
        ibPlayPauseIndicator.setOnClickListener(this);

//        mBufferingIndicator = findViewById(R.id.buffering_indicator);
        mMediaController = new MediaController(this);
        mMediaController.setInstantSeeking(false);
        mMediaController.setCallback(this);
        mMediaController.setAutoHideNavigation(false);
        mMediaController.setOnHiddenListener(this);
        mMediaController.setOnShownListener(this);
        mMediaController.setOnSelectQualityListener(this);
        //是否显示高清选择选项
        mMediaController.setShowQualitty(false);

        player = findViewById(R.id.video_view);
        player.setMediaController(mMediaController);
//        player.setMediaBufferingIndicator(mBufferingIndicator);
        player.setIsHardDecode(false);
        player.setOnInfoListener(this);

        //player.start();

        url=P2PTrans.getUrlAddPass(url);
        playUrl=P2PTrans.getTorrentPlayUrl(BaseCommon.P2P_SERVER_PORT, url);

        aviLayout.setVisibility(View.VISIBLE);
        //播放
        player.setVideoPath(playUrl);
        player.requestFocus();
        player.start();
        player.seekTo(playSeconds);

//        playUrl=P2PTrans.getTorrentPlayUrl(BaseCommon.P2P_SERVER_PORT,url);
//        playTorrentUrl(url);

        titleLayout.setVisibility(View.GONE);
        /*****************
         如果播放完一个视频，再播放另一个视频，请调用P2PTrans.stopStream方法停止一个视频或调用P2PTrans.stopAllStream方法停止所有的视频。
         ***************/
        //startRefreshDownloadInfo();

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void initData() {

    }

    private void playTorrentUrl(String urlStr) {
        //urlStr="http://demo.flvurl.cn/dianyunMovieBT/4667496-1hd.mp4.torrent";
        urlStr=P2PTrans.getUrlAddPass(urlStr);
        final String streamUrl =P2PTrans.getTorrentPlayUrl(BaseCommon.P2P_SERVER_PORT, urlStr);

        String url = String.format(Locale.CHINA, "%s:%d/bt/api/stream/start?type=%s&uri=%s", P2PTrans.LOCAL_HOST,
                BaseCommon.P2P_SERVER_PORT, P2PTrans.P2P_TYPE,urlStr);
//
        OkGo.<String>get(url).execute(new StringCallback() {
            @Override
            public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                String v=response.body();
                P2PTrans.StartStreamResult result=new Gson().fromJson(v, P2PTrans.StartStreamResult.class);
                if (null == result || result.getCode() != 0) {
//                    Toast.makeText( __this, "加载出错" + (result == null ? "" : result.getCode()), Toast.LENGTH_SHORT).show();
                    toast("加载出错");
                    return;
                }
                String pathStr=streamUrl;
                aviLayout.setVisibility(View.VISIBLE);
                //播放
                player.setVideoPath(pathStr);
                player.requestFocus();
                player.start();
                player.seekTo(playSeconds);
            }
            @Override
            public void onError(com.lzy.okgo.model.Response<String> response) {
                super.onError(response);
                Log.e("TAG","加载出错");
                toast("加载出错-1");
            }
        });

    }
//    private void playTorrentUrl(String xUrl) {
//        final String streamUrl =xUrl;
//
//        player.setVideoPath(streamUrl);
//        player.requestFocus();
//        player.seekTo(position);
//
//        player.selectScales(2);
//        Log.e("TAG","播放成功...6");
////        String url = String.format(Locale.CHINA, "%s:%d/bt/api/stream/start?type=%s&uri=%s", P2PTrans.LOCAL_HOST,
////                BaseCommon.P2P_SERVER_PORT, P2PTrans.P2P_TYPE, streamUrl);
////        url=P2PTrans.getUrlAddPass(url);
////        OkGo.<String>get(url).execute(new StringCallback() {
////            @Override
////            public void onSuccess(com.lzy.okgo.model.Response<String> response) {
////                String v=response.body();
////                P2PTrans.StartStreamResult result=new Gson().fromJson(v, P2PTrans.StartStreamResult.class);
////                if (null == result || result.getCode() != 0) {
//////                    Toast.makeText( __this, "加载出错" + (result == null ? "" : result.getCode()), Toast.LENGTH_SHORT).show();
////                    toast("加载出错-0");
////                    return;
////                }
//////                Log.e("TAG","准备播放...-5");
//////                String pathStr=streamUrl;
//////                aviLayout.setVisibility(View.VISIBLE);
//////                //播放
//////                player.setVideoPath(pathStr);
//////                player.requestFocus();
////////              mVideoView.start();
//////                player.seekTo(playSeconds);
////                player.setVideoPath(playUrl);
////                player.requestFocus();
////                player.seekTo(position);
////
////                player.selectScales(2);
////                Log.e("TAG","播放成功...6");
////            }
////            @Override
////            public void onError(com.lzy.okgo.model.Response<String> response) {
////                super.onError(response);
////                Log.e("TAG","加载出错");
////                toast("加载出错-1");
////            }
////        });
////        final String torrentUrlType ="turl";// getTorrentType();
////        if (streamUrl == null || streamUrl.isEmpty()) {
////            return;
////        }
////        final String  torrentPassword = "1234";
////        new AsyncTask<Void, Integer, P2PTrans.StartStreamResult>() {
////            @Override
////            protected P2PTrans.StartStreamResult doInBackground(Void... voids) {
////                return P2PTrans.startStream(streamUrl, torrentUrlType, BaseCommon.P2P_SERVER_PORT, torrentPassword);
////            }
////            @Override
////            protected void onPostExecute(P2PTrans.StartStreamResult result) {
////                if (null == result && result.getCode() != 0) {
////                    Toast.makeText(FullActivity.this, "启动任务失败" + (result == null ? "" : result.getCode()),
////                            Toast.LENGTH_SHORT).show();
////                    return;
////                }
////                handler.sendEmptyMessage(0);
////            }
////        }.execute();
//
//    }


    @Override
    public void handleFullScreenClicked(View view) {
//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        } else {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        }
        close();
    }

    @Override
    public void onBack() {
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
    }

    private void close(){
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Intent intent=new Intent();
        intent.putExtra("position",player.getCurrentPosition());
        setResult(100,intent);
        finish();
    }
    @Override
    public void handlePauseStartClicked(View view) {
        if (player.isPlaying()) {
//            ibPlayPauseIndicator.setVisibility(View.GONE);
            ibPlayPauseIndicator.setVisibility(View.GONE);
            ibPlayPauseIndicator.setImageResource(R.drawable.zanting);
        } else {
            ibPlayPauseIndicator.setImageResource(R.drawable.bofang);
            ibPlayPauseIndicator.setVisibility(View.GONE);
//            aviLayout.setVisibility(View.GONE);
        }
        if(player.getmCurrentState()!=3){
            aviLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == ibPlayPauseIndicator) {
            if (!player.isPlaying()) {
                player.start();
                ibPlayPauseIndicator.setImageResource(R.drawable.zanting);
            }else{//暂停
                player.pause();
                ibPlayPauseIndicator.setImageResource(R.drawable.bofang);
                ibPlayPauseIndicator.setVisibility(View.GONE);//visible
            }
            return;
        }
    }

    private void initWindowFeature() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        } else {
//            //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            finish();
//        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        player.selectScales(1);//player.getVideoLayoutScale()
        initWindowFeature();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN &&
//                getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            return true;
//        }
        if(keyCode==KeyEvent.KEYCODE_BACK){
            close();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        MainApplicationContext.onWindowFocusChanged(hasFocus,this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
        if (player != null) {
            playSeconds = player.getCurrentPosition();
            player.pause();
        }
        /**demo的内容，恢复系统其它媒体的状态*/
        //MediaUtils.muteAudioFocus(mContext, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPaused = false;
        if (player != null) {
            player.resume();
            player.seekTo(playSeconds);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            try {
                player.stopPlayback();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if (timer != null) {
            timer.cancel();
        }
//        DownHandler downHandler= CacheManager.getInstance().getDownHandler();
//        CacheModel model= downHandler.getCacheModelByUrl(playUrl);
//        if(model==null){
//            model=new CacheModel();
//            model.setUrl(playUrl);
//            downHandler.stopByUrl(model,false);
//        }
    }
    //显示
    @Override
    public void onShown() {
        titleLayout.setVisibility(View.VISIBLE);
        ibPlayPauseIndicator.setVisibility(View.GONE);//visible
    }
    //隐藏
    @Override
    public void onHidden() {
        titleLayout.setVisibility(View.GONE);
        ibPlayPauseIndicator.setVisibility(View.GONE);
    }

    @Override
    public void onSelectQuality(String msg) {
        Log.e("TAG","清晰度:"+msg);
        playUrl=P2PTrans.getTorrentPlayUrl(BaseCommon.P2P_SERVER_PORT,url);

//        停止当前播放的,记住播放位置
        this.playSeconds=player.getCurrentPosition();
        CacheModel cacheModel=new CacheModel();
        cacheModel.setUrl(this.url);
        CacheManager.getInstance().stopByUrl(cacheModel,false);
        playTorrentUrl(playUrl);
    }

    @Override
    public boolean onInfo(Object mp, int what, int extra) {
        Log.e("TAG", "onInfo:"+what+"--"+extra);
        if (what == DolitBaseMediaPlayer.MEDIA_INFO_BUFFERING_START) {
            Log.e("TAG", "onInfo: (MEDIA_INFO_BUFFERING_START) 开始缓冲");
            aviLayout.setVisibility(View.VISIBLE);
            //在这里显示转圈
            //showBuffingTip();
        } else if (what == DolitBaseMediaPlayer.MEDIA_INFO_BUFFERING_END) {
            Log.e("TAG", "onInfo: (MEDIA_INFO_BUFFERING_END) 缓冲结束");
            //aviPro.hide();
            aviLayout.setVisibility(View.GONE);
            //在这里隐藏转圈
            //hideBuffingTip();
            //            IMediaPlayer
//            int MEDIA_INFO_VIDEO_ROTATION_CHANGED = 10001;
//            int MEDIA_INFO_AUDIO_RENDERING_START  = 10002;
//            int MEDIA_INFO_AUDIO_DECODED_START    = 10003;
//            int MEDIA_INFO_VIDEO_DECODED_START    = 10004;
//            int MEDIA_INFO_OPEN_INPUT             = 10005;
//            int MEDIA_INFO_FIND_STREAM_INFO       = 10006;
//            int MEDIA_INFO_COMPONENT_OPEN         = 10007;

        }else if(what== IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START){//刚进去的时候缓冲读取信息 开始播放
            Log.e("TAG","播放器初始化好了,开始播放");
            //startUpdatePosition();
            aviLayout.setVisibility(View.GONE);
            //设置中间播放按钮

            ibPlayPauseIndicator.setImageResource(R.drawable.zanting);
            ibPlayPauseIndicator.setVisibility(View.GONE);//visible

        }
        return true;
    }


//    private void startRefreshDownloadInfo() {
//        if (timer != null) {
//            timer.cancel();
//            timer = null;
//        }
//
//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if (isPaused) {
//                    return;
//                }
//
//                if (streamInfoUrl == null || streamInfoUrl.length() == 0) {
//                    return;
//                }
//
//                Request request = new Request.Builder().url(streamInfoUrl).get().build();
//                try {
//                    Response response = httpClient.newCall(request).execute();
//                    if (response != null) {
//                        String content = response.body().string();
//                        try {
//                            JSONObject resultJson = new JSONObject(content);
//                            if (resultJson.getInt("code") != 0) {
//                                return;
//                            }
//
//                            JSONObject jsonObject = resultJson.getJSONObject("data");
//                            final String value = String.format(Locale.CHINA, "已经下载：%s，当前进度：%d，剩余时间：%ds，" +
//                                            "下载速度：%s， 上传速度：%s，下载连接数：%d，总连接数：%d，peer数量：%d，seed数量：%d",
//                                    Utils.getDisplayFileSize(jsonObject.getLong("downloadedBytes")), jsonObject.getInt("percent"),
//                                    jsonObject.getInt("secondsRemain"), Utils.getDisplayFileSize(jsonObject.getInt("downloadSpeed")),
//                                    jsonObject.getInt("uploadSpeed"), jsonObject.getInt("downConnectionCount"),
//                                    jsonObject.getInt("connectionCount"), jsonObject.getInt("totalCurrentPeerCount"),
//                                    jsonObject.getInt("totalCurrentSeedCount"));
////                            tvInfo.post(new Runnable() {
////                                @Override
////                                public void run() {
////                                    tvInfo.setText(value);
////                                }
////                            });
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }, 1000, 1000);
//    }
}
