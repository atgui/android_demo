package com.xcore.presenter.view;

import com.xcore.base.BaseView;
import com.xcore.data.bean.PlayerBean;
import com.xcore.data.bean.UpgradeBean;

public interface UpgradeView extends BaseView {
    void onUpgradeResult(UpgradeBean upgradeBean);
    void onResult(PlayerBean playerBean);
}
