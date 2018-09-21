package com.xcore.presenter.view;

import com.xcore.base.BaseView;
import com.xcore.data.bean.MyShareBean;

public interface MyShareView extends BaseView{
    void onShareResult(MyShareBean shareBean);
}
