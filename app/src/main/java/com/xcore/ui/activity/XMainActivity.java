package com.xcore.ui.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.common.BaseCommon;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.xcore.HeartTest;
import com.xcore.JavaSocketTest;
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.base.BaseActivity;
import com.xcore.cache.CacheManager;
import com.xcore.data.bean.NoticeAlertBean;
import com.xcore.data.utils.AGCallback;
import com.xcore.services.ApiFactory;
import com.xcore.ui.fragment.IndexFragment1;
import com.xcore.ui.fragment.RecommendFragment1;
import com.xcore.ui.fragment.SelfCenterFragment1;
import com.xcore.ui.fragment.TypeFragment1;
import com.xcore.utils.GuideUtil;
import com.xcore.utils.SystemUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

public class XMainActivity extends BaseActivity {

    private static ViewPager viewPager;
    private MenuItem menuItem;
    private BottomNavigationView bottomNavigationView;
    private static List<Fragment> fragments=new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_xmain;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        //默认 >3 的选中效果会影响ViewPager的滑动切换时的效果，故利用反射去掉
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        toolbar.setBackgroundColor(getResources().getColor(R.color.black_1A));
                        switch (item.getItemId()) {
                            case R.id.item_news:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.item_lib:
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.item_find:
                                viewPager.setCurrentItem(2);
                                break;
                            case R.id.item_more:
                                toolbar.setBackgroundColor(getResources().getColor(R.color.color_1f1711));
                                viewPager.setCurrentItem(3);
                                break;
                        }
                        return false;
                    }
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                menuItem = bottomNavigationView.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        setupViewPager(viewPager);

        init();
    }
    private HeartTest heartTimer;
    private void init(){
        MainApplicationContext.removeActivityBy(this);

        //初始化模块组件
//        MainApplicationContext.initDolitBT();

        //启动服务
        MainApplicationContext.startP2PServer();

        //新手引导
        GuideUtil.show(this,null);
        try {
            String playCacheStr=CacheManager.getInstance().getLocalHandler().get(CacheManager.PLAY_IS_CACHE);
            if(!TextUtils.isEmpty(playCacheStr)){
                MainApplicationContext.IS_PLAYING_TO_CACHE=true;
            }
            String idleCacheStr=CacheManager.getInstance().getLocalHandler().get(CacheManager.IDLE_IS_CACHE);
            if(!TextUtils.isEmpty(idleCacheStr)){
                MainApplicationContext.IS_IDLE_CACHE=true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //心跳
        heartTimer=new HeartTest();
        heartTimer.start();

        //测试api socket
        new JavaSocketTest().start();
    }



    @Override
    protected void initData() {

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        IndexFragment1 indexFragment= IndexFragment1.newInstance("","");
        fragments.add(indexFragment);

        RecommendFragment1 recommendFragment= RecommendFragment1.newInstance("","");
        fragments.add(recommendFragment);

        TypeFragment1 blankFragment= TypeFragment1.newInstance("","");
        fragments.add(blankFragment);

        SelfCenterFragment1 selfCenterFragment= new SelfCenterFragment1();
        fragments.add(selfCenterFragment);

        adapter.addFragment(indexFragment);
        adapter.addFragment(recommendFragment);
        adapter.addFragment(blankFragment);
        adapter.addFragment(selfCenterFragment);
        viewPager.setAdapter(adapter);

    }

    private long mExitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 3000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                MainApplicationContext.finishAllActivity();
                //finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();

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

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

    }
    public static class BottomNavigationViewHelper {

        @SuppressLint("RestrictedApi")
        @TargetApi(Build.VERSION_CODES.KITKAT)
        public static void disableShiftMode(BottomNavigationView navigationView) {

            BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigationView.getChildAt(0);
            try {
                Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
                shiftingMode.setAccessible(true);
                shiftingMode.setBoolean(menuView, false);
                shiftingMode.setAccessible(false);

                for (int i = 0; i < menuView.getChildCount(); i++) {
                    BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(i);
                    itemView.setShiftingMode(false);
                    itemView.setChecked(itemView.getItemData().isChecked());
                }

            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        heartTimer.destroy();

        //保存数据库
        CacheManager.getInstance().getDownHandler().saveCacheData();
    }
}
