package com.xcore.presenter.view;

import com.xcore.base.BaseView;
import com.xcore.cache.beans.CacheBean;

import java.util.List;


public interface RecodeView extends BaseView {
    void onCollectResult(List<CacheBean> cacheBeans);
    void onRecodeResult(List<CacheBean> cacheBeans);
}
