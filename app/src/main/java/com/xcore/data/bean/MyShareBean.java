package com.xcore.data.bean;

import com.xcore.data.BasePageBean;

import java.io.Serializable;
import java.util.List;

public class MyShareBean extends BasePageBean {

    List<MyShareBeanItem> list;

    public List<MyShareBeanItem> getList() {
        return list;
    }

    public void setList(List<MyShareBeanItem> list) {
        this.list = list;
    }

    public static class MyShareBeanItem implements Serializable {
        String status;
        String appuserName;
        long createTime;

        int state;

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getAppuserName() {
            return appuserName;
        }

        public void setAppuserName(String appuserName) {
            this.appuserName = appuserName;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }
    }
}
