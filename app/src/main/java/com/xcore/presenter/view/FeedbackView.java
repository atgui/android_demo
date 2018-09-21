package com.xcore.presenter.view;

import com.xcore.base.BaseView;
import com.xcore.data.bean.FeedbackBean;
import com.xcore.data.bean.FeedbackRecodeBean;
import com.xcore.data.bean.LikeBean;

public interface FeedbackView extends BaseView {
    void onTypeResult(FeedbackBean feedbackBean);
    void onAddResult(LikeBean likeBean);
    void onRecodeResult(FeedbackRecodeBean recodeBean);
}
