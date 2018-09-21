package com.xcore.presenter.view;

import com.xcore.base.BaseView;
import com.xcore.data.bean.PlayerBean;

public interface MeView extends BaseView{
    void onResult(PlayerBean playerBean);
}
