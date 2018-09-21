package com.xcore.data;

import java.io.Serializable;

public class CmModelBean implements Serializable{

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean succeed() {
        return code == 0;
    }
}
