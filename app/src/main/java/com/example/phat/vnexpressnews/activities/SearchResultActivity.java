package com.example.phat.vnexpressnews.activities;

import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.phat.vnexpressnews.Config;
import com.example.phat.vnexpressnews.R;
import com.example.phat.vnexpressnews.fragments.SearchResultFragment;
import com.example.phat.vnexpressnews.util.APIEndpointBuilder;
import com.example.phat.vnexpressnews.util.APIEndpointBuilder.APIEndpointType;

import static com.example.phat.vnexpressnews.util.LogUtils.LOGD;
import static com.example.phat.vnexpressnews.util.LogUtils.makeLogTag;

public class SearchResultActivity extends BaseActivity
        implements SearchResultFragment.OnFragmentInteractionListener {

    private static final String TAG = makeLogTag(SearchResultActivity.class);

    /** Constrains for saving or restoring arguments when creates this activity */
    private static final String ARG_CURRENT_KEYWORD = Config.PACKAGE_NAME
            + ".activities.ARG_CURRENT_KEYWORD";

    /** The fragment which is attached to this activity */
    private SearchResultFragment mSearchResultFragment;

    /** API Endpoint Builder, builds searching URL form entered query */
    private APIEndpointBuilder mAPIEndpointBuilder;

    private ContentLoadingProgressBar mProgressBar;
    private SearchView mSearchView;

    /** Current keyword that user wants to searching about */
    private String mCurrentKeyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        // Specify this activity doesn't support navigation drawer
        shouldSupportNavDrawer(false);

        if (savedInstanceState != null) {
            mCurrentKeyword = savedInstanceState.getString(ARG_CURRENT_KEYWORD);
            setupAPIEndpointBuilder();
        } else {
            handleIntent(getIntent());
            attachSearchResultFragment(mAPIEndpointBuilder.build());
        }

        setToolbarTitle(mCurrentKeyword);

        mProgressBar = (ContentLoadingProgressBar) findViewById(R.id.progress_bar);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleIntent(intent);
        showProgressBar();

        // Notify to the fragment that keyword changed
        mSearchResultFragment.onKeywordChanged(mAPIEndpointBuilder.build());

        // Clear focus of SearchView
        mSearchView.clearFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_searching, menu);

        setupSearchView(menu.findItem(R.id.search));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_CURRENT_KEYWORD, mCurrentKeyword);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mCurrentKeyword = intent.getStringExtra(SearchManager.QUERY);
            setupAPIEndpointBuilder();
            LOGD(TAG, "Makes a searching with keyword: " + mCurrentKeyword);
        }
    }

    /**
     * Attachs {@link SearchResultFragment} into this activity
     */
    private void attachSearchResultFragment(String url) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        mSearchResultFragment = SearchResultFragment.newInstance(url);
        ft.replace(R.id.searching_fragment_placeholder, mSearchResultFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    public void onRequestDataFailed() {
        // Hide progress bar
        hideProgressBar();

        // Notify to user
        Toast.makeText(SearchResultActivity.this, R.string.load_data_failed, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestDataSuccessfully() {
        hideProgressBar();
    }

    private void hideProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.hide();
        }
    }

    private void showProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.show();
        }
    }

    private void setupAPIEndpointBuilder() {
        if (mAPIEndpointBuilder == null) {
            mAPIEndpointBuilder = new APIEndpointBuilder(APIEndpointType.API_SEARCH_NEWS);
        }

        mAPIEndpointBuilder.setSearchKeyword(mCurrentKeyword);
    }

    private void setupSearchView(MenuItem searchItem) {
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(this, SearchResultActivity.class)));
    }
}
