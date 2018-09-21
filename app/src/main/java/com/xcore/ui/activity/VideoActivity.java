package com.xcore.ui.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.ViviTV.player.widget.DolitBaseMediaPlayer;
import android.media.ViviTV.player.widget.DolitVideoView;
import android.media.ViviTV.player.widget.MediaController;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.common.BaseCommon;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.nex3z.flowlayout.FlowLayout;
import com.wang.avi.AVLoadingIndicatorView;
import com.wx.goodview.GoodView;
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.base.MvpActivity;
import com.xcore.cache.CacheManager;
import com.xcore.cache.CacheModel;
import com.xcore.cache.DownHandler;
import com.xcore.cache.DownModel;
import com.xcore.cache.beans.CacheBean;
import com.xcore.cache.beans.CacheType;
import com.xcore.data.bean.CategoriesBean;
import com.xcore.data.bean.DetailBean;
import com.xcore.data.bean.LikeBean;
import com.xcore.data.bean.MovieBean;
import com.xcore.data.bean.NvStar;
import com.xcore.data.bean.PathBean;
import com.xcore.data.bean.RelatedBean;
import com.xcore.ext.ImageViewExt;
import com.xcore.presenter.DetailPresenter;
import com.xcore.presenter.view.DetailView;
import com.xcore.ui.adapter.NvStarAdapter;
import com.xcore.ui.adapter.RelatedAdapter;
import com.xcore.ui.enums.DetailTouchType;
import com.xcore.utils.SystemUtils;
import com.xcore.utils.ViewUtils;

import java.util.List;
import java.util.Locale;
import java.util.Timer;

import cn.dolit.p2ptrans.P2PTrans;
import tv.danmaku.ijk.media.player.IMediaPlayer;

public class VideoActivity extends MvpActivity<DetailView,DetailPresenter> implements DetailView,
        MediaController.IMediaControllerCallback,DolitBaseMediaPlayer.OnInfoListener,MediaController.OnShownListener,
        DolitBaseMediaPlayer.OnErrorListener,MediaController.OnHiddenListener,MediaController.OnSelectQualityListener,
        View.OnClickListener {
    private LinearLayout detailLayout;//详情简介
    private boolean showed=false;
    private TextView txtXiangQing;

    private FlowLayout flowLayout;
    private RecyclerView recyclerView;
    private TextView txtTitle;
    private TextView txtDate;
    private TextView txtTime;
    private TextView txtPlay;
    private TextView txtDesc;
    private ImageViewExt conver;

    private Button likeButton;
    private Button noLikeButton;
    private Button collectButton;
    private Button downButton;
    private Button shareButton;

    private boolean isDown=false;
    private boolean isShare=false;

    private ListView listView;
    private RelatedAdapter relatedAdapter;
    private NvStarAdapter nvStarAdapter;

    private String shortId;

    private boolean isCollect=false;
    private MovieBean movieBean;

    private MediaController mMediaController;
    private boolean isPaused = false;
    private int playSeconds=0;
    private String playUrl="";

    private Timer uTimer;


    private View mBufferingIndicator;

    private DolitVideoView player;
    private Timer timer;
//    private OkHttpClient httpClient = new OkHttpClient.Builder().build();
//    private String streamInfoUrl = "";
    private ImageButton ibPlayPauseIndicator;

    private boolean isFull=false;

    private AVLoadingIndicatorView aviPro;
    private RelativeLayout aviLayout;

    private RelativeLayout backLayout;
    private TextView titleTxt;

    //是否使用服务器给的数据播放或下载
    private boolean useServerData=true;

    private VideoActivity __this;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_video;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        initTransition();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        MainApplicationContext.onWindowFocusChanged(hasFocus,this);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        __this=this;

        findViewById(R.id.vLayout).setVisibility(View.VISIBLE);
        conver=findViewById(R.id.conver);

        backLayout=findViewById(R.id.backLayout);
        titleTxt=findViewById(R.id.titleTxt);

        aviLayout=findViewById(R.id.aviLayout);
        aviPro=findViewById(R.id.avi);
        aviPro.show();

        aviLayout.setVisibility(View.GONE);
        isShowPlay=false;

        backLayout.setVisibility(View.VISIBLE);

        ibPlayPauseIndicator = findViewById(R.id.ib_play_pause_indicator);
        ibPlayPauseIndicator.setOnClickListener(this);
        ibPlayPauseIndicator.setVisibility(View.GONE);

        Intent intent = getIntent();
        shortId=intent.getStringExtra("shortId");
        playUrl=intent.getStringExtra("playUrl");
        playSeconds=intent.getIntExtra("position",0);

        //看看播放进度 比如历史记录跳过来的时候有用到.
//        toast("position:"+playSeconds);

        mBufferingIndicator = findViewById(R.id.buffering_indicator);
        mMediaController = new MediaController(this);
        mMediaController.setInstantSeeking(false);
        mMediaController.setCallback(this);
        mMediaController.setAutoHideNavigation(false);
        mMediaController.setOnShownListener(this);
        mMediaController.setOnHiddenListener(this);
        mMediaController.setOnSelectQualityListener(this);
        //是否显示高清选择选项
        mMediaController.setShowQualitty(false);

        player = findViewById(R.id.video_view);
        player.setMediaController(mMediaController);
        player.setMediaBufferingIndicator(mBufferingIndicator);
        player.setOnInfoListener(this);
        player.setOnErrorListener(this);
        player.setIsHardDecode(false);

//        player.requestFocus();
//        player.setVideoPath(url);
//        player.start();

        /*****************
         如果播放完一个视频，再播放另一个视频，请调用P2PTrans.stopStream方法停止一个视频或调用P2PTrans.stopAllStream方法停止所有的视频。
         ***************/
        //startRefreshDownloadInfo();
//        streamInfoUrl="http://demo.flvurl.cn/dianyunMovieBT/4667496-1hd.mp4.torrent";
//        playTorrentUrl("http://demo.flvurl.cn/dianyunMovieBT/4667496-1hd.mp4.torrent");

//        conver=findViewById(R.id.conver);
        txtTitle=findViewById(R.id.txt_title);
        txtDate=findViewById(R.id.txt_date);
        txtTime=findViewById(R.id.txt_time);
        txtPlay=findViewById(R.id.txt_play);
        likeButton=findViewById(R.id.btn_like);
        noLikeButton=findViewById(R.id.btn_noLike);
        collectButton= findViewById(R.id.btn_collect);
        txtDesc=findViewById(R.id.txt_desc);

        detailLayout=findViewById(R.id.detailLayout);
        txtXiangQing=findViewById(R.id.txt_xiangqing);
        txtXiangQing.setOnClickListener(this);
        findViewById(R.id.img_point).setOnClickListener(this);

        listView=findViewById(R.id.listView);
        flowLayout=findViewById(R.id.bqFlowLayout);
        recyclerView=findViewById(R.id.recyclerView);

        nvStarAdapter=new NvStarAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setAdapter(nvStarAdapter);
        nvStarAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<NvStar>() {
            @Override
            public void onItemClick(NvStar item, int position) {
            Intent intent = new Intent(__this, ActressActivity.class);
            String nvStr=new Gson().toJson(item);
            intent.putExtra("nvItem",nvStr);
            startActivity(intent);
            }
        });

        relatedAdapter=new RelatedAdapter(this);
        listView.setAdapter(relatedAdapter);

        findViewById(R.id.btn_play).setOnClickListener(this);

        initButton();



        //每次进入时检测空间是否足够 剩余3G 就提示
        Long size=Long.valueOf(1024*1L*1024*1L*1024*1L*3*1L);
        boolean boo=SystemUtils.availableSpaceVerification(size);//小于3G的时候给提示
        if(!boo){//空间不足,请及时清理,以免影响看片。
            MainApplicationContext.showips("观看缓存过多,建议及时清理以便观看更多影片(不影响下载缓存)",this,"toClear");
        }


    }

    private void initTransition() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            postponeEnterTransition();
            ViewCompat.setTransitionName(findViewById(R.id.movieLayout), "transitionImg");
            addTransitionListener();
            startPostponedEnterTransition();
        }
    }


    Transition transition;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean addTransitionListener() {
        transition = getWindow().getSharedElementEnterTransition();
        if (transition != null) {
            transition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    // 动画完成之后 处理你自己的逻辑
                    transition.removeListener(this);
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                }

                @Override
                public void onTransitionPause(Transition transition) {
                }

                @Override
                public void onTransitionResume(Transition transition) {
                }
            });
            return true;
        }
        return false;
    }

    //设置ListView 高度
    public void setHeigth(ListView list) {
        ListAdapter listAdapter = list.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            View listItem = listAdapter.getView(i, null, list);
            //             listItem.measure(LinearLayout.LayoutParams.MATCH_PARENT,0);
            listItem.measure(0,0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = list.getLayoutParams();
        params.height = 50+totalHeight+ (list.getDividerHeight() * (listAdapter.getCount() - 1));
        list.setLayoutParams(params);
//
        ScrollView verticalScrollView= findViewById(R.id.verticalScrollView);
        verticalScrollView.smoothScrollTo(0,0);
    }
    //初始化按钮
    private void initButton(){
        txtXiangQing.setOnClickListener(this);
        detailLayout.setVisibility(View.GONE);

        //喜欢
        Drawable[] likeDs= likeButton.getCompoundDrawables();
        likeDs[1].setBounds(0,0,likeButton.getResources().getDimensionPixelSize(R.dimen._30),
                likeButton.getResources().getDimensionPixelSize(R.dimen._30));
        likeButton.setCompoundDrawables(likeDs[0],likeDs[1],likeDs[2],likeDs[3]);
        likeButton.setOnClickListener(this);

        //不喜欢
        Drawable[] noLikeDs= noLikeButton.getCompoundDrawables();
        noLikeDs[1].setBounds(0,0,noLikeButton.getResources().getDimensionPixelSize(R.dimen._30),
                noLikeButton.getResources().getDimensionPixelSize(R.dimen._30));
        noLikeButton.setCompoundDrawables(noLikeDs[0],noLikeDs[1],noLikeDs[2],noLikeDs[3]);
        noLikeButton.setOnClickListener(this);

        //下载
        downButton= findViewById(R.id.btn_down);
        Drawable[] downDs= downButton.getCompoundDrawables();
        downDs[1].setBounds(0,0,downButton.getResources().getDimensionPixelSize(R.dimen._30),
                downButton.getResources().getDimensionPixelSize(R.dimen._30));
        downButton.setCompoundDrawables(downDs[0],downDs[1],downDs[2],downDs[3]);
        downButton.setOnClickListener(this);

        //分享
        shareButton= findViewById(R.id.btn_share);
        Drawable[] shareDs= shareButton.getCompoundDrawables();
        shareDs[1].setBounds(0,0,shareButton.getResources().getDimensionPixelSize(R.dimen._30),
                shareButton.getResources().getDimensionPixelSize(R.dimen._30));
        shareButton.setCompoundDrawables(shareDs[0],shareDs[1],shareDs[2],shareDs[3]);
        shareButton.setOnClickListener(this);

        //收藏
        collectButton.setOnClickListener(this);

        CacheBean collect1=new CacheBean();
        collect1.settType(CacheType.CACHE_COLLECT);
        collect1.setShortId(shortId);

        //查找是否有收藏
        CacheBean cacheBean= CacheManager.getInstance().getDbHandler().query(collect1);
        if(cacheBean!=null){
            if(cacheBean.gettDelete().equals("1")){
                isCollect=true;
            }else{
                isCollect=false;
            }
        }else{
            isCollect=false;
        }
        setCollectResource();
        //onUpdateCollect();
    }
    //更新收藏资源
    private void setCollectResource(){
        Drawable drawable=null;
        if(isCollect==true){
            drawable=getResources().getDrawable(R.drawable.type_collected);
            collectButton.setTextColor(this.getResources().getColor(R.color.color_ff3158));
        }else{
            drawable=getResources().getDrawable(R.drawable.collet_item);
            collectButton.setTextColor(this.getResources().getColor(R.color.color_9c9c9c));
        }
        collectButton.setCompoundDrawables(null, drawable, null, null);
        Drawable[] collectDs= collectButton.getCompoundDrawables();
        collectDs[1].setBounds(0,0,collectButton.getResources().getDimensionPixelSize(R.dimen._30),
                collectButton.getResources().getDimensionPixelSize(R.dimen._30));
        if(movieBean!=null){
            movieBean.setCollect(isCollect);
        }
        collectButton.setCompoundDrawables(collectDs[0],collectDs[1],collectDs[2],collectDs[3]);
    }
    //更新喜欢或不喜欢资源
    private void setLikeResource(){
        if(movieBean==null){
            return;
        }
        Drawable drawable=null;
        if(movieBean.getPraiseState()==2){
            drawable = getResources().getDrawable(R.drawable.like_select);
            likeButton.setCompoundDrawables(null, drawable, null, null);
            Drawable[] likeDs= likeButton.getCompoundDrawables();
            likeDs[1].setBounds(0,0,likeButton.getResources()
                            .getDimensionPixelSize(R.dimen._30),
                    likeButton.getResources().getDimensionPixelSize(R.dimen._30));
            likeButton.setCompoundDrawables(likeDs[0],likeDs[1],likeDs[2],likeDs[3]);
            int v=movieBean.getPraise();
            likeButton.setText(v+"");
            likeButton.setTextColor(this.getResources().getColor(R.color.title_color));
        }else if(movieBean.getPraiseState()==3){
            drawable = getResources().getDrawable(R.drawable.no_like_select);
            noLikeButton.setCompoundDrawables(null, drawable, null, null);
            Drawable[] noLikeDs= noLikeButton.getCompoundDrawables();
            noLikeDs[1].setBounds(0,0,noLikeButton.getResources()
                            .getDimensionPixelSize(R.dimen._30),
                    noLikeButton.getResources().getDimensionPixelSize(R.dimen._30));
            noLikeButton.setCompoundDrawables(noLikeDs[0],noLikeDs[1],noLikeDs[2],noLikeDs[3]);
            int v=movieBean.getDislike();
            noLikeButton.setText(v+"");
            noLikeButton.setTextColor(this.getResources().getColor(R.color.title_color));
        }
    }
    @Override
    protected void initData() {
        new Handler().postDelayed(new Runnable(){
            public void run() {
                //execute the task
                presenter.getDetail(shortId);
            }
        }, 500);
    }

//    @Override
//    public void onBack() {
//        super.onBack();
//    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("TAG","onPause...");
        isPaused = true;
        if (player != null) {
            playSeconds = player.getCurrentPosition();
            player.pause();
            if (playUrl != null && playUrl.length() > 0){
                Log.e("TAG", "更新当前进度:" + playSeconds);
                presenter.updateCollect(movieBean, DetailTouchType.TOUCH_RECODE, playSeconds);
            }
        }
        /**demo的内容，恢复系统其它媒体的状态*/
        //MediaUtils.muteAudioFocus(mContext, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("TAG","onResume...");
        isPaused = false;
        if (player != null) {
            player.resume();
            player.seekTo(playSeconds);
            if(presenter!=null&&movieBean!=null) {
                Log.e("TAG", "更新当前进度:" + playSeconds);
                presenter.updateCollect(movieBean, DetailTouchType.TOUCH_RECODE, playSeconds);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            //playSeconds=player.getCurrentPosition();
            //Log.e("TAG","更新当前进度:"+playSeconds);
            //presenter.updateCollect(movieBean, DetailTouchType.TOUCH_RECODE,playSeconds);
            try {
                player.stopPlayback();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if (timer != null) {
            timer.cancel();
        }
        if(uTimer!=null){
            uTimer.cancel();
        }
        uTimer=null;

        DownHandler downHandler= CacheManager.getInstance().getDownHandler();
        CacheModel model= downHandler.getCacheModelByUrl(playUrl);
        if(model==null){
            model=new CacheModel();
            model.setUrl(playUrl);
            downHandler.stopByUrl(model,false);
        }
//        new Thread(new Runnable() {
//            public void run() {
//                //P2PTrans.stopAllStream(BaseCommon.P2P_SERVER_PORT);
//            }
//        }).start();
    }

    @Override
    public DetailPresenter initPresenter() {
        return new DetailPresenter();
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN &&
//                getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

//    @Override
//    public void onBackPressed() {
//      /*  if (player != null && player.onBackPressed()) {
//            return;
//        }*/
//        super.onBackPressed();
//        /**demo的内容，恢复设备亮度状态*/
//        //if (wakeLock != null) {
//        //    wakeLock.release();
//        //}
//    }

    @Override
    public void handleFullScreenClicked(View view) {
        //isFull=!isFull;
        Log.e("TAG","是否全屏:"+isFull);
        if(isFull==false){
            isShowPlay=false;
            Intent intent=new Intent(this,FullActivity.class);
            intent.putExtra("url",playUrl);
            playSeconds = player.getCurrentPosition();
            intent.putExtra("position",playSeconds);
//            intent.putExtra("stream_info_url",streamInfoUrl);
            intent.putExtra("title",movieBean.getTitle());
            //startActivity(intent);

            startActivityForResult(intent,100);
        }

//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        } else {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==100&&resultCode==100){
            int position=data.getIntExtra("position",playSeconds);
            playSeconds=position;
            Log.e("TAG","全屏播放到了"+position);
            aviLayout.setVisibility(View.VISIBLE);
            isShowPlay=false;
        }
    }

    @Override
    public void handlePauseStartClicked(View view) {
        isShowPlay=true;
        if (player.isPlaying()) {
//            ibPlayPauseIndicator.setVisibility(View.GONE);
            ibPlayPauseIndicator.setVisibility(View.GONE);
            ibPlayPauseIndicator.setImageResource(R.drawable.zanting);
            if(player.getmCurrentState()!=3){//不是播放中就显示转圈出来
                aviLayout.setVisibility(View.VISIBLE);
            }
        } else {
            ibPlayPauseIndicator.setImageResource(R.drawable.bofang);
            ibPlayPauseIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == ibPlayPauseIndicator) {
            if (!player.isPlaying()) {
                player.start();
                ibPlayPauseIndicator.setVisibility(View.GONE);
//                ibPlayPauseIndicator.setVisibility(View.VISIBLE);
//                ibPlayPauseIndicator.setImageResource(R.drawable.zanting);
            }else{//暂停
                player.pause();
                ibPlayPauseIndicator.setImageResource(R.drawable.bofang);
                ibPlayPauseIndicator.setVisibility(View.GONE);//visible
            }
            return;
        }
        switch (v.getId()){
            case R.id.img_point:
            case R.id.txt_xiangqing:
                showLayout();
                break;
            case R.id.btn_like://喜欢
                if(movieBean==null){
                    return;
                }
                //判断如果已经点、踩过了就不请求了
                if(movieBean.getPraiseState()!=0){
                    return;
                }
                if(MainApplicationContext.isGuest==true){
                    MainApplicationContext.showips("未登录",this,"toLogin");
                    return;
                }
                presenter.getLike(shortId, 1);
                break;
            case R.id.btn_noLike://不喜欢
                if(movieBean==null){
                    return;
                }
                if(movieBean.getPraiseState()!=0){
                    return;
                }
                if(MainApplicationContext.isGuest==true){
                    MainApplicationContext.showips("未登录",this,"toLogin");
                    return;
                }
                presenter.getLike(shortId, 2);
                break;
            case R.id.btn_down://下载
                if(this.movieBean==null||isDown==true){
                    return;
                }
                if(MainApplicationContext.isGuest==true){
                    MainApplicationContext.showips("未登录",this,"toLogin");
                    return;
                }
                boolean serverSuccess1=MainApplicationContext.serverSuccess;
                if(serverSuccess1==false){//启动服务失败,重新启动
                    MainApplicationContext.startP2PServer();
                    toast("启动下载服务中,请稍后!");
                    return;
                }
                presenter.getCachePath(shortId);
                break;
            case R.id.btn_share://分享
                if(movieBean==null||isShare==true){
                    return;
                }
                if(MainApplicationContext.isGuest==true){
                    MainApplicationContext.showips("未登录",this,"toLogin");
                    return;
                }
                isShare = false;
                boolean boo = SystemUtils.copy(movieBean.getMovieShareText(), this);
                if (boo == true) {
                    toast("复制成功,快去分享吧!", R.color.color_4eb034);
                    shareButton.setTextColor(this.getResources().getColor(R.color.color_4eb034));
                    Drawable drawable = getResources().getDrawable(R.drawable.share_select);
                    //分享
                    shareButton = findViewById(R.id.btn_share);
                    shareButton.setCompoundDrawables(null, drawable, null, null);
                    Drawable[] shareDs = shareButton.getCompoundDrawables();
                    shareDs[1].setBounds(0, 0, shareButton.getResources().getDimensionPixelSize(R.dimen._30),
                            shareButton.getResources().getDimensionPixelSize(R.dimen._30));
                    shareButton.setCompoundDrawables(shareDs[0], shareDs[1], shareDs[2], shareDs[3]);
                } else {
                    toast("复制失败,请复试!", R.color.red_color);
                }
                break;
            case R.id.btn_collect://收藏
                presenter.updateCollect(movieBean, DetailTouchType.TOUCH_COLLECT,0);
                break;
            case R.id.btn_play://点击播放
                //playUrl="http://demo.flvurl.cn/dianyunMovieBT/4667496-1hd.mp4.torrent";
                isShowPlay=false;
                boolean serverSuccess=MainApplicationContext.serverSuccess;
                if(serverSuccess==false){//启动服务失败,重新启动
                    MainApplicationContext.startP2PServer();
                    toast("启动播放服务中,请稍后!");
                    return;
                }
                if(!TextUtils.isEmpty(playUrl)){
                    findViewById(R.id.cLayout).setVisibility(View.GONE);
//                    streamInfoUrl=playUrl;
//                    playTorrentUrl(playUrl);
                    String urlStr=P2PTrans.getUrlAddPass(playUrl);
                    final String streamUrl =P2PTrans.getTorrentPlayUrl(BaseCommon.P2P_SERVER_PORT, urlStr);
                    aviLayout.setVisibility(View.VISIBLE);
                    //播放
                    player.setVideoPath(streamUrl);
                    player.requestFocus();
                    player.start();
                    player.seekTo(playSeconds);

                    //加入到观看记录
                    presenter.updateCollect(this.movieBean,DetailTouchType.TOUCH_RECODE,0);
                    return;
                }else {
                    presenter.getPlay(shortId,"");
                    backLayout.setVisibility(View.VISIBLE);
                }
                break;
        }

    }

    @Override
    public boolean onInfo(Object mp, int what, int extra) {
        Log.e("TAG", "onInfo:"+what+"--"+extra);
        if (what == DolitBaseMediaPlayer.MEDIA_INFO_BUFFERING_START) {
            Log.e("TAG", "onInfo: (MEDIA_INFO_BUFFERING_START) 开始缓冲");
            aviLayout.setVisibility(View.VISIBLE);
            isShowPlay=false;
            //在这里显示转圈
            //showBuffingTip();
        } else if (what == DolitBaseMediaPlayer.MEDIA_INFO_BUFFERING_END) {
            Log.e("TAG", "onInfo: (MEDIA_INFO_BUFFERING_END) 缓冲结束");
            //aviPro.hide();
            aviLayout.setVisibility(View.GONE);
            isShowPlay=true;
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
            Log.e("TAG","开始播放");
            //startUpdatePosition();
            aviLayout.setVisibility(View.GONE);
            //设置中间播放按钮
            isShowPlay=true;
            ibPlayPauseIndicator.setImageResource(R.drawable.zanting);
            ibPlayPauseIndicator.setVisibility(View.GONE);

        }
//        if (mOnInfoListener != null) {
        //onInfo(mp, what, extra);
//        }
        return true;
    }

    @Override
    public void onDetailResult(DetailBean detailBean) {
        movieBean=detailBean.getData();
        movieBean.setCollect(isCollect);

        conver.loadUrl(movieBean.getConverUrl());
//        CacheFactory.getInstance().getImage(this,conver,movieBean.getConverUrl());

        txtTitle.setText(movieBean.getTitle());
        titleTxt.setText(movieBean.getTitle());

        txtDate.setText(movieBean.getDate());
        txtTime.setText(movieBean.getTime());
        txtPlay.setText(movieBean.getPlayCountData());
        txtDesc.setText(movieBean.getDesc());
        //喜不喜欢
        likeButton.setText(movieBean.getPraise()+"");
        noLikeButton.setText(movieBean.getDislike()+"");

        //绑定数据
        flowLayout.removeAllViews();
        List<CategoriesBean> categoriesBeans= detailBean.getData().getTagsList();
        int ix=0;
        for(final CategoriesBean item:categoriesBeans){
            TextView textView= ViewUtils.getText(this,item.getName(),R.drawable.tag_feedback_tiwen);
            textView.setTextColor(getResources().getColor(R.color.title_color));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //到标签
                    Intent intent=new Intent(__this,TagActivity.class);
                    intent.putExtra("tag",item.getName());
                    startActivity(intent);
                }
            });
            flowLayout.addView(textView);
            ix++;
            if(ix>3){
                break;
            }
        }

        nvStarAdapter.setData(detailBean.getData().getActorList());
        List<RelatedBean> relatedBeanList=detailBean.getData().getRelated();
        relatedAdapter.setData(relatedBeanList);

        setHeigth(listView);

        setLikeResource();//更新喜欢、踩
    }

    @Override
    public void onGetPathResult(PathBean pathBean) {

        findViewById(R.id.cLayout).setVisibility(View.GONE);
        //点击播放得到路径
        aviLayout.setVisibility(View.VISIBLE);
        findViewById(R.id.vLayout).setVisibility(View.VISIBLE);
        backLayout.setVisibility(View.VISIBLE);

        //播放
        playUrl=BaseCommon.VIDEO_URL+pathBean.getData().getPath();// "http://demo.flvurl.cn/dianyunMovieBT/4667496-1hd.mp4.torrent";
        if(useServerData==true){
            playUrl=BaseCommon.VIDEO_URL+pathBean.getData().getPath();
        }
//        streamInfoUrl=playUrl;
        playTorrentUrl(playUrl);//pathBean.getData().getPath()

//        playTorrentUrl("http://demo.flvurl.cn/dianyunMovieBT/4667496-1hd.mp4.torrent");
        //加入到观看记录
        presenter.updateCollect(this.movieBean,DetailTouchType.TOUCH_RECODE,0);
    }

    @Override
    public void onUpdateCollect() {
        CacheBean collect1=new CacheBean();
        collect1.settType(CacheType.CACHE_COLLECT);
        collect1.setShortId(shortId);

        //查找是否有收藏
        CacheBean cacheBean= CacheManager.getInstance().getDbHandler().query(collect1);
        if(cacheBean!=null){
            if(cacheBean.gettDelete().equals("1")){
                isCollect=true;
                toast("添加收藏!",R.color.color_ff3158);
            }else{
                isCollect=false;
                toast("取消收藏!",R.color.color_9c9c9c);
            }
        }else{
            isCollect=false;
            toast("取消收藏!");
        }
        setCollectResource();
    }

    @Override
    public void onCacheResult(LikeBean likeBean) {
        toast("添加缓存成功",R.color.color_1296db);
        isDown=true;
        downButton.setTextColor(this.getResources().getColor(R.color.color_1296db));

        Drawable drawable=getResources().getDrawable(R.drawable.dianjhuancun);
        downButton.setCompoundDrawables(null, drawable, null, null);
        Drawable[] collectDs= downButton.getCompoundDrawables();
        collectDs[1].setBounds(0,0,downButton.getResources().getDimensionPixelSize(R.dimen._30),
                downButton.getResources().getDimensionPixelSize(R.dimen._30));
        downButton.setCompoundDrawables(collectDs[0],collectDs[1],collectDs[2],collectDs[3]);

        String xplayUrl=BaseCommon.VIDEO_URL+likeBean.getData();// "http://demo.flvurl.cn/dianyunMovieBT/4667496-1hd.mp4.torrent";//likeBean.getData();

        DownModel downModel=new DownModel();
        downModel.setShortId(shortId);
        downModel.setUrl(xplayUrl);
        downModel.setName(movieBean.getTitle());
        downModel.setConver(movieBean.getCover());

        CacheManager.getInstance().downByUrl(downModel);
    }

    @Override
    public void onLikeResult(LikeBean likeBean, int type) {
        final GoodView goodView = new GoodView(this);

        goodView.setDuration(2000);
        goodView.setAlpha(1,0);
        goodView.setDistance(80);
        if(type==1) {
            movieBean.setPraiseState(2);
            movieBean.setPraise(Integer.valueOf(likeBean.getData()));
            goodView.setTextInfo("+1",getResources().getColor(R.color.title_color),12);
            goodView.show(likeButton);
        }else if(type==2){
            movieBean.setPraiseState(3);
            movieBean.setDislike(Integer.valueOf(likeBean.getData()));
            goodView.setTextInfo("-1",getResources().getColor(R.color.title_color),12);
            goodView.show(noLikeButton);
        }
        //更新
        setLikeResource();
    }

    @Override
    public void onError(String msg) {
//        MainApplicationContext.showips(msg,null);
    }

    //详情
    private void showLayout(){
        showed=!showed;
        ImageView img=findViewById(R.id.img_point);
        if(showed){
            detailLayout.setVisibility(View.VISIBLE);
            //txtXiangQing.setText("详情 ↑");
            img.setImageResource(R.drawable.point_up);
        }else{
            detailLayout.setVisibility(View.GONE);
            //txtXiangQing.setText("详情 ↓");
            img.setImageResource(R.drawable.point_down);
        }
    }
    //播放准备
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
                    toast("加载出错-0");
                    return;
                }

                String pathStr=streamUrl;
                aviLayout.setVisibility(View.VISIBLE);
                //播放
                player.setVideoPath(pathStr);
                player.requestFocus();
//              mVideoView.start();
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

    @Override
    public boolean onError(Object mp, int what, int extra, long currentPosition) {
        Log.e("TAG",what+"--"+extra+":::::"+currentPosition);
        toast("初始加载出错");
        return false;
    }
    private boolean isShowPlay=true;
    //进度显示
    @Override
    public void onShown() {
        backLayout.setVisibility(View.VISIBLE);
        if(isShowPlay) {
            ibPlayPauseIndicator.setVisibility(View.GONE);
        }
    }

    //进度隐藏
    @Override
    public void onHidden() {
        backLayout.setVisibility(View.GONE);
        ibPlayPauseIndicator.setVisibility(View.GONE);
        isShowPlay=false;isShowPlay=true;
    }
    //选择清晰度
    @Override
    public void onSelectQuality(String msg) {
        Log.e("TAg","清晰度:"+msg);
    }

//    private void startRefreshDownloadInfo() {
//        if (timer != null) {
//            timer.cancel();
//            timer = null;
//        }
//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if (isPaused) {
//                    return;
//                }
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
//                            if(content==null||content.length()<=0){
//                                return;
//                            }
//                            JSONObject resultJson = new JSONObject(content);
//                            if (resultJson.getInt("code") != 0) {
//                                return;
//                            }
//
//                            JSONObject jsonObject = resultJson.getJSONObject("data");
//                            if(jsonObject==null){
//                                return;
//                            }
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
