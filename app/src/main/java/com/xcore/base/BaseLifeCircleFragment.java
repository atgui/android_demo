package com.xcore.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * 单层Fragment 只加载当前显示的 Fragment
 */
public class BaseLifeCircleFragment extends Fragment implements BaseView{

    public static final String TAG =  "TAG";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //Log.e(TAG,getClass().getSimpleName() + "  onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        LogUtils.i(TAG, getClass().getSimpleName() + "  onCreate getParentFragment  " + (getParentFragment() == null));
        //Log.e(TAG,getClass().getSimpleName() + "  onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Log.e(TAG,getClass().getSimpleName() + "  onCreateView");
//        LogUtils.i(TAG, getClass().getSimpleName() + "  onCreateView getParentFragment  " + (getParentFragment() == null));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
//        LogUtils.i(TAG,getClass().getSimpleName() + "  onAttachFragment");
//        LogUtils.i(TAG, getClass().getSimpleName() + "  onAttachFragment getParentFragment  " + (getParentFragment() == null));
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e(TAG,getClass().getSimpleName() + "  setUserVisibleHint " + isVisibleToUser);
//        LogUtils.i(TAG,getClass().getSimpleName() + "  isResumed() " + isResumed());
//        LogUtils.i(TAG,getClass().getSimpleName() + "  isAdded() " + isAdded());
//        LogUtils.i(TAG, getClass().getSimpleName() + "  setUserVisibleHint getParentFragment != null  " + (getParentFragment() != null));
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        //Log.e(TAG,getClass().getSimpleName() + "  onHiddenChanged " + hidden);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Log.e(TAG,getClass().getSimpleName() + "  onActivityCreated ");
//        LogUtils.i(TAG, getClass().getSimpleName() + "  onActivityCreated getParentFragment != null  " + (getParentFragment() != null));
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG,getClass().getSimpleName() + "  onStart ");
    }


    @Override
    public void onResume() {
        super.onResume();
        //Log.e(TAG,getClass().getSimpleName() + " onResume  ");
//        LogUtils.i(TAG,getClass().getSimpleName() + "   fragment.getUserVisibleHint() = "  + getUserVisibleHint());
//        LogUtils.i(TAG, getClass().getSimpleName() + "  onResume getParentFragment != null  " + (getParentFragment() != null));

    }

    @Override
    public void onPause() {
        super.onPause();
        //Log.e(TAG,getClass().getSimpleName() + "  onPause ");
//        LogUtils.i(TAG,getClass().getSimpleName() + "   fragment.getUserVisibleHint() = "  + getUserVisibleHint());
//        LogUtils.i(TAG, getClass().getSimpleName() + "  onPause getParentFragment != null  " + (getParentFragment() != null));

    }

    @Override
    public void onStop() {
        super.onStop();
        //Log.e(TAG,getClass().getSimpleName() + "  onStop ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //Log.e(TAG,getClass().getSimpleName() + "  onDestroyView ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.e(TAG,getClass().getSimpleName() + "  onDestroy ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //Log.e(TAG,getClass().getSimpleName() + "  onDetach ");
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    Toast toast;
    @Override
    public void toast(CharSequence s) {
        if(toast==null){
            toast=Toast.makeText(getContext(),s,Toast.LENGTH_SHORT);
        }
        toast.setGravity(0,0, Gravity.CENTER);
        toast.show();
    }

    @Override
    public void toast(int id) {

    }

    @Override
    public void toastLong(CharSequence s) {
        if(toast==null){
            toast=Toast.makeText(getContext(),s,Toast.LENGTH_LONG);
        }
        toast.setGravity(0,0, Gravity.CENTER);
        toast.show();
    }

    @Override
    public void toastLong(int id) {

    }

    @Override
    public void showNullLayout() {

    }

    @Override
    public void hideNullLayout() {

    }

    @Override
    public void showErrorLayout(View.OnClickListener listener) {

    }

    @Override
    public void hideErrorLayout() {

    }
}
