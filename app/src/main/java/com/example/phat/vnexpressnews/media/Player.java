package com.example.phat.vnexpressnews.media;

import android.content.Context;

import static com.example.phat.vnexpressnews.util.LogUtils.LOGI;
import static com.example.phat.vnexpressnews.util.LogUtils.makeLogTag;

/**
 * Abstraction of common playback operations of media items, such as play,
 * seek, etc. Used by PlaybackManager as a backend to handle actual playback
 * of media items.
 */
public abstract class Player {
    private static final String TAG = makeLogTag(Player.class);

    public static final int STATE_IDLE = 0;
    public static final int STATE_PREPARING_FOR_PLAY = 1;
    public static final int STATE_PREPARING_FOR_PAUSE = 2;
    public static final int STATE_READY = 3;
    public static final int STATE_PLAYING = 4;
    public static final int STATE_PAUSED = 5;
    public static final int STATE_COMPLETED = 6;
    public static final int STATE_ERROR = 7;

    protected Callback mCallback;

    public abstract void release();

    // Base operations
    public abstract void play(final MediaItem item);
    public abstract void seek(final MediaItem item);
    public abstract void getStatus(final MediaItem item, final boolean update);
    public abstract void pause();
    public abstract void resume();
    public abstract void stop();

    // Get state
    public abstract boolean isPaused();
    public abstract boolean isCompleted();

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    /** Factory method. This method will creates concrete instance of {@code Player}
     * based on some condition. But in present situation, we have one concrete implementation of
     * {@code Player} is {@link VideoPlayer.SurfaceViewPlayer}. So, we always create
     * a {@code SurfaceViewPlayer} instance */
    public static Player create(Context context) {
        Player player = new VideoPlayer.SurfaceViewPlayer(context);
        LOGI(TAG, "SurfaceViewPlayer was created");
        return player;
    }

    /** Implements this {@code Callback} to handle some specific event of Player */
    public interface Callback {
        void onError();
        void onCompletion();
        void onPlayerStateChanged(int oldState, int newState);
    }
}
