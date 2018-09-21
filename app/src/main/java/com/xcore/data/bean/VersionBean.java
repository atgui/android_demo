package com.xcore.data.bean;

import com.xcore.data.BaseBean;

import java.io.Serializable;

public class VersionBean extends BaseBean{

    private VersionData data;

    public VersionData getData() {
        return data;
    }

    public void setData(VersionData data) {
        this.data = data;
    }

    public static class VersionData implements Serializable{
        private String name;
        private int insideVersion;
        private String remark;
        private String md5;
        private String downUrl;
        private long heartbeatTime;
        @Override
        public String toString() {
            return "VersionData{" +
                    "name='" + name + '\'' +
                    ", insideVersion=" + insideVersion +
                    ", remark='" + remark + '\'' +
                    ", md5='" + md5 + '\'' +
                    ", downUrl='" + downUrl + '\'' +
                    ",heartbeatTime='" + heartbeatTime + '\'' +
                    '}';
        }

        public long getHeartbeatTime() {
            return heartbeatTime;
        }

        public void setHeartbeatTime(long heartbeatTime) {
            this.heartbeatTime = heartbeatTime;
        }

        public String getDownUrl() {
            return downUrl;
        }

        public void setDownUrl(String downUrl) {
            this.downUrl = downUrl;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getInsideVersion() {
            return insideVersion;
        }

        public void setInsideVersion(int insideVersion) {
            this.insideVersion = insideVersion;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
}
