package com.xcore.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xcore.R;
import com.xcore.base.MvpActivity;
import com.xcore.data.bean.NoticeBean;
import com.xcore.presenter.NoticePresenter;
import com.xcore.presenter.view.NoticeView;
import com.xcore.ui.adapter.NoticeDetailAdapter;

import java.util.Arrays;

public class NoticeDetailActivity extends MvpActivity<NoticeView,NoticePresenter> implements NoticeView {
    RefreshLayout refreshLayout;
    NoticeDetailAdapter noticeDetailAdapter;
    int pageIndex=1;
    boolean isMore=true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_notice_detail;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("公告详情");
        setEdit("");

        refreshLayout=findViewById(R.id.refreshLayout);
        RecyclerView recyclerView=findViewById(R.id.content_recyclerView);

        noticeDetailAdapter=new NoticeDetailAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(noticeDetailAdapter);

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
    }

    @Override
    protected void initData() {
        presenter.getNotices(pageIndex);
        pageIndex++;
    }

    @Override
    public NoticePresenter initPresenter() {
        return new NoticePresenter();
    }

    @Override
    public void onResult(NoticeBean noticeBean) {
        if(refreshLayout!=null){
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadMore();
        }
        if(noticeBean.getPageIndex()==1){
            noticeDetailAdapter.setData(noticeBean.getList());
        }else {
            noticeDetailAdapter.dataList.addAll(noticeBean.getList());
            noticeDetailAdapter.notifyDataSetChanged();
        }
    }
}
