package com.xcore.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xcore.R;
import com.xcore.base.MvpFragment1;
import com.xcore.data.bean.BannerBean;
import com.xcore.data.bean.HomeBean;
import com.xcore.data.utils.DataUtils;
import com.xcore.ext.ImageViewExt;
import com.xcore.presenter.HomePresenter;
import com.xcore.presenter.view.HomeView;
import com.xcore.ui.activity.BoxActivity;
import com.xcore.ui.activity.FindActivity;
import com.xcore.ui.activity.LastUpdatedActivity;
import com.xcore.ui.activity.MakeLTDActivity;
import com.xcore.ui.activity.SearchActivity;
import com.xcore.ui.activity.TagActivity;
import com.xcore.ui.activity.ThemeActivity;
import com.xcore.ui.activity.ThemeListActivity;
import com.xcore.ui.activity.TypeActivity;
import com.xcore.ui.adapter.HomeAdapter;
import com.xcore.ui.touch.IHomeOnClick;
import com.xcore.utils.ViewUtils;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;
import com.zhouwei.mzbanner.holder.MZViewHolder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexFragment1 extends MvpFragment1<HomeView,HomePresenter> implements HomeView,IHomeOnClick {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

//    private String mParam1;
//    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public IndexFragment1() {
    }

    private List<TextView> btTxts=new ArrayList<>();
    private List<ImageViewExt> btImgs=new ArrayList<>();
    private List<LinearLayout> btLayouts=new ArrayList<>();

    public static IndexFragment1 newInstance(String param1, String param2) {
        IndexFragment1 fragment = new IndexFragment1();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public HomePresenter initPresenter() {
        return new HomePresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_index;
    }
    //设置按钮
    private void setBtn(final int i, final HomeBean.HomeTypeItem typeItem){
//        CacheFactory.getInstance()
//                .getImage(getActivity(),btImgs.get(i),typeItem.getResUrl());
        btImgs.get(i).loadUrl(typeItem.getResUrl());
        btTxts.get(i).setText(typeItem.getName());
        btLayouts.get(i).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toJump(typeItem,v);
            }
        });
    }
    //点击跳转
    private void toJump(final HomeBean.HomeTypeItem typeItem,View view){
        Intent intent=null;
        switch (typeItem.getJump()){
            case 1://电影详情 banner 跳转？
//                intent=new Intent(getContext(), VideoActivity.class);
//                intent.putExtra("shortId",typeItem.getShortId());
                ViewUtils.toPlayer((Activity) getContext(),view,typeItem.getShortId(),null,null);
                return;
            case 2://类型
                intent=new Intent(getContext(), TypeActivity.class);
                intent.putExtra("shortId",typeItem.getShortId());
                intent.putExtra("type",2);
                intent.putExtra("tag",typeItem.getName());

                //先测试
//                intent=new Intent(getContext(), MakeLTDActivity.class);
//                intent.putExtra("shortId","q");
                break;
            case 3://标签
                intent=new Intent(getContext(), TagActivity.class);
                intent.putExtra("tag",typeItem.getName());
                break;
            case 4://专题
                intent=new Intent(getContext(), ThemeListActivity.class);
                if(typeItem.getShortId().equals("")){//没有专题ID就切换到推荐页
//                    XMainActivity.stupToFrament(1);
                    intent=new Intent(getContext(), ThemeListActivity.class);
                }else{//跳到专题页面
                    intent=new Intent(getContext(), ThemeActivity.class);
                    intent.putExtra("shortId",typeItem.getShortId());//传进专题ID
                }
                break;
            case 5://电影名星 跳到女星界面

                break;
            case 6://排序方式
                break;
            case 7://类型?
//                intent=new Intent(getContext(), LastUpdatedActivity.class);
//                intent.putExtra("shortId",typeItem.getShortId());
//                intent.putExtra("type",3);
//                intent.putExtra("tag",typeItem.getName());
                //XMainActivity.stupToFrament(7,typeItem.getShortId());
                intent=new Intent(getContext(), TypeActivity.class);
                intent.putExtra("shortId",typeItem.getShortId());
                intent.putExtra("type",7);
                intent.putExtra("tag",typeItem.getName());
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

    MZBannerView mz_banner;
    public static class BannerViewHolder implements MZViewHolder<BannerBean.BannerData> {
        private ImageViewExt mImageView;
        private TextView txt_title;

        @Override
        public View createView(Context context) {
            // 返回页面布局
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_banner_item,null);
            mImageView =view.findViewById(R.id.banner_image);
            txt_title=view.findViewById(R.id.txt_title);
            return view;
        }

        @Override
        public void onBind(Context context, int position, BannerBean.BannerData data) {
            txt_title.setText(data.getContent());
            // 数据绑定
            //CacheFactory.getInstance().getImage(context,mImageView,data.getPathUrl());
            mImageView.loadUrl(data.getPathUrl());
        }
    }
    //设置Banner
    private void setBanner(final List<BannerBean.BannerData> bannerBeans){
        mz_banner.setBannerPageClickListener(new MZBannerView.BannerPageClickListener() {
            @Override
            public void onPageClick(View view, int position) {
            BannerBean.BannerData bannerData= bannerBeans.get(position);
            HomeBean.HomeTypeItem homeTypeItem=new HomeBean.HomeTypeItem();
            homeTypeItem.setJump(bannerData.getJump());
            homeTypeItem.setShortId(bannerData.getShortId());
            toJump(homeTypeItem,view);
            }
        });
        //mz_banner.setIndicatorRes(R.drawable.banner_indicator,R.drawable.banner_indicator_select);
        // 设置数据
        mz_banner.setPages(bannerBeans, new MZHolderCreator<BannerViewHolder>() {
            @Override
            public BannerViewHolder createViewHolder() {
                return new BannerViewHolder();
            }
        });
    }

    HomeBean homeBean;
    private RefreshLayout refreshLayout;
    private ScrollView scrollView;
    private ListView listView;
//    private IndexAdapter indexAdapter;
    private HomeAdapter indexAdapter;

    private boolean isRefresh=false;
    private boolean isFirstBoo=false;


    TextView editText;
    @Override
    protected void initView(View view) {
        refreshLayout=view.findViewById(R.id.refreshLayout);
        scrollView=view.findViewById(R.id.scrollView);
        listView=view.findViewById(R.id.listView);

        mz_banner=view.findViewById(R.id.mz_banner);

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

        //设置搜索框
        editText= view.findViewById(R.id.edit_search);
        Drawable drawable=getResources().getDrawable(R.drawable.search_bg);
        drawable.setBounds(5,0,65,60);//searchEdit.getCompoundDrawables()[2]
        editText.setCompoundDrawables(drawable,null,editText.getCompoundDrawables()[2],null);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), SearchActivity.class);
                getContext().startActivity(intent);
            }
        });
        view.findViewById(R.id.img_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), BoxActivity.class);
                getContext().startActivity(intent);
            }
        });

//        indexAdapter=new IndexAdapter(getContext(),this);
        indexAdapter=new HomeAdapter(getContext(),this);
//        listView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        listView.setAdapter(indexAdapter);

        //下拉刷新
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                isRefresh=true;
                refreshData();
            }
        });
        refreshLayout.setEnableLoadMore(false);
        homeBean=DataUtils.homeBean;
        refreshView();

        //initData();

    }

    @Override
    public void doGetInfo() {
//        initGuideView();
        //initGuideView1();
    }

    private Map<String,Integer> pages=null;
    //刷新VIEW
    private void refreshView(){
        if(homeBean==null){
            return;
        }
        HomeBean.HomeData homeData=homeBean.getData();

        //设置banner
        setBanner(homeData.getBanner());

        List<HomeBean.HomeDataItem> items=new ArrayList<>();
        items.add(homeData.getNewList());
        items.add(homeData.getHotList());
        items.addAll(homeData.getTagsList());

        if(pages==null){
            pages=new HashMap<>();
            for(HomeBean.HomeDataItem item:items){
                pages.put(item.getShortId(),1);
            }
        }

        indexAdapter.setData(items);

        //设置item按钮
        List<HomeBean.HomeTypeItem> itemBeans= homeData.getTitleModels();
        for(int i=0;i<itemBeans.size();i++){
            setBtn(i,itemBeans.get(i));
        }

        //设置List 高度
        setHeigth(listView);
        scrollView.smoothScrollTo(0,0);
    }

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
    }

//    @Override
    public void initData() {

//        if(isRefresh) {
//          //presenter.getHomeData();
//        }
//        isRefresh=false;
    }

    private void refreshData(){
        if(isRefresh) {
            presenter.getHomeData();
        }
        isRefresh=false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onHomeTypeResult(HomeBean.HomeDataItem homeDataItem) {
        List<HomeBean.HomeDataItem> dataItems= indexAdapter.dataList;
        //判断没有了就从第一页开始请求
        if(homeDataItem.getList().size()<=0){
            toast("没有更多了...");
            pages.put(homeDataItem.getShortId(),0);
            return;
        }
        int position=-1;
        for (int i=0; i < dataItems.size(); i++) {
            HomeBean.HomeDataItem item = dataItems.get(i);
            if (item.getShortId().equals(homeDataItem.getShortId())) {
                position=i;
                item.setList(homeDataItem.getList());
                break;
            }
        }
        if(position==-1){
            return;
        }
        indexAdapter.notifyDataSetChanged(position,listView);
        //设置List 高度
        setHeigth(listView);
    }

    @Override
    public void onHomeResult(HomeBean homeBean) {
        if(refreshLayout!=null){
            refreshLayout.finishRefresh();
        }
        this.homeBean=homeBean;
        DataUtils.homeBean=homeBean;

        this.refreshView();
        scrollView.smoothScrollTo(0,0);
    }
    private boolean isLoad=true;
    android.os.Handler handler=new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            isLoad=true;
        }
    };

    //换一批
    @Override
    public void onClickChange(HomeBean.HomeDataItem dataItem) {
        int pageIndex=pages.get(dataItem.getShortId());
        pageIndex++;

        //查询//判断是标签查询 1 、还是类型查询 2
        if(dataItem.getType()==1){
            presenter.getTypeData(pageIndex,dataItem);
        }else if(dataItem.getType()==2){
            presenter.getTagData(pageIndex,dataItem);
        }else if(dataItem.getType()==3){//热播&&dataItem.getShortId().equals("u")
            presenter.getTypeHot(pageIndex,dataItem);
        }else if(dataItem.getType()==4){//最近更新
            presenter.getTypeTagData(pageIndex,dataItem);
        }
        pages.put(dataItem.getShortId(),pageIndex);
    }
    //更多
    @Override
    public void onClickMore(HomeBean.HomeDataItem dataItem) {
        Log.e("TAG","更多"+dataItem.getName());
        Intent intent=null;
        if(dataItem.getType()==1||dataItem.getType()==3||dataItem.getType()==4){//类型
            intent=new Intent(getContext(), LastUpdatedActivity.class);
        }else if(dataItem.getType()==2){//标签
            intent=new Intent(getContext(), TagActivity.class);
        }
        if(intent!=null) {
            intent.putExtra("shortId",dataItem.getShortId());
            intent.putExtra("tag",dataItem.getName());
            intent.putExtra("type",dataItem.getType());
            getContext().startActivity(intent);
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        if(mz_banner!=null){
            mz_banner.start();
        }
        if(isFirstBoo==false&&DataUtils.homeBean==null){//是第一次加载
            isRefresh=true;
            isFirstBoo=true;
        }
        refreshData();
    }

    @Override
    public void onFragmentPause() {
        super.onFragmentPause();
        if(mz_banner!=null){
            mz_banner.pause();
        }

    }

}
