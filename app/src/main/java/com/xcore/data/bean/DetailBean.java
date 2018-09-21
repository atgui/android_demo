package com.xcore.data.bean;

import com.xcore.data.BaseBean;

import java.io.Serializable;

public class DetailBean extends BaseBean{
    MovieBean data;

    public MovieBean getData() {
        return data;
    }

    public void setData(MovieBean data) {
        this.data = data;
    }
}
