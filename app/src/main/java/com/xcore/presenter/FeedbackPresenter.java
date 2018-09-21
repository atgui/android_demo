package com.xcore.presenter;

import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xcore.base.BasePresent;
import com.xcore.data.bean.FeedbackBean;
import com.xcore.data.bean.FeedbackRecodeBean;
import com.xcore.data.bean.LikeBean;
import com.xcore.data.utils.AGCallback;
import com.xcore.presenter.view.FeedbackView;
import com.xcore.services.ApiFactory;


public class FeedbackPresenter extends BasePresent<FeedbackView> {
    //得到类型
    public void getFeedbackTypes(){
        if(!checkNetwork()){
            return;
        }
        ApiFactory.getInstance().<FeedbackBean>getFeedbackTypes(new AGCallback<FeedbackBean>() {
            @Override
            public void onNext(FeedbackBean feedbackBean) {
                view.onTypeResult(feedbackBean);
            }
        });
    }
    //添加反馈
    public void addFeedback(final String categories, final String key){
        if(!checkNetwork()){
            return;
        }
        if(dialog!=null){
            dialog.show();
        }
        HttpParams params=new HttpParams();
        params.put("categories",categories);
        params.put("key",key);

        ApiFactory.getInstance().<LikeBean>addFeedback(params, new AGCallback<LikeBean>() {
            @Override
            public void onNext(LikeBean likeBean) {
                view.onAddResult(likeBean);
                if(dialog!=null){
                    dialog.cancel();
                }
            }

            @Override
            public void onError(Response<LikeBean> response) {
                super.onError(response);
                if(dialog!=null){
                    dialog.cancel();
                }
            }
        });
    }
    //获取列表
    public void getFeedList(final int pageIndex){
        if(!checkNetwork()){
            return;
        }
        if(dialog!=null){
            dialog.show();
        }
        HttpParams params=new HttpParams();
        params.put("PageIndex",pageIndex);

        ApiFactory.getInstance().<FeedbackRecodeBean>getFeedbackList(params, new AGCallback<FeedbackRecodeBean>() {
            @Override
            public void onNext(FeedbackRecodeBean recodeBean) {
                view.onRecodeResult(recodeBean);
                if(dialog!=null){
                    dialog.cancel();
                }
            }

            @Override
            public void onError(Response<FeedbackRecodeBean> response) {
                super.onError(response);
                if(dialog!=null){
                    dialog.cancel();
                }
            }
        });
    }

}
