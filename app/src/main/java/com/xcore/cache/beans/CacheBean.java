package com.xcore.cache.beans;

import com.common.BaseCommon;
import com.xcore.utils.ImageUtils;

import java.io.Serializable;

public class CacheBean implements Serializable{
    public String id;
    public String shortId;
    public String title;
    public String cover;
    public String time;
    public String tYear;
    public String tDesc;
    public String actor;
    public String pStar;
    public String playCount;//播放次数
    public String updateTime;//收藏时间
    public String tags;//标签  多个用|分开
    public String actors;//女星 多个用|分开
    public String tDelete;//是否删除
    public String tType;//类型

    public String playPosition="0";//播放进度 播放记录用

    public boolean selected=false;//是否选中
    public boolean showed=false;//是否显示

    @Override
    public String toString() {
        return "CacheBean{" +
                "id='" + id + '\'' +
                ", shortId='" + shortId + '\'' +
                ", title='" + title + '\'' +
                ", cover='" + cover + '\'' +
                ", time='" + time + '\'' +
                ", tYear='" + tYear + '\'' +
                ", tDesc='" + tDesc + '\'' +
                ", actor='" + actor + '\'' +
                ", pStar='" + pStar + '\'' +
                ", playCount='" + playCount + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", tags='" + tags + '\'' +
                ", actors='" + actors + '\'' +
                ", tDelete='" + tDelete + '\'' +
                ", tType='" + tType + '\'' +
                ", playPosition='" + playPosition + '\'' +
                ", selected=" + selected +
                ", showed=" + showed +
                '}';
    }

    //得到封面路径
    public String getConverUrl(){
        //return BaseCommon.RES_URL+this.cover;
        return ImageUtils.getRes(this.getCover());
    }
    public String getActorUrl(){
//        return BaseCommon.RES_URL+this.actor;
        return ImageUtils.getRes(this.actor);
    }


    public String getPlayPosition() {
        return playPosition;
    }

    public void setPlayPosition(String playPosition) {
        this.playPosition = playPosition;
    }

    public String getPlayCountData(){
        int p=Integer.valueOf(playCount);
        if(p>=10000){
            return (p/10000)+"万";
        }
        return p+"次播放";
    }
    public String getPlayCount() {
        return playCount;
    }

    public void setPlayCount(String playCount) {
        this.playCount = playCount;
    }

    public String gettDelete() {
        return tDelete;
    }

    public void settDelete(String tDelete) {
        this.tDelete = tDelete;
    }

    public String gettType() {
        return tType;
    }

    public void settType(String tType) {
        this.tType = tType;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShortId() {
        return shortId;
    }

    public void setShortId(String shortId) {
        this.shortId = shortId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String gettYear() {
        return tYear;
    }

    public void settYear(String tYear) {
        this.tYear = tYear;
    }

    public String gettDesc() {
        return tDesc;
    }

    public void settDesc(String tDesc) {
        this.tDesc = tDesc;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getpStar() {
        return pStar;
    }

    public void setpStar(String pStar) {
        this.pStar = pStar;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
