package com.xcore.data.bean;

import com.common.BaseCommon;
import com.xcore.utils.DateUtils;
import com.xcore.utils.ImageUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TypeListDataBean implements Serializable{
    String title;
    String releasetime;
    String cover;
    String shortId;
    int duration;
    String actor;
    boolean enable;
    String categoriesName;
    int playCount;
    String actorImage;

    public boolean isWhite=true;

    public String getTime(){
        return DateUtils.getM_S(this.getDuration());
    }
    public String getDate(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String sd = sdf.format(new Date(Long.parseLong(releasetime)));
        return sd;
    }

    public String getActorImageUrl(){
//        return BaseCommon.RES_URL+this.getActorImage();
        return ImageUtils.getRes(this.getActorImage());
    }
    public String getConverUrl(){
//        return BaseCommon.RES_URL+getCover();
        return ImageUtils.getRes(this.getCover());
    }

    @Override
    public String toString() {
        return "TypeListDataBean{" +
                "title='" + title + '\'' +
                ", releasetime='" + releasetime + '\'' +
                ", cover='" + cover + '\'' +
                ", shortId='" + shortId + '\'' +
                ", duration=" + duration +
                ", actor='" + actor + '\'' +
                ", enable=" + enable +
                ", categoriesName='" + categoriesName + '\'' +
                ", playCount=" + playCount +
                '}';
    }

    /**
     * 得到播放次数
     * @return
     */
    public String getPlayCountData(){
        if(playCount>=10000){
            if(playCount%10000>0) {
                double v= (playCount *1.0/ 10000);
                String value=String.format("%.2f", v);
                return value+"万次播放";
            }else{
                return (playCount / 10000) + "万次播放";
        }
        }
        return playCount+"次播放";
    }

    public String getActorImage() {
        return actorImage;
    }

    public void setActorImage(String actorImage) {
        this.actorImage = actorImage;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleasetime() {
        return releasetime;
    }

    public void setReleasetime(String releasetime) {
        this.releasetime = releasetime;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getShortId() {
        return shortId;
    }

    public void setShortId(String shortId) {
        this.shortId = shortId;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getCategoriesName() {
        return categoriesName;
    }

    public void setCategoriesName(String categoriesName) {
        this.categoriesName = categoriesName;
    }

}
