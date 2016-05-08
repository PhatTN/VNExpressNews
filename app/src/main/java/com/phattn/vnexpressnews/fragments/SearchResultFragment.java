package com.phattn.vnexpressnews.fragments;

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
import com.phattn.vnexpressnews.Config;
import com.phattn.vnexpressnews.R;
import com.phattn.vnexpressnews.activities.eventhandler.OnBriefArticleClickedHandler;
import com.phattn.vnexpressnews.adapters.BriefArticleAdapter;
import com.phattn.vnexpressnews.io.BriefArticlesHandler;
import com.phattn.vnexpressnews.io.BriefArticlesHandler.HandlerType;
import com.phattn.vnexpressnews.io.JSONHandler;
import com.phattn.vnexpressnews.io.RequestQueueManager;
import com.phattn.vnexpressnews.model.BriefArticle;
import com.phattn.vnexpressnews.util.ItemClickSupport;
import com.phattn.vnexpressnews.util.NetworkUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.phattn.vnexpressnews.util.LogUtils.LOGD;
import static com.phattn.vnexpressnews.util.LogUtils.LOGI;
import static com.phattn.vnexpressnews.util.LogUtils.LOGW;
import static com.phattn.vnexpressnews.util.LogUtils.makeLogTag;

/**
 * A placeholder fragment containing a simple view.
 */
public class SearchResultFragment extends Fragment
        implements Response.Listener<Set<BriefArticle>>, Response.ErrorListener {

    private static final String TAG = makeLogTag(SearchResultFragment.class);

    /** Constrains indicate when use Volley RequestQueue */
    private static final String REQUEST_TAG_SEARCH_ARTICLES =
            Config.PACKAGE_NAME + ".fragments.REQUEST_TAG_SEARCH_ARTICLES";

    /** Constrains for passing or receiving arguments when creates this fragments */
    private static final String ARG_API_ENDPOINT = Config.PACKAGE_NAME
            + ".fragments.ARG_API_ENDPOINT";

    private Context mContext;

    /** The URL for searching articles */
    private String mAPIEndpoint;

    /** Requesting and communicating with network */
    private RequestQueueManager mRequestQueueManager;

    /** Populates data to views */
    private RecyclerView mRecyclerView;
    private BriefArticleAdapter mBriefArticleAdapter;

    /** Data */
    private List<BriefArticle> mResultArticleList;

    /** Handles deserialization JSON to specify model */
    private JSONHandler<Set<BriefArticle>> mJSONHandler;

    /** Uses this listener to interact with the activity */
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters
     *
     * @param endpoint The URL for searching articles
     * @return A new instance of fragment {@code SearchResultFragment}
     */
    public static SearchResultFragment newInstance(String endpoint) {
        SearchResultFragment fragment = new SearchResultFragment();

        Bundle args = new Bundle();
        args.putString(ARG_API_ENDPOINT, endpoint);
        fragment.setArguments(args);

        return fragment;
    }

    public SearchResultFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mAPIEndpoint = getArguments().getString(ARG_API_ENDPOINT);
        }

        // Initialization necessary stubs.
        mContext = getActivityContext();
        mRequestQueueManager = RequestQueueManager.getInstance(mContext);
        mJSONHandler = new BriefArticlesHandler(HandlerType.SEARCHING_NEWS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_result, container, false);

        setupRecyclerView(view);

        // Start searching articles
        makeSearching();

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
    public void onErrorResponse(VolleyError volleyError) {
        if (volleyError.networkResponse != null) {
            LOGD(TAG, "Can not search articles from server. " +
                    "Status code: " + volleyError.networkResponse.statusCode +
                    ". NetworkTimeMs: " + volleyError.getNetworkTimeMs());
        } else {
            LOGD(TAG, "Can not search articles from server. Network response is null.");
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
    public void onResponse(Set<BriefArticle> resultList) {
        if (resultList == null || resultList.isEmpty()) {
            // TODO notify to user
            return;
        }

        if (!mResultArticleList.isEmpty()) {
            int size = mResultArticleList.size();
            mResultArticleList.clear();
            mBriefArticleAdapter.notifyItemRangeRemoved(0, size);
        }

        mResultArticleList.addAll(resultList);
        mBriefArticleAdapter.notifyItemRangeInserted(0, mResultArticleList.size());
        mRecyclerView.smoothScrollToPosition(0);

        // notify to the activity
        mListener.onRequestDataSuccessfully();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mRequestQueueManager != null) {
            mRequestQueueManager.cancelPendingRequest(REQUEST_TAG_SEARCH_ARTICLES);
        }
    }

    private void makeSearching() {
        if (mAPIEndpoint == null) {
            // TODO Notify to user that can not load data
            return;
        }

        mRequestQueueManager.getJson(mAPIEndpoint, null, mJSONHandler,
                REQUEST_TAG_SEARCH_ARTICLES, this, this);
        LOGI(TAG, "Start searching articles with endpoint: " + mAPIEndpoint);
    }

    /**
     * This method must be called whenever user changes the keyword. The activity will builds
     * new endpoint with that new keyword and passes new endpoint to this method when calls it.
     * This method will makes a request to VNExpress Server based on new passed endpoint.
     */
    public void onKeywordChanged(String newEndpoint) {
        mAPIEndpoint = newEndpoint;

        // Cancel current request if any
        mRequestQueueManager.cancelPendingRequest(REQUEST_TAG_SEARCH_ARTICLES);

        // Make a searching
        makeSearching();
    }

    private Context getActivityContext() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return getActivity();
        } else {
            return getContext();
        }
    }

    private void setupRecyclerView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.result_article_list);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(
                Config.DEFAULT_SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        mRecyclerView.setLayoutManager(layoutManager);

        // Sets up recycler view adapter
        mResultArticleList = new ArrayList<>();
        mBriefArticleAdapter = new BriefArticleAdapter(mContext, mResultArticleList);
        mRecyclerView.setAdapter(mBriefArticleAdapter);

        // Adds item clicked event to recycler view
        OnBriefArticleClickedHandler handler = new OnBriefArticleClickedHandler(getActivity());
        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(handler);
    }

    public interface OnFragmentInteractionListener {
        void onRequestDataFailed();
        void onRequestDataSuccessfully();
    }
}
