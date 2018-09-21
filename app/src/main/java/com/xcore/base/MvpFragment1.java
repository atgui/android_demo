package com.xcore.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lzy.okgo.OkGo;

public abstract class MvpFragment1<V,P extends BasePresent<V>> extends LazyLoadBaseFragment {
    protected P presenter;

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        presenter=initPresenter();
////        if(isLoad==false) {//数据只请求一次
//            //initData();
////            isLoad=true;
////        }
//    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        presenter=initPresenter();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.attach((V) this);
    }

    @Override
    public void onDestroy() {
        if(presenter!=null) {
            presenter.detach();
        }
        OkGo.getInstance().cancelTag(this);
        super.onDestroy();
    }

    public abstract P initPresenter();
}
