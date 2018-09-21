package com.xcore.data.bean;

import java.io.Serializable;

public class PathDataBean implements Serializable{
    String path;

    @Override
    public String toString() {
        return "PathDataBean{" +
                "path='" + path + '\'' +
                '}';
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
