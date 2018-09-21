package com.xcore.data.bean;

import com.xcore.data.BaseBean;

import java.util.ArrayList;
import java.util.List;

public class TypeListBean extends BaseBean{

    int pageCount;
    int pageSize;
    int pageIndex;
    int totalCount;
    String shortId;//发现页面有用到。

    List<TypeListDataBean> list=new ArrayList<>();

    @Override
    public String toString() {
        return "TypeListBean{" +
                "pageCount=" + pageCount +
                ", pageSize=" + pageSize +
                ", pageIndex=" + pageIndex +
                ", totalCount=" + totalCount +
                ", list=" + list +
                '}';
    }

    public String getShortId() {
        return shortId;
    }

    public void setShortId(String shortId) {
        this.shortId = shortId;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<TypeListDataBean> getList() {
        return list;
    }

    public void setList(List<TypeListDataBean> list) {
        this.list = list;
    }
}
