package com.xcore.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;
import com.xcore.R;
import com.xcore.base.MvpActivity;
import com.xcore.data.bean.CategoriesBean;
import com.xcore.data.bean.MakeLtdTabBean;
import com.xcore.data.bean.TabBean;
import com.xcore.data.bean.TagBean;
import com.xcore.data.bean.TypeListBean;
import com.xcore.data.bean.TypeTabBean;
import com.xcore.presenter.MakeLtdPresenter;
import com.xcore.presenter.view.MakeLtdView;
import com.xcore.ui.fragment.LastUpdateFragment;
import com.xcore.ui.touch.IOnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MakeLTDActivity extends MvpActivity<MakeLtdView,MakeLtdPresenter> implements MakeLtdView,IOnRefreshListener {
    FlowLayout flowLayout;
    TabLayout tabLayout;
    ViewPager viewPager;

    int pageIndex=1;

    private String quality="";

    List<TextView> txts=new ArrayList<>();
    List<TextView> qtxts=new ArrayList<>();

    int[] res=new int[]{
            R.drawable.tag_default,
            R.drawable.tag_default};

    private String tagShortId;//当前标签ID
    private String typeShortId;//当前类型ID
    private int currentType=-1;//传进来的类型 只做判断
    private String shortId;
    private String tShortId;

    private List<CategoriesBean> typeList=new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_make_ltd;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("全部");
        setEdit("");

        flowLayout=findViewById(R.id.flowLayout);
        viewPager=findViewById(R.id.mViewPager);

        TabLayout tabLayout=findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        Intent intent=getIntent();
        shortId=intent.getStringExtra("shortId");

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                Log.i("TAG","select page:"+position);
                CategoriesBean categoriesBean= typeList.get(position);
                typeShortId=categoriesBean.getName();
                if(position==0){
                    typeShortId="";
                }
                pageIndex=1;
                refreshData();

                setTitle(categoriesBean.getName());
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
    private List<LastUpdateFragment> oneFragments;
    private Map<String,LastUpdateFragment> lastUpdateFragmentMap;
    private void setupViewPager(List<CategoriesBean> categoriesBeans) {
        oneFragments=new ArrayList<>();
        lastUpdateFragmentMap=new HashMap<>();

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        for(CategoriesBean item :categoriesBeans){
            LastUpdateFragment oneFragment=new LastUpdateFragment();
            adapter.addFragment(oneFragment, item.getName());
            oneFragments.add(oneFragment);
            lastUpdateFragmentMap.put(item.getName(),oneFragment);
            oneFragment.setRefrehsLister(this);
        }
        viewPager.setAdapter(adapter);
    }
    private float dpToPx(float dp){
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, this.getResources().getDisplayMetrics());
    }
    private TextView getText(final CategoriesBean cBean, final int index){
        TextView textView=new TextView(this);
        textView.setText(cBean.getName());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        textView.setPadding((int)dpToPx(10), (int)dpToPx(3), (int)dpToPx(10), (int)dpToPx(3));
        textView.setBackgroundResource(res[0]);
        textView.setTextColor(getResources().getColor(R.color.item_txt_color));
        txts.add(textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            tagShortId=cBean.getShortId();
            pageIndex=1;
            refreshData();
            changeTxt(index);
            }
        });
        return textView;
    }
    //清晰度
    private TextView getText1(final CategoriesBean cBean, final int index){
        TextView textView=new TextView(this);
        textView.setText(cBean.getName());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        textView.setPadding((int)dpToPx(10), (int)dpToPx(3), (int)dpToPx(10), (int)dpToPx(3));
        textView.setBackgroundResource(res[0]);
        textView.setTextColor(getResources().getColor(R.color.item_txt_color));

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("".equals(quality)) {
                    quality = cBean.getShortId();
                }else{
                    quality="";
                }
                pageIndex=1;
                refreshData();
                changeTxt1(index);
            }
        });
        return textView;
    }
    //改就选中
    private void changeTxt(int positon){
        TextView textView=txts.get(positon);
        textView.setBackgroundResource(res[1]);
        textView.setTextColor(getResources().getColor(R.color.title_color));
        for(int i=0;i<txts.size();i++){
            if(positon==i){
                continue;
            }
            txts.get(i).setBackgroundResource(res[0]);
            txts.get(i).setTextColor(getResources().getColor(R.color.item_txt_color));
        }
    }
    private void changeTxt1(int positon){
        if(!"".equals(quality)) {
            TextView textView = qtxts.get(positon);
            textView.setBackgroundResource(res[1]);
            textView.setTextColor(getResources().getColor(R.color.title_color));
        }else{
            positon=-1;
        }
        for(int i=0;i<qtxts.size();i++){
            if(positon==i){
                continue;
            }
            qtxts.get(i).setBackgroundResource(res[0]);
            qtxts.get(i).setTextColor(getResources().getColor(R.color.item_txt_color));
        }
    }



    //初始化所有标签信息
    private void setTab(MakeLtdTabBean.MakeLtdTabItemData tabItemData){
        //设置排序
        List<CategoriesBean> tabInfos=tabItemData.getSorttype();
        int tabIndex=0;
        for (int i = 0; i < tabInfos.size(); i++) {
            CategoriesBean cBean = tabInfos.get(i);
            TextView textView = getText(cBean,i);
            flowLayout.addView(textView);
            if (cBean.getShortId().equals(shortId)) {
                tabIndex = i;
            }
        }
        changeTxt(tabIndex);
        tagShortId=tabInfos.get(tabIndex).getShortId();

        //设置清晰度
        List<CategoriesBean> qItems=tabItemData.getQuality();
        for(int k=0;k<qItems.size();k++){
            TextView textView= getText1(qItems.get(k),k);
            flowLayout.addView(textView);
            qtxts.add(textView);
        }

        //设置类型
        typeList= tabItemData.getTags();
        if(typeList!=null){
            CategoriesBean c=new CategoriesBean();
            c.setShortId("");
            c.setName("全部");
            typeList.add(0,c);
        }
        typeShortId=typeList.get(0).getShortId();
        setupViewPager(typeList);
        //设置默认选中
        //viewPager.setCurrentItem(0);
        refreshData();
    }
    @Override
    protected void initData() {
        presenter.getTags(shortId);
    }
    //刷新数据
    private void refreshData(){
        presenter.getMakeLtds(pageIndex,tagShortId,typeShortId,shortId,quality);
        pageIndex++;
    }

    @Override
    public MakeLtdPresenter initPresenter() {
        return new MakeLtdPresenter();
    }

    @Override
    public void onRefresh(int pageIndex) {
        if(pageIndex>0){
            this.pageIndex=pageIndex;
        }
        refreshData();
    }

    @Override
    public void onTags(MakeLtdTabBean makeLtdTabBean) {//下面一排类型
        setTab(makeLtdTabBean.getData());
    }

    @Override
    public void onResult(TypeListBean typeListBean) {
        LastUpdateFragment updateFragment;
        if("".equals(typeShortId)){
            updateFragment=lastUpdateFragmentMap.get("全部");
        }else{
            updateFragment=lastUpdateFragmentMap.get(typeShortId);
        }
        if(updateFragment!=null) {
            updateFragment.setData(typeListBean);
        }
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
