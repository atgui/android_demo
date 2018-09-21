package com.xcore.presenter.view;

import com.xcore.base.BaseView;
import com.xcore.data.bean.NvStar;
import com.xcore.data.bean.NvStarBean;
import com.xcore.data.bean.RecommendBean;
import com.xcore.data.bean.ThemeRecommendBean;

public interface RecommendView extends BaseView{
    void onRecommendNvStar(NvStarBean nvStarBean);

    void onRecommendTheme(ThemeRecommendBean recommendBean);

    void onRecommendResult(RecommendBean recommendBean);
}
