package com.xcore.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.cache.CacheManager;
import com.xcore.cache.CacheModel;
import com.xcore.ui.adapter.RunAdapter;
import com.xcore.ui.touch.OnFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OverCacheFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private LinearLayout bottomLayout;

    private RecyclerView overRecyclerView;
    private RunAdapter runAdapter;
    private TextView txtCache;
    private boolean isSelect=false;

    public OverCacheFragment() {
    }

    public static OverCacheFragment newInstance(String param1, String param2) {
        OverCacheFragment fragment = new OverCacheFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_over_cache, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        runAdapter=new RunAdapter(getContext());
        overRecyclerView=view.findViewById(R.id.overRecyclerView);
        overRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        overRecyclerView.setAdapter(runAdapter);

        runAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<CacheModel>() {
            @Override
            public void onItemClick(CacheModel item, int position) {
            //到播放页
//            Intent intent = new Intent(getActivity(), VideoActivity.class);
//            intent.putExtra("shortId",item.getShortId());
//            intent.putExtra("playUrl",item.getUrl());
//            startActivity(intent);
            }
        });

        txtCache= view.findViewById(R.id.txt_cacheCount);

        refreshData();

        bottomLayout=view.findViewById(R.id.bottomLayout);
        onEdit(false);

        view.findViewById(R.id.btn_selectAll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectAll();
            }
        });
        view.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
    }
    private void delete(){
        List<CacheModel> cacheModels= runAdapter.dataList;
        List<CacheModel> deleteList=new ArrayList<>();
        for(CacheModel cacheModel:cacheModels){
            if(cacheModel.isChecked()){
                deleteList.add(cacheModel);
            }
        }
        CacheManager.getInstance().getDownHandler().batchDelete(deleteList);
        refreshData();

        mListener.onItemClick(null);
        onEdit(false);
    }
    //刷新数据
    public void refreshData(){
        List<CacheModel> cacheModels=new ArrayList<>();
        Map<String,CacheModel> cacheModelMap= CacheManager.getInstance().getDownHandler().overCaches;
        for(String key:cacheModelMap.keySet()){
            CacheModel cacheModel= cacheModelMap.get(key);
            cacheModel.setChecked(false);
            cacheModel.setShowCheck(false);
            cacheModels.add(cacheModel);
        }
        runAdapter.setData(cacheModels);

        txtCache.setText("已缓存个数"+cacheModels.size());
    }
    //全选
    private void onSelectAll(){
        isSelect=!isSelect;
        runAdapter.selectAll(isSelect);
    }
    //点击编辑
    public void onEdit(boolean boo){
        if(boo){
            this.bottomLayout.setVisibility(View.VISIBLE);
        }else{
            this.bottomLayout.setVisibility(View.GONE);
        }
        runAdapter.showAll(boo);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
