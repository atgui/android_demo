package com.xcore.presenter.view;

import com.xcore.base.BaseView;
import com.xcore.data.bean.MakeLtdTabBean;
import com.xcore.data.bean.TypeListBean;

public interface MakeLtdView extends BaseView{
    void onTags(MakeLtdTabBean makeLtdTabBean);
    void onResult(TypeListBean typeListBean);

}
