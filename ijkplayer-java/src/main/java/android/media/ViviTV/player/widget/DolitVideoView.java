/*
 * Copyright (C) 2006 The Android Open Source Project
 * Copyright (C) 2012 YIXIA.COM
 * Copyright (C) 2013 Zhang Rui <bbcallen@gmail.com>

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.media.ViviTV.player.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.ViviTV.player.widget.DolitBaseMediaPlayer.OnCompletionListener;
import android.media.ViviTV.player.widget.DolitBaseMediaPlayer.OnErrorListener;
import android.media.ViviTV.player.widget.DolitBaseMediaPlayer.OnInfoListener;
import android.media.ViviTV.player.widget.DolitBaseMediaPlayer.OnPreparedListener;
import android.media.ViviTV.player.widget.DolitBaseMediaPlayer.OnSeekCompleteListener;
import android.media.ViviTV.player.widget.DolitBaseMediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Displays a video file. The VideoView class can load images from various
 * sources (such as resources or content providers), takes care of computing its
 * measurement from the video so that it can be used in any layout manager, and
 * provides various display options such as scaling and tinting.
 * <p>
 * VideoView also provide many wrapper methods for
 * {io.vov.vitamio.MediaPlayer}, such as { #getVideoWidth()},
 * {#setSubShown(boolean)}
 */
public class DolitVideoView extends SurfaceView implements IMediaPlayerControl {
    private static final String TAG = DolitVideoView.class.getName();

    private Uri mUri;
    private long mDuration;
    private String mUserAgent;
    private Map<String, String> mHeaders;

    public static final int STATE_ERROR = -1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_PREPARING = 1;
    public static final int STATE_PREPARED = 2;
    public static final int STATE_PLAYING = 3;
    public static final int STATE_PAUSED = 4;
    public static final int STATE_PLAYBACK_COMPLETED = 5;
    public static final int STATE_SUSPEND = 6;
    public static final int STATE_RESUME = 7;
    public static final int STATE_SUSPEND_UNSUPPORTED = 8;

    private int mCurrentState = STATE_IDLE;
    private int mTargetState = STATE_IDLE;

    //private int mVideoLayout = VIDEO_LAYOUT_ZOOM;//之前软解使用的
    private int m_videoLayoutScale = 0;
    public final static int A_4X3 = 1;
    public final static int A_16X9 = 2;
    public final static int A_RAW = 4; // 原始大小（目前沒有使用）
    public final static int A_DEFALT = 0; // 原始比例

    //原始大小
    public static final int VIDEO_LAYOUT_ORIGIN = 0;
    //按视频原始比率拉伸到屏幕的最大，不超过屏幕
    public static final int VIDEO_LAYOUT_SCALE = 1;
    //拉伸到屏幕大小。
    public static final int VIDEO_LAYOUT_STRETCH = 2;
    //占据整个屏幕，并有一部分视频显示不出来。
    //按屏幕宽高比和视频宽高比，宽度使用最大的，高度选用最小的
    public static final int VIDEO_LAYOUT_ZOOM = 3;

    private SurfaceHolder mSurfaceHolder = null;
    private DolitBaseMediaPlayer mMediaPlayer = null;
    private int mVideoWidth;
    private int mVideoHeight;
    @SuppressWarnings("unused")
    private int mVideoSarNum;
    @SuppressWarnings("unused")
    private int mVideoSarDen;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private WindowManager mWindowManager;
    private MediaController mMediaController;
    private View mBufferingIndicator;
    private OnCompletionListener mOnCompletionListener;
    private OnPreparedListener mOnPreparedListener;
    private OnErrorListener mOnErrorListener;
    private OnSeekCompleteListener mOnSeekCompleteListener;
    private OnInfoListener mOnInfoListener;
    private DolitBaseMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener;
    private int mCurrentBufferPercentage;
    private long mSeekWhenPrepared;
    private boolean mCanPause = true;
    private boolean mCanSeekBack = true;
    private boolean mCanSeekForward = true;
    private Context mContext;
    private boolean mIsHardDecode = true;//解码方式。是不是硬解。

    protected String mUser_Mac = "";
    protected String mLiveSeek = "0";
    protected String mLiveEpg = "-";
    protected String mLiveNextEpg = "-";
    protected String mLiveNextUrl = "-";
    protected String mLiveCookie = "";

    protected String mLive_Range = "mediaTV/range";
    protected String mLive_Referer = "mediaTV/user/|support|android-tvbox";
    protected String mLive_key = "";
    private boolean autoPlayAfterSurfaceCreated = true;
    private boolean mediaCodecEnabled = false;

    public DolitVideoView(Context context) {
        super(context);
        initVideoView(context);
    }

    public DolitVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DolitVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initVideoView(context);
    }

    public int getmCurrentState() {
        return mCurrentState;
    }

    public void setIsHardDecode(boolean bHardDecode) {
        this.mIsHardDecode = bHardDecode;
    }

    private void initVideoView(Context ctx) {
        mContext = ctx;
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mVideoWidth = 0;
        mVideoHeight = 0;
        mVideoSarNum = 0;
        mVideoSarDen = 0;
        getHolder().addCallback(mSHCallback);
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        mCurrentState = STATE_IDLE;
        mTargetState = STATE_IDLE;
        if (ctx instanceof Activity)
            ((Activity) ctx).setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    public boolean isValid() {
        return (mSurfaceHolder != null && mSurfaceHolder.getSurface().isValid());
    }

    public void setRequestWidthHeight(int width, int height) {
        if (mSurfaceHolder == null) {
            return;
        }

        mSurfaceHolder.setFixedSize(width, height);
    }

    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    public void setVideoURI(Uri uri) {
        mUri = uri;
        mSeekWhenPrepared = 0;
        openVideo();
        requestLayout();
        invalidate();
    }

    public void setVideoURI(Uri uri, Map<String, String> headers) {
        mUri = uri;
        mHeaders = headers;
        mSeekWhenPrepared = 0;
        openVideo();
        requestLayout();
        invalidate();
    }

    public void setUserAgent(String ua) {
        mUserAgent = ua;
    }

    public void setAutoPlayAfterSurfaceCreated(boolean autoPlayAfterSurfaceCreated) {
        this.autoPlayAfterSurfaceCreated = autoPlayAfterSurfaceCreated;
    }

    public void stopPlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
            mTargetState = STATE_IDLE;
        }
    }

    Handler handler = new Handler(Looper.getMainLooper());

    private void openVideo() {
        if (mUri == null || mSurfaceHolder == null)
            return;

        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        mContext.sendBroadcast(i);

        release(false);

        try {
            mDuration = -1;
            mCurrentBufferPercentage = 0;
            DolitBaseMediaPlayer mediaPlayer = null;
            if (mUri != null) {
                Log.e(TAG, "VideoView isHardDecode:" + mIsHardDecode);

                if (mIsHardDecode)
                    mediaPlayer = new HardMediaPlayer();
                else
                    mediaPlayer = new SoftMediaPlayer(mediaCodecEnabled);
                if (mediaPlayer.init(mContext) == false) {
                    DebugLog.e(TAG, "Init ViviTVMediaPlayer engine failed, please check Key!");
                    return;
                }
                if (mUserAgent != null) {
                    mediaPlayer.setUserAgent(mUserAgent);
                }
            }
            mMediaPlayer = mediaPlayer;
            mMediaPlayer.setLiveKey(mLive_key);
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);

            if (mUri != null) {
                mMediaPlayer.setDataSource(mContext, mUri.toString(), mHeaders);
            }
            mMediaPlayer.setDisplay(mSurfaceHolder);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.prepareAsync();
            handler.postDelayed(TimeOutError, TIMEOUTDEFAULT);
            mCurrentState = STATE_PREPARING;
            attachMediaController();
        } catch (IOException ex) {
            DebugLog.e(TAG, "Unable to open content(IOException): " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, DolitBaseMediaPlayer.MEDIA_INFO_ErrorBeforePlay, 0, 0);
            return;
        } catch (IllegalArgumentException ex) {
            DebugLog.e(TAG, "Unable to open content(IllegalArgumentException): " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, DolitBaseMediaPlayer.MEDIA_INFO_ErrorBeforePlay, 0, 0);
            return;
        } catch (IllegalStateException ex) {
            DebugLog.e(TAG, "Unable to open content(IllegalStateException): " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, DolitBaseMediaPlayer.MEDIA_INFO_ErrorBeforePlay, 0, 0);
            return;
        }
    }

    public void setMediaController(MediaController controller) {
        if (mMediaController != null)
            mMediaController.hide();
        mMediaController = controller;
        attachMediaController();
    }

    public void setMediaBufferingIndicator(View bufferingIndicator) {
        if (this.mBufferingIndicator != null)
            hideBuffingTip();
        this.mBufferingIndicator = bufferingIndicator;
    }

    private void attachMediaController() {
        if (mMediaPlayer != null && mMediaController != null) {
            mMediaController.setMediaPlayer(this);
            mMediaController.setAnchorView(this);
            mMediaController.setEnabled(isInPlaybackState());

            if (mUri != null) {
                List<String> paths = mUri.getPathSegments();
                String name = paths == null || paths.isEmpty() ? "null" : paths
                        .get(paths.size() - 1);
                mMediaController.setFileName(name);
            }
        }
    }

    OnVideoSizeChangedListener mSizeChangedListener = new OnVideoSizeChangedListener() {
        public void onVideoSizeChanged(Object mp, int width, int height, int videoWidth, int videoHeight, int sarNum, int sarDen) {
            DebugLog.dfmt(TAG, "onVideoSizeChanged: (%dx%d)", width, height);
            mVideoWidth = videoWidth;
            mVideoHeight = videoHeight;
            mVideoSarNum = sarNum;
            mVideoSarDen = sarDen;
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                selectScales(m_videoLayoutScale);
                //setVideoLayout(mVideoLayout);
                requestLayout();
            }
        }
    };

    OnPreparedListener mPreparedListener = new OnPreparedListener() {
        public void onPrepared(Object mp, int videoWidth, int videoHeight) {
            DebugLog.d(TAG, "onPrepared");
            mCurrentState = STATE_PREPARED;
            mTargetState = STATE_PLAYING;

            mCanPause = mCanSeekBack = mCanSeekForward = true;
            handler.removeCallbacks(TimeOutError);

            if (mOnPreparedListener != null)
                mOnPreparedListener.onPrepared(mMediaPlayer, videoWidth, videoHeight);
            if (mMediaController != null)
                mMediaController.setEnabled(true);
            mVideoWidth = videoWidth;
            mVideoHeight = videoHeight;

            long seekToPosition = mSeekWhenPrepared;

            if (seekToPosition != 0)
                seekTo(seekToPosition);
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                //setVideoLayout(mVideoLayout);
                //selectScales(m_videoLayoutScale);
                getHolder().setFixedSize(mVideoWidth, mVideoHeight);
                if (mSurfaceWidth == mVideoWidth
                        && mSurfaceHeight == mVideoHeight) {
                    if (mTargetState == STATE_PLAYING) {
                        start();
                        if (mMediaController != null)
                            mMediaController.show();
                    } else if (!isPlaying()
                            && (seekToPosition != 0 || getCurrentPosition() > 0)) {
                        if (mMediaController != null)
                            mMediaController.show(0);
                    }
                }
            } else if (mTargetState == STATE_PLAYING) {
                start();
            }
        }
    };

    private OnCompletionListener mCompletionListener = new OnCompletionListener() {
        public void onCompletion(Object mp) {
            DebugLog.d("TAG", "onCompletion");
            mCurrentState = STATE_PLAYBACK_COMPLETED;
            mTargetState = STATE_PLAYBACK_COMPLETED;
            if (mMediaController != null)
                mMediaController.hide();
            if (mOnCompletionListener != null)
                mOnCompletionListener.onCompletion(mMediaPlayer);
        }
    };

    private OnErrorListener mErrorListener = new OnErrorListener() {
        public boolean onError(Object mp, int framework_err, int impl_err, long currentPosition) {
            DebugLog.dfmt(TAG, "Error: %d, %d", framework_err, impl_err);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            if (mMediaController != null)
                mMediaController.hide();

            if (mOnErrorListener != null) {
                mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err, currentPosition);
            }

//            if (getWindowToken() != null) {
//                int message = framework_err == IMediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK ? R.string.vitamio_videoview_error_text_invalid_progressive_playback
//                        : R.string.vitamio_videoview_error_text_unknown;
//
//                new AlertDialog.Builder(mContext)
//                        .setTitle(R.string.vitamio_videoview_error_title)
//                        .setMessage(message)
//                        .setPositiveButton(
//                                R.string.vitamio_videoview_error_button,
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog,
//                                            int whichButton) {
//                                        if (mOnCompletionListener != null)
//                                            mOnCompletionListener
//                                                    .onCompletion(mMediaPlayer);
//                                    }
//                                }).setCancelable(false).show();
//            }
            return true;
        }
    };

    private DolitBaseMediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new DolitBaseMediaPlayer.OnBufferingUpdateListener() {
        public void onBufferingUpdate(Object mp, int percent) {
            mCurrentBufferPercentage = percent;
            //Log.e("TAG","OnBufferingUpdateListener::"+percent);
            if (mOnBufferingUpdateListener != null)
                mOnBufferingUpdateListener.onBufferingUpdate(mp, percent);
        }
    };

    private OnInfoListener mInfoListener = new OnInfoListener() {
        @Override
        public boolean onInfo(Object mp, int what, int extra) {
            //DebugLog.dfmt("TAG", "onInfo1: (%d, %d)", what, extra);
            if (what == DolitBaseMediaPlayer.MEDIA_INFO_BUFFERING_START) {
                //DebugLog.dfmt("TAG", "onInfo1: (MEDIA_INFO_BUFFERING_START)");
                showBuffingTip();
            } else if (what == DolitBaseMediaPlayer.MEDIA_INFO_BUFFERING_END) {
                //DebugLog.dfmt("TAG", "onInfo1: (MEDIA_INFO_BUFFERING_END)");
                hideBuffingTip();
            }
            if (mOnInfoListener != null) {
                //Log.e("TAG","缓冲。》》》");
                mOnInfoListener.onInfo(mp, what, extra);
            }
            return true;
        }
    };

    private OnSeekCompleteListener mSeekCompleteListener = new OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(Object mp) {
            DebugLog.d(TAG, "onSeekComplete");
            if (mOnSeekCompleteListener != null)
                mOnSeekCompleteListener.onSeekComplete(mp);
        }
    };

    public void setOnPreparedListener(OnPreparedListener l) {
        mOnPreparedListener = l;
    }

    public void setOnCompletionListener(OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    public void setOnErrorListener(OnErrorListener l) {
        mOnErrorListener = l;
    }

    public void setOnBufferingUpdateListener(DolitBaseMediaPlayer.OnBufferingUpdateListener l) {
        mOnBufferingUpdateListener = l;
    }

    public void setOnSeekCompleteListener(OnSeekCompleteListener l) {
        mOnSeekCompleteListener = l;
    }

    public void setOnInfoListener(OnInfoListener l) {
        mOnInfoListener = l;
    }

    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            mSurfaceHolder = holder;
            if (mMediaPlayer != null) {
                mMediaPlayer.setDisplay(mSurfaceHolder);
            }

            mSurfaceWidth = w;
            mSurfaceHeight = h;
            boolean isValidState = (mTargetState == STATE_PLAYING);
            boolean hasValidSize = (mVideoWidth == w && mVideoHeight == h);
            if (mMediaPlayer != null && isValidState && hasValidSize) {
                if (mSeekWhenPrepared != 0)
                    seekTo(mSeekWhenPrepared);
                start();
                if (mMediaController != null) {
                    if (mMediaController.isShowing())
                        mMediaController.hide();
                    mMediaController.show();
                }
            }
        }

        public void surfaceCreated(SurfaceHolder holder) {
            mSurfaceHolder = holder;
            if (mMediaPlayer != null && mCurrentState == STATE_SUSPEND
                    && mTargetState == STATE_RESUME) {
                mMediaPlayer.setDisplay(mSurfaceHolder);

                if (autoPlayAfterSurfaceCreated) {
                    resume();
                }
            } else {
                if (autoPlayAfterSurfaceCreated) {
                    openVideo();
                }
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            hideBuffingTip();
            mSurfaceHolder = null;
            if (mMediaController != null)
                mMediaController.hide();
            if (mCurrentState != STATE_SUSPEND)
                release(true);
        }
    };

    private void release(boolean cleartargetstate) {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
            if (cleartargetstate)
                mTargetState = STATE_IDLE;

        }
        handler.removeCallbacks(TimeOutError);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isInPlaybackState() && mMediaController != null)
            toggleMediaControlsVisiblity();
        return false;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        if (isInPlaybackState() && mMediaController != null)
            toggleMediaControlsVisiblity();
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK
                && keyCode != KeyEvent.KEYCODE_VOLUME_UP
                && keyCode != KeyEvent.KEYCODE_VOLUME_DOWN
                && keyCode != KeyEvent.KEYCODE_MENU
                && keyCode != KeyEvent.KEYCODE_CALL
                && keyCode != KeyEvent.KEYCODE_ENDCALL;
        if (isInPlaybackState() && isKeyCodeSupported
                && mMediaController != null) {
            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
                    || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                    || keyCode == KeyEvent.KEYCODE_SPACE) {
                if (isPlaying()) {
                    pause();
                    mMediaController.show();
                } else {
                    start();
                    mMediaController.hide();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
                    && isPlaying()) {
                pause();
                mMediaController.show();
            } else {
                toggleMediaControlsVisiblity();
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void toggleMediaControlsVisiblity() {
        if (mMediaController.isShowing()) {
            mMediaController.hide();
        } else {
            mMediaController.show();
        }
    }

    @Override
    public void start() {
        if (isInPlaybackState()) {
            try {
                mMediaPlayer.start();
                mCurrentState = STATE_PLAYING;
            } catch (IllegalStateException ex) {
                ex.printStackTrace();
            }
        }
        mTargetState = STATE_PLAYING;
    }

    @Override
    public void pause() {
        if (isInPlaybackState()) {
            try {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    mCurrentState = STATE_PAUSED;
                }
            } catch (IllegalStateException ex) {
                ex.printStackTrace();
            }
        }
        mTargetState = STATE_PAUSED;
    }

    public void resume() {
        if (mSurfaceHolder == null && mCurrentState == STATE_SUSPEND) {
            mTargetState = STATE_RESUME;
        } else if (mCurrentState == STATE_SUSPEND_UNSUPPORTED) {
            openVideo();
        }
    }

    @Override
    public int getDuration() {
        if (isInPlaybackState()) {
            if (mDuration > 0)
                return (int) mDuration;
            mDuration = mMediaPlayer.getDuration();
            return (int) mDuration;
        }
        mDuration = -1;
        return (int) mDuration;
    }

    @Override
    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            long position = mMediaPlayer.getCurrentPosition();
            return (int) position;
        }
        return 0;
    }

    @Override
    public void seekTo(long msec) {
        if (isInPlaybackState()) {
            try {
                mMediaPlayer.seekTo(msec);
            } catch (IllegalStateException ex) {
                ex.printStackTrace();
            }
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = msec;
        }
    }

    @Override
    public boolean isPlaying() {
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        if (mMediaPlayer != null)
            return mCurrentBufferPercentage;
        return 0;
    }

    public int getVideoWidth() {
        return mVideoWidth;
    }

    public int getVideoHeight() {
        return mVideoHeight;
    }

    protected boolean isInPlaybackState() {
        return (mMediaPlayer != null && mCurrentState != STATE_ERROR
                && mCurrentState != STATE_IDLE && mCurrentState != STATE_PREPARING);
    }

    public boolean canPause() {
        return mCanPause;
    }

    public boolean canSeekBackward() {
        return mCanSeekBack;
    }

    public boolean canSeekForward() {
        return mCanSeekForward;
    }

    //设置显示比例
    public void setScale(int flg) {
        //目前只有三种
        this.m_videoLayoutScale = flg % 3;
    }

    private void showBuffingTip() {
        if (this.mBufferingIndicator != null && this.mBufferingIndicator.getParent() == null) {
            Log.i(TAG, " ------------ set loading...");
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.format = PixelFormat.TRANSPARENT;
            lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            lp.width = LayoutParams.WRAP_CONTENT;
            lp.height = LayoutParams.WRAP_CONTENT;
            // lp.token = getWindowToken();
            lp.gravity = Gravity.CENTER;
            this.mWindowManager.addView(mBufferingIndicator, lp);
        }
    }

    private void hideBuffingTip() {
        if (this.mBufferingIndicator != null &&
                this.mBufferingIndicator.getParent() != null&&mBufferingIndicator instanceof View) {
            try {
                if(this.mWindowManager!=null&&mBufferingIndicator!=null) {
                    this.mWindowManager.removeView(mBufferingIndicator);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int getVideoLayoutScale() {
        return m_videoLayoutScale;
    }

    /**
     * 全屏状态 才可以使用 选择比例
     *
     * @param flg
     */
    public void selectScales(int flg) {
        m_videoLayoutScale = flg;
        if (getWindowToken() != null) {
            Rect rect = new Rect();
            getWindowVisibleDisplayFrame(rect);
            Log.d(TAG, "Rect = " + rect.top + ":" + rect.bottom + ":"
                    + rect.left + ":" + rect.right);

            double height = rect.bottom - rect.top;
            double width = rect.right - rect.left;
            Log.d(TAG, "diplay = " + width + ":" + height);

            if (height <= 0.0 || width <= 0.0 || mVideoHeight <= 0.0
                    || mVideoWidth <= 0.0) {
                return;
            }
            LayoutParams param = getLayoutParams();
            switch (flg) {
                case A_4X3:
                    if (width / height >= 4.0 / 3.0) { // 屏幕 宽了 以屏幕高为基
                        param.height = (int) height;
                        param.width = (int) (4 * height / 3);
                    } else { // 屏幕 高了 以宽为基
                        param.width = (int) width;
                        param.height = (int) (3 * width / 4);
                    }
                    System.out.println("A_4X3 === " + param.width + ":"
                            + param.height);
                    setLayoutParams(param);
                    break;
                case A_16X9:
//                    if (width / height >= 16.0 / 9.0) { // 屏幕 宽了 以屏幕高为基
//                        param.height = (int) height;
//                        param.width = (int) (16 * height / 9);
//                    } else { // 屏幕 高了 以宽为基
//                        param.width = (int) width;
//                        param.height = (int) (9 * width / 16);
//                    }
                    param.width = (int) width;
                    param.height = (int) (9 * width / 16);
                    System.out.println("A_16X9 === " + param.width + ":"
                            + param.height);
                    setLayoutParams(param);
                    break;
                case A_DEFALT: //
                    // shaoheyi 修复原始比例显示不正确问题参照ijkplayer demo把mVideoSarNum / mVideoSarDen考虑进来
                    // 本次修改删除onMeasure方法，因为我们通过setLayoutParams已经正确设置了宽高，这个父类的onMeasure会考虑进去的
                    // 在selectScales也调用了requestLayout
                    float videoWidthHeightRatio = mVideoWidth * 1.0f / mVideoHeight;
                    if (mVideoSarNum > 0 && mVideoSarDen > 0) {
                        videoWidthHeightRatio = videoWidthHeightRatio * mVideoSarNum / mVideoSarDen;
                    }

                    boolean videoViewShouldBeWider = videoWidthHeightRatio > width / height;
                    if (videoViewShouldBeWider) {
                        // too wide, fix width
                        param.width = (int) width;
                        param.height = (int) (width / videoWidthHeightRatio);
                    } else {
                        // too high, fix height
                        param.height = (int) height;
                        param.width = (int) (height * videoWidthHeightRatio);
                    }

                    Log.e("TAG","A_DEFALT === " + param.width + ":" + param.height);
                    setLayoutParams(param);
                    break;
            }
            if(flg==2){
                mMediaController.showFull();
            }else{
                mMediaController.hideFull();
            }
        }
    }

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

    private static final long TIMEOUTDEFAULT = 30000;
    //private static final int MEDIA_ERROR_TIMED_OUT = 0xffffff92;
    private Runnable TimeOutError = new Runnable() {

        @Override
        public void run() {
            Log.e(TAG, "open video time out : Uri = " + mUri);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;

            if (mOnErrorListener != null) {
                mOnErrorListener.onError(mMediaPlayer, DolitBaseMediaPlayer.MEDIA_INFO_TIMEOUT, -100, getCurrentPosition());
            }

        }
    };

    public void setMediaCodecEnabled(boolean mediaCodecEnabled) {
        this.mediaCodecEnabled = mediaCodecEnabled;
    }
}
