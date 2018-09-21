package com.xcore.presenter.view;

import com.xcore.base.BaseView;
import com.xcore.cache.beans.DictionaryBean;
import com.xcore.data.bean.SearchBean;
import com.xcore.data.bean.TypeListBean;

import java.util.List;

public interface SearchView extends BaseView {
    void getHostoryDictionary(List<DictionaryBean> dictionaryBeans);
    void getHotDictionary(SearchBean searchBean);

    void onResult(TypeListBean typeListBean);
}
