package com.xcore.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.base.BaseActivity;
import com.xcore.cache.CacheManager;
import com.xcore.cache.beans.CacheBean;
import com.xcore.ui.fragment.OverCacheFragment;
import com.xcore.ui.fragment.RunCacheFragment;
import com.xcore.ui.touch.OnFragmentInteractionListener;
import com.xcore.utils.SystemUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CacheActivity extends BaseActivity implements OnFragmentInteractionListener {
    private TabLayout tabLayout;
    private ViewPager mViewPager;

    private int currentPage=0;
    private boolean isEditBoo=false;

    private TextView txtDisk;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cache;
    }
    final List<Fragment> fragments=new ArrayList<>();


    @Override
    protected void initViews(Bundle savedInstanceState) {
        txtDisk=findViewById(R.id.txt_disk);

        xSpeace();

        findViewById(R.id.clear_speace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CacheManager.getInstance().getDownHandler().clearOtherDir();
                xSpeace();
            }
        });
        //findViewById(R.id.clear_speace).setVisibility(View.GONE);

        setTitle("我的缓存");
        setEdit("编辑",new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(currentPage==0) {
                RunCacheFragment cacheFragment= (RunCacheFragment) fragments.get(currentPage);
                isEditBoo=!isEditBoo;
                cacheFragment.onEdit(isEditBoo);
            }else{
                OverCacheFragment overCacheFragment= (OverCacheFragment) fragments.get(currentPage);
                isEditBoo=!isEditBoo;
                overCacheFragment.onEdit(isEditBoo);
            }
            if(isEditBoo){
                setEdit("取消");
            }else{
                setEdit("编辑");
            }
            }
        });

        currentPage=0;
        mViewPager= findViewById(R.id.mViewPager);
        tabLayout=findViewById(R.id.tabLayout);

        tabLayout.setupWithViewPager(mViewPager);

        fragments.add(RunCacheFragment.newInstance("",""));
        fragments.add(OverCacheFragment.newInstance("",""));

        ViewFragmentAdapter adapter=
                new ViewFragmentAdapter(getSupportFragmentManager(),fragments,
                        Arrays.asList("正在缓存","已缓存"));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                currentPage=position;
                if(position==0){
                    RunCacheFragment cacheFragment= (RunCacheFragment) fragments.get(currentPage);
                    cacheFragment.initData();
                }else{
                    OverCacheFragment overCacheFragment= (OverCacheFragment) fragments.get(currentPage);
                    overCacheFragment.refreshData();
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                isEditBoo=false;
                setEdit("编辑");
                if(currentPage==0) {
                    RunCacheFragment cacheFragment= (RunCacheFragment) fragments.get(currentPage);
                    cacheFragment.onEdit(isEditBoo);
                }else{
                    OverCacheFragment cacheFragment= (OverCacheFragment) fragments.get(currentPage);
                    cacheFragment.onEdit(isEditBoo);
                }
                currentPage=position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 更新空间大小
     */
    private void xSpeace(){
        long memorySize= SystemUtils.getAvailableInternalMemorySize();
        String valStr=SystemUtils.FormetFileSize(memorySize);

        final String sPath=MainApplicationContext.SD_PATH;
        String fStr=SystemUtils.getAutoFolderOrFileSize(sPath);

        txtDisk.setText("已用空间:"+fStr+" 可用空间:"+valStr);
    }

    @Override
    protected void initData() {

    }

    //点击选中?
    @Override
    public void onItemClick(CacheBean cacheBean) {
        isEditBoo=false;
        setEdit("编辑");
        xSpeace();
    }


    class ViewFragmentAdapter extends FragmentPagerAdapter{
        List<Fragment> fragments=new ArrayList<>();
        List<String> titles=new ArrayList<>();

        public ViewFragmentAdapter(FragmentManager fm,List<Fragment> fs,List<String> titls) {
            super(fm);
            this.fragments=fs;
            this.titles=titls;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return this.titles.get(position);
        }
    }
}
