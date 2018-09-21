package com.xcore.data.p2pbean;

import com.xcore.data.CmModelBean;
import java.io.Serializable;

public class StartStreamResultBean extends CmModelBean {

    private StreamInfo data;

    public StreamInfo getStream() {
        return data;
    }
    public void setStream(StreamInfo stream) {
        this.data = stream;
    }

    public static class StreamInfo implements Serializable{
        private String id;
        private int downloadSpeed;
        private int btStatus;
        private String selectedFilePath;
        private long downloadedBytes;
        private long totalBytes;
        private int percent;
        private int connCountDown;
        private int connCountTotal;
        private int pieceCount;
        private int pieceSize;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getDownloadSpeed() {
            return downloadSpeed;
        }

        public void setDownloadSpeed(int downloadSpeed) {
            this.downloadSpeed = downloadSpeed;
        }

        public int getBtStatus() {
            return btStatus;
        }

        public void setBtStatus(int btStatus) {
            this.btStatus = btStatus;
        }

        public String getSelectedFilePath() {
            return selectedFilePath;
        }

        public void setSelectedFilePath(String selectedFilePath) {
            this.selectedFilePath = selectedFilePath;
        }

        public long getDownloadedBytes() {
            return downloadedBytes;
        }

        public void setDownloadedBytes(long downloadedBytes) {
            this.downloadedBytes = downloadedBytes;
        }

        public long getTotalBytes() {
            return totalBytes;
        }

        public void setTotalBytes(long totalBytes) {
            this.totalBytes = totalBytes;
        }

        public int getPercent() {
            return percent;
        }

        public void setPercent(int percent) {
            this.percent = percent;
        }

        public int getConnCountDown() {
            return connCountDown;
        }

        public void setConnCountDown(int connCountDown) {
            this.connCountDown = connCountDown;
        }

        public int getConnCountTotal() {
            return connCountTotal;
        }

        public void setConnCountTotal(int connCountTotal) {
            this.connCountTotal = connCountTotal;
        }

        public int getPieceCount() {
            return pieceCount;
        }

        public void setPieceCount(int pieceCount) {
            this.pieceCount = pieceCount;
        }

        public int getPieceSize() {
            return pieceSize;
        }

        public void setPieceSize(int pieceSize) {
            this.pieceSize = pieceSize;
        }
    }

}
