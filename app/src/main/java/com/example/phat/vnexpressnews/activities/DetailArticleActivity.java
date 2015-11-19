package com.example.phat.vnexpressnews.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.phat.vnexpressnews.Config;
import com.example.phat.vnexpressnews.R;
import com.example.phat.vnexpressnews.fragments.DetailArticleFragment;

public class DetailArticleActivity extends BaseActivity
        implements DetailArticleFragment.OnFragmentInteractionListener {

    /** Tags are used to pass/receive data to/from Intent/Bundle */
    public static final String TAG_ARTICLE_ID = Config.PACKAGE_NAME
            + ".activities.TAG_ARTICLE_ID";
    public static final String TAG_ARTICLE_TYPE = Config.PACKAGE_NAME
            + ".activities.TAG_ARTICLE_TYPE";
    public static final String TAG_ARTICLE_THUMBNAIL_URL = Config.PACKAGE_NAME
            + ".activities.TAG_ARTICLE_THUMBNAIL_URL";

    /** The fragment which is attached to this activity */
    private DetailArticleFragment mDetailArticleFragment;

    /** Current article data */
    private int mArticleId;
    private int mArticleType;
    private String mArticleThumbnailUrl;

    private AppBarLayout mAppBarLayout;
    private ContentLoadingProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_article);

        // This activity doesn't support NavDrawer
        shouldSupportNavDrawer(false);

        if (savedInstanceState == null) {
            // If this is first time the activity launch, get data from Intent
            Intent intent = getIntent();
            mArticleId = intent.getIntExtra(TAG_ARTICLE_ID, -1);
            mArticleType = intent.getIntExtra(TAG_ARTICLE_TYPE, -1);
            mArticleThumbnailUrl = intent.getStringExtra(TAG_ARTICLE_THUMBNAIL_URL);

            // and, attach fragment
            attachDetailArticleFragment();
        } else {
            // If the activity is restored, get data from Bundle and do not need attach fragment again
            mArticleId = savedInstanceState.getInt(TAG_ARTICLE_ID);
            mArticleType = savedInstanceState.getInt(TAG_ARTICLE_TYPE);
            mArticleThumbnailUrl = savedInstanceState.getString(TAG_ARTICLE_THUMBNAIL_URL);
        }

        // Sets header photo
        ImageView headerPhoto = (ImageView) findViewById(R.id.article_thumbnail);
        mImageLoader.loadImage(mArticleThumbnailUrl, headerPhoto, false);

        // Sets up necessary stubs.
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setTitleEnabled(false);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        mProgressBar = (ContentLoadingProgressBar) findViewById(R.id.progress_bar);

        // Sets up FloatActionButton and its Click Event
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start CommentActivity
                Intent intent = new Intent(DetailArticleActivity.this, CommentActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TAG_ARTICLE_ID, mArticleId);
        outState.putInt(TAG_ARTICLE_TYPE, mArticleType);
        outState.putString(TAG_ARTICLE_THUMBNAIL_URL, mArticleThumbnailUrl);
    }

    /** Because this activity doesn't support Navigation Drawer, so we do not implement this method */
    @Override
    protected void onCategoryItemSelected(int categoryId) {}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:     // Handle UP button
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    /**
     * Attach {@link DetailArticleFragment} into this activity
     */
    private void attachDetailArticleFragment() {
        mDetailArticleFragment = DetailArticleFragment.newInstance(mArticleId, mArticleType);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.detail_article_fragment_placeholder, mDetailArticleFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    private void expandAppBarLayout() {
        mAppBarLayout.setExpanded(true, true);
    }

    @Override
    public void onRequestDataFailed() {
        // Stop progress bar
        hideProgressBar();

        // Notify to user
        Toast.makeText(DetailArticleActivity.this, R.string.load_data_failed, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestDataSuccessfully() {
        // Hide progress bar
        hideProgressBar();
    }

    private void hideProgressBar() {
        mProgressBar.hide();
    }

    /**
     * Expands {@link AppBarLayout} when {@link RecyclerView} hits top
     */
    @Override
    public void onRecyclerViewHitTop() {
        expandAppBarLayout();
    }
}
