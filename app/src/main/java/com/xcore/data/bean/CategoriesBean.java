package com.xcore.data.bean;

import java.io.Serializable;

public class CategoriesBean implements Serializable{
    private String shortId;
    private String name;

    private boolean isChecked;

    public CategoriesBean(){}
    public CategoriesBean(String shortId,String name){
        this.shortId=shortId;
        this.name=name;
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

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public String toString() {
        return "CategoriesBean{" +
                "shortId='" + shortId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
