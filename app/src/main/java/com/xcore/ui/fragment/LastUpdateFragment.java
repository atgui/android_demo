package com.xcore.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xcore.R;
import com.xcore.data.bean.TypeListBean;
import com.xcore.data.bean.TypeListDataBean;
import com.xcore.ui.adapter.TypeItemAdapter;
import com.xcore.ui.touch.IOnRefreshListener;

import java.util.List;

public class LastUpdateFragment extends Fragment {
    private List<TypeListDataBean> dataList;
    private TypeItemAdapter typeItemAdapter;
    private LinearLayout emptyLayout;
    private RefreshLayout refreshLayout;
    private LinearLayout rLayout;
    private boolean isMore=true;

    RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_last_update,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        refreshLayout=view.findViewById(R.id.refreshLayout);
        recyclerView= view.findViewById(R.id.recyclerView);
        emptyLayout=view.findViewById(R.id.empt_layout);
        rLayout=view.findViewById(R.id.rLayout);

        typeItemAdapter=new TypeItemAdapter(getContext());
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(typeItemAdapter);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if(iOnRefreshListener!=null){
                    iOnRefreshListener.onRefresh(1);
                }
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if(isMore==false){
                    refreshLayout.finishLoadMore(1000);
                    return;
                }
                if(iOnRefreshListener!=null){
                    iOnRefreshListener.onRefresh(-1);
                }
            }
        });

        typeItemAdapter.setOnItemClickListener(new TypeItemAdapter.OnItemClickListener<TypeListDataBean>() {
            @Override
            public void onItemClick(TypeListDataBean item, int position) {
//                Intent intent = new Intent(getContext(), VideoActivity.class);
//                intent.putExtra("shortId",item.getShortId());
//                startActivity(intent);
            }
        });

        rLayout.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.GONE);
    }

    private IOnRefreshListener iOnRefreshListener;
    public void setRefrehsLister(IOnRefreshListener refrehsLister){
        this.iOnRefreshListener=refrehsLister;
    }

    /**
     * 更新数据
     * @param typeListBean
     */
    public void setData(TypeListBean typeListBean){
        if(refreshLayout!=null){
            refreshLayout.finishLoadMore();
            refreshLayout.finishRefresh();
        }
        if(typeListBean.getPageIndex()==1&&typeListBean.getList().size()<=0){
            rLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
            return;
        }
        emptyLayout.setVisibility(View.GONE);
        rLayout.setVisibility(View.VISIBLE);
        if(typeListBean.getPageIndex()==1&&typeListBean.getList().size()>0){
            this.dataList=typeListBean.getList();
            typeItemAdapter.setData(dataList);

            //recyclerView.scrollToPosition(0);
        }else{
            if(typeListBean.getList().size()>0){
                this.dataList.addAll(typeListBean.getList());
                typeItemAdapter.notifyDataSetChanged();
            }else{
                isMore=false;
            }
        }
    }

}
