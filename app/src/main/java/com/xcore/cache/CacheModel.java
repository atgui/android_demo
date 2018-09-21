package com.xcore.cache;

import com.xcore.data.p2pbean.StartStreamResultBean;
import com.xcore.utils.ImageUtils;

import java.io.Serializable;

/**
 * 已缓存信息类  下载
 */
public class CacheModel implements Serializable {
    public String id;
    public String shortId;
    public String streamId;
    public String url;
    public String name;
    public String conver;
    public String updateTime;
    public String tDelete;

    public String totalSize="0";//总大小
    public String downSize="0";
    public String percent="0";

    private boolean isPlay;
    private boolean checked=false;
    private boolean isShowCheck=false;
    private int status;//下载状态 0 未开始  1 下载中 2 停止  3 下载失败
    private int complete=-1;//是否下载完成  -1 是未完成 且没有查看过本地文件夹  0 未完成,查看了本地文件夹 1 完成

    private long totalCount1=0;//总速度
    private int timeCount=0;//时间次数

    private StartStreamResultBean.StreamInfo streamInfo;//下载信息

    public String getConverUrl(){
        //return BaseCommon.RES_URL+this.getConver();
        return ImageUtils.getRes(this.getConver());
    }

    public long getTotalCount1() {
        return totalCount1;
    }

    public void setTotalCount1(long totalCount1) {
        this.totalCount1 = totalCount1;
    }

    public int getTimeCount() {
        return timeCount;
    }

    public void setTimeCount(int timeCount) {
        this.timeCount = timeCount;
    }

    @Override
    public String toString() {
        return "CacheModel{" +
                "id='" + id + '\'' +
                ", streamId='" + streamId + '\'' +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", conver='" + conver + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", tDelete='" + tDelete + '\'' +
                ", totalSize='" + totalSize + '\'' +
                ", isPlay=" + isPlay +
                ", checked=" + checked +
                ", isShowCheck=" + isShowCheck +
                ", status=" + status +
                ", complete=" + complete +
                ", streamInfo=" + streamInfo +
                '}';
    }

    public String getShortId() {
        return shortId;
    }

    public void setShortId(String shortId) {
        this.shortId = shortId;
    }

    public String getDownSize() {
        return downSize;
    }

    public void setDownSize(String downSize) {
        this.downSize = downSize;
    }

    public String getPercent() {
        return percent;
    }
    public void setPercent(String percent) {
        this.percent = percent;
    }

    public int getComplete() {
        return complete;
    }

    public void setComplete(int complete) {
        this.complete = complete;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(String totalSize) {
        this.totalSize = totalSize;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String gettDelete() {
        return tDelete;
    }

    public void settDelete(String tDelete) {
        this.tDelete = tDelete;
    }

    public String getConver() {
        return conver;
    }

    public void setConver(String conver) {
        this.conver = conver;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public StartStreamResultBean.StreamInfo getStreamInfo() {
        return streamInfo;
    }

    public void setStreamInfo(StartStreamResultBean.StreamInfo streamInfo) {
        this.streamInfo = streamInfo;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isShowCheck() {
        return isShowCheck;
    }

    public void setShowCheck(boolean showCheck) {
        isShowCheck = showCheck;
    }

}
