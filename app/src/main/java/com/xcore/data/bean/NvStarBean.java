package com.xcore.data.bean;

import com.xcore.data.BasePageBean;

import java.util.List;

public class NvStarBean extends BasePageBean{

    private List<NvStar> list;

    public List<NvStar> getList() {
        return list;
    }

    public void setList(List<NvStar> list) {
        this.list = list;
    }
}
