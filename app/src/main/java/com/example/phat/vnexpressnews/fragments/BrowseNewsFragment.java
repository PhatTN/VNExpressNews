package com.example.phat.vnexpressnews.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.phat.vnexpressnews.Config;
import com.example.phat.vnexpressnews.R;
import com.example.phat.vnexpressnews.activities.eventhandler.OnBriefArticleClickedHandler;
import com.example.phat.vnexpressnews.adapters.BriefArticleAdapter;
import com.example.phat.vnexpressnews.io.BriefArticlesHandler;
import com.example.phat.vnexpressnews.io.BriefArticlesHandler.HandlerType;
import com.example.phat.vnexpressnews.io.JSONHandler;
import com.example.phat.vnexpressnews.io.RequestQueueManager;
import com.example.phat.vnexpressnews.model.BriefArticle;
import com.example.phat.vnexpressnews.util.APIEndpointBuilder.APIEndpointType;
import com.example.phat.vnexpressnews.util.ItemClickSupport;
import com.example.phat.vnexpressnews.util.NetworkUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.example.phat.vnexpressnews.util.LogUtils.LOGD;
import static com.example.phat.vnexpressnews.util.LogUtils.LOGI;
import static com.example.phat.vnexpressnews.util.LogUtils.LOGW;
import static com.example.phat.vnexpressnews.util.LogUtils.makeLogTag;

/**
 * This fragment makes a network request to get {@link List<BriefArticle>}, then will displays
 * them using {@link RecyclerView}
 *
 * Activities that contain this fragment must implement the
 * {@link BrowseNewsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BrowseNewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BrowseNewsFragment extends Fragment
        implements Response.Listener<Set<BriefArticle>>, Response.ErrorListener {
    private static final String TAG = makeLogTag(BrowseNewsFragment.class);

    /** Constrains indicate when use Volley RequestQueue */
    private static final String REQUEST_TAG_BRIEF_ARTICLE =
            Config.PACKAGE_NAME + ".fragments.REQUEST_TAG_BRIEF_ARTICLE";

    /** Constrains for passing or receiving arguments when creates this fragment */
    private static final String API_ENDPOINT_PARAM = Config.PACKAGE_NAME
            + ".fragments.API_ENDPOINT_PARAM";
    private static final String API_ENDPOINT_TYPE_PARAM = Config.PACKAGE_NAME
            +".fragment.API_ENDPOINT_TYPE_PARAM";

    /** The URL for requesting news for this fragment */
    private String mAPIEndpoint;

    /** Requesting and communication with network */
    private RequestQueueManager mRequestQueueManager;

    /** Populates data to views */
    private RecyclerView mBriefArticleRV;
    private BriefArticleAdapter mBriefArticleAdapter;

    /** Data */
    private BriefArticle mTopBriefArticle;
    private List<BriefArticle> mBriefArticleList;

    /** Handles deserialization JSON to specify model */
    private JSONHandler<Set<BriefArticle>> mJSONHandler;
    private HandlerType mCurrentHandlerType;

    /** Uses this listener this interact with parent Activity */
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param endpoint The URL for requesting news for this fragment.
     * @param apiEndpointType  The APIEndpointType was used to build {@code endpoint}
     *                         which is passed in first argument.
     * @return A new instance of fragment BrowseNewsFragment.
     */
    public static BrowseNewsFragment newInstance(String endpoint, APIEndpointType apiEndpointType) {
        BrowseNewsFragment fragment = new BrowseNewsFragment();

        Bundle args = new Bundle();
        args.putString(API_ENDPOINT_PARAM, endpoint);
        args.putSerializable(API_ENDPOINT_TYPE_PARAM, apiEndpointType);
        fragment.setArguments(args);
        return fragment;
    }

    public BrowseNewsFragment() {
        // Required default empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Construct a RequestQueueManager for handle all network requesting in this fragment
        mRequestQueueManager = RequestQueueManager.getInstance(getActivity());

        if (getArguments() != null) {
            mAPIEndpoint = getArguments().getString(API_ENDPOINT_PARAM);
            mCurrentHandlerType = detectHandlerType(
                    (APIEndpointType)getArguments().getSerializable(API_ENDPOINT_TYPE_PARAM));
        }
        mJSONHandler = new BriefArticlesHandler(mCurrentHandlerType);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_browse_news, container, false);

        // Sets up recycler view for brief article list
        setupRecyclerView(view);

        // Detects when the recycler view hits top
        mBriefArticleRV.addOnScrollListener(new RecyclerView.OnScrollListener() {

            int mCurrentState;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                mCurrentState = newState;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!recyclerView.canScrollVertically(-1) &&
                        mCurrentState != RecyclerView.SCROLL_STATE_DRAGGING) {
                    // When RecyclerView hit top, notify to parent activity
                    mListener.onRecyclerViewHitTop();
                }
            }
        });

        // Start load brief article data
        loadBriefArticle();

        return view;
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
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mRequestQueueManager.cancelPendingRequest(REQUEST_TAG_BRIEF_ARTICLE);
    }

    /** Loads brief article data from VNExpress Server */
    private void loadBriefArticle() {
        if (mAPIEndpoint == null) {
            //TODO Notify to user that can not load data
            return;
        }

        mRequestQueueManager.getJson(mAPIEndpoint, null, mJSONHandler, REQUEST_TAG_BRIEF_ARTICLE, this, this);
        LOGI(TAG, "Start request new brief articles at: " + mAPIEndpoint);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        if (volleyError.networkResponse != null) {
            LOGD(TAG, "Can not load brief articles from server. " +
                    "Status code: " + volleyError.networkResponse.statusCode +
                    ". NetworkTimeMs: " + volleyError.getNetworkTimeMs());
        } else {
            LOGD(TAG, "Can not load brief articles from server. Network response is null.");
        }

        if (!NetworkUtils.hasNetworkConnection(getActivity())) {
            LOGW(TAG, "Can not connect to internet. Network connection is not available.");
            Toast.makeText(getActivity(), R.string.no_network_connection, Toast.LENGTH_SHORT).show();
        }

        // Notify to parent activity
        mListener.onRequestDataFailed();
        // TODO allow user refresh when can not load data
    }

    @Override
    public void onResponse(Set<BriefArticle> briefArticles) {
        if (briefArticles == null || briefArticles.isEmpty()) {
            // TODO notify to user
            return;
        }

        // First brief article will display in special place. Not in recycler view
        mTopBriefArticle = briefArticles.iterator().next();
        briefArticles.remove(mTopBriefArticle);
        mListener.onMainArticleChanged(mTopBriefArticle);

        if (!mBriefArticleList.isEmpty()) {
            int size = mBriefArticleList.size();
            mBriefArticleList.clear();
            mBriefArticleAdapter.notifyItemRangeRemoved(0, size);
        }

        mBriefArticleList.addAll(briefArticles);
        mBriefArticleAdapter.notifyItemRangeInserted(0, mBriefArticleList.size());
        mBriefArticleRV.smoothScrollToPosition(0);
    }

    /**
     * Calls this method whenever needs to request new data from network. It will sets up almost thing to
     * make sure this fragment work correctly. This method is usually called by the activity which this fragment
     * attach to.
     *
     * @param url The url where we use to query new data.
     * @param apiEndpointType  used to detect which {@link HandlerType} will be used.
     */
    public void process(String url, APIEndpointType apiEndpointType) {
        mAPIEndpoint = url;

        // Cancel current request
        mRequestQueueManager.cancelPendingRequest(REQUEST_TAG_BRIEF_ARTICLE);

        mCurrentHandlerType = detectHandlerType(apiEndpointType);
        ((BriefArticlesHandler)mJSONHandler).setHandlerType(mCurrentHandlerType);

        // Start new request
        loadBriefArticle();
    }

    /**
     * Detect a {@link HandlerType} based on passed {@link APIEndpointType}. This {@code HandlerType}
     * is used to pass to constructor when creates {@link BriefArticlesHandler}.
     *
     * @return A appropriate {@link HandlerType} with passed {@link APIEndpointType}
     */
    private HandlerType detectHandlerType(APIEndpointType apiEndpointType) {

        if (apiEndpointType == APIEndpointType.API_TOP_NEWS) {
            return HandlerType.TOP_NEWS;
        } else if (apiEndpointType == APIEndpointType.API_NEWS_WITH_CATEGORY) {
            return HandlerType.NEWS_WITH_CATEGORY;
        } else {
            throw new IllegalArgumentException("Inappropriate APIEndpointType");
        }
    }

    private void setupRecyclerView(View view) {
        mBriefArticleRV = (RecyclerView) view.findViewById(R.id.brief_article_list);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(Config.DEFAULT_SPAN_COUNT,
                StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        mBriefArticleRV.setLayoutManager(layoutManager);

        // Sets up recycler view adapter
        mBriefArticleList = new ArrayList<>();
        mBriefArticleAdapter = new BriefArticleAdapter(getActivity(), mBriefArticleList);
        mBriefArticleRV.setAdapter(mBriefArticleAdapter);

        // Adds item clicked event to recycler view
        OnBriefArticleClickedHandler handler = new OnBriefArticleClickedHandler(getActivity());
        ItemClickSupport.addTo(mBriefArticleRV).setOnItemClickListener(handler);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onMainArticleChanged(BriefArticle briefArticle);
        void onRecyclerViewHitTop();
        void onRequestDataFailed();
    }

}
