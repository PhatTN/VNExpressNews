package com.example.phat.vnexpressnews.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.phat.vnexpressnews.Config;
import com.example.phat.vnexpressnews.R;
import com.example.phat.vnexpressnews.adapters.ArticleAdapter;
import com.example.phat.vnexpressnews.io.FullArticleHandler;
import com.example.phat.vnexpressnews.io.JSONHandler;
import com.example.phat.vnexpressnews.io.RequestQueueManager;
import com.example.phat.vnexpressnews.model.Article;
import com.example.phat.vnexpressnews.model.Module;
import com.example.phat.vnexpressnews.util.APIEndpointBuilder;
import com.example.phat.vnexpressnews.util.APIEndpointBuilder.APIEndpointType;
import com.example.phat.vnexpressnews.util.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import static com.example.phat.vnexpressnews.util.LogUtils.LOGD;
import static com.example.phat.vnexpressnews.util.LogUtils.LOGI;
import static com.example.phat.vnexpressnews.util.LogUtils.LOGW;
import static com.example.phat.vnexpressnews.util.LogUtils.makeLogTag;

/**
 * This fragment makes network request a {@link Article} by {@code ArticleId}. Then, it parses
 * {@link Article#content} into List of appropriate {@link Module} and will displays these {@code Module}s
 * using {@link RecyclerView}
 *
 * Activities that contain this fragment must implement the
 * {@link DetailArticleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailArticleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailArticleFragment extends Fragment
        implements Response.Listener<Article>, Response.ErrorListener {
    private static final String TAG = makeLogTag(DetailArticleFragment.class);

    /** The fragment initialization parameters */
    private static final String ARG_ARTICLE_ID = Config.PACKAGE_NAME + ".fragments.ARTICLE_ID";
    private static final String ARG_ARTICLE_TYPE = Config.PACKAGE_NAME + ".fragments.ARTICLE_TYPE";

    /** Constrains indicate when use Volley RequestQueue */
    private static final String REQUEST_TAG_ARTICLE =
            Config.PACKAGE_NAME + ".fragments.REQUEST_TAG_ARTICLE";

    /** API Endpoint Builder, builds appropriate URL based on request from user */
    private APIEndpointBuilder mAPIEndpointBuilder;

    /** Parent activity context */
    private Context mContext;

    /** The adapter which will populates data into views */
    private ArticleAdapter mArticleAdapter;

    /** Data */
    private int mArticleId;
    private int mArticleType;
    private List<Module> mModules;

    /** Requesting and communication with network */
    private RequestQueueManager mRequestQueueManager;

    /** Handles deserialization JSON to specify model */
    private JSONHandler<Article> mJSONHandler;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DetailArticleFragment.
     */
    public static DetailArticleFragment newInstance(int articleId, int articleType) {
        DetailArticleFragment fragment = new DetailArticleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ARTICLE_ID, articleId);
        args.putInt(ARG_ARTICLE_TYPE, articleType);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailArticleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mContext = getActivity();
        } else {
            mContext = getContext();
        }

        // Sets up necessary stubs.
        mAPIEndpointBuilder = new APIEndpointBuilder(APIEndpointType.API_DETAIL_FROM_ARTICLE);
        mJSONHandler = new FullArticleHandler();
        mRequestQueueManager = RequestQueueManager.getInstance(mContext);

        if (getArguments() != null) {
            mArticleId = getArguments().getInt(ARG_ARTICLE_ID);
            mArticleType = getArguments().getInt(ARG_ARTICLE_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_article, container, false);

        // Sets up recycler view
        setupRecyclerView(view);

        // Start request an article
        requestArticle();

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
    public void onStop() {
        super.onStop();
        // Cancels current network request
        mRequestQueueManager.cancelPendingRequest(REQUEST_TAG_ARTICLE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

        if (!NetworkUtils.hasNetworkConnection(mContext)) {
            LOGW(TAG, "Can not connect to internet. Network connection is not available.");
            Toast.makeText(mContext, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
        }

        // Notify to parent activity that loading data was failed
        mListener.onRequestDataFailed();
    }

    @Override
    public void onResponse(Article article) {
        if (article == null) {
            // TODO notify to user
            return;
        }

        mArticleAdapter.setArticle(article);
        List<Module> modules = article.toModules();
        if (modules != null) {
            mModules.addAll(modules);
            mArticleAdapter.notifyItemRangeInserted(0, modules.size());
        } else {
            mArticleAdapter.notifyItemInserted(0);
        }
        LOGI(TAG, "Requests the article " + article.getTitle() + " successfully");

        // Notify to the parent activity
        mListener.onRequestDataSuccessfully();
    }

    /** Make a request to load an article by articleId */
    private void requestArticle() {
        // Build url based on articleId
        final String url = mAPIEndpointBuilder.setArticleId(mArticleId).build();

        // Make a request
        mRequestQueueManager.getJson(url, null, mJSONHandler, REQUEST_TAG_ARTICLE, this, this);
        LOGI(TAG, "Start request an article at: " + url);
    }

    private void setupRecyclerView(View view) {
        RecyclerView mArticleRV = (RecyclerView) view.findViewById(R.id.module_list);
        mArticleRV.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);
        mArticleRV.setLayoutManager(layoutManager);

        // Detects when the recycler view hits top
        mArticleRV.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private int mCurrentState;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                mCurrentState = newState;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (lm.findFirstCompletelyVisibleItemPosition() == 0
                        && mCurrentState != RecyclerView.SCROLL_STATE_DRAGGING) {
                    mListener.onRecyclerViewHitTop();
                }
            }
        });

        //Sets up recycler view adapter
        mModules = new ArrayList<>();
        mArticleAdapter = new ArticleAdapter(mContext, mModules, mArticleType, null);
        mArticleRV.setAdapter(mArticleAdapter);
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
        void onRecyclerViewHitTop();
    }

}
