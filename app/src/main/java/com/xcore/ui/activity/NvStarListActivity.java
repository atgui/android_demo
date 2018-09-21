package com.xcore.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nex3z.flowlayout.FlowLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.base.MvpActivity;
import com.xcore.data.bean.NvStar;
import com.xcore.data.bean.NvStarBean;
import com.xcore.presenter.NvStarListPresenter;
import com.xcore.presenter.view.NvStarListView;
import com.xcore.ui.adapter.NvStarRecommendAdapter;
import com.xcore.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NvStarListActivity extends MvpActivity<NvStarListView,NvStarListPresenter> implements NvStarListView {
    private int pageIndex=1;

    private RefreshLayout refreshLayout;
    NvStarRecommendAdapter starThemeAdapter;

    private boolean isMore=true;
    FlowLayout flowLayout;
    String sortTypeStr="";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_nv_star_list;
    }

    private List<String> tabStrList=Arrays.asList("人气最高","影片最多");
    private List<String> tabIds=Arrays.asList("q","u");
    private List<TextView> texts=new ArrayList<>();

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("女优列表");
        setEdit("");
        sortTypeStr=tabIds.get(0);

        flowLayout=findViewById(R.id.flowLayout);
        for(int i=0;i<tabStrList.size();i++){
            setTab(tabStrList.get(i),i);
        }
        changeText(0);

        RecyclerView recyclerView=findViewById(R.id.recyclerView);
        refreshLayout=findViewById(R.id.refreshLayout);

        starThemeAdapter=new NvStarRecommendAdapter(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this,4));
        recyclerView.setAdapter(starThemeAdapter);

        starThemeAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<NvStar>() {
            @Override
            public void onItemClick(NvStar item, int position) {
                Intent intent = new Intent(NvStarListActivity.this, ActressActivity.class);
                String jsonStr=new Gson().toJson(item);
                intent.putExtra("nvItem",jsonStr);

                startActivity(intent);
            }
        });

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pageIndex=1;
                initData();
            }
        });

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
    private void setTab(String str,final int ix){
        TextView txt=ViewUtils.getText(this,str,R.drawable.tag_default);
        txt.setTextSize(16);
        flowLayout.addView(txt);
        texts.add(txt);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageIndex=1;
                sortTypeStr=tabIds.get(ix);
                changeText(ix);
                initData();
            }
        });
    }
    private void changeText(int ix){
        texts.get(ix).setTextColor(getResources().getColor(R.color.title_color));
        //txt.setBackgroundResource(R.drawable.tag_yellow_radius_bg);
        for(int i=0;i<texts.size();i++){
            if(ix==i){
                continue;
            }
            texts.get(i).setTextColor(getResources().getColor(R.color.color_9c9c9c));
            //item.setBackgroundResource(R.drawable.tag_default);
        }
    }

    @Override
    protected void initData() {
        presenter.getNvstars(pageIndex,sortTypeStr);
        pageIndex++;
    }

    @Override
    public NvStarListPresenter initPresenter() {
        return new NvStarListPresenter();
    }

    @Override
    public void onResult(NvStarBean starBean) {
        if(refreshLayout!=null){
            refreshLayout.finishLoadMore();
            refreshLayout.finishRefresh();
        }
        if(starBean.getList()!=null&&starBean.getList().size()<=0){
            isMore=false;
            return;
        }
        if(starBean.getPageIndex()==1){
            starThemeAdapter.setData(starBean.getList());
        }else{
            starThemeAdapter.dataList.addAll(starBean.getList());
            starThemeAdapter.notifyDataSetChanged();
        }

    }
}
