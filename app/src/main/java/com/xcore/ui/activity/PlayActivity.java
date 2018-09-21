package com.xcore.ui.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.ViviTV.player.widget.DolitVideoView;
import android.media.ViviTV.player.widget.MediaController;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.common.BaseCommon;
import com.google.gson.Gson;
import com.nex3z.flowlayout.FlowLayout;
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.base.MvpActivity;
import com.xcore.cache.CacheManager;
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
import com.xcore.presenter.DetailPresenter;
import com.xcore.presenter.view.DetailView;
import com.xcore.ui.adapter.NvStarAdapter;
import com.xcore.ui.adapter.RelatedAdapter;
import com.xcore.ui.enums.DetailTouchType;
import com.xcore.utils.CacheFactory;
import com.xcore.utils.SystemUtils;
import com.xcore.utils.ViewUtils;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import cn.dolit.p2ptrans.P2PTrans;

public class PlayActivity extends MvpActivity<DetailView,DetailPresenter> implements DetailView,View.OnClickListener {
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
    private ImageView conver;

    private Button likeButton;
    private Button noLikeButton;
    private Button collectButton;

    private ImageView pauseBtn;

    private ListView listView;
    private RelatedAdapter relatedAdapter;
    private NvStarAdapter nvStarAdapter;

    private String shortId;

    private boolean isCollect=false;
    private MovieBean movieBean;

    private RelativeLayout converLayout;
    private DolitVideoView mVideoView;

    private MediaController mMediaController;
    private boolean isPaused = false;
    private int playSeconds=0;
    private String playUrl="";

    private String dUrl="";

    private long udpateTimer=30000;

    private Timer uTimer;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String pathStr= (String) msg.obj;
            converLayout.setVisibility(View.GONE);
            mVideoView.setVisibility(View.VISIBLE);

            //播放
            mVideoView.setVideoPath(pathStr);
            mVideoView.requestFocus();
//            mVideoView.start();
            mVideoView.seekTo(playSeconds);

            startUpdatePosition();
//            startRefreshDownloadInfo();
        }
    };

    //更新进度到数据库
    private void startUpdatePosition(){
        uTimer=new Timer();
        uTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                int position=mVideoView.getCurrentPosition();
                Log.e("TAG","更新当前进度:"+position);
                presenter.updateCollect(movieBean,DetailTouchType.TOUCH_RECODE,position);
            }
        },10000,udpateTimer);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_play;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        Intent intent= getIntent();
        shortId=intent.getStringExtra("shortId");
        String positon= intent.getStringExtra("position");
        if(positon!=null&&positon.length()>0){
            playSeconds=Integer.valueOf(positon);
        }

        dUrl=intent.getStringExtra("dUrl");

        //初始化播放器信息
        mVideoView=findViewById(R.id.video_view);

        mMediaController = new MediaController(this);
        mMediaController.setInstantSeeking(false);
        mVideoView.setMediaController(mMediaController);
//        mVideoView.setMediaBufferingIndicator(mBufferingIndicator);
        mVideoView.setIsHardDecode(false);
        mVideoView.setVisibility(View.GONE);

        converLayout=findViewById(R.id.converLayout);

        conver=findViewById(R.id.conver);
        txtTitle=findViewById(R.id.txt_title);
        txtDate=findViewById(R.id.txt_date);
        txtTime=findViewById(R.id.txt_time);
        txtPlay=findViewById(R.id.txt_play);
        likeButton=findViewById(R.id.btn_like);
        noLikeButton=findViewById(R.id.btn_noLike);
        collectButton= findViewById(R.id.btn_collect);
        txtDesc=findViewById(R.id.txt_desc);
        pauseBtn=findViewById(R.id.pauseBtn);
        pauseBtn.setOnClickListener(this);

        detailLayout=findViewById(R.id.detailLayout);
        txtXiangQing=findViewById(R.id.txt_xiangqing);
        listView=findViewById(R.id.listView);
        flowLayout=findViewById(R.id.bqFlowLayout);
        recyclerView=findViewById(R.id.recyclerView);

        nvStarAdapter=new NvStarAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setAdapter(nvStarAdapter);
        nvStarAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<NvStar>() {
            @Override
            public void onItemClick(NvStar item, int position) {
                Intent intent = new Intent(PlayActivity.this, ActressActivity.class);

                String nvStr=new Gson().toJson(item);
                intent.putExtra("nvItem",nvStr);

                startActivity(intent);
            }
        });

        relatedAdapter=new RelatedAdapter(this);
        listView.setAdapter(relatedAdapter);

        initButton();
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
        Button downButton= findViewById(R.id.btn_down);
        Drawable[] downDs= downButton.getCompoundDrawables();
        downDs[1].setBounds(0,0,downButton.getResources().getDimensionPixelSize(R.dimen._30),
                downButton.getResources().getDimensionPixelSize(R.dimen._30));
        downButton.setCompoundDrawables(downDs[0],downDs[1],downDs[2],downDs[3]);
        downButton.setOnClickListener(this);

        //分享
        Button shareButton= findViewById(R.id.btn_share);
        Drawable[] shareDs= shareButton.getCompoundDrawables();
        shareDs[1].setBounds(0,0,shareButton.getResources().getDimensionPixelSize(R.dimen._30),
                shareButton.getResources().getDimensionPixelSize(R.dimen._30));
        shareButton.setCompoundDrawables(shareDs[0],shareDs[1],shareDs[2],shareDs[3]);
        shareButton.setOnClickListener(this);

        //收藏
        collectButton.setOnClickListener(this);

        onUpdateCollect();
    }
    //更新收藏资源
    private void setCollectResource(){
        Drawable drawable=null;
        if(isCollect==true){
            drawable=getResources().getDrawable(R.drawable.type_collected);
        }else{
            drawable=getResources().getDrawable(R.drawable.collet_item);
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
            int v=movieBean.getPraise()+1;
            movieBean.setPraise(v);
            likeButton.setText(v+"");
        }else if(movieBean.getPraiseState()==3){
            drawable = getResources().getDrawable(R.drawable.no_like_select);
            noLikeButton.setCompoundDrawables(null, drawable, null, null);
            Drawable[] noLikeDs= noLikeButton.getCompoundDrawables();
            noLikeDs[1].setBounds(0,0,noLikeButton.getResources()
                            .getDimensionPixelSize(R.dimen._30),
                    noLikeButton.getResources().getDimensionPixelSize(R.dimen._30));
            noLikeButton.setCompoundDrawables(noLikeDs[0],noLikeDs[1],noLikeDs[2],noLikeDs[3]);
            int v=movieBean.getDislike()+1;
            movieBean.setDislike(v);
            noLikeButton.setText(v+"");
        }
    }
    @Override
    protected void initData() {
        presenter.getDetail(shortId);
    }

    @Override
    public void onBack() {
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public DetailPresenter initPresenter() {
        return new DetailPresenter();
    }

    @Override
    public void onDetailResult(DetailBean detailBean) {
        if(detailBean.getStatus()!=200){
            toast(detailBean.getMessage());
            return;
        }
        movieBean=detailBean.getData();
        movieBean.setCollect(isCollect);
        CacheFactory.getInstance().getImage(this,conver,movieBean.getConverUrl());

        txtTitle.setText(movieBean.getTitle());
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
        for(final CategoriesBean item:categoriesBeans){
            TextView textView= ViewUtils.getText(this,item.getName(),R.drawable.tag_feedback_huifu);
            textView.setTextColor(getResources().getColor(R.color.color_9c9c9c));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //到标签
                    Intent intent=new Intent(PlayActivity.this,TagActivity.class);
                    intent.putExtra("tag",item.getName());
                    startActivity(intent);
                }
            });
            flowLayout.addView(textView);
        }

        nvStarAdapter.setData(detailBean.getData().getActorList());
        List<RelatedBean> relatedBeanList=detailBean.getData().getRelated();
        relatedAdapter.setData(relatedBeanList);

        setHeigth(listView);

        setLikeResource();//更新喜欢、踩
    }

    @Override
    public void onGetPathResult(PathBean pathBean) {
        //点击播放得到路径
        if(pathBean.getStatus()!=200){
            toast(pathBean.getMessage());
            return;
        }
        //播放
        String url="http://23.234.12.131:808/ABP722.torrent";//"http://23.234.12.131:808/new/tus061.torrent";//"http://23.234.12.131:81/AVKH095.torrent";//ABP740-28.torrent
        playUrl=url;
        playTorrentUrl(playUrl);//pathBean.getData().getPath()

        //加入到观看记录
        presenter.updateCollect(this.movieBean,DetailTouchType.TOUCH_RECODE,0);
    }
    //更新收藏
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
            }else{
                isCollect=false;
            }
        }else{
            isCollect=false;
        }
        setCollectResource();
    }

    @Override
    public void onCacheResult(LikeBean likeBean) {
//        if(likeBean.getStatus()!=200){
//            toast(likeBean.getMessage());
//            return;
//        }
//        if(movieBean==null){
//            toast("下载失败,请重新打开页面");
//            return;
//        }
        DownModel downModel=new DownModel();
        downModel.setShortId(shortId);
        downModel.setName(movieBean.getTitle());
        downModel.setUrl(playUrl);
        downModel.setConver(movieBean.getCover());

        CacheManager.getInstance().downByUrl(downModel);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
        if (mVideoView != null) {
            playSeconds = mVideoView.getCurrentPosition();
            mVideoView.pause();
        }
        /**demo的内容，恢复系统其它媒体的状态*/
        //MediaUtils.muteAudioFocus(mContext, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPaused = false;
        if (mVideoView != null) {
            mVideoView.resume();
            mVideoView.seekTo(playSeconds);
        }
        //if (mVideoView != null) {
        //  mVideoView.resume();
        // }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
        if (uTimer != null) {
            uTimer.cancel();
        }
        uTimer=null;
        new Thread(new Runnable() {
            public void run() {
                P2PTrans.stopAllStream(8777);
            }
        }).start();
    }

    @Override
    public void onLikeResult(LikeBean likeBean,int type) {
        if(likeBean.getStatus()!=200){
            toast(likeBean.getMessage());
            return;
        }
        if(type==1) {
            movieBean.setPraiseState(2);
        }else if(type==2){
            movieBean.setPraiseState(3);
        }
        //更新
        setLikeResource();
    }

    @Override
    public void onError(String msg) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
                presenter.getLike(shortId,1);
                break;
            case R.id.btn_noLike://不喜欢
                if(movieBean==null){
                    return;
                }
                if(movieBean.getPraiseState()!=0){
                    return;
                }
                presenter.getLike(shortId,2);
                break;
            case R.id.btn_down://下载
//                presenter.getCachePath(shortId);
                playUrl="http://23.234.12.131:808/ABP722.torrent";
                onCacheResult(null);

                break;
            case R.id.btn_share://分享
                if(movieBean==null){
                    return;
                }
                SystemUtils.copy(movieBean.getMovieShareText(),this);
                break;
            case R.id.btn_collect://收藏
                presenter.updateCollect(movieBean, DetailTouchType.TOUCH_COLLECT,0);
                break;
            case R.id.pauseBtn://点击播放
                presenter.getPlay(shortId,"");

//                if(dUrl==null||dUrl.equals("")) {
//                    presenter.getPlay(shortId);
//                }else{
//                    PathBean pathBean=new PathBean();
//                    pathBean.setStatus(200);
//                    pathBean.setMessage("success");
//                    PathDataBean pathDataBean=new PathDataBean();
//                    pathDataBean.setPath(dUrl);
//                    pathBean.setData(pathDataBean);
//                    onGetPathResult(pathBean);
//                }
                break;
        }
    }
    //详情
    private void showLayout(){
        showed=!showed;
        if(showed){
            detailLayout.setVisibility(View.VISIBLE);
            txtXiangQing.setText("详情 ↑");
        }else{
            detailLayout.setVisibility(View.GONE);
            txtXiangQing.setText("详情 ↓");
        }
    }
    //播放准备
    private void playTorrentUrl(String urlStr) {
        final String streamUrl = P2PTrans.getTorrentPlayUrl(BaseCommon.P2P_SERVER_PORT, urlStr);
        final String torrentUrlType = "turl";
        if (streamUrl == null || streamUrl.isEmpty()) {
            return;
        }
        final String  torrentPassword = MainApplicationContext.DOLITBT_PASS;
        new AsyncTask<Void, Integer, P2PTrans.StartStreamResult>() {
            @Override
            protected P2PTrans.StartStreamResult doInBackground(Void... voids) {
                return P2PTrans.startStream(streamUrl, torrentUrlType, BaseCommon.P2P_SERVER_PORT, torrentPassword);
            }
            @Override
            protected void onPostExecute(P2PTrans.StartStreamResult result) {
                if (null == result && result.getCode() != 0) {
                    Toast.makeText(PlayActivity.this, "启动任务失败" + (result == null ? "" : result.getCode()),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Message msgv=new Message();
                msgv.obj=streamUrl;

                handler.sendMessage(msgv);
            }
        }.execute();
    }


    //进度
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
//
//                            Log.e("TAG",value);
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
