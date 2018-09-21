package com.xcore.presenter.view;

import com.xcore.base.BaseView;
import com.xcore.data.bean.SpeedDataBean;

public interface UpdateUserView extends BaseView {
    //上传成功
    void onUploadResult(SpeedDataBean speedDataBean);
    //更新成功
    void onUpdateResult();

    void onError(String msg);
}
