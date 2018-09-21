package android.media.ViviTV.player.widget;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.Map;


/**
 * 播放器的视频视图，这个是处理显示用的核心类
 * 支持联播、设置http的头等信息
 * TODO：客户反馈有视频会播放一段时间后退出（m3u8，需要测试）
 */


public class HardMediaPlayer extends DolitBaseMediaPlayer {
    private MediaPlayer mMediaPlayer = null;
    private OnInfoListener mThisOnInfoListener = null;
    private OnCompletionListener mThisOnCompletionListener = null;
    private OnPreparedListener mThisOnPreparedListener = null;
    private OnBufferingUpdateListener mThisOnBufferingUpdateListener = null;
    private OnSeekCompleteListener mThisOnSeekCompleteListener = null;
    private OnErrorListener mThisOnErrorListener = null;
    private OnVideoSizeChangedListener mThisOnVideoSizeChangedListener = null;

    public HardMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
    }

    @Override
    public void setDisplay(SurfaceHolder sh) {
        if (this.mMediaPlayer != null)
            mMediaPlayer.setDisplay(sh);
    }

    @Override
    public void setDataSource(Context context, String path, Map<String, String> headers) throws IOException,
            IllegalArgumentException, SecurityException, IllegalStateException {
        this.mContext = context;
        if (this.mMediaPlayer != null) {
            mDataSource = path;
            mMediaPlayer.setDataSource(mContext, Uri.parse(mDataSource), getPlayer_headers(mDataSource, headers));
            //mMediaPlayer.setDataSource(mDataSource);
            //之前硬解是下面的调用方式
            //mDataSource = getLocalURL(path);
            //mMediaPlayer.setDataSource(mContext, Uri.parse(mDataSource), getPlayer_headers(mDataSource, headers));
        }
    }

    @Override
    public String getDataSource() {
        return mDataSource;
    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        if (this.mMediaPlayer != null)
            this.mMediaPlayer.prepareAsync();
    }

    @Override
    public void start() throws IllegalStateException {
        if (this.mMediaPlayer != null)
            this.mMediaPlayer.start();

    }

    @Override
    public void stop() throws IllegalStateException {
        if (this.mMediaPlayer != null)
            this.mMediaPlayer.stop();

    }

    @Override
    public void pause() throws IllegalStateException {
        if (this.mMediaPlayer != null)
            this.mMediaPlayer.pause();

    }

    @Override
    public void setScreenOnWhilePlaying(boolean screenOn) {
        if (this.mMediaPlayer != null)
            this.mMediaPlayer.setScreenOnWhilePlaying(screenOn);

    }

    @Override
    public int getVideoWidth() {
        if (this.mMediaPlayer != null)
            return this.mMediaPlayer.getVideoWidth();
        return 0;
    }

    @Override
    public int getVideoHeight() {
        if (this.mMediaPlayer != null)
            return this.mMediaPlayer.getVideoHeight();
        return 0;
    }

    @Override
    public boolean isPlaying() {
        if (this.mMediaPlayer != null) {
            try {
                return this.mMediaPlayer.isPlaying();
            } catch (IllegalStateException ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    public void seekTo(long msec) throws IllegalStateException {
        if (this.mMediaPlayer != null)
            this.mMediaPlayer.seekTo((int) msec);
    }

    @Override
    public long getCurrentPosition() {
        if (this.mMediaPlayer != null)
            return this.mMediaPlayer.getCurrentPosition();
        return 0;
    }

    @Override
    public long getDuration() {
        if (this.mMediaPlayer != null)
            return this.mMediaPlayer.getDuration();
        return 0;
    }

    @Override
    public void release() {
        if (this.mMediaPlayer != null)
            this.mMediaPlayer.release();

    }

    @Override
    public void reset() {
        if (this.mMediaPlayer != null)
            this.mMediaPlayer.getVideoHeight();

    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        if (this.mMediaPlayer != null)
            this.mMediaPlayer.setVolume(leftVolume, rightVolume);

    }

    @Override
    public Object getRealMediaPlayer() {
        if (this.mMediaPlayer != null)
            return this.mMediaPlayer;
        return null;
    }

    @Override
    public void setAudioStreamType(int streamtype) {
        if (this.mMediaPlayer == null)
            this.mMediaPlayer.setAudioStreamType(streamtype);
    }

    @Override
    public void setWakeMode(Context context, int mode) {
        if (this.mMediaPlayer != null)
            this.mMediaPlayer.setWakeMode(context, mode);
    }

    @Override
    public void setSurface(Surface surface) {
        if (this.mMediaPlayer != null)
            this.mMediaPlayer.setSurface(surface);
    }


    @Override
    public void setOnPreparedListener(OnPreparedListener listener) {
        this.mThisOnPreparedListener = listener;
        this.mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        this.mThisOnCompletionListener = listener;
        this.mMediaPlayer.setOnCompletionListener(mOnCompletionListener);

    }

    @Override
    public void setOnBufferingUpdateListener(OnBufferingUpdateListener listener) {
        this.mThisOnBufferingUpdateListener = listener;
        this.mMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
    }

    @Override
    public void setOnSeekCompleteListener(OnSeekCompleteListener listener) {
        this.mThisOnSeekCompleteListener = listener;
        this.mMediaPlayer.setOnSeekCompleteListener(mOnSeekCompleteListener);
    }

    @Override
    public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener listener) {
        this.mThisOnVideoSizeChangedListener = listener;
        this.mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
    }

    @Override
    public void setOnErrorListener(OnErrorListener listener) {
        this.mThisOnErrorListener = listener;
        this.mMediaPlayer.setOnErrorListener(mOnErrorListener);
    }

    @Override
    public void setOnInfoListener(OnInfoListener listener) {
        this.mThisOnInfoListener = listener;
        this.mMediaPlayer.setOnInfoListener(mOnInfoListener);

    }

    MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            if (mThisOnVideoSizeChangedListener != null)
                mThisOnVideoSizeChangedListener.onVideoSizeChanged(mp, width, height, mp.getVideoWidth(), mp.getVideoHeight(), 0, 0);
        }
    };

    private MediaPlayer.OnInfoListener mOnInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if (mThisOnInfoListener != null) {
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    what = DolitBaseMediaPlayer.MEDIA_INFO_BUFFERING_START;
                } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    what = DolitBaseMediaPlayer.MEDIA_INFO_BUFFERING_END;
                }
                return mThisOnInfoListener.onInfo(mp, what, extra);
            }
            return false;
        }

    };

    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (mThisOnCompletionListener != null) {
                mThisOnCompletionListener.onCompletion(mp);
            }
        }
    };

    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            if (mThisOnErrorListener != null) {
                return mThisOnErrorListener.onError(mp, what, extra, mp.getCurrentPosition());
            }
            return false;
        }
    };

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mp) {
            if (mThisOnPreparedListener != null) {
                mThisOnPreparedListener.onPrepared(mp, mp.getVideoWidth(), mp.getVideoHeight());
            }
        }
    };

    private MediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            if (mThisOnBufferingUpdateListener != null) {
                mThisOnBufferingUpdateListener.onBufferingUpdate(mp, percent);
            }
        }
    };

    private MediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {

        @Override
        public void onSeekComplete(MediaPlayer mp) {
            if (mThisOnSeekCompleteListener != null) {
                mThisOnSeekCompleteListener.onSeekComplete(mp);
            }
        }
    };

    @Override
    public boolean init(Context context) {
        return (this.mMediaPlayer != null);
    }

    @Override
    public void setUserAgent(String strUserAgent) {
        //该方法是为了软解准备的，硬解暂时不用
    }

    /**
     * 判定错误是否为硬解码失败
     *
     * @param what
     * @param extra
     * @return
     * @log 2016.03.24 shy 三星手机P2P频道切换软解才能播；我们找出所有非硬解失败的情况，其余的情况我们都切换
     */
    public static boolean isHardDecodeError(int what, int extra) {
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            return extra != MediaPlayer.MEDIA_ERROR_IO
                    && extra != MediaPlayer.MEDIA_ERROR_TIMED_OUT;
        } else {
            return true;
        }
    }

}
