package com.xcore.ui.activity;

import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xcore.R;
import com.xcore.base.BaseActivity;
import com.xcore.cache.beans.CacheBean;
import com.xcore.ui.fragment.OverCacheFragment;
import com.xcore.ui.fragment.RecodeFragment;
import com.xcore.ui.fragment.RunCacheFragment;
import com.xcore.ui.touch.OnFragmentInteractionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HRecodeActivity extends BaseActivity implements OnFragmentInteractionListener {

    private TabLayout tabLayout;
    private ViewPager mViewPager;

    private int currentPage=0;
    private boolean isEditBoo=false;
    final List<Fragment> fragments=new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_hrecode;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("历史记录");
        setEdit("编辑",new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecodeFragment overCacheFragment= (RecodeFragment) fragments.get(currentPage);
                isEditBoo=!isEditBoo;
                if(isEditBoo){
                    setEdit("取消");
                }else{
                    setEdit("编辑");
                }
                overCacheFragment.onEdit(isEditBoo);
            }
        });

        currentPage=0;
        mViewPager= findViewById(R.id.mViewPager);
        tabLayout=findViewById(R.id.tabLayout);

        tabLayout.setupWithViewPager(mViewPager);

        fragments.add(RecodeFragment.newInstance("1",""));
        fragments.add(RecodeFragment.newInstance("2",""));
        fragments.add(RecodeFragment.newInstance("3",""));

        ViewFragmentAdapter adapter=
                new ViewFragmentAdapter(getSupportFragmentManager(),fragments,
                        Arrays.asList("今天","七日内","更早"));
        mViewPager.setAdapter(adapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                isEditBoo=false;
                setEdit("编辑");
                RecodeFragment recodeFragment= (RecodeFragment) fragments.get(currentPage);
                recodeFragment.onEdit(isEditBoo);
                currentPage=position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onItemClick(CacheBean cacheBean) {
        isEditBoo=false;
        setEdit("编辑");
    }


    class ViewFragmentAdapter extends FragmentPagerAdapter {
        List<Fragment> fragments=new ArrayList<>();
        List<String> titles=new ArrayList<>();

        public ViewFragmentAdapter(FragmentManager fm, List<Fragment> fs, List<String> titls) {
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
