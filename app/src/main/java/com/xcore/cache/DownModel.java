package com.xcore.cache;

/**
 * 缓存信息类
 */
public class DownModel {
    private String shortId;
    private String name;
    private String url;
    private String conver;

    public String getConver() {
        return conver;
    }

    public void setConver(String conver) {
        this.conver = conver;
    }

    public DownModel(){}
    public DownModel(String name,String url){
        this.name=name;
        this.url=url;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
