package com.xcore.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xcore.R;
import com.xcore.base.MvpActivity;
import com.xcore.data.bean.ThemeCoverBean;
import com.xcore.data.bean.ThemeRecommendBean;
import com.xcore.data.bean.TypeListBean;
import com.xcore.presenter.ThemePresenter;
import com.xcore.presenter.view.ThemeView;
import com.xcore.ui.adapter.RThemeAdapter;

public class ThemeListActivity extends MvpActivity<ThemeView,ThemePresenter> implements ThemeView {

    private int pageIndex=1;
    private boolean isMore=true;
    private RefreshLayout refreshLayout;
    private RThemeAdapter rThemeAdapter;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_theme_list;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("专题列表");
        setEdit("");

        refreshLayout=findViewById(R.id.refreshLayout);

        ListView listView=findViewById(R.id.listView);

        rThemeAdapter=new RThemeAdapter(this);
        listView.setAdapter(rThemeAdapter);


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
                if(!isMore){
                    refreshLayout.finishLoadMore(1000);
                    return;
                }
                initData();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ThemeRecommendBean.ThemeData item= rThemeAdapter.dataList.get(position);

                Intent intent=new Intent(ThemeListActivity.this,ThemeActivity.class);
                intent.putExtra("shortId",item.getShortId());
                intent.putExtra("name",item.getName());
                intent.putExtra("conver",item.getConverUrl());
                intent.putExtra("desc",item.getRemarks());
                intent.putExtra("maxConver",item.getMaxConverUrl());
                startActivity(intent);
            }
        });

    }

    @Override
    protected void initData() {
        presenter.getThemes(pageIndex);
        pageIndex++;
    }

    @Override
    public ThemePresenter initPresenter() {
        return new ThemePresenter();
    }

    @Override
    public void onResoult(TypeListBean typeListBean) {
    }

    @Override
    public void onRecommendTheme(ThemeRecommendBean recommendBean) {
        if(refreshLayout!=null){
            refreshLayout.finishRefresh(1000);
            refreshLayout.finishLoadMore(1000);
        }
        if(recommendBean.getList().size()<=0){
            isMore=false;
            return;
        }
        if(recommendBean.getPageIndex()==1){
            rThemeAdapter.setData(recommendBean.getList());
        }else{
            rThemeAdapter.dataList.addAll(recommendBean.getList());
            rThemeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRecommendCoverResult(ThemeCoverBean themeCoverBean) {

    }
}
