package com.xcore.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nex3z.flowlayout.FlowLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.base.MvpFragment;
import com.xcore.data.bean.CategoriesBean;
import com.xcore.data.bean.TabBean;
import com.xcore.data.bean.Tag;
import com.xcore.data.bean.TagBean;
import com.xcore.data.bean.TypeListDataBean;
import com.xcore.presenter.TagSelectPreseneter;
import com.xcore.presenter.view.TagSelectView;
import com.xcore.ui.activity.TagSelectActivity;
import com.xcore.ui.adapter.TagSelectAdapter;
import com.xcore.ui.adapter.TagSelectContentAdapter;
import com.xcore.ui.touch.ITagOnClick;
import com.xcore.ui.touch.ITouchListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;

public class TagSelectFragment extends MvpFragment<TagSelectView,TagSelectPreseneter> implements TagSelectView,ITouchListener,ITagOnClick {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private int pageIndex=1;
    private boolean isLoad=false;

    private boolean isMore=true;

    Handler mHandler;

    private RefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    private TagSelectContentAdapter tagSelectAdapter;
    private List<TagBean> tagBeans=new ArrayList<>();


    public TagSelectFragment() {
    }

    public static TagSelectFragment newInstance(String param1, String param2) {
        TagSelectFragment fragment = new TagSelectFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public TagSelectPreseneter initPresenter() {
        return new TagSelectPreseneter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tag_select;
    }

    @Override
    protected void initView(View view) {
        refreshLayout=view.findViewById(R.id.refreshLayout);

        tagSelectAdapter=new TagSelectContentAdapter(getContext(),this);

        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(tagSelectAdapter);

        tagSelectAdapter.setData(tagBeans);

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if(isMore==false){
                    refreshLayout.finishLoadMore(1000);
                    return;
                }
                init();
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pageIndex=1;
                init();
            }
        });
        refreshLayout.setPrimaryColorsId(R.color.title_color, android.R.color.white);//全局设置主题颜色
//        refreshLayout.setPrimaryColors()

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                switch (newState) {
//                    case SCROLL_STATE_IDLE:
//                        Glide.with(getActivity()).resumeRequests();
//                        break;
//                    case SCROLL_STATE_SETTLING:
//                    case SCROLL_STATE_DRAGGING:
//                        Glide.with(getActivity()).resumeRequests();
//                        break;
//                    default:
//                        Glide.with(getActivity()).resumeRequests();
//                        break;
//                }
//
//            }
//
//        });

    }

    @Override
    protected void initData() {
        if(mParam2.equals("0")&&isLoad==false){
            pageIndex=1;
            init();
            isLoad=true;
        }
    }

    private void init(){
        if(presenter!=null) {
            presenter.getTagList(pageIndex, mParam1);
            pageIndex++;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //这个地方已经产生了耦合，若还有其他的activity，这个地方就得修改
        if(context instanceof TagSelectActivity){
            mHandler =  ((TagSelectActivity)context).mHandler;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mHandler=null;
    }

    @Override
    public void onTagResult(TagBean tagBean) {
    }

    @Override
    public void onResult(TagBean tagBean) {
        if(refreshLayout!=null){
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadMore();
        }
        //得到信息
        if(tagBean.getPageIndex()==1){
            if(tagSelectAdapter.dataList.size()>0) {
                tagSelectAdapter.dataList.remove(0);
            }
            if(tagBeans.size()>0) {
                tagBeans.remove(0);
            }
            tagBeans.clear();
            tagSelectAdapter.dataList.clear();
            tagBeans.add(tagBean);
            tagSelectAdapter.setData(tagBeans);

            for(Tag tag:tagBean.getList()){
                tag.setChecked(false);
                if(mHandler!=null){
                    Message msg=new Message();
                    msg.what=1;
                    msg.obj=tag;
                    mHandler.sendMessage(msg);
                }
            }
            return;
        }
//        if(tagBean.getList().size()<=0){
//            isMore=false;
//            return;
//        }

        TagBean tagBean1=tagBeans.get(0);
        tagBean1.getList().addAll(tagBean.getList());
        tagSelectAdapter.setData(tagBeans);
    }

    @Override
    public void onTouch0(String shortId) {
        mParam1=shortId;
        if(isLoad==false) {
            pageIndex=1;
            init();
            isLoad=true;
        }
    }

    //删除 取消选择
    @Override
    public void onTouch1(String shortId) {
        if(tagBeans.size()>0){
            TagBean tagBean=tagBeans.get(0);
            List<Tag> tags=tagBean.getList();
            for(Tag tag:tags){
                if(tag.getShortId().equals(shortId)){
                    tag.setChecked(false);
                    break;
                }
            }
            tagSelectAdapter.setData(tagBeans);
        }
    }

    @Override
    public void onTouch2(String shortId) {
    }

    @Override
    public void onClick(Tag tag) {
        //点击选中或取消选中
        Log.e("TAG","点击了"+tag.toString());
        if(mHandler!=null){
            Message msg=new Message();
            msg.what=1;
            msg.obj=tag;
            mHandler.sendMessage(msg);
            if(tagBeans.size()>0) {
                TagBean tagBean = tagBeans.get(0);
                for (int i = 0; i < tagBean.getList().size(); i++) {
                    Tag tag1 = tagBean.getList().get(i);
                    if (tag.getShortId().equals(tag1.getShortId())) {
                        tag1.setChecked(tag.isChecked());
                    }
                }
                tagSelectAdapter.setData(tagBeans);
            }
        }
    }
}
