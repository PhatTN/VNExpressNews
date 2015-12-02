package com.example.phat.vnexpressnews.media;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.media.MediaItemStatus;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.example.phat.vnexpressnews.R;
import com.example.phat.vnexpressnews.fragments.VideoPlayerFragment;

import java.io.IOException;

import static com.example.phat.vnexpressnews.util.LogUtils.LOGD;
import static com.example.phat.vnexpressnews.util.LogUtils.LOGE;
import static com.example.phat.vnexpressnews.util.LogUtils.LOGI;
import static com.example.phat.vnexpressnews.util.LogUtils.makeLogTag;

/**
 * Handles playback of a single media item using MediaPlayer
 */
public abstract class VideoPlayer extends Player implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnErrorListener {
    private static final String TAG = makeLogTag(VideoPlayer.class);

    private final Context mContext;

    private final Handler mHandler = new Handler();
    private final Handler mUpdateSurfaceHandler = new Handler(mHandler.getLooper());

    private MediaPlayer mMediaPlayer;

    private int mState = STATE_IDLE;

    private int mSeekToPos;

    private int mVideoWidth;
    private int mVideoHeight;

    private Surface mSurface;

    public VideoPlayer(Context context) {
        mContext = context;
    }

    @Override
    public void play(final MediaItem item) {
        LOGD(TAG, "Play: item=" + item);
        // reset player to make sure it plays in fresh state
        reset();
        mSeekToPos = (int) item.getPosition();
        try {
            mMediaPlayer.setDataSource(mContext, item.getUri());
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.prepareAsync();
        } catch (IllegalStateException e) {
            LOGE(TAG, "MediaPlayer throws IllegalStateException, url=" + item.getUri());
        } catch (IOException e) {
            LOGE(TAG, "MediaPlayer throws IOException, uri=" + item.getUri());
        } catch (IllegalArgumentException e) {
            LOGE(TAG, "MediaPlayer throws IllegalArgumentException, uri=" + item.getUri());
        } catch (SecurityException e) {
            LOGE(TAG, "MediaPlayer throws SecurityException, uri=" + item.getUri());
        }
        if (item.getState() == MediaItemStatus.PLAYBACK_STATE_PLAYING) {
            resume();
        } else {
            pause();
        }
    }

    @Override
    public void seek(final MediaItem item) {
        LOGD(TAG, "Seek: item=" + item);
        int pos = (int) item.getPosition();
        if (mState == STATE_PLAYING || mState == STATE_PAUSED) {
            mMediaPlayer.seekTo(pos);
            mSeekToPos = pos;
        } else if (mState == STATE_IDLE || mState == STATE_PREPARING_FOR_PLAY
                || mState == STATE_PREPARING_FOR_PAUSE) {
            // Seek before onPrepared() arrives,
            // need to performed delayed seek in onPrepared()
            mSeekToPos = pos;
        }
    }

    @Override
    public void getStatus(final MediaItem item, final boolean update) {
        if (mState == STATE_PLAYING || mState == STATE_PAUSED) {
            item.setDuration(mMediaPlayer.getDuration());
            // Use mSeekToPos if we're currently seeking (mSeekToPos is reset
            // when seeking is completed)
            item.setPosition(mSeekToPos > 0 ? mSeekToPos : mMediaPlayer.getCurrentPosition());
            item.setTimestamp(SystemClock.elapsedRealtime());
        }
        // Updates items state
        item.setState(getMediaItemState());
    }

    @Override
    public void pause() {
        LOGD(TAG, "Pause");
        if (mState == STATE_PLAYING) {
            mMediaPlayer.pause();
            setState(STATE_PAUSED);
        } else if (mState == STATE_PREPARING_FOR_PLAY) {
            setState(STATE_PREPARING_FOR_PAUSE);
        }
    }

    @Override
    public void resume() {
        LOGD(TAG, "Resume");
        if (mState == STATE_READY || mState == STATE_PAUSED || mState == STATE_COMPLETED) {
            mMediaPlayer.start();
            setState(STATE_PLAYING);
        } else if (mState == STATE_IDLE || mState == STATE_PREPARING_FOR_PAUSE) {
            setState(STATE_PREPARING_FOR_PLAY);
        }
    }

    @Override
    public void stop() {
        LOGD(TAG, "Stop");
        if (mState == STATE_PLAYING || mState == STATE_PAUSED) {
            mMediaPlayer.stop();
            setState(STATE_IDLE);
        }
    }

    @Override
    public void release() {
        LOGD(TAG, "Releasing");
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public boolean isPaused() {
        return mState == STATE_PAUSED || mState == STATE_PREPARING_FOR_PAUSE;
    }

    @Override
    public boolean isCompleted() {
        return mState == STATE_COMPLETED;
    }

    //MediaPlayer Listeners
    @Override
    public void onPrepared(MediaPlayer mp) {
        LOGD(TAG, "onPrepared");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mState == STATE_IDLE) {
                    setState(STATE_READY);
                    updateVideoRect();
                } else if (mState == STATE_PREPARING_FOR_PLAY
                        || mState == STATE_PREPARING_FOR_PAUSE) {
                    int prevState = mState;
                    setState(mState == STATE_PREPARING_FOR_PLAY ? STATE_PLAYING : STATE_PAUSED);
                    updateVideoRect();
                    if (mSeekToPos > 0) {
                        LOGD(TAG, "Seek to initial pos:" + mSeekToPos);
                        mMediaPlayer.seekTo(mSeekToPos);
                    }
                    if (prevState == STATE_PREPARING_FOR_PLAY) {
                        mMediaPlayer.start();
                    }
                }
            }
        });
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        LOGD(TAG, "onCompletion");
        setState(STATE_COMPLETED);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onCompletion();
                }
            }
        });
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        setState(STATE_ERROR);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onError();
                }
            }
        });
        // Return true so that onCompletion is not called
        return true;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        LOGD(TAG, "onSeekComplete");
        mSeekToPos = 0;
    }

    protected Context getContext() { return mContext; }
    protected MediaPlayer getMediaPlayer() { return mMediaPlayer; }
    protected int getVideoWidth() { return mVideoWidth; }
    protected int getVideoHeight() { return mVideoHeight; }
    protected int getState() { return mState; }
    protected void setState(int newState) {
        if (newState != mState) {
            int prevState = mState;
            mState = newState;

            if (mCallback != null) {
                mCallback.onPlayerStateChanged(prevState, mState);
            }
        }
    }
    protected void setSurface(Surface surface) {
        mSurface = surface;
        updateSurface();
    }

    protected void removeSurface(SurfaceHolder surfaceHolder) {
        if (mSurface == surfaceHolder.getSurface()) {
            setSurface(null);
            if (mMediaPlayer != null) {
                mMediaPlayer.setDisplay(null);
            }
        }

    }

    public void updateSurface() {
        mUpdateSurfaceHandler.removeCallbacksAndMessages(null);
        mUpdateSurfaceHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mMediaPlayer == null) {
                    // just return if media player is already gone
                    return;
                }
                if (mSurface != null) {
                    mMediaPlayer.setSurface(mSurface);
                } else {
                    mMediaPlayer.setDisplay(null);
                }
            }
        });
    }

    protected abstract void updateSize();

    private void reset() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnSeekCompleteListener(this);
        updateSurface();
        setState(STATE_IDLE);
        mSeekToPos = 0;
    }

    private void updateVideoRect() {
        if (mState != STATE_IDLE && mState != STATE_PREPARING_FOR_PLAY
                && mState != STATE_PREPARING_FOR_PAUSE && mState != STATE_ERROR) {
            int width = mMediaPlayer.getVideoWidth();
            int height = mMediaPlayer.getVideoHeight();
            if (width > 0 && height > 0) {
                mVideoWidth = width;
                mVideoHeight = height;
                updateSize();
            } else {
                LOGE(TAG, "Video rect is 0x0!");
                mVideoWidth = mVideoHeight = 0;
            }
        }
    }

    private int getMediaItemState() {
        switch (mState) {
            case STATE_PREPARING_FOR_PLAY:
            case STATE_PLAYING:
                return MediaItemStatus.PLAYBACK_STATE_PLAYING;
            case STATE_PREPARING_FOR_PAUSE:
            case STATE_PAUSED:
                return MediaItemStatus.PLAYBACK_STATE_PAUSED;
            case STATE_COMPLETED:
                return MediaItemStatus.PLAYBACK_STATE_FINISHED;
            case STATE_ERROR:
                return MediaItemStatus.PLAYBACK_STATE_ERROR;
            case STATE_IDLE:
            case STATE_READY:
                return MediaItemStatus.PLAYBACK_STATE_PENDING;
            default:
                return MediaItemStatus.PLAYBACK_STATE_INVALIDATED;
        }
    }

    /**
     * Handles playback of a single media item using MediaPlayer in SurfaceView
     */
    public static class SurfaceViewPlayer extends VideoPlayer implements
            SurfaceHolder.Callback, VideoPlayerFragment.OnConfigurationsChangedListener {
        private static final String TAG = makeLogTag(SurfaceViewPlayer.class);
        private SurfaceView mSurfaceView;
        private View mLayout;

        public SurfaceViewPlayer(Context context) {
            super(context);

            setupSurface(context);
        }

        @Override
        public void release() {
            super.release();

            // remove surface holder callback
            SurfaceHolder holder = mSurfaceView.getHolder();
            holder.removeCallback(this);

            // hide the surface view when SurfaceViewPlayer is destroyed
            mSurfaceView.setVisibility(View.GONE);
            mLayout.setVisibility(View.GONE);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            LOGD(TAG, "Surface Created");
            setSurface(holder.getSurface());
            updateSize();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            LOGD(TAG, "Surface Changed:" + width + "x" + height);
            setSurface(holder.getSurface());
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            LOGD(TAG, "Surface Destroyed");
            removeSurface(holder);
        }

        @Override
        protected void updateSize() {
            int width = getVideoWidth();
            int height = getVideoHeight();
            if (width > 0 && height > 0) {
                int surfaceWidth = mLayout.getWidth();
                int surfaceHeight = mLayout.getHeight();

                // Calculate the new size of mSurfaceView
                ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();
                if (surfaceWidth * height < surfaceHeight * width) {
                    lp.width = surfaceWidth;
                    lp.height = surfaceWidth * height / width;
                } else {
                    lp.width = surfaceHeight * width / height;
                    lp.height = surfaceHeight;
                }
                LOGI(TAG, "Video rect is " + lp.width + "x" + lp.height);
                mSurfaceView.setLayoutParams(lp);
            }
        }

        @Override
        public void onNewSurfaceCreated(Context context) {
            setupSurface(context);
        }

        private void setupSurface(Context context) {
            mLayout = ((Activity) context).findViewById(R.id.player);
            mSurfaceView = (SurfaceView) ((Activity) context).findViewById(R.id.surface_view);

            if (mLayout.getVisibility() == View.GONE) {
                mLayout.setVisibility(View.VISIBLE);
            }

            if (mSurfaceView.getVisibility() == View.GONE) {
                mSurfaceView.setVisibility(View.VISIBLE);
            }

            // add surface holder callback
            SurfaceHolder holder = mSurfaceView.getHolder();
            holder.addCallback(this);
        }
    }
}
