package com.xcore.presenter.view;

import com.xcore.base.BaseView;
import com.xcore.data.bean.TypeListBean;
import com.xcore.data.bean.TypeTabBean;

public interface ITypeView extends BaseView{
    void onTagResult(TypeTabBean typeTabBean);
    void onViewResult(TypeListBean typeListBean);

    void onError(String msg);
}
