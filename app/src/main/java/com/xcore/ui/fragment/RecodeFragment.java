package com.xcore.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xcore.R;
import com.xcore.cache.CacheManager;
import com.xcore.cache.beans.CacheBean;
import com.xcore.cache.beans.CacheType;
import com.xcore.ui.adapter.Recode1Adapter;
import com.xcore.ui.touch.OnFragmentInteractionListener;
import com.xcore.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RecodeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;//1 今天  2 七天  3 更早
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private Recode1Adapter recodeAdapter;
    private LinearLayout bottomLayout;

    private boolean selected=false;
    private Button btnAll;
    private Button btnDel;

    private int pageIndex=1;

    public RecodeFragment() {
    }

    public static RecodeFragment newInstance(String param1, String param2) {
        RecodeFragment fragment = new RecodeFragment();
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

    private List<CacheBean> cacheBeans=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView=view.findViewById(R.id.recyclerView);
        refreshLayout=view.findViewById(R.id.refreshLayout);
        bottomLayout=view.findViewById(R.id.bottomLayout);
        btnAll=view.findViewById(R.id.btn_all);
        btnDel=view.findViewById(R.id.btn_del);

        bottomLayout.setVisibility(View.GONE);

        recodeAdapter=new Recode1Adapter(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(recodeAdapter);

        recodeAdapter.setData(cacheBeans);

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
                initData();
            }
        });
        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allSelect();
            }
        });
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });

        Log.e("TAG","INIT>..");

        initData();
    }
    public void init(){

    }

    //初始化数据
    private void initData(){
        List<CacheBean> cacheBeans=new ArrayList<>();

        if(mParam1.equals("1")){//今天
            //得到今天的开始时间
            Calendar currentDate = Calendar.getInstance();
            currentDate.set(Calendar.HOUR_OF_DAY, 0);
            currentDate.set(Calendar.MINUTE, 0);
            currentDate.set(Calendar.SECOND, 0);
            Date time = currentDate.getTime();

            //得到今天的最后时间
            Calendar endDate = Calendar.getInstance();
            currentDate.set(Calendar.HOUR_OF_DAY, 23);
            currentDate.set(Calendar.MINUTE, 59);
            currentDate.set(Calendar.SECOND, 59);
            Date endTime = currentDate.getTime();

           cacheBeans= CacheManager.getInstance().getDbHandler()
                   .getQuery(CacheType.CACHE_RECODE,time.getTime()+"",
                           endTime.getTime()+"",pageIndex,20);

        }else if(mParam1.equals("2")){//七日 不包括今天  今天之后 七日之前
            //得到今天的最后时间
            Calendar qDate = Calendar.getInstance();
            qDate.set(Calendar.HOUR_OF_DAY, 0);
            qDate.set(Calendar.MINUTE, 0);
            qDate.set(Calendar.SECOND, 0);
            Date sTime = qDate.getTime();

            Date sDate=DateUtils.getDateBeforeOrAfter(new Date(),-7);
            cacheBeans= CacheManager.getInstance().getDbHandler()
                    .getQuery(CacheType.CACHE_RECODE,sDate.getTime()+"",
                            sTime.getTime()+"",pageIndex,20);
        }else if(mParam1.equals("3")){//更早 七天之前的
            Date sDate=DateUtils.getDateBeforeOrAfter(new Date(),-7);
            cacheBeans= CacheManager.getInstance().getDbHandler()
                    .getQuery(CacheType.CACHE_RECODE,sDate.getTime()+"",
                            "",pageIndex,20);
        }
        onResult(cacheBeans);
        pageIndex++;
    }
    //更新信息
    public void onResult(List<CacheBean> cacheBeanList){
        if(refreshLayout!=null){
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadMore();
        }
        if(pageIndex==1){
            cacheBeans.clear();
            cacheBeans.addAll(cacheBeanList);
        }else{
            cacheBeans.addAll(cacheBeanList);
        }
        recodeAdapter.setData(cacheBeans);
        if(cacheBeanList.size()<=0){
            bottomLayout.setVisibility(View.GONE);
        }
    }
    //点击编辑
    public void onEdit(boolean boo){
        if(boo){
            bottomLayout.setVisibility(View.VISIBLE);
        }else{
            bottomLayout.setVisibility(View.GONE);
        }
        recodeAdapter.allShow(boo);
    }

    //全选
    public void allSelect(){
        selected=!selected;
        recodeAdapter.allSelect(selected);
    }
    //删除选中
    public void delete(){
        List<CacheBean> cacheBeans=recodeAdapter.dataList;
        for(CacheBean item:cacheBeans){
            if(item.selected) {
                item.settDelete("0");
                item.setPlayPosition("0");
                CacheManager.getInstance().getDbHandler().update(item);
            }
        }
        pageIndex=1;
        //重新更新一次
        initData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
