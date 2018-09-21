package com.xcore.presenter.view;

import com.xcore.base.BaseView;
import com.xcore.cache.beans.CacheBean;
import com.xcore.data.bean.DetailBean;
import com.xcore.data.bean.LikeBean;
import com.xcore.data.bean.PathBean;

public interface DetailView extends BaseView {
    void onDetailResult(DetailBean detailBean);
    void onGetPathResult(PathBean pathBean);
    //更新收藏
    void onUpdateCollect();
    void onCacheResult(LikeBean likeBean);
    void onLikeResult(LikeBean likeBean,int type);
//    void onDownResult();
    void onError(String msg);
}
