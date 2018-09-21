package com.xcore.data.bean;

import com.xcore.data.BaseBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlayerBean extends BaseBean{
    public UserInfo data;

    public UserInfo getData() {
        return data;
    }

    public void setData(UserInfo data) {
        this.data = data;
    }
}