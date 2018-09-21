package com.xcore.data.bean;

import com.xcore.data.BaseBean;

import java.io.Serializable;

public class NoticeAlertBean extends BaseBean {

    NoticeItemData data;

    @Override
    public String toString() {
        return "NoticeAlertBean{" +
                "data=" + data +
                '}';
    }

    public NoticeItemData getData() {
        return data;
    }

    public void setData(NoticeItemData data) {
        this.data = data;
    }

    public static class NoticeItemData implements Serializable{
        String title;
        String text;

        @Override
        public String toString() {
            return "NoticeItemData{" +
                    "title='" + title + '\'' +
                    ", text='" + text + '\'' +
                    '}';
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
