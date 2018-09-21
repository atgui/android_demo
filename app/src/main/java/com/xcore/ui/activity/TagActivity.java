package com.xcore.ui.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.ViewUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.common.BaseCommon;
import com.google.gson.Gson;
import com.nex3z.flowlayout.FlowLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.base.MvpActivity;
import com.xcore.data.bean.CategoriesBean;
import com.xcore.data.bean.TypeListBean;
import com.xcore.data.bean.TypeListDataBean;
import com.xcore.data.bean.TypeTabBean;
import com.xcore.data.utils.DataUtils;
import com.xcore.presenter.ActressPresenter;
import com.xcore.presenter.view.ActressView;
import com.xcore.ui.adapter.TypeItemAdapter;
import com.xcore.utils.DateUtils;


import java.util.ArrayList;
import java.util.List;

import cn.dolit.p2ptrans.P2PTrans;

public class TagActivity extends MvpActivity<ActressView,ActressPresenter> implements ActressView {
    private TypeItemAdapter tagAdapter;
    private String tag="";//可能是多个,用逗号分开
    private int pageIndex=1;
    private String sorttype="";//排序
    private RefreshLayout refreshLayout;
    private boolean isMore=true;

    private LinearLayout empty_layout;
    private LinearLayout rlayout;

    private FlowLayout flowLayout;
    private TextView infoTxt;

    TextView titleTxt;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tag;
    }

    @Override
    public String getParamsStr() {
        return "标签页面"+tag;
    }

    private TextView getText(final CategoriesBean categoriesBean, final int position){
        TextView textView= com.xcore.utils.ViewUtils.getText(this,categoriesBean.getName(),R.drawable.tag_default);// new TextView(this);
        textView.setTextColor(getResources().getColor(R.color.item_txt_color));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            changeTxt(textInfos.get(position),textInfos);
            pageIndex=1;
            sorttype=categoriesBean.getShortId();
            initData();
            }
        });
        return textView;
    }
    private List<TextView> textInfos=new ArrayList<>();
    private void changeTxt(TextView curTxt,List<TextView> txts){
//        curTxt.setBackgroundResource(R.drawable.tag_select_bg);
        curTxt.setTextColor(getResources().getColor(R.color.title_color));
        for(TextView txt:txts){
            if(txt==curTxt){
                continue;
            }
            txt.setTextColor(getResources().getColor(R.color.item_txt_color));
        }
    }

    TypeTabBean typeTabBean;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        infoTxt=findViewById(R.id.infoTxt);
        infoTxt.setVisibility(View.GONE);

        setEdit("筛选",new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TagActivity.this,TagSelectActivity.class);
                List<CategoriesBean> categoriesBeans=new ArrayList<>();

                String[] tags= tag.split(",");
                for(String item:tags){
                    CategoriesBean categoriesBean=new CategoriesBean("",item);
                    categoriesBeans.add(categoriesBean);
                }
                String infos=new Gson().toJson(categoriesBeans);
                intent.putExtra("selectedTxtArr",infos);
//                startActivity(intent);
                startActivityForResult(intent,100);

            }
        });

        refreshLayout=findViewById(R.id.refreshLayout);
        empty_layout=findViewById(R.id.empty_layout);
        rlayout=findViewById(R.id.rlayout);
        flowLayout=findViewById(R.id.flowLayout);

        titleTxt=findViewById(R.id.infoTxt);
        titleTxt.setText(tag);

        rlayout.setVisibility(View.VISIBLE);
        empty_layout.setVisibility(View.GONE);

        Intent intent= getIntent();
        tag=intent.getStringExtra("tag");
        String toTag=intent.getStringExtra("toTag");
        if(toTag!=null&&toTag.length()>0){//从筛选跳过来的
            infoTxt.setVisibility(View.VISIBLE);
            setTitle("筛选结果");
            tag=intent.getStringExtra("key");
            titleTxt.setText(tag);
            pageIndex=1;
            //initData();
        }else {
            setTitle(tag);
        }
        if(DataUtils.typeTabBean!=null) {
            typeTabBean = DataUtils.typeTabBean;
        }
        initTab();

        tagAdapter=new TypeItemAdapter(this);

        RecyclerView content_recyclerView=findViewById(R.id.content_recyclerView);
        content_recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        content_recyclerView.setAdapter(tagAdapter);

        tagAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<TypeListDataBean>() {
            @Override
            public void onItemClick(TypeListDataBean item, int position) {
//                Log.e("TAG","点击了"+position+"个ITEM");
//                //跳到详情页面
//                Intent intent = new Intent(TagActivity.this, VideoActivity.class);
//                intent.putExtra("shortId",item.getShortId());
//                startActivity(intent);
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
            if(isMore==true){
                initData();
            }else{
                refreshLayout.finishLoadMore(1000);
            }
            }
        });
    }

    private void initTab(){
        if(typeTabBean!=null) {
            List<CategoriesBean> categoriesBeans = typeTabBean.getData().getSorttype();
            if (categoriesBeans.size() > 0) {
                for (int i = 0; i < categoriesBeans.size(); i++) {
                    CategoriesBean categoriesBean = categoriesBeans.get(i);
                    TextView txt = getText(categoriesBean, i);
                    flowLayout.addView(txt);
                    textInfos.add(txt);
                }
                sorttype = categoriesBeans.get(0).getShortId();
                changeTxt(textInfos.get(0), textInfos);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100&&resultCode==100){
            infoTxt.setVisibility(View.VISIBLE);
            setTitle("筛选结果");
            tag=data.getStringExtra("key");
            if(tag!=null&&tag.length()>0) {
                titleTxt.setText(tag);
                pageIndex = 1;
                initData();
            }
        }
    }

    @Override
    protected void initData() {
        if(DataUtils.typeTabBean==null){
            presenter.getTags();
            return;
        }else{
            if(typeTabBean==null) {
                typeTabBean=DataUtils.typeTabBean;
                initTab();
            }
        }
        if(tag==null){
            return;
        }
        if(tag.length()<=0){
            return;
        }
        presenter.getTag(tag,sorttype,pageIndex);
        pageIndex++;
    }

    @Override
    public ActressPresenter initPresenter() {
        return new ActressPresenter();
    }

    @Override
    public void onActressResult(TypeListBean typeListBean) {
    }

    @Override
    public void onTagResult(TypeListBean typeListBean) {
        if(refreshLayout!=null){
            refreshLayout.finishLoadMore();
            refreshLayout.finishRefresh();
        }
        if(typeListBean.getList()==null){
            empty_layout.setVisibility(View.VISIBLE);
            rlayout.setVisibility(View.GONE);
            return;
        }
        if(typeListBean.getPageIndex()==1&&typeListBean.getList().size()<=0){
            empty_layout.setVisibility(View.VISIBLE);
            rlayout.setVisibility(View.GONE);
            return;
        }
        rlayout.setVisibility(View.VISIBLE);
        empty_layout.setVisibility(View.GONE);
        if(typeListBean.getList().size()<=0){
            isMore=false;
            return;
        }
        if(typeListBean.getPageIndex()==1){
            tagAdapter.dataList.clear();
        }
        tagAdapter.dataList.addAll(typeListBean.getList());
        tagAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTags(TypeTabBean typeTabBean) {
        initData();
    }

}
