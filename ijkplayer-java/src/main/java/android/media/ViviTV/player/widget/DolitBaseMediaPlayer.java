package android.media.ViviTV.player.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public abstract class DolitBaseMediaPlayer {
    public static final String TAG = DolitBaseMediaPlayer.class.getName();
    public static final int MEDIA_INFO_BUFFERING_START = -700001;
    public static final int MEDIA_INFO_BUFFERING_END = -700002;
    public static final int MEDIA_INFO_ErrorBeforePlay = -700003;
    public static final int MEDIA_INFO_TIMEOUT = -700004;
    protected Context mContext = null;
    protected String mDataSource;
    protected String mUser_Mac = "";
    protected String mLiveSeek = "0";
    protected String mLiveEpg = "-";
    protected String mLiveNextEpg = "-";
    protected String mLiveNextUrl = "-";
    protected String mLiveCookie = "";

    protected String mLive_Range = "mediaTV/range";
    protected String mLive_Referer = "mediaTV/user/|support|android-tvbox";
    protected String mLive_key = "";

    public abstract void setDisplay(SurfaceHolder sh);

    public abstract void setDataSource(Context context, String path, Map<String, String> headers) throws IOException,
            IllegalArgumentException, SecurityException, IllegalStateException;

    public abstract String getDataSource();

    public abstract void prepareAsync() throws IllegalStateException;

    public abstract void start() throws IllegalStateException;

    public abstract void stop() throws IllegalStateException;

    public abstract void pause() throws IllegalStateException;

    public abstract void setScreenOnWhilePlaying(boolean screenOn);

    public abstract int getVideoWidth();

    public abstract int getVideoHeight();

    public abstract boolean isPlaying();

    public abstract void seekTo(long msec) throws IllegalStateException;

    public abstract long getCurrentPosition();

    public abstract long getDuration();

    public abstract void release();

    public abstract void reset();

    public abstract void setVolume(float leftVolume, float rightVolume);

    public abstract Object getRealMediaPlayer();

    public abstract void setOnPreparedListener(OnPreparedListener listener);

    public abstract void setOnCompletionListener(OnCompletionListener listener);

    public abstract void setOnBufferingUpdateListener(
            OnBufferingUpdateListener listener);

    public abstract void setOnSeekCompleteListener(
            OnSeekCompleteListener listener);

    public abstract void setOnVideoSizeChangedListener(
            OnVideoSizeChangedListener listener);

    public abstract void setOnErrorListener(OnErrorListener listener);

    public abstract void setOnInfoListener(OnInfoListener listener);

    /*--------------------
     * Listeners
     */
    public static interface OnPreparedListener {
        public void onPrepared(Object mp, int videoWidth, int videoHeight);
    }

    public static interface OnCompletionListener {
        public void onCompletion(Object mp);
    }

    public static interface OnBufferingUpdateListener {
        public void onBufferingUpdate(Object mp, int percent);
    }

    public static interface OnSeekCompleteListener {
        public void onSeekComplete(Object mp);
    }

    public static interface OnVideoSizeChangedListener {
        public void onVideoSizeChanged(Object mp, int width, int height, int videoWidth, int videoHeight,
                                       int sar_num, int sar_den);
    }

    public static interface OnErrorListener {
        public boolean onError(Object mp, int what, int extra, long currentPosition);
    }

    public static interface OnInfoListener {
        public boolean onInfo(Object mp, int what, int extra);
    }


    public abstract void setAudioStreamType(int streamtype);

    @Deprecated
    public abstract void setWakeMode(Context context, int mode);

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public abstract void setSurface(Surface surface);

    public abstract boolean init(Context context);

    public abstract void setUserAgent(String strUserAgent);

    public void setUserMac(String strUserMac) {
        this.mUser_Mac = strUserMac;
    }

    public void setLiveSeek(String strLiveSeek) {
        this.mLiveSeek = strLiveSeek;
    }

    public void setLiveEpg(String strLiveEpg) {
        this.mLiveEpg = strLiveEpg;
    }

    public void setLiveCookie(String strLiveCookie) {
        this.mLiveCookie = strLiveCookie;
    }

    public void setLiveRange(String strLiveRange) {
        this.mLive_Range = strLiveRange;
    }

    public void setLiveReferer(String strLive_Referer) {
        this.mLive_Referer = strLive_Referer;
    }

    public void setLiveKey(String strLivKey) {
        this.mLive_key = strLivKey;
    }

    public String GetEncodeUrl(String strSourceUrl) {
        String strUrl = "";
        try {
            strUrl = formatEncodeUrl(strSourceUrl);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "encodeUrl[" + strSourceUrl.toString() + "] error:"
                    + e.getMessage());
            return strSourceUrl;
        }
        return strUrl;
    }

    private String formatEncodeUrl(String url)
            throws UnsupportedEncodingException {
        String[] arr = url.split("/");
        StringBuffer tempPath = new StringBuffer("");
        for (int i = 0; i < arr.length; i++) {
            if (!arr[i].contains("%")) {
                arr[i] = URLEncoder.encode(arr[i], "UTF-8");
            }
            arr[i] = arr[i].replace(" ", "%20");
            arr[i] = arr[i].replace("+", "%20");
            arr[i] = arr[i].replace("%3A", ":");
            arr[i] = arr[i].replace("%3a", ":");
            tempPath.append(arr[i]);
            tempPath.append("/");
        }
        return tempPath.toString().substring(0, tempPath.length() - 1);
    }

    /*****************
     * 下面是硬解的函数，现在没有用，都屏蔽了
     ***************************/
    /*
	 * 去掉直播请求头
	 */
    protected Map<String, String> getPlayer_headers(String PlayLiveUrl, Map<String, String> PHeaders) {
        if (PHeaders != null) return PHeaders;
        Map<String, String> headers = new HashMap<String, String>();
        //避免User-Mac的值为空的情况下，127.0.0.1服务器端判断错误。
        if (!mUser_Mac.isEmpty())
            headers.put("User-Mac", mUser_Mac);
        headers.put("User-Key", this.mLive_key);
        headers.put("User-Ver", "MediaTVPlayer/MediaTV (" + Build.MODEL + ")");
        /**
         * 判断是否自动加域名来路
         */
        if (is_str(mLive_Referer, "|")) {
            String[] Array_Live_Referer = mLive_Referer.split("\\|");
            for (int i = 0; i < Array_Live_Referer.length; i++) {
                String a_Referer = Array_Live_Referer[i];
                if (is_str(a_Referer, "@")) {
                    String[] b_Referer = a_Referer.split("@");
                    if (is_str(PlayLiveUrl, b_Referer[0])) {
                        headers.put("Referer", b_Referer[1]);
                    }
                } else if (is_str(PlayLiveUrl, a_Referer)) {
                    headers.put("Referer", PlayLiveUrl);
                }
            }
        } else if (is_str(mLive_Referer, "@")) {
            String[] b_Referer = mLive_Referer.split("@");
            if (is_str(PlayLiveUrl, b_Referer[0])) {
                headers.put("Referer", b_Referer[1]);
            }
        } else if (is_str(PlayLiveUrl, mLive_Referer)) {
            headers.put("Referer", PlayLiveUrl);
        }
        if (mLiveCookie != null && mLiveCookie != "" && mLiveCookie != "-") {
            headers.put("Cookie", mLiveCookie);
        }
        if (mLive_Range != null && mLive_Range != "" && mLive_Range != "-" && is_str(mLive_Range, "|")) {
            String[] Array_Live_Range = mLive_Range.split("\\|");
            for (int i = 0; i < Array_Live_Range.length; i++) {
                String a_Range = Array_Live_Range[i];
                if (is_str(PlayLiveUrl, a_Range)) {
                    headers.put("Range", "bytes=");
                    break;
                }
            }
        } else if (is_str(PlayLiveUrl, mLive_Range)) {
            headers.put("Range", "bytes=");
        }
        return headers;
    }

    /**
     * 判断字符串是否存在
     *
     * @param str
     * @param txt
     * @return
     */
    private static boolean is_str(String strUrl, String txt) {
        if (strUrl.contains(txt)) {
            return true;
        }
        return false;
    }

//	/**
//	 * 特殊地址特殊处理
//	 * @param urlString
//	 * @return
//	 */
//    public String getLocalURL(String urlString){
//    	urlString = urlString.replace("%20", " ").replace("+", " ").replace("%3a", ":").replace("%3A", ":");
//		urlString = urlString.replace("%2f", "/").replace("%2F", "/").replace("%40", "@").replace("%26", "&");
//    	//----排除HTTP特殊----//
//		String targetUrl = urlString;
//		if (is_str(urlString, "91vst.com") || is_str(urlString, "myvst.net") || is_str(urlString, "52itv.cn") || is_str(urlString, "hdplay.cn") || is_str(urlString, "ku6.com/broadcast/sub")) {
//			targetUrl = getRedirectUrl(urlString);
//		}
//    	if (is_str(targetUrl, "totiptv.com/live/") && !is_str(targetUrl, ".m3u8?bitrate=800")) {
//    		targetUrl = targetUrl + "?bitrate=800";
//    	}
//    	else if (targetUrl.length() > 12 && is_str(targetUrl, "channel=pa://")){
//    		targetUrl = get_CNLiveURL(targetUrl);
//    		System.out.println("parse CNTVLiveurl: " + targetUrl);
//    	}
//    	else if (targetUrl.length() > 12 && is_str(targetUrl, ".m3u8") && is_str(targetUrl, "gdtv.cn")){
//    		targetUrl = get_PlayM3U8LiveURL(targetUrl);
//    		System.out.println("parse M3U8Liveurl: " + targetUrl);
//    	}
//    	else if (targetUrl.length() > 12 && is_str(targetUrl, "$$") && is_str(targetUrl, "synacast.com")){
//    		targetUrl = get_PPTVLiveURL(targetUrl);
//    		System.out.println("parse PPTVLiveurl: " + targetUrl);
//    	}
//    	if (!targetUrl.contains("http://")) {
//    		return targetUrl;
//    	}
//        return targetUrl;
//    }
//    
//    /* 正宗的抓取重定向URL */
//	
//	private static HttpURLConnection conn;
//	
//	/* 抓取直播重定向 */
//	@SuppressWarnings("deprecation")
//	static String getRedirectUrl(String urlStr) {
//		if (!urlStr.startsWith("http://")) {
//			return urlStr;
//		}
//		String playUrl = urlStr;
//		try {
//			URL url = new URL(urlStr);
//			conn = (HttpURLConnection) url.openConnection();
//			conn.setInstanceFollowRedirects(false);
//			conn.setRequestProperty("User-Agent", MainApp.User_Agent);
//			conn.setRequestProperty("User-Mac", MainApp.User_Mac);
//			conn.setRequestProperty("User-Key", MainApp.get_livekey());
//			conn.setRequestProperty("User-Ver", MainApp.User_Ver);
//			String loginKey = MainApp.getLoginKey();
//			if (loginKey != null && loginKey != "-" && loginKey.length() > 60) {
//				conn.setRequestProperty("Cookie", loginKey);
//			}
//			if (MainApp.Live_Referer != null && MainApp.Live_Referer != "-"
//					&& is_str(MainApp.Live_Referer, "|")) {
//				String[] Array_Live_Referer = MainApp.Live_Referer.split("\\|");
//				for (int i = 0; i < Array_Live_Referer.length; i++) {
//					String a_Referer = Array_Live_Referer[i];
//					if (is_str(a_Referer, "@")) {
//						String[] b_Referer = a_Referer.split("@");
//						if (is_str(urlStr, b_Referer[0])) {
//							conn.setRequestProperty("Referer", b_Referer[1]);
//						}
//					} else if (is_str(urlStr, a_Referer)) {
//						conn.setRequestProperty("Referer", urlStr);
//					}
//				}
//			} else if (MainApp.Live_Referer != null
//					&& is_str(MainApp.Live_Referer, "@")) {
//				String[] b_Referer = MainApp.Live_Referer.split("@");
//				if (is_str(urlStr, b_Referer[0])) {
//					conn.setRequestProperty("Referer", b_Referer[1]);
//				}
//			} else if (MainApp.Live_Referer != null
//					&& is_str(urlStr, MainApp.Live_Referer)) {
//				conn.setRequestProperty("Referer", urlStr);
//			}
//			System.out.println("Return status: " + conn.getResponseCode());
//			if (conn.getResponseCode() == 301 || conn.getResponseCode() == 302) {
//				playUrl = conn.getHeaderField("Location");
//				String Play_epg = conn.getHeaderField("Play_epg");
//				String Next_epg = conn.getHeaderField("Next_epg");
//				String Next_url = conn.getHeaderField("Next_url");
//				String Play_Seek = conn.getHeaderField("Play_Seek");
//				if (conn.getHeaderField("Set-Cookie") != null) {
//					MainApp.setLive_Cookie(conn.getHeaderField("Set-Cookie"));
//				}
//				if (conn.getHeaderField("Cookie") != null) {
//					MainApp.setLive_Cookie(MainApp.LiveCookie + ";" + conn.getHeaderField("Cookie"));
//				}
//				if (Play_Seek != null && Play_Seek.length() > 0
//						&& Integer.parseInt(Play_Seek) > 0) {
//					System.out.println("Positioning time: " + Play_Seek + "秒");
//					MainApp.setLiveSeek(Play_Seek);
//				}
//				if (Play_epg != null && Play_epg.length() > 3) {
//					Play_epg = URLDecoder.decode(Play_epg);
//					System.out.println("The current program EPG: " + Play_epg);
//					MainApp.setLiveEpg(Play_epg);
//				}
//				if (Next_epg != null && Next_epg.length() > 3) {
//					Next_epg = URLDecoder.decode(Next_epg);
//					System.out.println("The next programme EPG: " + Next_epg);
//					MainApp.setLiveNextEpg(Next_epg);
//				}
//				if (Next_url != null && Next_url.length() > 12) {
//					System.out.println("The next programme URL: " + Next_url);
//					MainApp.setLiveNextUrl(urlStr);
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			conn.disconnect();
//		}
//		if (playUrl == null) {
//			playUrl = urlStr;
//		}
//		return playUrl;
//	}
//
//	/* PPTV地址抓取 */
//	private String get_PPTVLiveURL(String Playurl) {
//		String LiveUrl = "";
//		try {
//			String[] SpSrc = Playurl.split("\\$\\$");
//			String xmlurl = MainApp.curl(SpSrc[0].trim() + "?type=m3u8.web.pad");
//			if (xmlurl != null && is_str(xmlurl, "server_host")) {
//				Document document = new SAXReader().read(new ByteArrayInputStream(xmlurl.getBytes("utf-8")));
//				Element root = document.getRootElement();
//				String server_key = root.element("key").getText().trim();
//				String server_host = root.element("server_host").getText().trim();
//				LiveUrl = "http://" + server_host + "/" + SpSrc[1].trim() + "?type=ppbox&k=" + server_key;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		if (LiveUrl.length() > 12) {
//			return LiveUrl;
//		}
//		return Playurl;
//	}
//	
//	/* CNTV地址抓取 */
//	private String get_CNLiveURL(String Playurl) {
//		String LiveUrl = "";
//		try {
//			LiveUrl = "http://" + vst_jq(MainApp.curl(Playurl), "://", "\"");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		if (LiveUrl.length() > 12) {
//			return LiveUrl;
//		}
//		return Playurl;
//	}
//	
//	
//	/* M3U8地址抓取 */
//	private String get_PlayM3U8LiveURL(String Playurl) {
//		String LiveUrl = "";
//		try {
//			LiveUrl = "http://" + vst_jq(MainApp.curl(Playurl), "://", "\r\n");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		if (LiveUrl.length() > 12) {
//			return LiveUrl;
//		}
//		return Playurl;
//	}
//	
//	
//	/* 截取字符串 */
//	private String vst_jq(String str, String start, String end) {
//		String retstr = "";
//		if (is_str(str, start)) {
//			String string = str.split(start)[1].trim();
//			if (end != null && is_str(string, end)) {
//				retstr = string.split(end)[0].trim();
//			}
//			else return string;
//		}
//		return retstr;
//	}

}
