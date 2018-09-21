package com.xcore.data.bean;

import com.xcore.data.BaseBean;
import com.xcore.data.BasePageBean;

import java.io.Serializable;
import java.util.List;

public class NoticeBean extends BasePageBean{

    List<NoticeItem> list;

    public List<NoticeItem> getList() {
        return list;
    }

    public void setList(List<NoticeItem> list) {
        this.list = list;
    }

    public static class NoticeItem  implements Serializable {
        String title;
        String text;
        String releasetime;

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

        public String getReleasetime() {
            return releasetime;
        }

        public void setReleasetime(String releasetime) {
            this.releasetime = releasetime;
        }
    }
}
