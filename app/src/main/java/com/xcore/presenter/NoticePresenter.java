package com.xcore.presenter;

import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xcore.base.BasePresent;
import com.xcore.data.bean.NoticeBean;
import com.xcore.data.utils.AGCallback;
import com.xcore.presenter.view.NoticeView;
import com.xcore.services.ApiFactory;

public class NoticePresenter extends BasePresent<NoticeView>{
    //得到公告信息
    public void getNotices(final int pageIndex){
        if(!checkNetwork()){
            return;
        }
        if(dialog!=null){
            dialog.show();
        }
        HttpParams params=new HttpParams();
        params.put("PageIndex",pageIndex);

        ApiFactory.getInstance().<NoticeBean>getNotices(params, new AGCallback<NoticeBean>() {
            @Override
            public void onNext(NoticeBean noticeBean) {
                view.onResult(noticeBean);
                if(dialog!=null){
                    dialog.cancel();
                }
            }

            @Override
            public void onError(Response<NoticeBean> response) {
                super.onError(response);
                if(dialog!=null){
                    dialog.cancel();
                }
            }
        });
    }

}
