package com.xcore.ui.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.nex3z.flowlayout.FlowLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.base.MvpActivity;
import com.xcore.data.bean.CategoriesBean;
import com.xcore.data.bean.NvStar;
import com.xcore.data.bean.TypeListBean;
import com.xcore.data.bean.TypeListDataBean;
import com.xcore.data.bean.TypeTabBean;
import com.xcore.data.utils.DataUtils;
import com.xcore.presenter.ActressPresenter;
import com.xcore.presenter.view.ActressView;
import com.xcore.ui.adapter.TypeItemAdapter;
import com.xcore.utils.CacheFactory;
import com.xcore.utils.DateUtils;
import com.xcore.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;


public class ActressActivity extends MvpActivity<ActressView,ActressPresenter> implements ActressView {
    private TypeItemAdapter actressAdapter;

    private String shortId="";
    private int pageIndex=1;
    private boolean showAll=false;
    private String type="";

    private boolean isMore=true;

    RefreshLayout refreshLayout;
    FlowLayout flowLayout;

    List<CategoriesBean> categoriesBeanList;

    TypeTabBean tabBean;
    ImageView conver;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_actress;
    }

    @Override
    public String getParamsStr() {
        return "女优ID:"+shortId;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if(DataUtils.typeTabBean!=null) {
            tabBean = DataUtils.typeTabBean;
        }
        ImageView img=findViewById(R.id.btn_back);
        ViewUtils.setImageColor(img,ColorStateList.valueOf(Color.WHITE));

        String nvStr=intent.getStringExtra("nvItem");
        NvStar nvStar=  new Gson().fromJson(nvStr,NvStar.class);
        shortId =nvStar.getActorShortId();

        this.setTitle(nvStar.getActorName());
        this.setEdit("");

        conver=findViewById(R.id.conver);
        if(!nvStar.getConverUrl().equals("")) {
            CacheFactory.getInstance().getImage(this, conver, nvStar.getConverUrl());
        }else{//没有女星大图,显示默认的

        }

        TextView txtname=findViewById(R.id.txt_name);
        txtname.setText(nvStar.getActorName());

        TextView txtSw=findViewById(R.id.txtSw);
        //生日:1999-09-09 三围:88 88 88 杯罩:G
        String info="生日:"+getDatetime(nvStar.getBirthday())+"    三围:"+nvStar.getBust()+"    "
                +nvStar.getWaist()+"    "+nvStar.getHips();//+" 罩杯:Z";
        txtSw.setText(info);

        flowLayout=findViewById(R.id.tagFlowLayout);

        setTab();

        actressAdapter=new TypeItemAdapter(this);
        RecyclerView contentRecyclerView=findViewById(R.id.content_recyclerView);
        contentRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        contentRecyclerView.setAdapter(actressAdapter);
        contentRecyclerView.setNestedScrollingEnabled(false);

        actressAdapter.setOnItemClickListener(new TypeItemAdapter.OnItemClickListener<TypeListDataBean>() {
            @Override
            public void onItemClick(TypeListDataBean item, int position) {
                //点击item跳转到播放界面
//                Intent intent = new Intent(ActressActivity.this, VideoActivity.class);
//                intent.putExtra("shortId",item.getShortId());
//                startActivity(intent);
            }
        });
        contentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case SCROLL_STATE_IDLE:
                        Glide.with(ActressActivity.this).resumeRequests();
                        break;
                    case SCROLL_STATE_SETTLING:
                    case SCROLL_STATE_DRAGGING:
                        Glide.with(ActressActivity.this).resumeRequests();
                        break;
                    default:
                        Glide.with(ActressActivity.this).resumeRequests();
                        break;
                }
            }
        });
        refreshLayout=findViewById(R.id.refreshLayout);

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
    private void setTab(){
        if(tabBean!=null) {
            categoriesBeanList = tabBean.getData().getSorttype();
        }

        if(categoriesBeanList!=null) {
            for (CategoriesBean item : categoriesBeanList) {
                TextView textView = getText(item.getName(), item.getShortId());

                flowLayout.addView(textView);
                textViews.add(textView);
            }
            type=categoriesBeanList.get(0).getShortId();
        }
        if(textViews.size()>0) {
            changeTxt(textViews.get(0));
        }
    }

    private List<TextView> textViews=new ArrayList<>();

    private TextView getText(String txt,String ix){
        final String shortId=ix;
        final TextView textView=ViewUtils.getText(this,txt,R.drawable.tag_default);// new TextView(this);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type=shortId;
                pageIndex=1;
                initData();
                changeTxt(textView);
            }
        });
        return textView;
    }
    public void changeTxt(TextView txt){
        txt.setTextColor(getResources().getColor(R.color.title_color));
        //txt.setBackgroundResource(R.drawable.tag_yellow_radius_bg);
        for(TextView item:textViews){
            if(item==txt){
                continue;
            }
            item.setTextColor(getResources().getColor(R.color.color_9c9c9c));
            //item.setBackgroundResource(R.drawable.tag_default);
        }
    }
    @Override
    protected void initData() {
        if(tabBean==null){
            presenter.getTags();
            return;
        }
        presenter.getActress(shortId,pageIndex,type,showAll);
        pageIndex++;
    }

    @Override
    public ActressPresenter initPresenter() {
        return new ActressPresenter();
    }

    @Override
    public void onActressResult(TypeListBean actressBean) {
        if(refreshLayout!=null){
            refreshLayout.finishLoadMore();
            refreshLayout.finishRefresh();
        }
        if(actressBean.getPageIndex()==1){
            actressAdapter.setData(actressBean.getList());
        }else {
            actressAdapter.dataList.addAll(actressBean.getList());
            actressAdapter.notifyDataSetChanged();
        }
        conver.setFocusable(true);
        conver.setFocusableInTouchMode(true);
//        conver.requestFocus(2000);
        if(actressBean.getList().size()<=0){
            isMore=false;
            return;
        }
    }

    @Override
    public void onTagResult(TypeListBean typeListBean) {
    }

    @Override
    public void onTags(TypeTabBean typeTabBean) {
        try {
            tabBean = typeTabBean;
            setTab();
        }catch (Exception e){}
        finally {
            initData();
        }
    }

    /**
     * 根据时间戳得到日期+时间
     * @param time
     * @return
     */
    public String getDatetime(long time){
        if(time<0){
            return "未知";
        }
        return DateUtils.getDate("yyyy-MM-dd",time);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        MainApplicationContext.onWindowFocusChanged(hasFocus,this);
    }
}
