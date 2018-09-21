package com.xcore.ui.touch;

import com.xcore.data.bean.MovieBean;
import com.xcore.ui.enums.DetailTouchType;

public interface IDetailOnClick {
    /**
     * 点击
     * @param movieBean
     * @param position
     * @param type 0:点击收藏  1:点击分享  2:点击下载  3:点击Item
     */
    void onItemClick(MovieBean movieBean,int position,DetailTouchType type);
}
