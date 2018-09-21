package com.xcore.presenter.view;

import com.xcore.base.BaseView;
import com.xcore.data.bean.RegisterBean;
import com.xcore.data.bean.TokenBean;

public interface LoginView extends BaseView {
    void getTokenResult(TokenBean tokenBean);
    void getRegisterResult(RegisterBean registerBean);
    void onError(String msg);

    void onFinalResult();
}
