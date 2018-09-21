package com.xcore.data.bean;

import com.common.BaseCommon;
import com.xcore.data.BaseBean;

import java.io.Serializable;
import java.util.List;

public class CdnBean extends BaseBean {

    public CdnItem data;

    @Override
    public String toString() {
        return "CdnBean{" +
                "data=" + data +
                '}';
    }

    public CdnItem getData() {
        return data;
    }

    public void setData(CdnItem data) {
        this.data = data;
    }

    public static class CdnItem implements Serializable {

        List<CdnDataItem> apiList;
        List<CdnDataItem> imageList;
        List<CdnDataItem> recordList;
        List<CdnDataItem> torrentList;
        List<CdnDataItem> httpAccelerateList;
        List<CdnDataItem> apkDownList;
        List<CdnDataItem> testUrlList;

        @Override
        public String toString() {
            return "CdnItem{" +
                    "apiList=" + apiList +
                    ", imageList=" + imageList +
                    ", recordList=" + recordList +
                    ", torrentList=" + torrentList +
                    ", httpAccelerateList=" + httpAccelerateList +
                    ", apkDownList=" + apkDownList +
                    '}';
        }

        public List<CdnDataItem> getTestUrlList() {
            return testUrlList;
        }

        public void setTestUrlList(List<CdnDataItem> testUrlList) {
            this.testUrlList = testUrlList;
        }

        public List<CdnDataItem> getApkDownList() {
            return apkDownList;
        }

        public void setApkDownList(List<CdnDataItem> apkDownList) {
            this.apkDownList = apkDownList;
        }

        public List<CdnDataItem> getApiList() {
            return apiList;
        }

        public void setApiList(List<CdnDataItem> apiList) {
            this.apiList = apiList;
        }

        public List<CdnDataItem> getImageList() {
            return imageList;
        }

        public void setImageList(List<CdnDataItem> imageList) {
            this.imageList = imageList;
        }

        public List<CdnDataItem> getRecordList() {
            return recordList;
        }

        public void setRecordList(List<CdnDataItem> recordList) {
            this.recordList = recordList;
        }

        public List<CdnDataItem> getTorrentList() {
            return torrentList;
        }

        public void setTorrentList(List<CdnDataItem> torrentList) {
            this.torrentList = torrentList;
        }

        public List<CdnDataItem> getHttpAccelerateList() {
            return httpAccelerateList;
        }

        public void setHttpAccelerateList(List<CdnDataItem> httpAccelerateList) {
            this.httpAccelerateList = httpAccelerateList;
        }
    }
    public static class CdnDataItem implements Serializable{
        String url;
        int cdnConfigType;
        Integer port;

        @Override
        public String toString() {
            return "CdnDataItem{" +
                    "url='" + url + '\'' +
                    ", cdnConfigType=" + cdnConfigType +
                    '}';
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getCdnConfigType() {
            return cdnConfigType;
        }

        public void setCdnConfigType(int cdnConfigType) {
            this.cdnConfigType = cdnConfigType;
        }
    }
}
