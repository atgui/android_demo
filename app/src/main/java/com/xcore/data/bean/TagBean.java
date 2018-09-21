package com.xcore.data.bean;

import com.xcore.data.BaseBean;
import java.util.List;

public class TagBean extends BaseBean {
    int pageIndex;
    int pageSize;
    int pageCount;
    int totalCount;
    String shortId;//大标签Id

    List<Tag> data;
    List<Tag> list;

    @Override
    public String toString() {
        return "TagBean{" +
                "pageIndex=" + pageIndex +
                ", pageSize=" + pageSize +
                ", pageCount=" + pageCount +
                ", totalCount=" + totalCount +
                ", shortId='" + shortId + '\'' +
                ", data=" + data +
                ", list=" + list +
                '}';
    }

    public List<Tag> getList() {
        return list;
    }

    public void setList(List<Tag> list) {
        this.list = list;
    }

    public List<Tag> getData() {
        return data;
    }

    public void setData(List<Tag> data) {
        this.data = data;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getShortId() {
        return shortId;
    }

    public void setShortId(String shortId) {
        this.shortId = shortId;
    }
}
