package com.xcore.data.bean;

import com.common.BaseCommon;
import com.xcore.utils.ImageUtils;

import java.io.Serializable;
import java.util.List;

public class BannerBean implements Serializable{
    public List<BannerData> data;

    @Override
    public String toString() {
        return "BannerBean{" +
                "data=" + data +
                '}';
    }

    public List<BannerData> getData() {
        return data;
    }

    public void setData(List<BannerData> data) {
        this.data = data;
    }

    public static class BannerData implements Serializable {
        String path;
        int jump;
        String content;
        String shortId;

        @Override
        public String toString() {
            return "BannerData{" +
                    "path='" + path + '\'' +
                    ", jump=" + jump +
                    ", content='" + content + '\'' +
                    ", shortId='" + shortId + '\'' +
                    '}';
        }

        public String getShortId() {
            if(shortId==null){
                int v=content.indexOf(":");
                String sId=content.substring(v+1);
                if(sId!=null&&sId.length()>0) {
                    return sId;
                }
                return "";
            }
            return shortId;
        }

        public void setShortId(String shortId) {
            this.shortId = shortId;
        }

        public String getPathUrl(){
            return ImageUtils.getRes(this.getPath());
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public int getJump() {
            return jump;
        }

        public void setJump(int jump) {
            this.jump = jump;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
