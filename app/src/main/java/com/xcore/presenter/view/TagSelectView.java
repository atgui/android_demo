package com.xcore.presenter.view;

import com.xcore.base.BaseView;
import com.xcore.data.bean.TabBean;
import com.xcore.data.bean.TagBean;
import com.xcore.data.bean.TypeListBean;

public interface TagSelectView extends BaseView{
    void onTagResult(TagBean tagBean);
    void onResult(TagBean tagBean);
}
