package com.xcore.data.bean;

import com.common.BaseCommon;
import com.xcore.utils.ImageUtils;

import java.io.Serializable;

public class NvStar implements Serializable{
    String actorImage;
    String maxCover;
    String actorName;
    String actorShortId;
    long birthday;
    int height;
    int bust;
    int waist;
    int hips;

    public String getConverUrl(){
//        return BaseCommon.RES_URL+this.cover;
        if(this.getMaxCover()==null||this.getMaxCover().equals("")){
            return "";
        }
        return getMaxCover();
    }
    public String getHeadUrl(){
//        return BaseCommon.RES_URL+this.actorImage;
        return this.getActorImage();
    }

    public String getMaxCover() {
        return ImageUtils.getRes(maxCover);
    }

    public void setMaxCover(String maxCover) {
        this.maxCover = maxCover;
    }

    public String getActorImage() {
        return ImageUtils.getRes(actorImage);
    }

    public void setActorImage(String actorImage) {
        this.actorImage = actorImage;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public String getActorShortId() {
        return actorShortId;
    }

    public void setActorShortId(String actorShortId) {
        this.actorShortId = actorShortId;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getBust() {
        return bust;
    }

    public void setBust(int bust) {
        this.bust = bust;
    }

    public int getWaist() {
        return waist;
    }

    public void setWaist(int waist) {
        this.waist = waist;
    }

    public int getHips() {
        return hips;
    }

    public void setHips(int hips) {
        this.hips = hips;
    }
}
