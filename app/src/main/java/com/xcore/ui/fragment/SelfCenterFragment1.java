package com.xcore.ui.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.base.MvpFragment1;
import com.xcore.cache.CacheManager;
import com.xcore.cache.CacheModel;
import com.xcore.cache.DBHandler;
import com.xcore.cache.beans.CacheBean;
import com.xcore.cache.beans.CacheCountBean;
import com.xcore.cache.beans.CacheType;
import com.xcore.data.bean.PlayerBean;
import com.xcore.data.bean.UserInfo;
import com.xcore.ext.ImageViewExt;
import com.xcore.presenter.MePresenter;
import com.xcore.presenter.view.MeView;
import com.xcore.ui.activity.CacheActivity;
import com.xcore.ui.activity.CollectActivity;
import com.xcore.ui.activity.FeedbackActivity;
import com.xcore.ui.activity.GuideActivity;
import com.xcore.ui.activity.HRecodeActivity;
import com.xcore.ui.activity.LoginActivity;
import com.xcore.ui.activity.NoticeDetailActivity;
import com.xcore.ui.activity.SettingActivity;
import com.xcore.ui.activity.SpreadShareActivity;
import com.xcore.ui.activity.UpgradeActivity;
import com.xcore.ui.adapter.DownAdapter;
import com.xcore.ui.adapter.SelfAdapter;
import com.xcore.utils.CacheFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenpengfei on 2017/3/21.
 */
public class SelfCenterFragment1 extends MvpFragment1<MeView,MePresenter> implements MeView,View.OnClickListener{
    private SelfAdapter hositoryAdapter;
    private DownAdapter cacheAdapter;
    private SelfAdapter collectAdapter;

    private ImageView item_avatar;//头像

    private Button gfggButton;
    private Button yjfkButton;
    private Button djtqButton;
    private Button gfqButton;

    private TextView vipInfo;//等级信息
    private ImageView vipImg;//vip res
    private TextView uname;//昵称
    private TextView cacheCountTxt;//缓存次数
    private TextView playCountTxt;//播放次数
    private TextView codeTxt;//邀请码

    private RefreshLayout refreshLayout;

    private LinearLayout toQcode;//二维码
    private LinearLayout converLayout;//广告布局
    private ImageViewExt conver;//广告封面

    private LinearLayout cacheList;//缓存列表
    private LinearLayout collectList;//我的收藏
    private LinearLayout historyList;//历史记录

    private TextView hCountTxt;
    private TextView cCountTxt;
    private TextView dCountTxt;
    private TextView dayCountLabel;

    private RecyclerView collectRecyclerView;
    private RecyclerView cacheRecyclerView;
    private RecyclerView hositoryRecyclerView;

    private PlayerBean playerBean;

    private String toUrl="";

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_self;
    }

    @Override
    protected void initView(View view) {
        collectRecyclerView=view.findViewById(R.id.collectRecyclerView);
        cacheRecyclerView=view.findViewById(R.id.cacheRecyclerView);
        hositoryRecyclerView=view.findViewById(R.id.hositoryRecyclerView);
        refreshLayout=view.findViewById(R.id.refreshLayout);

        this.initButton(view);
        view.findViewById(R.id.setting).setOnClickListener(this);
        view.findViewById(R.id.upgradeLayout).setOnClickListener(this);

        dayCountLabel=view.findViewById(R.id.day_countLabel);
        vipImg=view.findViewById(R.id.vipImg);
        vipInfo=view.findViewById(R.id.vipInfo);
        item_avatar=view.findViewById(R.id.item_avatar);
        uname=view.findViewById(R.id.uname);
        cacheCountTxt=view.findViewById(R.id.cacheCountTxt);
        playCountTxt=view.findViewById(R.id.playCountTxt);
        codeTxt=view.findViewById(R.id.codeTxt);

        codeTxt.setOnClickListener(this);

        hCountTxt=view.findViewById(R.id.hCountTxt);
        cCountTxt=view.findViewById(R.id.cCountTxt);
        dCountTxt=view.findViewById(R.id.dCountTxt);

        toQcode=view.findViewById(R.id.toQcode);
        conver=view.findViewById(R.id.conver);
        converLayout=view.findViewById(R.id.converLayout);
        converLayout.setVisibility(View.GONE);

        cacheList=view.findViewById(R.id.cacheList);
        cacheList.setOnClickListener(this);

        collectList=view.findViewById(R.id.collectList);
        collectList.setOnClickListener(this);

        historyList=view.findViewById(R.id.historyList);
        historyList.setOnClickListener(this);

        conver.setOnClickListener(this);
        toQcode.setOnClickListener(this);

        collectAdapter=new SelfAdapter(getContext());
        collectRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        collectRecyclerView.setAdapter(collectAdapter);
        collectAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<CacheBean>() {
            @Override
            public void onItemClick(CacheBean item, int position) {
//                Intent intent = new Intent(getActivity(), VideoActivity.class);
//                intent.putExtra("shortId",item.getShortId());
//                startActivity(intent);
            }
        });

        hositoryAdapter=new SelfAdapter(getContext());
        hositoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        hositoryRecyclerView.setAdapter(hositoryAdapter);
        hositoryAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<CacheBean>() {
            @Override
            public void onItemClick(CacheBean item, int position) {
//                Intent intent = new Intent(getActivity(), VideoActivity.class);
//                intent.putExtra("shortId",item.getShortId());
//                Integer position1=Integer.valueOf(item.getPlayPosition());
//                intent.putExtra("position",position1);
//                startActivity(intent);
            }
        });

        cacheAdapter=new DownAdapter(getContext());
                cacheRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL,false));
        cacheRecyclerView.setAdapter(cacheAdapter);
        cacheAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<CacheModel>() {
            @Override
            public void onItemClick(CacheModel item, int position) {
                Intent intent=new Intent(getContext(),CacheActivity.class);
                startActivity(intent);
            }
        });

        //下拉刷新
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshData();
            }
        });
        refreshLayout.setEnableLoadMore(false);

        //更新个人信息
//        if(this.playerBean!=null) {
//            //更新信息
//            UserInfo userInfo = this.playerBean.getData();
//            updateInfo(userInfo);
//        }
    }
    //初始化总数
    void initCount(){
        hCountTxt.setText("目前观看过0部");
        cCountTxt.setText("目前已收藏0部");
        dCountTxt.setText("目前已缓存0部");

        List<CacheCountBean> cacheCountBeans=new ArrayList<>();
        //得到总数
        DBHandler dbHandler= CacheManager.getInstance().getDbHandler();
        List<CacheCountBean> cacheBeans=dbHandler.getCacheCount();
        cacheCountBeans.addAll(cacheBeans);

        List<CacheCountBean> downs =dbHandler.getDownCount();
        cacheCountBeans.addAll(downs);

        for(CacheCountBean item:cacheCountBeans){
            if(item.vt.equals(CacheType.CACHE_RECODE)){
                hCountTxt.setText("目前观看过"+item.count+"部");
            }else if(item.vt.equals(CacheType.CACHE_COLLECT)){
                cCountTxt.setText("目前已收藏"+item.count+"部");
            }else if(item.vt.equals(CacheType.CACHE_DOWN)){
                dCountTxt.setText("目前已缓存"+item.count+"部");
            }
        }
    }

    //初始化观看记录
    void initHository(){
        List<CacheBean> cacheBeanList=CacheManager.getInstance().getDbHandler().query(1, CacheType.CACHE_RECODE);
        Log.e("TAG",cacheBeanList.toString());
        if(cacheBeanList!=null&&cacheBeanList.size()>0){
            hositoryAdapter.setData(cacheBeanList);
            hositoryRecyclerView.setVisibility(View.VISIBLE);
        }else{
            hositoryRecyclerView.setVisibility(View.GONE);
        }
    }

    //初始化缓存
    void initCache(){
        List<CacheModel> cacheModels= CacheManager.getInstance().getDataList();
        cacheAdapter.setData(cacheModels);
    }

    //初始化收藏
    void initCollect(){
        List<CacheBean> cacheBeanList=CacheManager.getInstance().getDbHandler().query(1, CacheType.CACHE_COLLECT);
        if(cacheBeanList!=null&&cacheBeanList.size()>0){
            collectAdapter.setData(cacheBeanList);
            collectRecyclerView.setVisibility(View.VISIBLE);
        }else{
            collectRecyclerView.setVisibility(View.GONE);
        }
    }

    //初始化按钮信息
    private void initButton(View view){
        gfggButton=view.findViewById(R.id.btn_gfgg);
        Drawable[] gfggDs= gfggButton.getCompoundDrawables();
        gfggDs[1].setBounds(0,0,gfggButton.getResources().getDimensionPixelSize(R.dimen._30),
                gfggButton.getResources().getDimensionPixelSize(R.dimen._30));
        gfggButton.setCompoundDrawables(gfggDs[0],gfggDs[1],gfggDs[2],gfggDs[3]);
        gfggButton.setOnClickListener(this);

        yjfkButton=view.findViewById(R.id.btn_yjfk);
        Drawable[] yjfkDs= yjfkButton.getCompoundDrawables();
        yjfkDs[1].setBounds(0,0,yjfkButton.getResources().getDimensionPixelSize(R.dimen._30),
                yjfkButton.getResources().getDimensionPixelSize(R.dimen._30));
        yjfkButton.setCompoundDrawables(yjfkDs[0],yjfkDs[1],yjfkDs[2],yjfkDs[3]);
        yjfkButton.setOnClickListener(this);

        djtqButton=view.findViewById(R.id.btn_djtq);
        Drawable[] djtqDs= djtqButton.getCompoundDrawables();
        djtqDs[1].setBounds(0,0,djtqButton.getResources().getDimensionPixelSize(R.dimen._30),
                djtqButton.getResources().getDimensionPixelSize(R.dimen._30));
        djtqButton.setCompoundDrawables(djtqDs[0],djtqDs[1],djtqDs[2],djtqDs[3]);
        djtqButton.setOnClickListener(this);

        gfqButton=view.findViewById(R.id.btn_gfq);
        Drawable[] gfqDs= gfqButton.getCompoundDrawables();
        gfqDs[1].setBounds(0,0,gfqButton.getResources().getDimensionPixelSize(R.dimen._30),
                gfqButton.getResources().getDimensionPixelSize(R.dimen._30));
        gfqButton.setCompoundDrawables(gfqDs[0],gfqDs[1],gfqDs[2],gfqDs[3]);
        gfqButton.setOnClickListener(this);
    }

    private void refreshData(){
        presenter.getUserInfo();

        //初始化本地缓存
        initCount();
        initHository();
        initCache();
        initCollect();
    }

    @Override
    public MePresenter initPresenter() {
        return new MePresenter();
    }

    @Override
    public void onClick(View v) {
        Intent intent=null;
        switch (v.getId()){
            case R.id.setting:
            intent=new Intent(getContext(), SettingActivity.class);
            break;
            case R.id.codeTxt:
                if(MainApplicationContext.isGuest==true){
                    intent=new Intent(getContext(), LoginActivity.class);
                    intent.putExtra("toLogin","comeLogin");
                }
                break;
            case R.id.upgradeLayout:
                intent=new Intent(getContext(), UpgradeActivity.class);
                //把得到的参数传过去
                String pStr=new Gson().toJson(playerBean);
                intent.putExtra("user",pStr);
                break;
            case R.id.conver:
                //跳网页
                intent = new Intent( Intent.ACTION_VIEW );
                intent.setData( Uri.parse( toUrl) ); //这里面是需要调转的rul
                intent = Intent.createChooser( intent, null );
                break;
            case R.id.toQcode:
                if(MainApplicationContext.isGuest==true){
                    intent=new Intent(getContext(), LoginActivity.class);
                    intent.putExtra("toLogin","comeLogin");
                }else {
                    intent = new Intent(getContext(), SpreadShareActivity.class);
                }
                break;
            case R.id.cacheList:
                intent=new Intent(getContext(), CacheActivity.class);
                break;
            case R.id.collectList:
                intent=new Intent(getContext(), CollectActivity.class);
                break;
            case R.id.historyList:
                intent=new Intent(getContext(), HRecodeActivity.class);
                break;
            case R.id.btn_gfgg:
                intent=new Intent(getContext(),NoticeDetailActivity.class);
                break;
            case R.id.btn_yjfk:
                intent=new Intent(getContext(),FeedbackActivity.class);
                break;
            case R.id.btn_djtq:
                intent=new Intent(getContext(),GuideActivity.class);
                break;
            case R.id.btn_gfq://跳网页
                intent = new Intent( Intent.ACTION_VIEW );//https://www.potato.im/1avco1
                intent.setData( Uri.parse("https://www.potato.im/1avco1") ); //这里面是需要调转的rul
                intent = Intent.createChooser( intent, null );
                break;
        }
        if(intent!=null){
            startActivity(intent);
        }
    }
    //更新信息
    private void updateInfo(UserInfo userInfo){
        //设置头像
        CacheFactory.getInstance().getImage(getContext(),item_avatar,userInfo.getHeadUrl());

        vipImg.setImageResource(userInfo.getRes(userInfo.getVip()));
        vipInfo.setText(userInfo.getVipStr(userInfo.getVip()));
        uname.setText(userInfo.getName());
        playCountTxt.setText(userInfo.getPlayStr());
        cacheCountTxt.setText(userInfo.getCacheStr());

        if(userInfo.getShareCode().length()>0) {
            codeTxt.setText("邀请码:" + userInfo.getShareCode());
        }else{
            codeTxt.setText("登录/注册");
        }
        converLayout.setVisibility(View.GONE);
        String acStr=userInfo.getActivityText();
        if(acStr!=null&&acStr.length()>0&& !TextUtils.isEmpty(acStr)){
            converLayout.setVisibility(View.VISIBLE);
            int index=acStr.indexOf("|");
            if(index>0) {
                //图片封面
                String converUrl = acStr.substring(0, index);
                //image.av9.tv/images/product/1807/180715/180715004/BLK357_C_c.jpg
                conver.loadUrl(converUrl);
//                CacheFactory.getInstance().getImage(getContext(),conver, converUrl);
                //跳转地址
                toUrl = acStr.substring(index + 1);
            }
        }
        if(userInfo.isSuperVIP()){
            dayCountLabel.setVisibility(View.GONE);
        }else{
            dayCountLabel.setVisibility(View.VISIBLE);
        }
        if(userInfo.getUnread()!=null&&userInfo.getUnread()>0){
            String vInfoStr="您的反馈有回复了,快去查看吧!!!";
            MainApplicationContext.showips(vInfoStr,getActivity(),"toFeedbackList");
        }

        String vRecord=userInfo.getAppUserVIPLevelUpgradeRecord();
        if(vRecord==null||"".equals(vRecord)||vRecord.isEmpty()){
            return;
        }
        int lUpgrade=Integer.valueOf(vRecord);
        String vStr=userInfo.getVipStr(lUpgrade);
        String vInfoStr="恭喜升级到"+vStr;
        MainApplicationContext.showips(vInfoStr,getActivity(),"");
    }
    @Override
    public void onResult(PlayerBean playerBean) {
        if(refreshLayout!=null){
            refreshLayout.finishRefresh();
        }
        this.playerBean=playerBean;
        //更新信息
        UserInfo userInfo=playerBean.getData();
        updateInfo(userInfo);

        MainApplicationContext.isGuest=userInfo.tourist;
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        refreshData();
    }

    @Override
    public void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        //refreshData();
    }

}
