package com.example.phat.vnexpressnews.activities;

import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phat.vnexpressnews.Config;
import com.example.phat.vnexpressnews.R;
import com.example.phat.vnexpressnews.fragments.BrowseNewsFragment;
import com.example.phat.vnexpressnews.fragments.BrowseNewsFragment.OnFragmentInteractionListener;
import com.example.phat.vnexpressnews.model.BriefArticle;
import com.example.phat.vnexpressnews.util.APIEndpointBuilder;
import com.example.phat.vnexpressnews.util.APIEndpointBuilder.APIEndpointType;

public class BrowseNewsActivity extends BaseActivity
        implements OnFragmentInteractionListener {

    /** The fragment which is attached to this activity */
    private BrowseNewsFragment mBrowseNewsFragment;

    /** API Endpoint Builder, builds appropriate URL based on request from user */
    private APIEndpointBuilder mAPIEndpointBuilder;

    private AppBarLayout mAppBarLayout;
    private ContentLoadingProgressBar mProgressBar;

    /** The category is being chosen */
    private int mCurrentCategoryId = Config.DEFAULT_CATEGORY_ID_TOP_NEWS;

    /** Constrains for saving or restoring arguments when creates this activity */
    private static final String CURRENT_SELECTED_CATEGORY_ID = Config.PACKAGE_NAME
            + ".activities.CURRENT_SELECTED_CATEGORY_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_news);

        // Specify this activity supports a navigation drawer
        shouldSupportNavDrawer(true);

        if (savedInstanceState != null) {
            mCurrentCategoryId = savedInstanceState.getInt(CURRENT_SELECTED_CATEGORY_ID);
        }

        setupAPIEndpointBuilder();

        if (savedInstanceState == null) {
            attachBrowseNewsFragment(mAPIEndpointBuilder.build());
        }

        CollapsingToolbarLayout mCollapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        mCollapsingToolbar.setTitleEnabled(false);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        mProgressBar = (ContentLoadingProgressBar) findViewById(R.id.progress_bar);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setSelectedNavItem(mCurrentCategoryId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.browse_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCategoryItemSelected(int categoryId) {
        if (categoryId == mCurrentCategoryId) {
            return;
        }
        mCurrentCategoryId = categoryId;

        String url;
        if (categoryId == Config.DEFAULT_CATEGORY_ID_TOP_NEWS) {
            url = mAPIEndpointBuilder
                    .setAPIEndpointType(APIEndpointType.API_TOP_NEWS)
                    .setCategoryList(mCategoryList)
                    .build();
        } else {
            url = mAPIEndpointBuilder
                    .setAPIEndpointType(APIEndpointType.API_NEWS_WITH_CATEGORY)
                    .setCategoryId(mCurrentCategoryId)
                    .build();
        }

        if (mBrowseNewsFragment == null) {
            attachBrowseNewsFragment(url);
            return;
        }

        mProgressBar.show();
        mBrowseNewsFragment.process(url, mAPIEndpointBuilder.getAPIEndpointType());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_SELECTED_CATEGORY_ID, mCurrentCategoryId);
    }

    @Override
    public void onMainArticleChanged(BriefArticle briefArticle) {
        // Bind new top brief article to views
        TextView title = (TextView) findViewById(R.id.brief_article_title);
        title.setText(briefArticle.getTitle());
        ImageView thumbnail = (ImageView) findViewById(R.id.brief_article_thumbnail);
        mImageLoader.loadImage(briefArticle.getThumbnailUrl(), thumbnail, false);

        expandAppBarLayout();

        // Hides LoadingContentProgressBar when loading data finished.
        mProgressBar.hide();
    }

    @Override
    public void onRecyclerViewHitTop() {
        expandAppBarLayout();
    }

    @Override
    public void onRequestDataFailed() {
        // Stop progress bar
        mProgressBar.hide();

        // Notify to user
        Toast.makeText(BrowseNewsActivity.this, R.string.load_data_failed, Toast.LENGTH_LONG).show();
    }

    private void expandAppBarLayout() {
        mAppBarLayout.setExpanded(true, true);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    /**
     *  Attach {@link BrowseNewsFragment} into this activity
     */
    private void attachBrowseNewsFragment(String url) {
        mBrowseNewsFragment = BrowseNewsFragment.newInstance(url, mAPIEndpointBuilder.getAPIEndpointType());
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.browser_news_fragment_placeholder, mBrowseNewsFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    private void setupAPIEndpointBuilder() {
        if (mAPIEndpointBuilder == null) {
            // Uses APIEndpointType.API_TOP_NEWS as default APIEndpointType
            mAPIEndpointBuilder = new APIEndpointBuilder(APIEndpointType.API_TOP_NEWS);
        }

        if (mCurrentCategoryId == Config.DEFAULT_CATEGORY_ID_TOP_NEWS) {
            mAPIEndpointBuilder.setAPIEndpointType(APIEndpointType.API_TOP_NEWS)
                    .setCategoryList(mCategoryList);
            return;
        }

        mAPIEndpointBuilder.setAPIEndpointType(APIEndpointType.API_NEWS_WITH_CATEGORY)
                .setCategoryId(mCurrentCategoryId);
    }
}
