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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.tu.loadingdialog.LoadingDailog;
import com.common.BaseCommon;
import com.lzy.okgo.cache.CacheMode;
import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.cache.CacheManager;
import com.xcore.cache.CacheModel;
import com.xcore.cache.DownHandler;
import com.xcore.cache.DownModel;
import com.xcore.cache.beans.CacheBean;
import com.xcore.ui.adapter.Run1Adapter;
import com.xcore.ui.adapter.RunAdapter;
import com.xcore.ui.touch.OnFragmentInteractionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.dolit.p2ptrans.P2PTrans;

import static com.xcore.cache.CacheManager.getInstance;

public class RunCacheFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private LinearLayout bottomLayout;

//    private RecyclerView runRecyclerView;
//    private RunAdapter runAdapter;
    private TextView totalTxt;

    private ListView runListView;
    private Run1Adapter run1Adapter;

    private ImageView pauseImage;
    private TextView pauseTxt;

    private boolean isSelect=false;
    private boolean isEdit=false;

    private boolean isPlay=false;
    private Timer timer;

    protected LoadingDailog dialog=null;

    public RunCacheFragment() {

    }

    public static RunCacheFragment newInstance(String param1, String param2) {
        RunCacheFragment fragment = new RunCacheFragment();
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
        return inflater.inflate(R.layout.fragment_run_cache, container, false);
    }

    private List<String> streams=new ArrayList<>();
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pauseImage= view.findViewById(R.id.pauseImage);
        pauseTxt=view.findViewById(R.id.pauseTxt);

        runListView=view.findViewById(R.id.run_listView);
        run1Adapter=new Run1Adapter(getContext());
        runListView.setAdapter(run1Adapter);
        runListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CacheModel item= run1Adapter.dataList.get(position);
                if(item.getStatus()==1){//下载状态 -> 暂停
                    CacheManager.getInstance().stopByUrl(item,false);
                    isPlay=CacheManager.getInstance().getDownHandler().isRunTask();
                    updatePlay();
                }else{
                    DownModel downModel=new DownModel();
                    downModel.setShortId(item.shortId);
                    downModel.setName(item.getName());
                    downModel.setConver(item.getConver());
                    downModel.setUrl(item.getUrl());
                    CacheManager.getInstance().downByUrl(downModel);

                    isPlay=true;
                    updatePlay();
                }
            }
        });

        totalTxt=view.findViewById(R.id.totalTxt);
        bottomLayout=view.findViewById(R.id.bottomLayout);
        Button btnSelectAll=view.findViewById(R.id.btn_selectAll);
        Button btnDelete= view.findViewById(R.id.btn_delete);
        btnSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelect=!isSelect;
//                runAdapter.selectAll(isSelect);
                run1Adapter.selectAll(isSelect);
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
        onEdit(false);
        initData();

        pauseImage.setImageResource(R.drawable.pause);
        pauseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPauseAll();
            }
        });
        pauseTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPauseAll();
            }
        });
        isPlay=CacheManager.getInstance().getDownHandler().isRunTask();
        updatePlay();
        startTimer();
    }
    //删除
    private void delete(){
        List<CacheModel> cacheModels=run1Adapter.dataList;// runAdapter.dataList;
        List<CacheModel> deleteList=new ArrayList<>();
        for(CacheModel cacheModel:cacheModels){
            if(cacheModel.isChecked()){
                deleteList.add(cacheModel);
            }
        }
        CacheManager.getInstance().getDownHandler().batchDelete(deleteList);

        initData();

        mListener.onItemClick(null);
        onEdit(false);


        if(isPlay==false){//显示
            pauseTxt.setText("全部开始");
            pauseImage.setImageResource(R.drawable.pause);
            //开始计时器
            //startTimer();
        }
    }
    //全部暂停或开始
    public void onPauseAll(){
        isPlay=!isPlay;
        List<CacheModel> cacheModels=run1Adapter.dataList;// runAdapter.dataList;
        if(isPlay==false){//开始->暂停   任务开始
            for(CacheModel cacheModel:cacheModels){
                CacheManager.getInstance().stopByUrl(cacheModel,false);
            }
            //stopTimer();
            run1Adapter.setData(cacheModels);
        }else{
            for(CacheModel cacheModel:cacheModels){
                cacheModel.setStatus(1);
                DownModel downModel=new DownModel();
                downModel.setShortId(cacheModel.shortId);
                downModel.setName(cacheModel.getName());
                downModel.setConver(cacheModel.getConver());
                downModel.setUrl(cacheModel.getUrl());
                CacheManager.getInstance().downByUrl(downModel);
            }
        }
        updatePlay();
    }
    private void updatePlay(){
        if(dialog==null){
            LoadingDailog.Builder loadBuilder;
            loadBuilder=new LoadingDailog.Builder(getActivity())
                    .setMessage("请稍后...")
                    .setCancelable(false)
                    .setCancelOutside(false);
            dialog=loadBuilder.create();
        }
        if(dialog!=null){
            dialog.show();
        }
        if(isPlay){//显示
            pauseTxt.setText("全部暂停");
            pauseImage.setImageResource(R.drawable.play);
            //开始计时器
            //startTimer();
        }else{
            pauseTxt.setText("全部开始");
            pauseImage.setImageResource(R.drawable.pause);
            //stopTimer();
        }
        if(dialog!=null){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(dialog!=null){
                        dialog.cancel();
                        dialog.dismiss();
                    }
                    dialog=null;
                }
            },2000);
        }

    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
        if(msg.what==0){
           initData();
        }
        }
    };
    //初始数据
    public void initData(){
        //得到缓存信息
        List<CacheModel> cacheModels=new ArrayList<>();
        Map<String,CacheModel> cahceModelMap= CacheManager.getInstance().getDownHandler().runCaches;
        int len=0;
        if(cahceModelMap.size()>0) {
            for (String key : cahceModelMap.keySet()) {
                CacheModel cacheModel = cahceModelMap.get(key);
                int percent=Integer.valueOf(cacheModel.getPercent());
                if(cacheModel.getComplete()==1||percent>=100){
                    continue;
                }
                if(cacheModel.getStatus()==1){
                    len++;
                }
                cacheModel.setShowCheck(false);
                cacheModel.setChecked(false);
                cacheModels.add(cacheModel);
            }
        }
        run1Adapter.setData(cacheModels);
        totalTxt.setText("同时缓存个数"+cacheModels.size());

        isPlay=len>0;
        if(isPlay){//显示
            pauseTxt.setText("全部暂停");
            pauseImage.setImageResource(R.drawable.play);
            //开始计时器
            //startTimer();
        }
//        else{
//            pauseTxt.setText("全部开始");
//            pauseImage.setImageResource(R.drawable.pause);
//            //stopTimer();
//        }
    }

    private void stopTimer(){
        if(timer!=null){
            timer.cancel();
        }
        timer=null;
    }
    //开始计时器
    private void startTimer(){
        if(timer==null){
            Log.e("TAG","计时器启动...");
            timer=new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                if(isEdit==false) {//不在编辑状态的时候才刷新
                    handler.sendEmptyMessage(0);
                }
                }
            },1000,2000);
        }
    }
    //点击编辑
    public void onEdit(boolean boo){
        isEdit=boo;
        if(boo){
            this.bottomLayout.setVisibility(View.VISIBLE);
        }else{
            this.bottomLayout.setVisibility(View.GONE);
        }
//        runAdapter.showAll(boo);
        run1Adapter.showAll(boo);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
}
