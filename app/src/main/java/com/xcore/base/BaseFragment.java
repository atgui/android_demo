package com.xcore.base;

import android.app.ProgressDialog;
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
import android.widget.Toolbar;

import com.xcore.R;
import com.xcore.utils.StatusBarUtil;

public abstract class BaseFragment extends Fragment implements BaseView {
    public View view;
    protected ProgressDialog progress;

    public boolean isLoad=false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        if(view!=null){
//            ViewGroup parent= (ViewGroup) view.getParent();
//            if(parent!=null){
//                parent.removeView(view);
//            }
//            return view;
//        }
        view=inflater.inflate(getLayoutId(),container,false);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
//        Toolbar toolbar=view.findViewById(R.id.toolbar1);
//        if(toolbar!=null){
//            toolbar.setFitsSystemWindows(true);
//        }
//        StatusBarUtil.darkMode(getActivity());

        initView(view);
    }
    protected abstract int getLayoutId();
    protected abstract void initView(View view);
    protected abstract void initData();


    @Override
    public void showProgress() {
        if (progress == null) {
            progress = new ProgressDialog(getActivity());
        }
        progress.setMessage("加载中...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }
    @Override
    public void hideProgress() {
        if (progress != null && progress.isShowing()) {
            progress.hide();
        }
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
