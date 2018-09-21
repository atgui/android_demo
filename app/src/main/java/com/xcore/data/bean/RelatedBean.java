package com.xcore.data.bean;

import com.common.BaseCommon;
import com.xcore.utils.ImageUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//相关影片
public class RelatedBean implements Serializable{

    private String cover;
    private String name;
    private List<CategoriesBean> tagslist;
    private int hot;
    private String shortId;
    private long releaseTime;

    public String getShortId() {
        return shortId;
    }

    public void setShortId(String shortId) {
        this.shortId = shortId;
    }

    /**
     * 根据时间戳得到日期
     * @return
     */
    public String getDate(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String sd = sdf.format(new Date(releaseTime));      // 时间戳转换成时间
        return sd;
    }
    /**
     * 得到播放次数
     * @param
     * @return
     */
    public String getPlayCount(){
        if(this.getHot()>=10000){
            if(this.getHot()%10000>0) {
                double v= (this.getHot() *1.0/ 10000);
                String value=String.format("%.2f", v);
                return value+"万次播放";
            }else{
                return (this.getHot() / 10000) + "万次播放";
            }
        }
        return this.getHot()+"次播放";
    }
    @Override
    public String toString() {
        return "RelatedBean{" +
                "cover='" + cover + '\'' +
                ", name='" + name + '\'' +
                ", tagslist=" + tagslist +
                ", hot=" + hot +
                ", shortId='" + shortId + '\'' +
                ", releaseTime=" + releaseTime +
                '}';
    }

    public long getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(long releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getConverUrl(){
//        return BaseCommon.RES_URL+this.getCover();
        return ImageUtils.getRes(this.getCover());
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CategoriesBean> getTagslist() {
        return tagslist;
    }

    public void setTagslist(List<CategoriesBean> tagslist) {
        this.tagslist = tagslist;
    }

    public int getHot() {
        return hot;
    }

    public void setHot(int hot) {
        this.hot = hot;
    }
}
