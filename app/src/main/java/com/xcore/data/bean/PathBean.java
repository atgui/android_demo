package com.xcore.data.bean;

import com.xcore.data.BaseBean;

import java.io.Serializable;

public class PathBean extends BaseBean{
    PathDataBean data;

    @Override
    public String toString() {
        return "PathBean{" +
                "data=" + data +
                '}';
    }

    public PathDataBean getData() {
        return data;
    }

    public void setData(PathDataBean data) {
        this.data = data;
    }
}
