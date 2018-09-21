package com.xcore.data.bean;

import com.xcore.data.BaseBean;
import java.util.List;

public class TypeTabBean extends BaseBean {

    private TabBean data;

    public TabBean getData() {
        return data;
    }

    public void setData(TabBean data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "TypeTabBean{" +
                "data=" + data +
                '}';
    }
}
