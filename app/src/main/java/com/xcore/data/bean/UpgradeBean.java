package com.xcore.data.bean;

import com.xcore.R;
import com.xcore.data.BaseBean;

import java.io.Serializable;
import java.util.List;

public class UpgradeBean extends BaseBean{
    List<Item> data;

    public List<Item> getData() {
        return data;
    }

    public void setData(List<Item> data) {
        this.data = data;
    }

    public static class Item implements Serializable{
        int vip;
        int experienceRangeEnd;
        int experienceRangeStart;
        String description;

        public String getVipStr(){
            String vipStr="普通会员";
            switch (this.getVip()){
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
        public String getVipInfo(){
            String vipStr="<font color='#8f792e' size='70px'>"+getVipStr()+"</font> <font color='#9C9C9C'>("
                    +this.getExperienceRangeStart()+"-"+this.getExperienceRangeEnd()+")</font>";
            return vipStr;
        }

        //得到资源信息
        public int getRes(){
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


        public int getVip() {
            return vip;
        }

        public void setVip(int vip) {
            this.vip = vip;
        }

        public int getExperienceRangeEnd() {
            return experienceRangeEnd;
        }

        public void setExperienceRangeEnd(int experienceRangeEnd) {
            this.experienceRangeEnd = experienceRangeEnd;
        }

        public int getExperienceRangeStart() {
            return experienceRangeStart;
        }

        public void setExperienceRangeStart(int experienceRangeStart) {
            this.experienceRangeStart = experienceRangeStart;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
