package com.xcore.ui.fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xcore.R;
import com.xcore.base.BaseFragment;
import com.xcore.data.bean.TypeListBean;
import com.xcore.data.bean.TypeListDataBean;
import com.xcore.ui.adapter.TypeItemAdapter;
import com.xcore.ui.touch.OnFragmentListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;

public class TypeSubFragment extends BaseFragment {
    private TypeItemAdapter typeItemAdapter;
    private LinearLayout emptyLayout;
    private RefreshLayout refreshLayout;
    private LinearLayout rLayout;
    private boolean isMore=true;

    List<TypeListDataBean> listDataBeanList=new ArrayList<>();

    private OnFragmentListener mListener;
    public void setmListener(OnFragmentListener listener){
        this.mListener=listener;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_type_item;
    }

    @Override
    protected void initView(View view) {
        refreshLayout=view.findViewById(R.id.refreshLayout);
        RecyclerView recyclerView= view.findViewById(R.id.recyclerView);
        emptyLayout=view.findViewById(R.id.empt_layout);
        rLayout=view.findViewById(R.id.rLayout);

        emptyLayout.setVisibility(View.GONE);

        typeItemAdapter=new TypeItemAdapter(getActivity());
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(typeItemAdapter);
        typeItemAdapter.setData(listDataBeanList);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if(mListener!=null){
                    mListener.onRefresh(false);
                }else{
                    refreshLayout.finishRefresh(1000);
                }
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if(mListener!=null){
                    mListener.onRefresh(true);
//                    if(isMore==true) {
//                        mListener.onRefresh(true);
//                    }else{
//                        refreshLayout.finishLoadMore(1000);
//                    }
                }else{
                    refreshLayout.finishLoadMore(1000);
                }
            }
        });
        typeItemAdapter.setOnItemClickListener(new TypeItemAdapter.OnItemClickListener<TypeListDataBean>() {
            @Override
            public void onItemClick(TypeListDataBean item, int position) {
//                Intent intent = new Intent(getContext(), VideoActivity.class);
//                intent.putExtra("shortId",item.getShortId());
//                startActivity(intent);

//                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity()
//                        ,new Pair<View, String>(getView(),"shared_image_")
//                        ,new Pair<View, String>(getView(),"shared_text_"));
//                startActivity(intent,activityOptionsCompat.toBundle());

            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            switch (newState) {
                case SCROLL_STATE_IDLE:
                    Glide.with(getActivity()).resumeRequests();
                    break;
                case SCROLL_STATE_SETTLING:
                case SCROLL_STATE_DRAGGING:
                    Glide.with(getActivity()).resumeRequests();
                    break;
                default:
                    Glide.with(getActivity()).resumeRequests();
                    break;
            }
            }
        });
    }

    @Override
    protected void initData() {
    }

    public void onError(String msg){
        if(refreshLayout!=null){
            refreshLayout.finishLoadMore();
            refreshLayout.finishRefresh();
        }
    }

    //刷新 fragment 数据
    public void setData(TypeListBean typeListBean){
        if(refreshLayout!=null){
            refreshLayout.finishLoadMore();
            refreshLayout.finishRefresh();
        }
        //是第一页且没有数据
        if(typeListBean.getPageIndex()==1&&typeListBean.getList().size()<=0){
            emptyLayout.setVisibility(View.VISIBLE);
            rLayout.setVisibility(View.GONE);
            isMore=false;
            listDataBeanList.clear();
        }else {
            emptyLayout.setVisibility(View.GONE);
            rLayout.setVisibility(View.VISIBLE);
            if (typeListBean.getPageIndex() == 1) {
                listDataBeanList = typeListBean.getList();
            } else {
                listDataBeanList.addAll(typeListBean.getList());
            }
        }
        //Log.e("TAG","数据长度:"+listDataBeanList.size());
        typeItemAdapter.setData(listDataBeanList);
    }

}
