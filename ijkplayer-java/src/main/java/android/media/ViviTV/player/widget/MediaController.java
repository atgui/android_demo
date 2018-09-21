/*
 * Copyright (C) 2006 The Android Open Source Project
 * Copyright (C) 2012 YIXIA.COM
 * Copyright (C) 2013 Zhang Rui <bbcallen@gmail.com>
 *
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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import tv.danmaku.ijk.media.player.R;

/**
 * A view containing controls for a MediaPlayer. Typically contains the buttons
 * like "Play/Pause" and a progress slider. It takes care of synchronizing the
 * controls with the state of the MediaPlayer.
 * <p>
 * The way to use this class is to a) instantiate it programatically or b)
 * create it in your xml layout.
 * <p>
 * a) The MediaController will create a default set of controls and put them in
 * a window floating above your application. Specifically, the controls will
 * float above the view specified with setAnchorView(). By default, the window
 * will disappear if left idle for three seconds and reappear when the user
 * touches the anchor view. To customize the MediaController's style, layout and
 * controls you should extend MediaController and override the {#link
 * {@link #makeControllerView()} method.
 * <p>
 * b) The MediaController is a FrameLayout, you can put it in your layout xml
 * and get it through {@link #findViewById(int)}.
 * <p>
 * NOTES: In each way, if you want customize the MediaController, the SeekBar's
 * id must be mediacontroller_progress, the Play/Pause's must be
 * mediacontroller_pause, current time's must be mediacontroller_time_current,
 * total time's must be mediacontroller_time_total, file name's must be
 * mediacontroller_file_name. And your resources must have a pause_button
 * drawable and a play_button drawable.
 * <p>
 * Functions like show() and hide() have no effect when MediaController is
 * created in an xml layout.
 */
public class MediaController extends FrameLayout implements View.OnClickListener {
    private static final String TAG = MediaController.class.getSimpleName();

    public interface IMediaControllerCallback {
        void handleFullScreenClicked(View view);

        void handlePauseStartClicked(View view);
    }

    private IMediaPlayerControl mPlayer;
    private Context mContext;
    private PopupWindow mWindow;
    private int mAnimStyle;
    private View mAnchor;
    private View mRoot;
    private SeekBar mProgress;
    private TextView mEndTime, mCurrentTime;
    private OutlineTextView mInfoView;
    private String mTitle;
    private long mDuration;
    private boolean mShowing;
    private boolean mDragging;
    private boolean mInstantSeeking = true;
    private static final int sDefaultTimeout = 3000;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    private boolean mFromXml = false;
    private ImageButton mPauseButton;
    private AudioManager mAM;
    private ImageButton ibFullScreen;
    private IMediaControllerCallback callback;
    private boolean autoHideNavigation = false;
    private TextView tvQuality;
    private int[] loc = new int[2];
    private Rect rect = new Rect();
    private PopupWindow qualityWindow;

    private boolean showQualitty=false;

    public MediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRoot = this;
        mFromXml = true;
        initController(context);
    }

    public MediaController(Context context) {
        super(context);
        if (!mFromXml && initController(context))
            initFloatingWindow();
    }

    private boolean initController(Context context) {
        mContext = context;
        mAM = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        return true;
    }
    //是否显示高清、流畅等选择
    public void setShowQualitty(boolean showQualitty){
        this.showQualitty=showQualitty;
    }
    public void setCallback(IMediaControllerCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        if (mRoot != null)
            initControllerView(mRoot);
    }

    private void initFloatingWindow() {
        mWindow = new PopupWindow(mContext);
        mWindow.setFocusable(false);
        mWindow.setBackgroundDrawable(null);
        mWindow.setOutsideTouchable(true);
        mAnimStyle = android.R.style.Animation;
    }

    /**
     * Set the view that acts as the anchor for the control view. This can for
     * example be a VideoView, or your Activity's main view.
     *
     * @param view The view to which to anchor the controller when it is visible.
     */
    public void setAnchorView(View view) {
        mAnchor = view;
        if (!mFromXml) {
            removeAllViews();
            mRoot = makeControllerView();
            mWindow.setContentView(mRoot);
            mWindow.setWidth(LayoutParams.MATCH_PARENT);
            mWindow.setHeight(LayoutParams.WRAP_CONTENT);
        }
        initControllerView(mRoot);
    }

    public void setAutoHideNavigation(boolean autoHideNavigation) {
        this.autoHideNavigation = autoHideNavigation;
    }

    /**
     * Create the view that holds the widgets that control playback. Derived
     * classes can override this to create their own.
     *
     * @return The controller view.
     */
    protected View makeControllerView() {
        return LayoutInflater.from(mContext).inflate(R.layout.mediacontroller, this);
    }

    private void initControllerView(View v) {
        mPauseButton = (ImageButton) v.findViewById(R.id.mediacontroller_play_pause);
        if (mPauseButton != null) {
            mPauseButton.requestFocus();
            mPauseButton.setOnClickListener(mPauseListener);
        }

        mProgress = (SeekBar) v.findViewById(R.id.mediacontroller_seekbar);
        if (mProgress != null) {
            if (mProgress instanceof SeekBar) {
                SeekBar seeker = (SeekBar) mProgress;
                seeker.setOnSeekBarChangeListener(mSeekListener);
                seeker.setThumbOffset(1);
            }
            mProgress.setMax(1000);
        }

        mEndTime = (TextView) v.findViewById(R.id.mediacontroller_time_total);
        mCurrentTime = (TextView) v.findViewById(R.id.mediacontroller_time_current);
        ibFullScreen = v.findViewById(R.id.mediacontroller_fullscreen);
        ibFullScreen.setOnClickListener(this);
        tvQuality = v.findViewById(R.id.tv_quality);
        tvQuality.setOnClickListener(this);
    }

    public void setMediaPlayer(IMediaPlayerControl player) {
        mPlayer = player;
        updatePausePlay();
    }

    /**
     * Control the action when the seekbar dragged by user
     *
     * @param seekWhenDragging True the media will seek periodically
     */
    public void setInstantSeeking(boolean seekWhenDragging) {
        mInstantSeeking = seekWhenDragging;
    }

    public void show() {
        show(sDefaultTimeout);
    }

    /**
     * Set the content of the file_name TextView
     *
     * @param name
     */
    public void setFileName(String name) {
        mTitle = name;
    }

    /**
     * Set the View to hold some information when interact with the
     * MediaController
     *
     * @param v
     */
    public void setInfoView(OutlineTextView v) {
        mInfoView = v;
    }

    private void disableUnsupportedButtons() {
        try {
            if (mPauseButton != null && !mPlayer.canPause())
                mPauseButton.setEnabled(false);
        } catch (IncompatibleClassChangeError ex) {
        }
    }

    /**
     * <p>
     * Change the animation style resource for this controller.
     * </p>
     * <p>
     * <p>
     * If the controller is showing, calling this method will take effect only
     * the next time the controller is shown.
     * </p>
     *
     * @param animationStyle animation style to use when the controller appears and
     *                       disappears. Set to -1 for the default animation, 0 for no
     *                       animation, or a resource identifier for an explicit animation.
     */
    public void setAnimationStyle(int animationStyle) {
        mAnimStyle = animationStyle;
    }

    /**
     * Show the controller on screen. It will go away automatically after
     * 'timeout' milliseconds of inactivity.
     *
     * @param timeout The timeout in milliseconds. Use 0 to show the controller
     *                until hide() is called.
     */
    @SuppressLint("InlinedApi")
    public void show(int timeout) {
//        if (!mShowing && mAnchor != null && mAnchor.getWindowToken() != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//                mAnchor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
//            }
//            if (mPauseButton != null)
//                mPauseButton.requestFocus();
//            disableUnsupportedButtons();
//
//            if (mFromXml) {
//                setVisibility(View.VISIBLE);
//            } else {
//                int[] location = new int[2];
//
//                mAnchor.getLocationOnScreen(location);
//                Rect anchorRect = new Rect(location[0], location[1],
//                        location[0] + mAnchor.getWidth(), location[1]
//                        + mAnchor.getHeight());
//
//                mWindow.setAnimationStyle(mAnimStyle);
//                mWindow.showAtLocation(mAnchor, Gravity.BOTTOM,
//                        anchorRect.left, 0);
//            }
//            mShowing = true;
//            if (mShownListener != null)
//                mShownListener.onShown();
//        }
//        updatePausePlay();
//        mHandler.sendEmptyMessage(SHOW_PROGRESS);
//
//        if (timeout != 0) {
//            mHandler.removeMessages(FADE_OUT);
//            mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT),
//                    timeout);
//        }
        if (!mShowing && mAnchor != null && mAnchor.getWindowToken() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mAnchor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
            if (mPauseButton != null)
                mPauseButton.requestFocus();
            disableUnsupportedButtons();

            if (mFromXml) {
                setVisibility(View.VISIBLE);
            } else {
                int[] location = new int[2];

                mAnchor.getLocationOnScreen(location);
                Rect anchorRect = new Rect(location[0], location[1], location[0] + mAnchor.getWidth(), location[1] + mAnchor.getHeight());

                mWindow.getContentView().measure(0, 0);
                int height = mWindow.getContentView().getMeasuredHeight();

                mWindow.setAnimationStyle(mAnimStyle);
                mWindow.showAtLocation(mAnchor, Gravity.TOP, anchorRect.left, anchorRect.bottom - height);
            }
            mShowing = true;
            if (mShownListener != null)
                mShownListener.onShown();
        }
        updatePausePlay();
        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT),
                    timeout);
        }
    }

    public boolean isShowing() {
        return mShowing;
    }

    @SuppressLint("InlinedApi")
    public void hide() {
        if (mAnchor == null)
            return;

        if (mShowing) {
            if (autoHideNavigation && Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mAnchor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
            try {
                mHandler.removeMessages(SHOW_PROGRESS);
                if (mFromXml)
                    setVisibility(View.GONE);
                else
                    mWindow.dismiss();
            } catch (IllegalArgumentException ex) {
                DebugLog.d(TAG, "MediaController already removed");
            }
            mShowing = false;
            if (mHiddenListener != null)
                mHiddenListener.onHidden();
        }

        if (qualityWindow != null && qualityWindow.isShowing()) {
            qualityWindow.dismiss();
        }
    }

    public interface OnShownListener {
        public void onShown();
    }

    private OnShownListener mShownListener;

    public void setOnShownListener(OnShownListener l) {
        mShownListener = l;
    }

    public interface OnHiddenListener {
        public void onHidden();
    }

    private OnHiddenListener mHiddenListener;

    public void setOnHiddenListener(OnHiddenListener l) {
        mHiddenListener = l;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            long pos;
            switch (msg.what) {
                case FADE_OUT:
                    hide();
                    break;
                case SHOW_PROGRESS:
                    pos = setProgress();
                    if (!mDragging && mShowing) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                        updatePausePlay();
                    }
                    break;
            }
        }
    };

    private long setProgress() {
        if (mPlayer == null || mDragging)
            return 0;

        int position = mPlayer.getCurrentPosition();
        int duration = mPlayer.getDuration();
        if (mProgress != null) {
            if (duration > 0) {
                long pos = 1000L * position / duration;
                mProgress.setProgress((int) pos);
            }
            int percent = mPlayer.getBufferPercentage();
            mProgress.setSecondaryProgress(percent * 10);
        }

        mDuration = duration;

        if (mEndTime != null)
            mEndTime.setText(generateTime(mDuration));
        if (mCurrentTime != null)
            mCurrentTime.setText(generateTime(position));

        return position;
    }

    private static String generateTime(long position) {
        int totalSeconds = (int) (position / 1000);

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return String.format(Locale.US, "%02d:%02d", minutes, seconds).toString();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        show(sDefaultTimeout);
        return true;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        show(sDefaultTimeout);
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (event.getRepeatCount() == 0 && (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_SPACE)) {
            doPauseResume();
            show(sDefaultTimeout);
            if (mPauseButton != null)
                mPauseButton.requestFocus();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
                updatePausePlay();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
            hide();
            return true;
        } else {
            show(sDefaultTimeout);
        }
        return super.dispatchKeyEvent(event);
    }

    private OnClickListener mPauseListener = new OnClickListener() {
        public void onClick(View v) {
            doPauseResume();
            show(sDefaultTimeout);
        }
    };

    private void updatePausePlay() {
        if (mRoot == null || mPauseButton == null)
            return;

        if (mPlayer.isPlaying())
            mPauseButton.setImageResource(R.drawable.mediacontroller_pause_button);
        else
            mPauseButton.setImageResource(R.drawable.mediacontroller_play_button);
    }

    private void doPauseResume() {
        if (mPlayer.isPlaying())
            mPlayer.pause();
        else
            mPlayer.start();
        updatePausePlay();

        // notify outside
        if (callback != null) {
            callback.handlePauseStartClicked(mPauseButton);
        }
    }

    private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
        public void onStartTrackingTouch(SeekBar bar) {
            mDragging = true;
            show(3600000);
            mHandler.removeMessages(SHOW_PROGRESS);
            if (mInstantSeeking)
                mAM.setStreamMute(AudioManager.STREAM_MUSIC, true);
            if (mInfoView != null) {
                mInfoView.setText("");
                mInfoView.setVisibility(View.VISIBLE);
            }
        }

        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser)
                return;

            long newposition = (mDuration * progress) / 1000;
            String time = generateTime(newposition);
            if (mInstantSeeking)
                mPlayer.seekTo(newposition);
            if (mInfoView != null)
                mInfoView.setText(time);
            if (mCurrentTime != null)
                mCurrentTime.setText(time);
        }

        public void onStopTrackingTouch(SeekBar bar) {
            if (!mInstantSeeking)
                mPlayer.seekTo((mDuration * bar.getProgress()) / 1000);
            if (mInfoView != null) {
                mInfoView.setText("");
                mInfoView.setVisibility(View.GONE);
            }
            show(sDefaultTimeout);
            mHandler.removeMessages(SHOW_PROGRESS);
            mAM.setStreamMute(AudioManager.STREAM_MUSIC, false);
            mDragging = false;
            mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
        }
    };

    @Override
    public void setEnabled(boolean enabled) {
        if (mPauseButton != null)
            mPauseButton.setEnabled(enabled);
        if (mProgress != null)
            mProgress.setEnabled(enabled);
        disableUnsupportedButtons();
        super.setEnabled(enabled);
    }
    public void showFull(){
        ibFullScreen.setImageResource(R.drawable.full_no);
    }

    public void hideFull(){
        ibFullScreen.setImageResource(R.drawable.quanping);
    }
    @Override
    public void onClick(View v) {
        if (v == ibFullScreen) {
            if (callback != null) {
                callback.handleFullScreenClicked(v);
            }
        } else if (tvQuality == v) {
            doOnQualityClicked();
        }
    }
    private OnSelectQualityListener onSelectQualityListener;
    public void setOnSelectQualityListener(OnSelectQualityListener l){
        this.onSelectQualityListener=l;
    }
    public interface OnSelectQualityListener{
        void onSelectQuality(String msg);
    }
    List<String> qualityList = new ArrayList<>();
    /**
     * 设置清晰度
     */
    public void quality(List<String> qualityList){
        this.qualityList=qualityList;
    }
    public void setTvQualityText(String txt){
        if(tvQuality!=null) {
            tvQuality.setText(txt);
        }
    }
    private final void doOnQualityClicked() {
        if (qualityWindow != null) {
            qualityWindow.dismiss();
            qualityWindow = null;
            return;
        }
        if(this.showQualitty==false){
            return;
        }
        List<String> qualityList =this.qualityList;// new ArrayList<>();
        if(qualityList.size()<=0){
            //qualityList.add("标清");
        }
//        qualityList.add("流畅");
//        qualityList.add("标清");
//        qualityList.add("高清");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.layout_quality_item, R.id.tv_quality_name, qualityList);
        ListView lvMain = new ListView(getContext());
        lvMain.setAdapter(adapter);
        lvMain.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj = parent.getItemAtPosition(position);
                if (obj instanceof String) {
                    String msg=(String) obj;
                    //tvQuality.setText(msg);
                    if(onSelectQualityListener!=null){
                        onSelectQualityListener.onSelectQuality(msg);
                    }
                }
                if (qualityWindow != null) {
                    qualityWindow.dismiss();
                    qualityWindow = null;
                }
            }
        });

        qualityWindow = new PopupWindow(getContext());
        qualityWindow.setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.ic_bg_quality_window)));
        qualityWindow.setContentView(lvMain);
        qualityWindow.setWidth(lvMain.getMeasuredWidth());
        qualityWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        tvQuality.getLocationOnScreen(loc);
        int y = loc[1] - lvMain.getMeasuredHeight() - this.getMeasuredHeight();
        if(qualityList.size()==4){
            y-=(qualityList.size()*35)-30;
        }else if(qualityList.size()==3){
            y-=(qualityList.size()*30);
        }
        // TODO mAnchor getParent is not right maybe
        qualityWindow.showAtLocation((View) mAnchor.getParent(), Gravity.LEFT | Gravity.TOP, loc[0], y);
    }
}
