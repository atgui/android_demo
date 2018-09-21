package com.xcore.presenter.view;

import com.xcore.base.BaseView;
import com.xcore.data.bean.BannerBean;
import com.xcore.data.bean.HomeBean;
import com.xcore.data.bean.TypeListBean;

public interface HomeView extends BaseView {
    //类型查询
    void onHomeTypeResult(HomeBean.HomeDataItem homeDataItem);

    //首页所有数据
    void onHomeResult(HomeBean homeBean);
}
