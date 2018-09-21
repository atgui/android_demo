package com.xcore.data.bean;

import com.common.BaseCommon;
import com.xcore.data.BaseBean;
import com.xcore.utils.ImageUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HomeBean extends BaseBean{
    private HomeData data=new HomeData();

    public HomeData getData() {
        return data;
    }

    public void setData(HomeData data) {
        this.data = data;
    }

    public static class HomeData implements Serializable {
        List<BannerBean.BannerData> banner=new ArrayList<>();
        HomeDataItem hotList=new HomeDataItem();
        HomeDataItem newList=new HomeDataItem();
        List<HomeDataItem> tagsList=new ArrayList<HomeDataItem>();
        List<HomeTypeItem> titleModels=new ArrayList<>();

        @Override
        public String toString() {
            return "HomeData{" +
                    "banner=" + banner +
                    ", hotList=" + hotList +
                    ", newList=" + newList +
                    ", tagsList=" + tagsList +
                    '}';
        }

        public List<HomeTypeItem> getTitleModels() {
            return titleModels;
        }

        public void setTitleModels(List<HomeTypeItem> titleModels) {
            this.titleModels = titleModels;
        }

        public List<BannerBean.BannerData> getBanner() {
            return banner;
        }

        public void setBanner(List<BannerBean.BannerData> banner) {
            this.banner = banner;
        }

        public HomeDataItem getHotList() {
            return hotList;
        }

        public void setHotList(HomeDataItem hotList) {
            this.hotList = hotList;
        }

        public HomeDataItem getNewList() {
            return newList;
        }

        public void setNewList(HomeDataItem newList) {
            this.newList = newList;
        }

        public List<HomeDataItem> getTagsList() {
            return tagsList;
        }

        public void setTagsList(List<HomeDataItem> tagsList) {
            this.tagsList = tagsList;
        }
    }

    public static class HomeTypeItem implements Serializable{
        String shortId;
        String name;
        int jump;
        String resId;

        public String getResId() {
            return resId;
        }

        public String getResUrl(){
            return ImageUtils.getRes(this.resId);
        }

        public void setResId(String resId) {
            this.resId = resId;
        }

        public String getShortId() {
            return shortId;
        }

        public void setShortId(String shortId) {
            this.shortId = shortId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getJump() {
            return jump;
        }

        public void setJump(int jump) {
            this.jump = jump;
        }
    }

    public static class HomeDataItem implements Serializable{
        String shortId;
        String name;
        /**
         * 1 跳转到类型  2 跳转到标签
         */
        int type;
        List<TypeListDataBean> list;

        @Override
        public String toString() {
            return "HomeDataItem{" +
                    "shortId='" + shortId + '\'' +
                    ", name='" + name + '\'' +
                    ", type=" + type +
                    ", list=" + list +
                    '}';
        }

        public String getShortId() {
            return shortId;
        }

        public void setShortId(String shortId) {
            this.shortId = shortId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public List<TypeListDataBean> getList() {
            return list;
        }

        public void setList(List<TypeListDataBean> list) {
            this.list = list;
        }
    }
}
