package com.xcore.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.nex3z.flowlayout.FlowLayout;
import com.xcore.R;
import com.xcore.base.MvpActivity;
import com.xcore.data.bean.CategoriesBean;
import com.xcore.data.bean.TabBean;
import com.xcore.data.bean.TypeListBean;
import com.xcore.data.bean.TypeTabBean;
import com.xcore.data.utils.DataUtils;
import com.xcore.presenter.TypePresenter;
import com.xcore.presenter.view.ITypeView;
import com.xcore.ui.fragment.TypeSubFragment;
import com.xcore.ui.touch.OnFragmentListener;
import com.xcore.utils.ViewUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeActivity extends MvpActivity<ITypeView,TypePresenter> implements ITypeView,OnFragmentListener{
    private ViewPager mViewPager;
    private FlowLayout one_flowLayout;
    private FlowLayout two_flowLayout;

    private int pageCurrent=0;//当前页

    private String t0="";//最近更新
    private String t1="";//国产
    private String t2="";//分类

    ViewPagerAdapter adapter;
    //分页
    Map<String,Integer> pageIndexs=new HashMap<>();

    //fragments  分类list
    private List<TypeSubFragment> itemFragments=new ArrayList<>();
    private List<CategoriesBean> cList=new ArrayList<>();
    //txt 种类
    private List<TextView> txtList0=new ArrayList<>();
    private List<TextView> txtList1=new ArrayList<>();

    private Map<String,Boolean> loads=new HashMap<>();

    private int type=0;
    private String shortId="";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_type;
    }
    TabBean tabBean=null;
    @Override
    protected void initViews(Bundle savedInstanceState) {
        setEdit("");
        setTitle("全部");

        one_flowLayout=findViewById(R.id.one_flowLayout);
        two_flowLayout=findViewById(R.id.two_flowLayout);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);
        if(DataUtils.typeTabBean!=null) {
            tabBean = DataUtils.typeTabBean.getData();
        }

        if(tabBean!=null){
            setTab(tabBean);
        }

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                Log.e("TAG","切换page:"+position);
                if(tabBean!=null) {
                    pageCurrent=position;
                    String sId=cList.get(pageCurrent).getShortId();
                    t2=sId;
//                boolean boo=loads.get(sId);
//                if(boo==false) {
                    refreshData();
//                }
                    setTitle(tabBean.getCategories().get(position).getName());
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        Intent intent=getIntent();
        shortId=intent.getStringExtra("shortId");
        type=intent.getIntExtra("type",0);
        intent.getStringExtra("tag");

    }

    @Override
    protected void initData() {
        if(type==2){
            this.toType(shortId);
        }else if(type==7){
            this.toSpecies(shortId);
        }else{
            refreshData();
        }
    }
    //刷新数据
    public void refreshData(){
        if(tabBean==null){
            presenter.getTags();
            return;
        }

        String shortId=cList.get(pageCurrent).getShortId();
        int pageIndex=pageIndexs.get(shortId);
        presenter.getViewData(pageIndex,t0,t1,t2,false);
    }

    //跳类型
    public void toType(String shortId){
        int position=-1;
        int index=0;
        for(CategoriesBean item:cList){
            if(item.getShortId().equals(shortId)){
                position=index;
                break;
            }
            index++;
        }
        if(position>0){
            mViewPager.setCurrentItem(position);
        }else{
            refreshData();
        }
    }

    private List<CategoriesBean> typs=new ArrayList<>();

    //跳种类
    public void toSpecies(String shortId){
        t1=shortId;
        int position=0;
        int index=0;
        for(CategoriesBean item:typs){
            if(shortId.equals(item.getShortId())){
                position=index;
                break;
            }
            index++;
        }
        changeTxt(txtList1,txtList1.get(position));
        //mViewPager.setCurrentItem(pageCurrent);
        refreshData();
    }

    private void setTab(TabBean tabBean){
        one_flowLayout.removeAllViews();
        two_flowLayout.removeAllViews();
        txtList0.clear();
        txtList1.clear();
        typs.clear();

        List<CategoriesBean> sortBeans=tabBean.getSorttype();
        if(sortBeans.size()>0&&t0.equals("")){
            t0=sortBeans.get(0).getShortId();
        }
        int sortPosition=0;
        for(int sortI=0;sortI<sortBeans.size();sortI++){
            CategoriesBean sBean=sortBeans.get(sortI);
            TextView txtV0=getText0(sBean,R.drawable.tag_default);
            if(sBean.getShortId().equals(t0)){
                sortPosition=sortI;
            }
            txtList0.add(txtV0);
            one_flowLayout.addView(txtV0);
        }
        changeTxt(txtList0,txtList0.get(sortPosition));

        List<CategoriesBean> species=tabBean.getSpecies();
        if(species.size()>0&&t1.equals("")){
            t1=species.get(0).getShortId();
        }
        int sPosition=0;
        for(int sI=0;sI<species.size();sI++){
            CategoriesBean sBean=species.get(sI);
            TextView txtV1=getText1(sBean,R.drawable.tag_default);
            two_flowLayout.addView(txtV1);
            if(sBean.getShortId().equals(t1)){
                sPosition=sI;
            }
            txtList1.add(txtV1);
            typs.add(sBean);
        }
        changeTxt(txtList1,txtList1.get(sPosition));

        List<CategoriesBean> categoriesBeans=tabBean.getCategories();
        if(categoriesBeans.size()>0&&t2.equals("")){
            t2=categoriesBeans.get(0).getShortId();
        }
        if(adapter==null) {
            adapter = new ViewPagerAdapter(getSupportFragmentManager());
            for (CategoriesBean item : categoriesBeans) {
                TypeSubFragment itemFragment = new TypeSubFragment();
                itemFragment.setmListener(this);
                adapter.addFragment(itemFragment, item.getName());
                itemFragments.add(itemFragment);
                cList.add(item);
                pageIndexs.put(item.getShortId(),1);
                loads.put(item.getShortId(),false);
            }
        }
        mViewPager.setAdapter(adapter);
    }

    private TextView getText0(final CategoriesBean cBean, int res){
        final TextView textView= ViewUtils.getText(this,cBean.getName(),R.drawable.tag_default);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t0=cBean.getShortId();
                changeTxt(txtList0,textView);
                String shortId=cList.get(pageCurrent).getShortId();
                pageIndexs.put(shortId,1);
                refreshData();
            }
        });
        return textView;
    }
    private TextView getText1(final CategoriesBean cBean, int res){
        final TextView textView=ViewUtils.getText(this,cBean.getName(),R.drawable.tag_default);// new TextView(getContext());
//        textView.setText(cBean.getName());
//        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
//        textView.setPadding((int)dpToPx(10), (int)dpToPx(2), (int)dpToPx(10), (int)dpToPx(2));
//        textView.setBackgroundResource(res);
//        textView.setTextColor(getResources().getColor(R.color.item_txt_color));
//        txtList1.add(textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                itemFragments.get(pageCurrent).onTouch1(cBean.getShortId());
                t1=cBean.getShortId();
                changeTxt(txtList1,textView);
                String shortId=cList.get(pageCurrent).getShortId();
                pageIndexs.put(shortId,1);
                refreshData();
            }
        });
        return textView;
    }
    private void changeTxt(List<TextView> txts,TextView txt){
        txt.setTextColor(this.getResources().getColor(R.color.title_color));
        for(TextView item : txts){
            if(item==txt){
                continue;
            }
            item.setTextColor(this.getResources().getColor(R.color.item_txt_color));
        }
    }

    @Override
    public TypePresenter initPresenter() {
        return new TypePresenter();
    }

    @Override
    public void onTagResult(TypeTabBean typeTabBean) {
        try {
            tabBean = typeTabBean.getData();
            refreshData();
        }catch (Exception e){}
    }

    @Override
    public void onViewResult(TypeListBean typeListBean) {
        String shortId=cList.get(pageCurrent).getShortId();
        loads.put(shortId,true);
        itemFragments.get(pageCurrent).setData(typeListBean);
    }

    @Override
    public void onError(String msg) {
        try {
            itemFragments.get(pageCurrent).onError(msg);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh(boolean isLoadMore) {
        String shortId=cList.get(pageCurrent).getShortId();
        int pageIndex=pageIndexs.get(shortId);
        if(isLoadMore==true){
            //加载更多
            pageIndex++;
        }else{//刷新
            pageIndex=1;
        }
        pageIndexs.put(shortId,pageIndex);
        refreshData();
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
