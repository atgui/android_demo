package com.xcore.data.bean;

import com.xcore.data.BaseBean;
import com.xcore.data.BasePageBean;

import java.io.Serializable;
import java.util.List;

public class FeedbackRecodeBean extends BasePageBean {

    List<RecodeItem> list;
    public List<RecodeItem> getList() {
        return list;
    }

    public void setList(List<RecodeItem> list) {
        this.list = list;
    }

    public static class RecodeItem implements Serializable {
        String guestBookTypeName;
        String content;
        String shortId;
        String parentId;
//        String imageArray;
        long releasetime;
//                "imageArray": [],

        String replyContent;
        long replyTime;

        public String getGuestBookTypeName() {
            return guestBookTypeName;
        }

        public void setGuestBookTypeName(String guestBookTypeName) {
            this.guestBookTypeName = guestBookTypeName;
        }

        public String getReplyContent() {
            return replyContent;
        }

        public void setReplyContent(String replyContent) {
            this.replyContent = replyContent;
        }

        public long getReplyTime() {
            return replyTime;
        }

        public void setReplyTime(long replyTime) {
            this.replyTime = replyTime;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getShortId() {
            return shortId;
        }

        public void setShortId(String shortId) {
            this.shortId = shortId;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public long getReleasetime() {
            return releasetime;
        }

        public void setReleasetime(long releasetime) {
            this.releasetime = releasetime;
        }
    }
}
