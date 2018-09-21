package com.xcore.data.bean;

import com.xcore.data.BaseBean;

import java.io.Serializable;
import java.util.List;

public class SearchBean extends BaseBean{
    public List<SearchItem> data;

    public List<SearchItem> getData() {
        return data;
    }

    public void setData(List<SearchItem> data) {
        this.data = data;
    }

    public static class SearchItem implements Serializable{
        String name;
        int sort;
        int change;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        /**
         * 0 持平  1 涨  -1 跌
         * @return
         */
        public int getChange() {
            return change;
        }

        public void setChange(int change) {
            this.change = change;
        }
    }
}
