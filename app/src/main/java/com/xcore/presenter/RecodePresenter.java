package com.xcore.presenter;

import com.xcore.base.BasePresent;
import com.xcore.cache.CacheManager;
import com.xcore.cache.beans.CacheBean;
import com.xcore.cache.beans.CacheType;
import com.xcore.presenter.view.RecodeView;

import java.util.List;

public class RecodePresenter extends BasePresent<RecodeView> {
    /**
     * 得到收藏缓存
     * @param pageIndex
     */
    public void getCollectList(int pageIndex){
        List<CacheBean> beanList= dbHandler.query(pageIndex, CacheType.CACHE_COLLECT);
        view.onCollectResult(beanList);
    }

    /**
     * 得到观看缓存
     * @param pageIndex
     */
    public void getRecodetList(int pageIndex){
        List<CacheBean> beanList= dbHandler.query(pageIndex, CacheType.CACHE_RECODE);
        view.onRecodeResult(beanList);
    }

    public void updateCollect(CacheBean cacheBean){
        CacheBean cMod=cacheBean;//new CacheBean();
        cMod.settDelete(cacheBean.gettDelete());
        cMod.settType(CacheType.CACHE_COLLECT);
        cMod.setShortId(cacheBean.getShortId());

//        dbHandler.insertCache(cMod);// .update(cMod);
    }

}
