package cn.dolit.p2ptrans;

import android.text.TextUtils;
import android.util.Log;

import com.common.BaseCommon;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.lzy.okgo.model.HttpParams;
import com.xcore.MainApplicationContext;
import com.xcore.data.p2pbean.StartStreamResultBean;
import com.xcore.data.p2pbean.StreamsResultBean;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class P2PTrans {
    private static final String TAG = "p2ptrans";
    public static final String LOCAL_HOST = "http://127.0.0.1";
    private static Module module;
    private static final int PLL_NONE = -1;
    private static final int PLL_ERROR = 0;
    private static final int PLL_WARN = 1;
    private static final int PLL_INFO = 2;
    private static final int PLL_DEBUG = 3;
    private static final int PLL_VERBOSE_DEBUG = 4;

    public static final String P2P_TYPE="turl";

    static {
        module = new Module();
    }

    /**
     * 启动
     * @param ip
     * @param port
     * @return
     */
    public static int run(String ip, int port) {
        String address;
        if (TextUtils.isEmpty(ip))
            address = String.valueOf(port);
        else
            address = ip + ":" + port;
        module.setLogLevel(PLL_DEBUG);
        return module.run(address);
    }
    //结束
    public static int shutdown(int port) {
        try {
            URL url = new URL(LOCAL_HOST + ":" + port + "/shutdown");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    public static StartStreamResult stopStream(int port, final String torrentUrl) {
        if (TextUtils.isEmpty(torrentUrl)) {
            return new StartStreamResult(-1);
        }
        try {
            String url = String.format(Locale.CHINA, "%s:%d/bt/api/stream/stop?uri=%s&pass=1234", LOCAL_HOST, port, torrentUrl);
            String resultStr=httpGet(url);

            StartStreamResult result =new Gson().fromJson(resultStr,StartStreamResult.class);// com.alibaba.fastjson.JSON.parseObject(resultStr, StartStreamResult.class);
            return result;
        } catch (Exception e) {
            return new StartStreamResult(-4);
        }
    }

    public static String stopAllStream(int port) {
        return httpGet(LOCAL_HOST + ":" + port + "/bt/api/stream/stopall");
    }

    public static String stopByUrl(int port,String url) {
        String url1=LOCAL_HOST + ":" + port + "/bt/api/stream/stop?uri="+url;
        url1=getUrlAddPass(url1);
        return httpGet(url1);
    }

    public static String getTorrentPlayUrl(int port, String url) {
        return LOCAL_HOST + ":" + port + "/bt/stream?type=turl&uri=" + url;
    }

    public static String getTorrentPlayPath(int port, String path) {
        return LOCAL_HOST + ":" + port + "/bt/stream?type=tfile&uri=" + path;
    }

    public static boolean setTorrentSavePath(int port, String path) {
        String result = httpGet(LOCAL_HOST + ":" + port + "/bt/api/set/torrentdir?" + path);
        return result.indexOf("success") >= 0;
    }

    public static boolean setFileSavePath(int port, String path) {
        String result = httpGet(LOCAL_HOST + ":" + port + "/bt/api/set/filedir?" + path);
        return result.indexOf("success") >= 0;
    }

    public static boolean enableStream(int port, String torrentSavePath, String fileSavePath, String key) {
        if (!setFileSavePath(port, fileSavePath))
            return false;
        if (!setTorrentSavePath(port, torrentSavePath))
            return false;
        String result = httpGet(LOCAL_HOST + ":" + port + "/bt/api/enablestream?k=" + key);
        return result.indexOf("success") >= 0;
    }

    public static boolean testStream(int port) {
        String result = httpGet(LOCAL_HOST + ":" + port + "/bt/api/conns");
        return result.indexOf("success") >= 0;
    }

    public static String getStreamInfoUrl(int port, String streamID) {
        return LOCAL_HOST + ":" + port + "/bt/api/streaminfo?id=" + streamID;
    }
    //帮助
    public static String getHelpUrl(int port) {
        return LOCAL_HOST + ":" + port + "/bt/help";
    }

    /**
     * 给路径地址添加 pass 和 priv
     * @param url 要检查添加的路径地址
     * @return 返回添加后的路径地址
     */
    public static String getUrlAddPass(String url){
        String xUrl=url;
        String pass=MainApplicationContext.DOLITBT_PASS;
        String privateStr=MainApplicationContext.DOLITBT_PRIVETE;
        if(xUrl.indexOf("http://demo.flvurl.cn/dianyunMovieBT/4667496-1hd.mp4.torrent")>-1){
            //privateStr="";
        }else{
            //私有密
            pass="urlsphvhcoupnlcteviqhnoanvbkdqyp";
            privateStr="1";
        }
        if(!TextUtils.isEmpty(pass)){
            xUrl+="&pass="+pass;
        }
        if(!TextUtils.isEmpty(privateStr)){
            xUrl+="&priv="+privateStr;
        }
        List<String> httpAcceles=BaseCommon.httpAccelerateLists;
        if(httpAcceles.size()<=0){
            return xUrl;
        }
        int ix=0;
        try {
            int ixx = url.lastIndexOf("/");
            int vxx = url.lastIndexOf(".Torrent");
            if(vxx<0) {
                vxx = url.lastIndexOf(".torrent");
            }
            String xxxUrl = url.substring(ixx + 1, vxx);
            for(String hUrl: httpAcceles){
                if(ix==0) {
                    xUrl += "&http-urls=" + hUrl+""+xxxUrl+".xv";
                }else{
                    xUrl+="|"+hUrl+""+xxxUrl+".xv";
                }
                ix++;
            }
        }catch (Exception e){
            //e.printStackTrace();
        }
        return xUrl;
    }

    /**
     * 得到信息
     * @param urlStr
     * @return
     */
    public static String httpGet(String urlStr) {
        String result = "";
        StringBuffer sb = new StringBuffer();
        InputStream is = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            is = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            result = sb.toString();
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            result = "";
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.i(TAG, "Error closing InputStream");
                }
            }
        }
        return result;
    }

    /**
     * 得到流信息
     * @param torrentUrl  种子地址
     * @param torrentType 类型
     * @param port        端口
     * @param torrentPwd  密码
     * @return
     */
    public static StartStreamResult startStream(final String torrentUrl, final String torrentType, int port, final String torrentPwd) {
        if (TextUtils.isEmpty(torrentUrl)) {
            return new StartStreamResult(-2);
        }

        try {
            String url = String.format(Locale.CHINA, "%s:%d/bt/api/stream/start?type=%s&uri=%s", LOCAL_HOST, port, torrentType, torrentUrl);
            if(torrentPwd != null && ! torrentPwd.isEmpty() )
                url += "&pass=" + torrentPwd;
            String resultStr = httpGet(url);

            StartStreamResult result =new Gson().fromJson(resultStr,StartStreamResult.class);// com.alibaba.fastjson.JSON.parseObject(resultStr, StartStreamResult.class);
            if (result == null) {
                return new StartStreamResult(-4);
            }
            return result;
        } catch (Exception e) {
            return new StartStreamResult(-1);
        }
    }

    /**
     * 得到所有任务信息
     * @param port 端口
     * @return     返回有任务信息
     */
    public static StreamsResult getStreams(int port) {
        try {
            String url = String.format(Locale.CHINA, "%s:%d/bt/api/streams", LOCAL_HOST, port);
            String content = httpGet(url);
            JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();

            StreamsResult result = new StreamsResult();
            result.setCode(jsonObject.get("code").getAsInt());
            result.setMessage(jsonObject.get("message").getAsString());
            JsonObject jsonObj= jsonObject.getAsJsonObject("data");
            JsonArray jsonArr= jsonObj.getAsJsonArray("streams:");

            List<StreamInfo> arr=new ArrayList<>();
            Gson gson=new Gson();
            for(JsonElement obj : jsonArr ){
                StreamInfo cse = gson.fromJson( obj , StreamInfo.class);
                arr.add(cse);
            }
            result.setStreams(arr);
            if (result == null) {
                return new StreamsResult(-1);
            }

            return result;
        } catch (Exception e) {
            return new StreamsResult(-2);
        }
    }

    /**
     * 得到所有任务信息
     * @param port 端口
     * @return     返回有任务信息
     */
    public static StreamsResultBean getStreams1(int port) {
        StreamsResultBean result = new StreamsResultBean();
        try {
            String url = String.format(Locale.CHINA, "%s:%d/bt/api/streams", LOCAL_HOST, port);
            String content = httpGet(url);
            JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();

            result.setCode(jsonObject.get("code").getAsInt());
            result.setMessage(jsonObject.get("message").getAsString());
            JsonObject jsonObj= jsonObject.getAsJsonObject("data");
            JsonArray jsonArr= jsonObj.getAsJsonArray("streams:");

            List<StartStreamResultBean.StreamInfo> arr=new ArrayList<StartStreamResultBean.StreamInfo>();
            Gson gson=new Gson();
            for(JsonElement obj : jsonArr ){
                StartStreamResultBean.StreamInfo cse = gson.fromJson( obj , StartStreamResultBean.StreamInfo.class);
                arr.add(cse);
            }
            result.setStreams(arr);
            if (result == null) {
                result.setCode(-1);
            }
            return result;
        } catch (Exception e) {
            result.setCode(-2);
        }
        return result;
    }


    ////////////////////////////////////////////////////////////////////
    /////////////////////////////////model///////////////////////////////
    ////////////////////////////////////////////////////////////////////
    public static final class StartStreamResult extends CmModel {
        @SerializedName("data")
        private StreamInfo stream;

        public StartStreamResult() {
        }

        public StartStreamResult(int code) {
            super(code);
        }

        public StreamInfo getStream() {
            return stream;
        }

        public void setStream(StreamInfo stream) {
            this.stream = stream;
        }
    }

    public static final class StreamsResult extends CmModel{
        private List<StreamInfo> streams;

        public StreamsResult() {
        }

        public StreamsResult(int code) {
            super(code);
        }

        public List<StreamInfo> getStreams() {
            return streams;
        }

        public void setStreams(List<StreamInfo> streams) {
            this.streams = streams;
        }

        public HashMap<String, StreamInfo> getStreamsMap() {
            HashMap<String, StreamInfo> result = new HashMap<>();
            if (streams == null) {
                return result;
            }

            for (StreamInfo info : streams) {
                if (TextUtils.isEmpty(info.getId()) || info == null) {
                    continue;
                }

                result.put(info.getId(), info);
            } // end of for

            return result;
        }
    }

    public static final class StreamInfo{
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

    public static class CmModel{
        private int code;
        private String message;

        public CmModel() {
        }

        public CmModel(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public boolean succeed() {
            return code == 0;
        }
    }
}