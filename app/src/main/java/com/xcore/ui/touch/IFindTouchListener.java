package com.xcore.ui.touch;

import com.xcore.data.bean.TypeListDataBean;

public interface IFindTouchListener {
    void onDownClick(TypeListDataBean dataBean);
    //void onCollectClick(TypeListDataBean dataBean);
    void onShareClick(TypeListDataBean dataBean);
}
