package com.xcore.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.xcore.MainApplicationContext;
import com.xcore.cache.CacheManager;
import com.xcore.cache.DownHandler;

import java.util.Timer;
import java.util.TimerTask;

public abstract class LazyLoadBaseFragment extends BaseLifeCircleFragment {

    protected View rootView = null;
    private boolean mIsFirstVisible = true;
    private boolean isViewCreated = false;
    private boolean currentVisibleState = false;

    private Timer _autoTimer;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //走这里分发可见状态情况有两种，1. 已缓存的 Fragment 被展示的时候 2. 当前 Fragment 由可见变成不可见的状态时
        // 对于默认 tab 和 间隔 checked tab 需要等到 isViewCreated = true 后才可以通过此通知用户可见，
        // 这种情况下第一次可见不是在这里通知 因为 isViewCreated = false 成立，可见状态在 onActivityCreated 中分发
        // 对于非默认 tab，View 创建完成  isViewCreated =  true 成立，走这里分发可见状态，mIsFirstVisible 此时还为 false  所以第一次可见状态也将通过这里分发
        if (isViewCreated){
            if (isVisibleToUser && !currentVisibleState) {
                dispatchUserVisibleHint(true);
            }else if (!isVisibleToUser && currentVisibleState){
                dispatchUserVisibleHint(false);
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 将 View 创建完成标志位设为 true
        isViewCreated = true;
        // 默认 Tab getUserVisibleHint() = true !isHidden() = true
        // 对于非默认 tab 或者非默认显示的 Fragment 在该生命周期中只做了 isViewCreated 标志位设置 可见状态将不会在这里分发
        if (!isHidden() && getUserVisibleHint()){
            dispatchUserVisibleHint(true);
        }

    }


    /**
     * 统一处理 显示隐藏  做两件事
     * 设置当前 Fragment 可见状态 负责在对应的状态调用第一次可见和可见状态，不可见状态函数
     * @param visible
     */
    private void dispatchUserVisibleHint(boolean visible) {
        currentVisibleState = visible;
        if (visible) {
            if (mIsFirstVisible) {
                mIsFirstVisible = false;
                onFragmentFirstVisible();
            }
            onFragmentResume();
        }else {
            onFragmentPause();
        }
    }

    /**
     * 该方法与 setUserVisibleHint 对应，调用时机是 show，hide 控制 Fragment 隐藏的时候，
     * 注意的是，只有当 Fragment 被创建后再次隐藏显示的时候才会调用，第一次 show 的时候是不会回调的。
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){
            dispatchUserVisibleHint(false);
        }else {
            dispatchUserVisibleHint(true);
        }
    }

    /**
     * 需要再 onResume 中通知用户可见状态的情况是在当前页面再次可见的状态 !mIsFirstVisible 可以保证这一点，
     * 而当前页面 Activity 可见时所有缓存的 Fragment 都会回调 onResume
     * 所以我们需要区分那个Fragment 位于可见状态
     * (!isHidden() && !currentVisibleState && getUserVisibleHint()）可条件可以判定哪个 Fragment 位于可见状态
     */
    @Override
    public void onResume() {
        super.onResume();
        if (!mIsFirstVisible){
            if (!isHidden() && !currentVisibleState && getUserVisibleHint()){
                dispatchUserVisibleHint(true);
            }
        }
    }

    /**
     * 当用户进入其他界面的时候所有的缓存的 Fragment 都会 onPause
     * 但是我们想要知道只是当前可见的的 Fragment 不可见状态，
     * currentVisibleState && getUserVisibleHint() 能够限定是当前可见的 Fragment
     */
    @Override
    public void onPause() {
        super.onPause();

        if (currentVisibleState && getUserVisibleHint()){
            dispatchUserVisibleHint(false);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //当 View 被销毁的时候我们需要重新设置 isViewCreated mIsFirstVisible 的状态
        isViewCreated = false;
        mIsFirstVisible = true;
    }

    /**
     * 对用户第一次可见
     */
    public void onFragmentFirstVisible(){
        Log.e(TAG,getClass().getSimpleName() + "  对用户第一次可见");
    }

    /**
     *   对用户可见
     */
    public void onFragmentResume(){
        Log.e(TAG,getClass().getSimpleName() + "  对用户可见");

        //先判断空闲自动缓存是否开启
        boolean idleBoo= MainApplicationContext.IS_IDLE_CACHE;
        if(idleBoo==true&&!"PVideoViewActivity".equals(getClass().getSimpleName())) {
            //先把所有任务停止了
            final DownHandler downHandler= CacheManager.getInstance().getDownHandler();
            downHandler.stopAll();
            if (_autoTimer == null) {
                _autoTimer = new Timer();
                _autoTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        downHandler.startAll();
                    }
                }, MainApplicationContext.IDLE_TIMER);
            }
        }
    }

    /**
     *  对用户不可见
     */
    public void onFragmentPause(){
        Log.e(TAG,getClass().getSimpleName() + "  对用户不可见");
        boolean idleBoo= MainApplicationContext.IS_IDLE_CACHE;
        Log.e("TAG",getClass().getSimpleName());
        if(idleBoo==true&&!"PVideoViewActivity".equals(getClass().getSimpleName())) {
            CacheManager.getInstance().getDownHandler().stopAll();
        }
        if(_autoTimer!=null){
            _autoTimer.cancel();
        }
        _autoTimer=null;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);

        if (rootView == null) {
            Log.e("TAG","创建VIEW.");
            rootView = inflater.inflate(getLayoutId(), container, false);
        }
        initView(rootView);

        ViewTreeObserver vto = rootView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override public void onGlobalLayout() {
                /* 获取子控件信息 */
                doGetInfo();
                /* 获取子控件信息后，注销回调用接口 */
                rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);  } });
        return rootView;
    }
    public void doGetInfo(){}
    /**
     * 返回布局 resId
     *
     * @return layoutId
     */
    protected abstract int getLayoutId();


    /**
     * 初始化view
     *
     * @param rootView
     */
    protected abstract void initView(View rootView);

}
