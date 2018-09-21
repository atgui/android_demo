package com.xcore.presenter.view;

import com.xcore.base.BaseView;
import com.xcore.data.bean.ThemeCoverBean;
import com.xcore.data.bean.ThemeRecommendBean;
import com.xcore.data.bean.TypeListBean;

public interface ThemeView extends BaseView {
    void onResoult(TypeListBean typeListBean);
    void onRecommendTheme(ThemeRecommendBean recommendBean);
    void onRecommendCoverResult(ThemeCoverBean themeCoverBean);
}
