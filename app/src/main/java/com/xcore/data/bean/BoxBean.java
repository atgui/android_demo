package com.xcore.data.bean;

import com.xcore.data.BaseBean;

public class BoxBean extends BaseBean{
    int data;

    @Override
    public String toString() {
        return "BoxBean{" +
                "data=" + data +
                '}';
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }
}
