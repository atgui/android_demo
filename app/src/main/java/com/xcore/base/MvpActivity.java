package com.xcore.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.xcore.MainApplicationContext;
import com.xcore.R;

public abstract class MvpActivity<V,P extends BasePresent<V>> extends BaseActivity{

    protected P presenter;
    private String className=this.getClass().getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        presenter = initPresenter();
        super.onCreate(savedInstanceState);
        presenter.attach((V) this);
        initData();
        presenter.joinView(className,getParamsStr());
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if(presenter!=null) {
//            presenter.attach((V) this);
//        }
    }

    @Override
    protected void onDestroy() {
        if(presenter!=null) {
            presenter.outView(className,getParamsStr());
            presenter.detach();
        }
        OkGo.getInstance().cancelTag(this);
        super.onDestroy();
    }

    public abstract P initPresenter();
    public String getParamsStr(){
        return "这个是"+className+"页面哦!!!";
    }


}