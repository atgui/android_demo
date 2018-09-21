package com.xcore.ui.activity;

import android.os.Bundle;

import com.xcore.R;
import com.xcore.base.BaseActivity;

public class ProtocolActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_protocol;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("用户协议");
        setEdit("");

    }

    @Override
    protected void initData() {

    }
}
