package com.xcore.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.base.MvpActivity;
import com.xcore.cache.CacheManager;
import com.xcore.cache.beans.CacheBean;
import com.xcore.cache.beans.CacheType;
import com.xcore.presenter.CollectPresenter;
import com.xcore.presenter.view.CollectView;
import com.xcore.ui.adapter.Recode1Adapter;
import com.xcore.ui.decorations.LinearDividerItemDecoration;

import java.util.List;

public class CollectActivity extends MvpActivity<CollectView,CollectPresenter> implements CollectView{
    private RefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private Recode1Adapter recodeAdapter;
    private LinearLayout bottomLayout;

    private boolean selected=false;
    private Button btnAll;
    private Button btnDel;

    private boolean isEdit=false;
    private int pageIndex=1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_collect;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("我的收藏");
        setEdit("编辑",new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEdit();
            }
        });

        bottomLayout=findViewById(R.id.bottomLayout);
        btnAll=findViewById(R.id.btn_all);
        btnDel=findViewById(R.id.btn_del);

        refreshLayout=findViewById(R.id.refreshLayout);
        recyclerView=findViewById(R.id.recyclerView);

        bottomLayout.setVisibility(View.GONE);

        recodeAdapter=new Recode1Adapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(recodeAdapter);
        recyclerView.addItemDecoration(new LinearDividerItemDecoration(this,1,1,getResources().getColor(R.color.color_black)));

        recodeAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<CacheBean>() {
            @Override
            public void onItemClick(CacheBean item, int position) {
//                Intent intent=new Intent(CollectActivity.this,VideoActivity.class);
//                intent.putExtra("shortId",item.getShortId());
//                startActivity(intent);
            }
        });

//        editBt.setOnClickListener();
        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAll();
            }
        });
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });

        initData();
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
    public CollectPresenter initPresenter() {
        return new CollectPresenter();
    }

    //全选
    private void selectAll(){
        selected=!selected;
        recodeAdapter.allSelect(selected);
    }
    //删除
    private void delete(){
        List<CacheBean> cacheBeans=recodeAdapter.dataList;
        for(CacheBean item:cacheBeans){
            if(item.selected){
                item.settDelete("0");
                item.setPlayPosition("0");
                CacheManager.getInstance().getDbHandler().update(item);
                presenter.deleteCollect(item.getShortId());
            }
        }
        pageIndex=1;
        initData();
    }
    private void setEdit(){
        isEdit=!isEdit;
        if(isEdit){
            bottomLayout.setVisibility(View.VISIBLE);
//            editBt.setText("取消");
            setEdit("取消");
        }else{
            bottomLayout.setVisibility(View.GONE);
            setEdit("编辑");
        }
        recodeAdapter.allShow(isEdit);
    }

    //初始化
    @Override
    public void initData(){
        List<CacheBean> cacheBeans= CacheManager.getInstance().getDbHandler().query(pageIndex,CacheType.CACHE_COLLECT);
        onResult(cacheBeans);
        pageIndex++;
    }
    //得到结果
    public void onResult(List<CacheBean> cacheBeanList){
        if(refreshLayout!=null){
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadMore();
        }
        if(cacheBeanList.size()<=0){
            isEdit=false;
            setEdit("编辑");
            bottomLayout.setVisibility(View.GONE);
        }
        if(pageIndex==1){
            recodeAdapter.setData(cacheBeanList);
        }else{
            recodeAdapter.dataList.addAll(cacheBeanList);
            recodeAdapter.notifyDataSetChanged();
        }
    }
}
