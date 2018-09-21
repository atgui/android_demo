package com.xcore.data.bean;

import com.xcore.data.BaseBean;

import java.io.Serializable;
import java.util.List;

public class FeedbackBean extends BaseBean{

    List<FeedbackItem> data;

    public List<FeedbackItem> getData() {
        return data;
    }

    public void setData(List<FeedbackItem> data) {
        this.data = data;
    }

    public static class FeedbackItem implements Serializable {
        String name;
        String shortId;

        public boolean selected=false;

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
    }
}
