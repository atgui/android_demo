package com.xcore.cache.beans;

import java.io.Serializable;

public class DictionaryBean implements Serializable {
    public String id;
    public String dicName;
    public String dicUpdateTime;
    public String dicDelete;

    @Override
    public String toString() {
        return "DictionaryBean{" +
                "id=" + id +
                ", dicName='" + dicName + '\'' +
                ", dicUpdateTime='" + dicUpdateTime + '\'' +
                ", dicDelete='" + dicDelete + '\'' +
                '}';
    }

    public String getDicDelete() {
        return dicDelete;
    }

    public void setDicDelete(String dicDelete) {
        this.dicDelete = dicDelete;
    }

    public String getDicUpdateTime() {
        return dicUpdateTime;
    }

    public void setDicUpdateTime(String dicUpdateTime) {
        this.dicUpdateTime = dicUpdateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDicName() {
        return dicName;
    }

    public void setDicName(String dicName) {
        this.dicName = dicName;
    }
}
