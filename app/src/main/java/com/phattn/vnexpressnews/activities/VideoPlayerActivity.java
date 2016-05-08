package com.phattn.vnexpressnews.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.phattn.vnexpressnews.Config;
import com.phattn.vnexpressnews.R;
import com.phattn.vnexpressnews.fragments.VideoPlayerFragment;

import static com.phattn.vnexpressnews.util.LogUtils.makeLogTag;

public class VideoPlayerActivity extends BaseActivity
        implements VideoPlayerFragment.OnFragmentInteractionListener {
    private static final String TAG = makeLogTag(VideoPlayerActivity.class);

    /** Tags are used to pass/receiver data to/from Intent/Bundle */
    public static final String TAG_ARTICLE_ID = Config.PACKAGE_NAME
            + ".activities.TAG_ARTICLE_ID";

    public static final String TAG_VIDEO_PLAYER_FRAGMENT = Config.PACKAGE_NAME
            + ".activities.TAG_VIDEO_PLAYER_FRAGMENT";

    /** The fragment which is attached to this activity */
    private VideoPlayerFragment mFragment;

    /** Used to request article from Server */
    private int mArticleId;

    /** Is Orientation in portrait mode? */
    private boolean mIsPortrait;

    private Handler mHandler = new Handler();
    private final Runnable mHidePlayerController = new Runnable() {
        @Override
        public void run() {
            if (mFragment != null) {
                mFragment.hidePlayerController();
            }
        }
    };

    private ContentLoadingProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        // Specify this activity doesn't support navigation drawer
        shouldSupportNavDrawer(false);

        // Sets toolbars title
        setToolbarTitle(R.string.toolbar_title_video);

        if (savedInstanceState == null) {
            // If this is first time the activity launch, get data from Intent
            Intent intent = getIntent();
            mArticleId = intent.getIntExtra(TAG_ARTICLE_ID, -1);
        } else {
            // If the activity is restored, get data from Bundle
            mArticleId = savedInstanceState.getInt(TAG_ARTICLE_ID);
        }

        mIsPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        mProgressBar = (ContentLoadingProgressBar) findViewById(R.id.progress_bar);

        if (savedInstanceState != null) {
            mProgressBar.hide();
        }

        // Attach fragment to this activity
        attachFragment();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                View decorView = getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                decorView.setSystemUiVisibility(uiOptions);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mIsPortrait) {
            mHandler.postDelayed(mHidePlayerController, 3000);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TAG_ARTICLE_ID, mArticleId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:     // Handles UP button
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);

        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                if (mFragment != null) {
                    mFragment.showPlayerController();
                }
                return true;
            case (MotionEvent.ACTION_UP) :
                if (mFragment != null) {
                    mHandler.removeCallbacks(mHidePlayerController);
                    mHandler.postDelayed(mHidePlayerController, 3000);
                }
                return true;
            default :
                return super.onTouchEvent(event);
        }
    }

    @Override
    protected void onStop() {
        if (!mIsPortrait) {
            mHandler.removeCallbacks(mHidePlayerController);
        }
        super.onStop();
    }

    private void attachFragment() {
        FragmentManager fm = getFragmentManager();
        VideoPlayerFragment existedFragment = (VideoPlayerFragment) fm.findFragmentByTag(TAG_VIDEO_PLAYER_FRAGMENT);
        if (existedFragment == null) {
            mFragment = VideoPlayerFragment.newInstance(mArticleId);
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.video_player_fragment_placeholder, mFragment, TAG_VIDEO_PLAYER_FRAGMENT);
            ft.commit();
        } else {
            mFragment = existedFragment;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    public void onRequestDataFailed() {
        mProgressBar.hide();
        Toast.makeText(VideoPlayerActivity.this, R.string.load_data_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestDataSuccessfully() {
        mProgressBar.hide();
    }
}
