package com.xcore.ui.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;
import com.xcore.R;
import com.xcore.ui.touch.IOnRefreshListener;
import com.xcore.base.MvpActivity;
import com.xcore.data.bean.CategoriesBean;
import com.xcore.data.bean.TabBean;
import com.xcore.data.bean.TypeListBean;
import com.xcore.data.bean.TypeTabBean;
import com.xcore.data.utils.DataUtils;
import com.xcore.presenter.LastUpdatedPresenter;
import com.xcore.presenter.view.LastUpdatedView;
import com.xcore.ui.fragment.LastUpdateFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LastUpdatedActivity extends MvpActivity<LastUpdatedView,LastUpdatedPresenter> implements LastUpdatedView,IOnRefreshListener {
    FlowLayout flowLayout;
    TabLayout tabLayout;
    ViewPager viewPager;

    int pageIndex=1;

    TypeTabBean typeTabBean;//标签信息
    List<TextView> txts=new ArrayList<>();

    int[] res=new int[]{
            R.drawable.tag_default,
            R.drawable.tag_default};

    private String tagShortId;//当前标签ID
    private String typeShortId;//当前类型ID
    private int currentType=-1;//传进来的类型 只做判断
    private String shortId;
    private String tShortId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_last_updated;
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
            lastUpdateFragmentMap.put(item.getShortId(),oneFragment);
            oneFragment.setRefrehsLister(this);
        }
        viewPager.setAdapter(adapter);
    }
    private float dpToPx(float dp){
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, this.getResources().getDisplayMetrics());
    }
    private TextView getText(final CategoriesBean cBean,final int index){
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
                initData();
                changeTxt(index);
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

    TypeTabBean tabBean;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setEdit("");

        if(DataUtils.typeTabBean!=null){
            tabBean=DataUtils.typeTabBean;
        }

        flowLayout=findViewById(R.id.flowLayout);
        tabLayout=findViewById(R.id.tab_layout);
        viewPager=findViewById(R.id.mViewPager);

        Intent intent=getIntent();
        currentType=intent.getIntExtra("type",0);

        String tag=intent.getStringExtra("tag");
        shortId=intent.getStringExtra("shortId");
        tShortId=intent.getStringExtra("ttShortId");
        if(tShortId==null){
            tShortId="";
        }
        setTitle(tag);

        if(tabBean!=null) {
            this.typeTabBean = tabBean;
            setTab();
        }

        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                Log.i("TAG","select page:"+position);
                CategoriesBean categoriesBean= typeTabBean.getData().getCategories().get(position);
                typeShortId=categoriesBean.getShortId();
                pageIndex=1;
                initData();

                setTitle(categoriesBean.getName());
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
    //初始化所有标签信息
    private void setTab(){
        TabBean tabBean= this.typeTabBean.getData();
        List<CategoriesBean> tabInfos=tabBean.getSorttype();
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

        List<CategoriesBean> layotInfos= tabBean.getCategories();
        int position=0;
        if(currentType==-1){//判断下标
            for(int i=0;i<layotInfos.size();i++){
                CategoriesBean cBean= layotInfos.get(i);
                if(shortId.equals(cBean.getShortId())){
                    position=i;
                    break;
                }
            }
        }
        setupViewPager(layotInfos);
        //设置默认选中
        viewPager.setCurrentItem(position);
        typeShortId=layotInfos.get(position).getShortId();
    }

    @Override
    protected void initData() {
        if(typeTabBean==null) {
            presenter.getTags();
        }else{//直接请求数据
            presenter.getData(pageIndex,typeShortId,tagShortId,tShortId);
            pageIndex++;
        }
    }

    @Override
    public LastUpdatedPresenter initPresenter() {
        return new LastUpdatedPresenter();
    }

    @Override
    public void onTagResult(TypeTabBean typeTabBean) {
        //得到所有的类型
        TabBean tabBean=typeTabBean.getData();
        List<CategoriesBean> categoriesBeans= tabBean.getCategories();

        categoriesBeans.add(0,new CategoriesBean("","全部"));
        tabBean.getSorttype().add(0,new CategoriesBean("","全部"));
//        tabBean.getSpecies().add(0,new CategoriesBean("","全部"));

        DataUtils.typeTabBean=typeTabBean;
        this.typeTabBean=typeTabBean;
        setTab();
        initData();
    }

    @Override
    public void onDataResult(TypeListBean typeListBean) {
        LastUpdateFragment updateFragment= lastUpdateFragmentMap.get(typeShortId);
        updateFragment.setData(typeListBean);
    }

    @Override
    public void onRefresh(int pageIndex) {
        if(pageIndex>0){
            this.pageIndex=pageIndex;
        }
        initData();
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
