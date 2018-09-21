package com.xcore.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.lzy.okgo.OkGo;

public abstract class MvpFragment<V,P extends BasePresent<V>> extends BaseFragment {
    protected P presenter;



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter=initPresenter();
//        if(isLoad==false) {//数据只请求一次
            initData();
//            isLoad=true;
//        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("TAG","Fragment onResume");
        presenter.attach((V) this);
    }

    @Override
    public void onDestroy() {
        presenter.detach();
        OkGo.getInstance().cancelTag(this);
        super.onDestroy();
    }

    public abstract P initPresenter();
}
