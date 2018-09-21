package com.xcore.presenter.view;

import com.xcore.base.BaseView;
import com.xcore.data.bean.LikeBean;
import com.xcore.data.bean.PathBean;
import com.xcore.data.bean.TypeListBean;

public interface FindView extends BaseView{
    void onResult(TypeListBean typeListBean);
    void onPathResult(LikeBean likeBean);
}
