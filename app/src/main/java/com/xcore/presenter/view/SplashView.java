package com.xcore.presenter.view;

import com.xcore.base.BaseView;
import com.xcore.data.bean.CdnBean;
import com.xcore.data.bean.GuestBean;
import com.xcore.data.bean.JsonDataBean;
import com.xcore.data.bean.RegisterBean;
import com.xcore.data.bean.TokenBean;
import com.xcore.data.bean.VersionBean;

public interface SplashView extends BaseView {
    void onJsonResult(JsonDataBean jsonDataBean);
    void onVersionResult(VersionBean versionBean);
    void onCdnResult(CdnBean cdnBean);

    void onGetGuest(GuestBean guestBean);


    void getTokenResult(TokenBean tokenBean);
    void getRegisterResult(RegisterBean registerBean);
    void onError(String msg);

    void onFinalResult();

    void onGetSpeedResult(String apiPath);

    void onGetResSpeedResult(String resPath);

    void onGetTorrentSpeedResult(String torPath);

    void onSpeedFinalResult();
}
