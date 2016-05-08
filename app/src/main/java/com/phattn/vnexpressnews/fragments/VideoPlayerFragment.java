package com.phattn.vnexpressnews.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.phattn.vnexpressnews.Config;
import com.phattn.vnexpressnews.R;
import com.phattn.vnexpressnews.adapters.ArticleAdapter;
import com.phattn.vnexpressnews.io.FullArticleHandler;
import com.phattn.vnexpressnews.io.JSONHandler;
import com.phattn.vnexpressnews.io.RequestQueueManager;
import com.phattn.vnexpressnews.media.MediaItem;
import com.phattn.vnexpressnews.media.Player;
import com.phattn.vnexpressnews.model.Article;
import com.phattn.vnexpressnews.model.Module;
import com.phattn.vnexpressnews.util.APIEndpointBuilder;
import com.phattn.vnexpressnews.util.APIEndpointBuilder.APIEndpointType;
import com.phattn.vnexpressnews.util.DateTimeUtils;
import com.phattn.vnexpressnews.util.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import static com.phattn.vnexpressnews.util.LogUtils.LOGD;
import static com.phattn.vnexpressnews.util.LogUtils.LOGI;
import static com.phattn.vnexpressnews.util.LogUtils.LOGW;
import static com.phattn.vnexpressnews.util.LogUtils.makeLogTag;

/**
 * This fragment makes a network request to get {@link Article} by {@code #ArticleId},
 * extracts video from that {@code Article}. Then, plays the video and uses {@code RecyclerView}
 * to display video's related information.
 *
 * To make sure user experiences is smooth while playing video when orientation changed.
 * We use {@code setRetainInstance(true)} to keep video playing without interruption.
 */
public class VideoPlayerFragment extends Fragment implements Player.Callback,
        Response.Listener<Article>, Response.ErrorListener {
    private static final String TAG = makeLogTag(VideoPlayerFragment.class);

    /** The fragment initialization parameters */
    private static final String ARG_ARTICLE_ID = Config.PACKAGE_NAME + ".fragments.ARG_ARTICLE_ID";

    /** Constrains indicate when using Volley RequestQueue */
    private static final String REQUEST_TAG_ARTICLE = Config.PACKAGE_NAME
            + ".fragments.REQUEST_TAG_ARTICLE";

    /** Used to save/restore when activity stop/create */
    private static final String TAG_MEDIA_ITEM = Config.PACKAGE_NAME + ".fragments.TAG_MEDIA_ITEM";
    private static final String TAG_LIST_MODULE = Config.PACKAGE_NAME + ".fragments.TAG_LIST_MODULE";

    /** API Endpoint Builder, builds appropriate URL based on request from user */
    private APIEndpointBuilder mAPIEndpointBuilder;

    private RecyclerView mArticleRV;

    /** The adapter which will populates data into views */
    private ArticleAdapter mArticleAdapter;

    /** Data */
    private int mArticleId;
    private List<Module> mModules;

    /** Requesting and communication with network */
    private RequestQueueManager mRequestQueueManager;

    /** Handles deserialization JSON to specify model */
    private JSONHandler<Article> mJSONHandler;

    /** Is Orientation in portrait mode? */
    private boolean isPortrait;

    /** Video Player Controller */
    private LinearLayout mPlayerControllerLayout;
    private ImageButton mPauseResumeButton;
    private ImageButton mFullscreenButton;
    private SeekBar mSeekBar;
    /** Display {@code MediaItem}'s time info. Will be formatted as
     * 'elapsedTime / Duration'. Ex: '04:35 / 06:12' */
    private TextView mTimeInfo;

    /** Player states flags */
    private boolean mNeedResume;
    private boolean mSeeking;

    /** This flag indicates whenever the parent activity recreates since
     * changing configurations such as changing orientations */
    private boolean mIsConfigurationsChanged = false;

    /** MediaItem used to save related states of current playing video */
    private MediaItem mMediaItem;

    /** Used to play Video */
    private Player mPlayer;

    private final Handler mHandler = new Handler();
    /** A recursion task updates seekbar each one second */
    private final Runnable mUpdateSeekRunnable = new Runnable() {
        @Override
        public void run() {
            updateProgress();
            // update Ui every 1 second
            mHandler.postDelayed(this, 1000);
        }
    };

    /** The parent activity */
    private Activity mActivity;

    /** Event listeners */
    private OnConfigurationsChangedListener mListener;
    private OnFragmentInteractionListener mFragmentListener;

    public VideoPlayerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param articleId  Article Id, used to request article from network
     * @return A new instance of fragment VideoPlayerFragment.
     */
    public static VideoPlayerFragment newInstance(int articleId) {
        VideoPlayerFragment fragment = new VideoPlayerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ARTICLE_ID, articleId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Makes sure this fragment will be not destroyed when
        // the parent activity recreate.
        setRetainInstance(true);

        if (getArguments() != null) {
            mArticleId = getArguments().getInt(ARG_ARTICLE_ID);
        } else if (savedInstanceState != null) {
            mMediaItem = savedInstanceState.getParcelable(TAG_MEDIA_ITEM);
            mModules = savedInstanceState.getParcelableArrayList(TAG_LIST_MODULE);
        }

        // Sets up necessary stubs.
        mAPIEndpointBuilder = new APIEndpointBuilder(APIEndpointType.API_DETAIL_FROM_ARTICLE);
        mJSONHandler = new FullArticleHandler();
        mRequestQueueManager = RequestQueueManager.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_player, container, false);

        // Finds out current orientation mode
        isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        mActivity = getActivity();

        // Setup pause/resume button and its event
        mPauseResumeButton = (ImageButton) view.findViewById(R.id.pause_resume_button);
        mPauseResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handles pause/resume action corresponding to the current player state.
                if (mPlayer.isPaused() || mPlayer.isCompleted()) {
                    mPlayer.resume();
                } else {
                    mPlayer.pause();
                }
            }
        });

        // Setup fullscreen/collapsing button and its event
        mFullscreenButton = (ImageButton) view.findViewById(R.id.fullscreen_button);
        // Depending on orientation mode, we set corresponding icon
        mFullscreenButton.setImageResource(isPortrait ?
                R.drawable.ic_fullscreen : R.drawable.ic_fullscreen_exit);
        mFullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Depending on orientation mode, we set corresponding action
                mActivity.setRequestedOrientation(isPortrait ?
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        });

        mSeekBar = (SeekBar) view.findViewById(R.id.seekbar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mMediaItem != null && mMediaItem.getDuration() > 0) {
                    long pos = progress * mMediaItem.getDuration() / 100;
                    mMediaItem.setPosition(pos);
                    mMediaItem.setTimestamp(SystemClock.elapsedRealtime());
                    mPlayer.seek(mMediaItem);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mSeeking = false;
                updateUi();
            }
        });

        mTimeInfo = (TextView) view.findViewById(R.id.time_info);

        mPlayerControllerLayout = (LinearLayout) view.findViewById(R.id.player_controller);

        // In landscape mode, video is played in full screen, so doesn't need
        // RecyclerView to display related information. Since, only set up
        // RecyclerView in portrait mode
        if (isPortrait) {
            setupRecyclerView(view);
        } else if (mArticleAdapter == null) {
            // Also sets up the Adapter, so when changing orientation to
            // portrait, this adapter will be set to RecyclerView
            mModules = new ArrayList<>();
            mArticleAdapter = new ArticleAdapter(mActivity, mModules, Article.ARTICLE_TYPE_VIDEO, null);
        }

        // Make a request article if it didn't make yet,
        if (mMediaItem == null) {
            requestArticle();
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mPlayer == null) {
            // Sets up mPlayer if it wasn't set up yet.
            setupMediaPlayer();
        } else if (mIsConfigurationsChanged) {
            // If changing configurations, we need to notify
            // that new surface was created. And mPlayer needs
            // to update
            mListener.onNewSurfaceCreated(mActivity);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Since in onStop() method, we release mPlayer when do not need anymore.
        // So, we need to setup it again
        if (mPlayer == null) {
            setupMediaPlayer();

            if (mMediaItem != null) {
                mPlayer.play(mMediaItem);
                mPlayer.resume();
            }
        }
    }

    @Override
    public void onResume() {
        if (mNeedResume) {
            mPlayer.resume();
            mNeedResume = false;
        }
        mHandler.postDelayed(mUpdateSeekRunnable, 1000);
        super.onResume();
    }

    @Override
    public void onPause() {
        if (!mPlayer.isPaused()) {
            mNeedResume = true;
            mPlayer.pause();
        }
        mHandler.removeCallbacks(mUpdateSeekRunnable);
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(TAG_MEDIA_ITEM, mMediaItem);
        outState.putParcelableArrayList(TAG_LIST_MODULE, (ArrayList<Module>) mModules);
    }

    @Override
    public void onStop() {

        // Cancels current network request
        mRequestQueueManager.cancelPendingRequest(REQUEST_TAG_ARTICLE);

        if (!mActivity.isChangingConfigurations()) {
            mIsConfigurationsChanged = false;

            // We release Player as soon as possible when we do not need anymore
            mPlayer.release();
            mPlayer = null;
        } else {
            mIsConfigurationsChanged = true;
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Release other resources
        if (mArticleRV != null) {
            mArticleRV.removeAllViews();
            mArticleRV = null;
        }
        if (mArticleAdapter != null) {
            mArticleAdapter = null;
        }
    }

    /**
     * This for android devices uses version above or equal 23
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onAttachToContext(context);
    }

    /**
     * This for android devices uses version below 23
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttachToContext(activity);
        }
    }

    protected void onAttachToContext(Context context) {
        try {
            mFragmentListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /** Make a request to load an article by articleId */
    private void requestArticle() {
        // Build url based on articleId
        final String url = mAPIEndpointBuilder.setArticleId(mArticleId).build();

        // Make a request
        mRequestQueueManager.getJson(url, null, mJSONHandler, REQUEST_TAG_ARTICLE, this, this);
    }

    private void updateProgress() {
        // Estimate content position from last status time and elapsed time.
        int progress = 0;
        String timeInfo;
        if (mMediaItem != null) {
            mPlayer.getStatus(mMediaItem, false);
            long duration = mMediaItem.getDuration();
            if (duration > 0) {
                long position = mMediaItem.getPosition();
                long timeDelta = mPlayer.isPaused() ? 0 :
                        (SystemClock.elapsedRealtime() - mMediaItem.getTimestamp());
                progress = (int)(100.0 * (position + timeDelta) / duration);

                timeInfo = DateTimeUtils.toMinutes(position) + " / " + DateTimeUtils.toMinutes(duration);
                mTimeInfo.setText(timeInfo);
            }

        }
        mSeekBar.setProgress(progress);
    }

    private void updateUi() {
        // Show pause or resume icon depending on current state
        mPauseResumeButton.setImageResource(mPlayer.isPaused() ? R.drawable.ic_play : R.drawable.ic_pause);
    }

    private void setupMediaPlayer() {
        mPlayer = Player.create(mActivity);
        mPlayer.setCallback(VideoPlayerFragment.this);
        mListener = (OnConfigurationsChangedListener) mPlayer;
    }

    public void hidePlayerController() {
        if (mPlayerControllerLayout != null && mPlayerControllerLayout.getVisibility() == View.VISIBLE) {
            Animation hideAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.player_controller_leave_animation);
            mPlayerControllerLayout.startAnimation(hideAnimation);

            mPlayerControllerLayout.setVisibility(View.INVISIBLE);
        }
    }

    public void showPlayerController() {
        if (mPlayerControllerLayout != null && mPlayerControllerLayout.getVisibility() == View.INVISIBLE) {
            Animation showUpAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.player_controller_show_up_animation);
            mPlayerControllerLayout.startAnimation(showUpAnimation);

            mPlayerControllerLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setupRecyclerView(View view) {
        mArticleRV = (RecyclerView) view.findViewById(R.id.module_list);
        mArticleRV.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity,
                LinearLayoutManager.VERTICAL, false);
        mArticleRV.setLayoutManager(layoutManager);

        // Sets up recycler view adapter
        if (mMediaItem == null) {
            mModules = new ArrayList<>();
            mArticleAdapter = new ArticleAdapter(mActivity, mModules, Article.ARTICLE_TYPE_VIDEO, null);
            mArticleRV.setAdapter(mArticleAdapter);
        } else {
            mArticleRV.setAdapter(mArticleAdapter);
        }
    }

    @Override
    public void onError() {
        Toast.makeText(mActivity, R.string.can_not_play_video, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCompletion() {
        if (mPauseResumeButton != null) {
            mPauseResumeButton.setImageResource(R.drawable.ic_play);
        }
    }

    @Override
    public void onPlayerStateChanged(int oldState, int newState) {
        if (newState == Player.STATE_PLAYING || newState == Player.STATE_PREPARING_FOR_PLAY) {
            if (mPauseResumeButton != null) {
                mPauseResumeButton.setImageResource(R.drawable.ic_pause);
            }
        } else if (newState == Player.STATE_PAUSED || newState == Player.STATE_PREPARING_FOR_PAUSE) {
            if (mPauseResumeButton != null) {
                mPauseResumeButton.setImageResource(R.drawable.ic_play);
            }
        }
    }

    public interface OnConfigurationsChangedListener {
        void onNewSurfaceCreated(Context context);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        if (volleyError.networkResponse != null) {
            LOGD(TAG, "Can not load the article with articleId: " + mArticleId
                    + " from server. Status code: " + volleyError.networkResponse.statusCode
                    + ". NetworkTimeMs: " + volleyError.getNetworkTimeMs());
        } else {
            LOGD(TAG, "Can not load the article with articleId: " + mArticleId
                    + " from server. Network response is null");
        }

        if (!NetworkUtils.hasNetworkConnection(mActivity)) {
            LOGW(TAG, "Can not connect to internet. Network connection is not available.");
            Toast.makeText(mActivity, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
        }

        // Notify to parent activity that loading data was failed
        mFragmentListener.onRequestDataFailed();
    }

    @Override
    public void onResponse(Article article) {
        if (article == null) {
            // TODO notify to user
            return;
        }

        mArticleAdapter.setArticle(article);
        List<Module> modules = article.toModules();
        if (modules != null && modules.size() > 0) {
            mModules.addAll(modules);
            mArticleAdapter.notifyItemRangeInserted(0, modules.size());
        } else {
            mArticleAdapter.notifyItemInserted(0);
        }
        LOGI(TAG, "Requests the article '" + article.getTitle() + "' successfully");

        if (mMediaItem == null && article.getVideos() != null && article.getVideos().size() > 0) {
            mMediaItem = new MediaItem(article.getVideos().get(0));
            mPlayer.play(mMediaItem);
            mPlayer.resume();
        } else if (article.getVideos() == null || article.getVideos().size() == 0) {
            // When there are no attached videos in article. We will display error message to
            // user
            if (isPortrait) {
                // Since, we use WRAP_CONTENT for player view. When there are no videos to play,
                // it will fill entire screen, that is bad. So we set height for player view to
                // avoid that thing. We do not need to do this action in landscape. Because Player
                // fill full parent is default behavior of that mode.
                View view = mActivity.findViewById(R.id.player);
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = (int) getResources().getDimension(R.dimen.video_player_height_when_empty);
                view.setLayoutParams(layoutParams);
            }
            // Display message view
            mActivity.findViewById(R.id.player_message).setVisibility(View.VISIBLE);
        }

        // Notify to the parent activity
        mFragmentListener.onRequestDataSuccessfully();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onRequestDataFailed();
        void onRequestDataSuccessfully();
    }
}
