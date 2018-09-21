package com.xcore.presenter.view;

import com.xcore.base.BaseView;
import com.xcore.data.bean.TypeListBean;
import com.xcore.data.bean.TypeTabBean;

public interface ActressView extends BaseView {
    void onActressResult(TypeListBean typeListBean);
    void onTagResult(TypeListBean typeListBean);

    void onTags(TypeTabBean typeTabBean);
}
