package com.xcore.data.bean;

import java.io.Serializable;

public class Tag implements Serializable{
    private String shortId;
    private String name;


    private boolean checked=false;

    @Override
    public String toString() {
        return "Tag{" +
                "shortId='" + shortId + '\'' +
                ", name='" + name + '\'' +
                ", checked=" + checked +
                '}';
    }

    public void setShortId(String shortId) {
        this.shortId = shortId;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getShortId() {
        return shortId;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
