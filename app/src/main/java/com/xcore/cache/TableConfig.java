package com.xcore.cache;

public class TableConfig {
    public static String UPDATE_COLUM="shortId";

    //收藏记录
    //缓存记录
    //观看记录
    public static class Cache{
        public static final String TABLE_CACHE= "tb_cache";

        public static final String CACHE_ID="id";
        public static final String CACHE_SHORT_ID="shortId";
        public static final String CACHE_TITLE="title";
        public static final String CACHE_COVER="cover";
        public static final String CACHE_TIME="time";
        public static final String CACHE_YEAR="tYear";
        public static final String CACHE_DESC="tDesc";
        public static final String CACHE_ACTOR="actor";
        public static final String CACHE_P_STAR="pStar";
        public static final String CACHE_UPDATE_TIME="updateTime";
        public static final String CACHE_TAGS="tags";
        public static final String CACHE_ACTORS="actors";
        public static final String CACHE_DELETE="tDelete";
        public static final String CACHE_TYPE="tType";
        public static final String CACHE_PLAY_COUNT="playCount";
        public static final String CACHE_PLAY_POSITION="playPosition";

    }
    //历史搜索
    public static class Dictionary{
        public static final String TABLE_DIC= "tb_dic";
        public static final String DIC_ID="id";
        public static final String DIC_NAME="dicName";
        public static final String DIC_UPDATE_TIME="dicUpdateTime";
        public static final String DIC_DELETE="dicDelete";

    }
    //下载
    public static class DOWN{
        public static final String TABLE_DOWN="tb_down";
        public static final String DOWN_ID="id";
        public static final String DOWN_STREAM_ID="streamId";
        public static final String DOWN_URL="url";
        public static final String DOWN_NAME="name";
        public static final String DOWN_CONVER="conver";
        public static final String DOWN_UPDATE_TIME="updateTime";
        public static final String DOWN_DELETE="tDelete";
        public static final String DOWN_TOTAL_SIZE="totalSize";
        public static final String DOWN_PERCENT="percent";
        public static final String DOWN_DOWNSIZE="downSize";
        public static final String DOWN_SHORT_ID="shortId";
    }

}
