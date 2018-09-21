package com.xcore.data.bean;

import java.io.Serializable;

public class TypeItemBean implements Serializable{

    private int id;
    private String name;
    private boolean isChecked;

    public TypeItemBean(){}
    public TypeItemBean(int id,String name){
        this();
        this.id=id;
        this.name=name;
        this.isChecked=false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
