package com.xcore.data.bean;

import com.xcore.data.BaseBean;

public class ThemeCoverBean extends BaseBean{
    private ThemeRecommendBean.ThemeData data;

    @Override
    public String toString() {
        return "ThemeCoverBean{" +
                "data=" + data +
                '}';
    }

    public ThemeRecommendBean.ThemeData getData() {
        return data;
    }

    public void setData(ThemeRecommendBean.ThemeData data) {
        this.data = data;
    }
}
