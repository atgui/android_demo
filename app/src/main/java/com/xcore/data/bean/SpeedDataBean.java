package com.xcore.data.bean;

import com.xcore.data.BaseBean;

public class SpeedDataBean extends BaseBean {

    private String data;

    @Override
    public String toString() {
        return "SpeedDataBean{" +
                "data='" + data + '\'' +
                '}';
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
