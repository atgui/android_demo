package com.xcore.data.bean;

import com.xcore.data.BaseBean;

import java.io.Serializable;
import java.util.List;

public class MakeLtdTabBean extends BaseBean {

    private MakeLtdTabItemData data;

    public MakeLtdTabItemData getData() {
        return data;
    }

    public void setData(MakeLtdTabItemData data) {
        this.data = data;
    }

    public static class MakeLtdTabItemData implements Serializable{
        private List<CategoriesBean> sorttype;
        private List<CategoriesBean> tags;
        private List<CategoriesBean> quality;//调高清选项


        public List<CategoriesBean> getQuality() {
            return quality;
        }

        public void setQuality(List<CategoriesBean> quality) {
            this.quality = quality;
        }

        public List<CategoriesBean> getSorttype() {
            return sorttype;
        }

        public void setSorttype(List<CategoriesBean> sorttype) {
            this.sorttype = sorttype;
        }

        public List<CategoriesBean> getTags() {
            return tags;
        }

        public void setTags(List<CategoriesBean> tags) {
            this.tags = tags;
        }
    }
}
