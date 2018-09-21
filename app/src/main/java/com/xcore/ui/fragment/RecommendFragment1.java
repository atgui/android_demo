package com.xcore.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.base.MvpFragment1;
import com.xcore.data.bean.HomeBean;
import com.xcore.data.bean.NvStar;
import com.xcore.data.bean.NvStarBean;
import com.xcore.data.bean.RecommendBean;
import com.xcore.data.bean.ThemeRecommendBean;
import com.xcore.ext.ImageViewExt;
import com.xcore.presenter.RecommendPresenter;
import com.xcore.presenter.view.RecommendView;
import com.xcore.ui.activity.ActressActivity;
import com.xcore.ui.activity.FindActivity;
import com.xcore.ui.activity.MakeLTDActivity;
import com.xcore.ui.activity.NvStarListActivity;
import com.xcore.ui.activity.TagActivity;
import com.xcore.ui.activity.TagSelectActivity;
import com.xcore.ui.activity.ThemeActivity;
import com.xcore.ui.activity.ThemeListActivity;
import com.xcore.ui.activity.TypeActivity;
import com.xcore.ui.adapter.NvStarRecommendAdapter;
import com.xcore.ui.adapter.RThemeAdapter;
import com.xcore.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class RecommendFragment1 extends MvpFragment1<RecommendView,RecommendPresenter> implements RecommendView {

    private NvStarRecommendAdapter nvStarAdapter;

    private RThemeAdapter rThemeAdapter;
    private ListView listView;

    private NestedScrollView scrollView;
    RefreshLayout refreshLayout;

    private List<TextView> btTxts=new ArrayList<>();
    private List<ImageViewExt> btImgs=new ArrayList<>();
    private List<LinearLayout> btLayouts=new ArrayList<>();
    boolean isRefresh=true;

    private RecommendBean rBean;

    private int pageIndex=1;

    public RecommendFragment1() {
    }
    public static RecommendFragment1 newInstance(String param1, String param2) {
        RecommendFragment1 fragment = new RecommendFragment1();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public RecommendPresenter initPresenter() {
        return new RecommendPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recommend;
    }

    //点击跳转
    private void toJump(final HomeBean.HomeTypeItem typeItem,View v){
        Intent intent=null;
        switch (typeItem.getJump()){
            case 1://电影详情 banner 跳转？
//                intent=new Intent(getContext(), VideoActivity.class);
//                intent.putExtra("shortId",typeItem.getShortId());
                ViewUtils.toPlayer((Activity) getContext(), v, typeItem.getShortId(), null, null);
                return;
            case 2://类型
                intent=new Intent(getContext(), TypeActivity.class);
                intent.putExtra("shortId",typeItem.getShortId());
                intent.putExtra("type",2);
                intent.putExtra("tag",typeItem.getName());
//                XMainActivity.stupToFrament(2,typeItem.getShortId());
                break;
            case 3://标签
                intent=new Intent(getContext(), TagActivity.class);
                intent.putExtra("tag",typeItem.getName());
                break;
            case 4://专题
                intent=new Intent(getContext(), ThemeListActivity.class);
//                if(typeItem.getShortId().equals("")){//没有专题ID就切换到推荐页
//                    XMainActivity.stupToFrament(1);
//                }else{//跳到专题页面
//                    intent=new Intent(getContext(), ThemeActivity.class);
//                    intent.putExtra("shortId",typeItem.getShortId());//传进专题ID
//                }
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                intent=new Intent(getContext(), TypeActivity.class);
                intent.putExtra("shortId",typeItem.getShortId());
                intent.putExtra("type",7);
                intent.putExtra("tag",typeItem.getName());
//                XMainActivity.stupToFrament(7,typeItem.getShortId());
                break;
            case 8://浏览地址
                if(!TextUtils.isEmpty(typeItem.getShortId())){
                    intent = new Intent( Intent.ACTION_VIEW );//https://www.potato.im/1avco1
                    intent.setData( Uri.parse(typeItem.getShortId()) ); //这里面是需要调转的rul
                    intent = Intent.createChooser( intent, null );
                }
                break;
            case 9://下载地址 直接下载..  到浏览器打开下载
                if(!TextUtils.isEmpty(typeItem.getShortId())){
                    intent = new Intent( Intent.ACTION_VIEW );//https://www.potato.im/1avco1
                    intent.setData( Uri.parse(typeItem.getShortId()) ); //这里面是需要调转的rul
                    intent = Intent.createChooser( intent, null );
                }
                break;
            case 10://发现
                intent=new Intent(getContext(), FindActivity.class);
                intent.putExtra("tag","");
                break;
            case 11://跳一本道、东京热....
                intent=new Intent(getContext(), MakeLTDActivity.class);
                intent.putExtra("shortId",typeItem.getShortId());
                break;
        }
        if(intent!=null){
            getContext().startActivity(intent);
        }
    }
    //类型推荐
    private void setTypeButton(final HomeBean.HomeTypeItem typeItem,final int i){
        btTxts.get(i).setText(typeItem.getName());
        btImgs.get(i).loadUrl(typeItem.getResUrl());
//        CacheFactory.getInstance().getImage(getContext(),btImgs.get(i),typeItem.getResUrl());

        btLayouts.get(i).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toJump(typeItem,v);
            }
        });
    }

    @Override
    protected void initView(View view) {
        TextView textView= view.findViewById(R.id.tagSelect);
//        textView.setVisibility(View.GONE);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), TagSelectActivity.class);
                intent.putExtra("toTag","1");
                startActivity(intent);
            }
        });

        int[] txtList=new int[]{R.id.txt_0,R.id.txt_1,R.id.txt_2,R.id.txt_3};
        int[] resList=new int[]{R.id.image_0,R.id.image_1,R.id.image_2,R.id.image_3};
        int[] layoutList=new int[]{R.id.btn_0,R.id.btn_1,R.id.btn_2,R.id.btn_3};

        btTxts.clear();
        btImgs.clear();
        btLayouts.clear();

        for(int i=0;i<txtList.length;i++){
            TextView txt= view.findViewById(txtList[i]);
            btTxts.add(txt);

            ImageViewExt img=view.findViewById(resList[i]);
            btImgs.add(img);

            LinearLayout linearLayout= view.findViewById(layoutList[i]);
            btLayouts.add(linearLayout);
        }

        scrollView=view.findViewById(R.id.scrollView);
        refreshLayout=view.findViewById(R.id.refreshLayout);

        //设置类型按钮
        //this.setTypeButton(view);
//        //设置女星推荐
        RecyclerView recyclerView= view.findViewById(R.id.nv_recyclerView);

        nvStarAdapter=new NvStarRecommendAdapter(getContext());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),4));
        recyclerView.setAdapter(nvStarAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        nvStarAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<NvStar>() {
            @Override
            public void onItemClick(NvStar item, int position) {
                Intent intent = new Intent(getContext(), ActressActivity.class);
                String jsonStr=new Gson().toJson(item);
                intent.putExtra("nvItem",jsonStr);

                getContext().startActivity(intent);
            }
        });

        rThemeAdapter=new RThemeAdapter(getContext());
        listView=view.findViewById(R.id.listView);
        listView.setAdapter(rThemeAdapter);
//        listView.setNestedScrollingEnabled(false);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ThemeRecommendBean.ThemeData item= rThemeAdapter.dataList.get(position);

                Intent intent=new Intent(getContext(),ThemeActivity.class);
                intent.putExtra("shortId",item.getShortId());
                intent.putExtra("name",item.getName());
                intent.putExtra("conver",item.getConverUrl());
                intent.putExtra("maxConver",item.getMaxConverUrl());
                intent.putExtra("desc",item.getRemarks());
                startActivity(intent);
            }
        });

        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pageIndex=1;
                isRefresh=true;
                refreshData();
            }
        });

        view.findViewById(R.id.nv_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), NvStarListActivity.class);
                startActivity(intent);
            }
        });

        setView();
    }

    //设置
    private void setView(){
        if(rBean!=null) {
            List<HomeBean.HomeTypeItem> typeItemBeans= rBean.getData().getTitleModels();
            for(int i=0;i<typeItemBeans.size();i++){
                HomeBean.HomeTypeItem typeItem= typeItemBeans.get(i);
                setTypeButton(typeItem,i);
            }

            nvStarAdapter.setData(rBean.getData().getActorList());
            rThemeAdapter.setData(rBean.getData().getThemeList());
            setHeigth(listView);
        }
    }

    //设置List 高度
    public void setHeigth(ListView list) {
        ListAdapter listAdapter = list.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            View listItem = listAdapter.getView(i, null, list);
            //             listItem.measure(LinearLayout.LayoutParams.MATCH_PARENT,0);
            listItem.measure(0,0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = list.getLayoutParams();
        params.height = 50+totalHeight+ (list.getDividerHeight() * (listAdapter.getCount() - 1));
        list.setLayoutParams(params);

        scrollView.smoothScrollTo(0,0);
    }

//    @Override
//    protected void initData() {
////        if(rBean==null||isRefresh==true){
////            presenter.getRecommendData();
////        }
//    }
    private void refreshData(){
        if(rBean==null||isRefresh==true){
            presenter.getRecommendData();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onRecommendNvStar(NvStarBean nvStarBean) {
    }

    @Override
    public void onRecommendTheme(ThemeRecommendBean recommendBean) {
    }

    @Override
    public void onRecommendResult(RecommendBean recommendBean) {
        if(refreshLayout!=null){
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadMore();
        }
        if(recommendBean==null){
            return;
        }
        isRefresh=false;
        this.rBean=recommendBean;
        setView();
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        refreshData();
    }
}
