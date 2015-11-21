package com.example.phat.vnexpressnews.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;

import com.example.phat.vnexpressnews.Config;
import com.example.phat.vnexpressnews.R;
import com.example.phat.vnexpressnews.fragments.CommentFragment;
import com.example.phat.vnexpressnews.util.APIEndpointBuilder;

public class CommentActivity extends BaseActivity
        implements CommentFragment.OnFragmentInteractionListener {

    /** Tags are used to pass/receive data to/from Intent/Bundle */
    public static final String TAG_ARTICLE_ID = Config.PACKAGE_NAME
            + ".activities.TAG_ARTICLE_ID";
    public static final String TAG_CATEGORY_ID = Config.PACKAGE_NAME
            + ".activities.TAG_CATEGORY_ID";
    public static final String TAG_ARTICLE_TITLE = Config.PACKAGE_NAME
            + ".activities.TAG_ARTICLE_TITLE";

    /** The fragment which is attached to this activity */
    private CommentFragment mCommentFragment;

    /** Data */
    private int mArticleId;
    private int mCategoryId;
    private String mArticleTitle;

    private ContentLoadingProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // Specify this activity doesn't support navigation drawer
        shouldSupportNavDrawer(false);

        // Sets title for toolbar
        setToolbarTitle(R.string.toolbar_title_comments);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            mArticleId = intent.getIntExtra(TAG_ARTICLE_ID, 0);
            mCategoryId = intent.getIntExtra(TAG_CATEGORY_ID, 0);
            mArticleTitle = intent.getStringExtra(TAG_ARTICLE_TITLE);
        } else {
            mArticleId = savedInstanceState.getInt(TAG_ARTICLE_ID);
            mCategoryId = savedInstanceState.getInt(TAG_CATEGORY_ID);
            mArticleTitle = savedInstanceState.getString(TAG_ARTICLE_TITLE);
        }

        String url = buildEndpoint();
        attachCommentFragment(url);

        mProgressBar = (ContentLoadingProgressBar) findViewById(R.id.progress_bar);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TAG_ARTICLE_ID, mArticleId);
        outState.putInt(TAG_CATEGORY_ID, mCategoryId);
        outState.putString(TAG_ARTICLE_TITLE, mArticleTitle);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    private void attachCommentFragment(String url) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        mCommentFragment = CommentFragment.newInstance(url);
        ft.replace(R.id.comment_fragment_placeholder, mCommentFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    private String buildEndpoint() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_GET_COMMENTS);
        builder.setArticleId(mArticleId);
        builder.setCategoryId(mCategoryId);
        builder.setArticleTitle(mArticleTitle);
        return builder.build();
    }

    @Override
    public void onWebViewLoadingFinished() {
        // When WebView loading finished, we hide progress bar
        mProgressBar.hide();
    }
}
