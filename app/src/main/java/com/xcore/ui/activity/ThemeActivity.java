package com.xcore.ui.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.base.MvpActivity;
import com.xcore.data.bean.ThemeCoverBean;
import com.xcore.data.bean.ThemeRecommendBean;
import com.xcore.data.bean.TypeListBean;
import com.xcore.data.bean.TypeListDataBean;
import com.xcore.ext.ImageViewExt;
import com.xcore.presenter.ThemePresenter;
import com.xcore.presenter.view.ThemeView;
import com.xcore.ui.adapter.TypeItemAdapter;
import com.xcore.utils.CacheFactory;
import com.xcore.utils.ViewUtils;

public class ThemeActivity extends MvpActivity<ThemeView,ThemePresenter> implements ThemeView {
    private String shortId="";

    private int pageIndex=1;
    private TypeItemAdapter typeItemAdapter;
    private RefreshLayout refreshLayout;
    private boolean isMore=true;

    TextView txtTitle;
    ImageViewExt converImage;
    TextView remark;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_theme;
    }

    @Override
    public String getParamsStr() {
        return "专题页面ID:"+shortId;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
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
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        Intent intent= getIntent();
        shortId=intent.getStringExtra("shortId");

        ImageView img=findViewById(R.id.btn_back);
        ViewUtils.setImageColor(img, ColorStateList.valueOf(Color.WHITE));

        txtTitle= findViewById(R.id.txt_title);
        remark=findViewById(R.id.txt_remark);
        //txtTitle.setText(title);

        converImage=findViewById(R.id.conver);
        //CacheFactory.getInstance().getImage(this,converImage,maxConver);

        refreshLayout=findViewById(R.id.refreshLayout);

        RecyclerView recyclerView=findViewById(R.id.content_recyclerView);
        typeItemAdapter=new TypeItemAdapter(this);

        StaggeredGridLayoutManager staggeredGridLayoutManager= new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(typeItemAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        typeItemAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<TypeListDataBean>() {
            @Override
            public void onItemClick(TypeListDataBean item, int position) {
//                Intent intent=new Intent(ThemeActivity.this, VideoActivity.class);
//                intent.putExtra("shortId",item.getShortId());
//                startActivity(intent);
            }
        });

        refreshLayout.setEnableRefresh(false);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if(isMore){
                    initData();
                }else{
                    refreshLayout.finishLoadMore(1000);
                }
            }
        });

    }
    private boolean isLoadCover=false;
    @Override
    protected void initData() {
        if(!isLoadCover){
            presenter.getThemeCover(shortId);
            isLoadCover=true;
        }
        presenter.getResult(shortId,pageIndex);
        pageIndex++;
    }

    @Override
    public ThemePresenter initPresenter() {
        return new ThemePresenter();
    }

    @Override
    public void onResoult(TypeListBean typeListBean) {
        if(refreshLayout!=null){
            refreshLayout.finishLoadMore();
        }

        if(typeListBean.getList().size()<=0){
            isMore=false;
            return;
        }
        int index=typeListBean.getPageIndex();
        if(index==1){
            typeItemAdapter.setData(typeListBean.getList());
        }else{
            typeItemAdapter.dataList.addAll(typeListBean.getList());
            typeItemAdapter.notifyDataSetChanged();
        }
        if(index==1){
            converImage.setFocusable(true);
            converImage.setFocusableInTouchMode(true);
            converImage.requestFocus();
        }
    }

    @Override
    public void onRecommendTheme(ThemeRecommendBean recommendBean) {

    }

    @Override
    public void onRecommendCoverResult(ThemeCoverBean themeCoverBean) {
        if(themeCoverBean==null){
            return;
        }
        ThemeRecommendBean.ThemeData data= themeCoverBean.getData();
        String mark="";
        String title="";
        if(data.getRemarks()!=null){
            mark=data.getRemarks();
        }
        if(!TextUtils.isEmpty(data.getTitle())){
            title=data.getTitle();
        }else{
            title=data.getName();
        }
        txtTitle.setText(title);
        remark.setText(mark);

        converImage.loadUrl(data.getMaxConverUrl());

    }
}
