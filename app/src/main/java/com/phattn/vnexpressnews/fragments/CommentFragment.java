package com.phattn.vnexpressnews.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.phattn.vnexpressnews.Config;
import com.phattn.vnexpressnews.R;

import static com.phattn.vnexpressnews.util.LogUtils.makeLogTag;

/**
 * This fragment just contains a webview and the webview will displays comments
 */
public class CommentFragment extends Fragment {
    
    private static final String TAG = makeLogTag(CommentFragment.class);

    /** The fragment initialization parameters */
    private static final String ARG_URL = Config.PACKAGE_NAME + ".fragments.ARG_URL";

    /** Parent activity */
    private Activity mActivity;

    /** The URL that is used for webview load comments */
    private String mEndpoint;

    private WebView mWebView;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided paratemters
     *
     * @return A new instance of fragment {@code CommentFragment}
     */
    public static CommentFragment newInstance(String url) {
        CommentFragment fragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    public CommentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getActivity();

        if (getArguments() != null) {
            mEndpoint = getArguments().getString(ARG_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_comment, container, false);

        setupWebView(rootView);

        return rootView;
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
    public void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
    }

    /**
     * This method sets up a WebView. Because for reduce memory consumption, we use {@code ApplicationContext}
     * to create a WebView. Using this way can avoids memory consumption issue in WebView, but it has some
     * problems with playing video or displaying a dialog in WebView. Because the comments page doesn't
     * have these features. So, we're safe when using this way.
     */
    private void setupWebView(View view) {
        // Creates a WebView with ApplicationContext
        mWebView = new WebView(mActivity.getApplicationContext());

        // Add it to GroupView
        LinearLayout webViewWrapper = (LinearLayout) view.findViewById(R.id.comment_webview_wrapper);
        webViewWrapper.addView(mWebView);

        // Because the page (we going to load) use javascript, so we enable JavaScript support
        mWebView.getSettings().setJavaScriptEnabled(true);
        // Set WebViewClient to our own WebViewClient implementation.
        mWebView.setWebViewClient(new CommentsWebViewClient() {
            @Override
            public void onPageCompletelyFinished() {
                // Notify the activity when webview loading finished
                mListener.onWebViewLoadingFinished();
            }
        });
        // Start loading web page
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl(mEndpoint);
            }
        });
    }

    /**
     * This WebViewClient finds out when WebView loading really finished. When the page has
     * {@code iframe} component in it. The default
     * {@link WebViewClient#onPageFinished(WebView, String)} will triggers when each {@code iframe}
     * loading finished. In some cases, we want to know when the page loading completely finished, includes
     * inner {@code iframe} component. So, this WebViewClients purpose for that wish.
     */
    private abstract class CommentsWebViewClient extends WebViewClient {

        boolean loadingFinished = true;
        boolean redirect = false;

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!loadingFinished) {
                redirect = true;
            }

            loadingFinished = false;
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            loadingFinished = false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (!redirect) {
                loadingFinished = true;
            }

            if (loadingFinished && !redirect) {
                onPageCompletelyFinished();
            } else {
                redirect = false;
            }
        }

        /**
         * Implements this method to handle whenever the page loading completely finished
         */
        public abstract void onPageCompletelyFinished();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onWebViewLoadingFinished();
    }

}
