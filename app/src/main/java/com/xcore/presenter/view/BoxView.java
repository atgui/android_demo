package com.xcore.presenter.view;

import com.xcore.base.BaseView;
import com.xcore.data.bean.BoxBean;

public interface BoxView extends BaseView {

    void onGetTimeResult(BoxBean boxBean);

    void onGetOpenBoxResult(BoxBean boxBean);

}
