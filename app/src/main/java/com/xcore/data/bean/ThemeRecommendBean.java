package com.xcore.data.bean;

import com.common.BaseCommon;
import com.xcore.data.BaseBean;
import com.xcore.data.BasePageBean;
import com.xcore.utils.ImageUtils;

import java.io.Serializable;
import java.util.List;

public class ThemeRecommendBean extends BasePageBean {
    public List<ThemeData> list;

    @Override
    public String toString() {
        return "ThemeRecommendBean{" +
                "list=" + list +
                '}';
    }

    public List<ThemeData> getList() {
        return list;
    }

    public void setList(List<ThemeData> list) {
        this.list = list;
    }

    public static class ThemeData implements Serializable{
        String name;
        String shortId;
        String cover;
        String maxcover;
        String remarks;
        String title;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getConverUrl(){
//            return BaseCommon.API_URL+getCover();
            return ImageUtils.getRes(this.getCover());
        }

        public String getMaxConverUrl(){
//            return BaseCommon.API_URL+getMaxcover();
            return ImageUtils.getRes(this.getMaxcover());
        }

        @Override
        public String toString() {
            return "ThemeData{" +
                    "name='" + name + '\'' +
                    ", shortId='" + shortId + '\'' +
                    ", cover='" + cover + '\'' +
                    ", remarks='" + remarks + '\'' +
                    '}';
        }

        public String getMaxcover() {
            return maxcover;
        }

        public void setMaxcover(String maxcover) {
            this.maxcover = maxcover;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getShortId() {
            return shortId;
        }

        public void setShortId(String shortId) {
            this.shortId = shortId;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }
    }
}
