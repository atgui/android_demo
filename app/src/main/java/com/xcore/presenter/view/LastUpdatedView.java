package com.xcore.presenter.view;

import com.xcore.base.BaseView;
import com.xcore.data.bean.CategoriesBean;
import com.xcore.data.bean.TypeListBean;
import com.xcore.data.bean.TypeTabBean;

import java.util.List;

public interface LastUpdatedView extends BaseView {
    void onTagResult(TypeTabBean typeTabBean);

    void onDataResult(TypeListBean typeListBean);
}
