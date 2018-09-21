package com.xcore.data.bean;

import com.xcore.data.BaseBean;

import java.io.Serializable;

public class GuestBean extends BaseBean {

    GuestItemBean data;

    @Override
    public String toString() {
        return "GuestBean{" +
                "data=" + data +
                '}';
    }

    public GuestItemBean getData() {
        return data;
    }

    public void setData(GuestItemBean data) {
        this.data = data;
    }

    public static class GuestItemBean implements Serializable{
        String password;
        String name;
        //是否是游客  true:是游客
        boolean tourist;

        @Override
        public String toString() {
            return "GuestItemBean{" +
                    "password='" + password + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }

        public boolean isTourist() {
            return tourist;
        }

        public void setTourist(boolean tourist) {
            this.tourist = tourist;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
