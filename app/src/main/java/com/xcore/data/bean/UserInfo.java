package com.xcore.data.bean;

import android.text.TextUtils;
import android.view.inputmethod.BaseInputConnection;

import com.common.BaseCommon;
import com.xcore.R;
import com.xcore.utils.ImageUtils;

import java.io.Serializable;

public class UserInfo implements Serializable {
    public String name;
    public Integer maxCachingCount;
    public Integer maxPlayCount;
    public Integer playCount;
    public Integer cacheCount;
    public String shareCode;
    public String image;
    public Integer sex;
    public String nickName;
    public Integer vip;//当前等级
    public Integer nextVip;//下一级
    public Integer experienceRange;//当前经验
    public Integer nextExperienceRange;//下一级经验
    public String activityText;//公告信息 | 隔开
    public boolean superVIP;//是否无限观看
    public Integer experienceRangeToday;//当日经验

    public String shareUrl;
    public String shareText;
    public String qrcodeUrl;

    public String appUserVIPLevelUpgradeRecord;

    //是否是游客  true:是游客
    public boolean tourist;

    public Long unread;//反馈有返回了

    public String getQrcodeUrl() {
        return qrcodeUrl;
    }

    public void setQrcodeUrl(String qrcodeUrl) {
        this.qrcodeUrl = qrcodeUrl;
    }

    public Long getUnread() {
        return unread;
    }

    public void setUnread(Long unread) {
        this.unread = unread;
    }

    public String getAppUserVIPLevelUpgradeRecord() {
        return appUserVIPLevelUpgradeRecord;
    }

    public void setAppUserVIPLevelUpgradeRecord(String appUserVIPLevelUpgradeRecord) {
        this.appUserVIPLevelUpgradeRecord = appUserVIPLevelUpgradeRecord;
    }

    public String getHeadUrl(){
//        return BaseCommon.API_URL+""+this.image;
        return ImageUtils.getRes(this.getImage());
    }

    public boolean isTourist() {
        return tourist;
    }

    public void setTourist(boolean tourist) {
        this.tourist = tourist;
    }

    //得到播放次数
    public String getPlayStr(){
        if(this.superVIP==true){
            return "无限观看";
        }
        String str=this.playCount+"/";
        if(this.playCount>99){
            str="99+/";
        }
        if(this.maxPlayCount>99){
            str+="99+";
        }else{
            str+=this.maxPlayCount;
        }
        return str;
    }
    //得到播放次数
    public String getCacheStr(){
        String str=this.cacheCount+"/";
        if(this.cacheCount>99){
            str="99+/";
        }
        if(this.maxCachingCount>99){
            str+="99+";
        }else{
            str+=this.maxCachingCount;
        }
        return str;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", maxCachingCount=" + maxCachingCount +
                ", maxPlayCount=" + maxPlayCount +
                ", playCount=" + playCount +
                ", cacheCount=" + cacheCount +
                ", shareCode='" + shareCode + '\'' +
                ", image='" + image + '\'' +
                ", sex=" + sex +
                ", nickName='" + nickName + '\'' +
                ", vip=" + vip +
                ", nextVip=" + nextVip +
                ", experienceRange=" + experienceRange +
                ", nextExperienceRange=" + nextExperienceRange +
                ", activityText='" + activityText + '\'' +
                ", superVIP=" + superVIP +
                ", experienceRangeToday=" + experienceRangeToday +
                ", shareUrl='" + shareUrl + '\'' +
                ", shareText='" + shareText + '\'' +
                '}';
    }

    public boolean isSuperVIP() {
        return superVIP;
    }

    public void setSuperVIP(boolean superVIP) {
        this.superVIP = superVIP;
    }

    //得到会员等级
    public String getVipStr(int vip){
        String vipStr="普通会员";
        switch (vip){
            case 1:
                vipStr="普通会员";
                break;
            case 2:
                vipStr="白银会员";
                break;
            case 3:
                vipStr="黄金会员";
                break;
            case 4:
                vipStr="钻石会员";
                break;
            case 5:
                vipStr="至尊会员";
                break;
        }
        return vipStr;
    }

    //得到资源信息
    public int getRes(int vip){
        int res= R.drawable.dengji_one;
        switch (vip){
            case 1:
                res=R.drawable.dengji_one;
                break;
            case 2:
                res=R.drawable.dengji_two;
                break;
            case 3:
                res=R.drawable.dengji_three;
                break;
            case 4:
                res=R.drawable.dengji_four;
                break;
            case 5:
                res=R.drawable.dengji_five;
                break;
        }
        return res;
    }


    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getShareText() {
        return shareText;
    }

    public void setShareText(String shareText) {
        this.shareText = shareText;
    }

    public String getName() {
        if(TextUtils.isEmpty(this.nickName)) {
            return name;
        }else{
            return this.nickName;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMaxCachingCount() {
        return maxCachingCount;
    }

    public void setMaxCachingCount(Integer maxCachingCount) {
        this.maxCachingCount = maxCachingCount;
    }

    public Integer getMaxPlayCount() {
        return maxPlayCount;
    }

    public void setMaxPlayCount(Integer maxPlayCount) {
        this.maxPlayCount = maxPlayCount;
    }

    public Integer getPlayCount() {
        return playCount;
    }

    public void setPlayCount(Integer playCount) {
        this.playCount = playCount;
    }

    public Integer getCacheCount() {
        return cacheCount;
    }

    public void setCacheCount(Integer cacheCount) {
        this.cacheCount = cacheCount;
    }

    public String getShareCode() {
        if(shareCode==null){
            return "";
        }
        return shareCode;
    }

    public void setShareCode(String shareCode) {
        this.shareCode = shareCode;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getVip() {
        if(vip==null){
            return 0;
        }
        return vip;
    }

    public void setVip(Integer vip) {
        this.vip = vip;
    }

    public Integer getNextVip() {
        if(nextVip==null){
            return 0;
        }
        return nextVip;
    }

    public void setNextVip(Integer nextVip) {
        this.nextVip = nextVip;
    }

    public Integer getExperienceRange() {
        if(experienceRange==null){
            return 0;
        }
        return experienceRange;
    }

    public void setExperienceRange(Integer experienceRange) {
        this.experienceRange = experienceRange;
    }

    public Integer getNextExperienceRange() {
        if(nextExperienceRange==null){
            return 0;
        }
        return nextExperienceRange;
    }

    public void setNextExperienceRange(Integer nextExperienceRange) {
        this.nextExperienceRange = nextExperienceRange;
    }

    public String getActivityText() {
        return activityText;
    }

    public void setActivityText(String activityText) {
        this.activityText = activityText;
    }

    public Integer getExperienceRangeToday() {
        return experienceRangeToday;
    }

    public void setExperienceRangeToday(Integer experienceRangeToday) {
        this.experienceRangeToday = experienceRangeToday;
    }
}
