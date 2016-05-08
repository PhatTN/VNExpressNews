package com.phattn.vnexpressnews.activities;

import android.app.FragmentTransaction;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.phattn.vnexpressnews.Config;
import com.phattn.vnexpressnews.R;
import com.phattn.vnexpressnews.fragments.DetailArticleFragment;
import com.phattn.vnexpressnews.model.Article;
import com.phattn.vnexpressnews.provider.ArticleContract;

public class DetailArticleActivity extends BaseActivity
        implements DetailArticleFragment.OnFragmentInteractionListener {

    /** AsyncQueryHandler tokens */
    private static final int QUERY_TOKEN_ARTICLE = 1000;

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

        setToolbarTitle(R.string.toolbar_title_news);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_article, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:     // Handle UP button
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.bookmark:
                // TODO save article here
                Toast.makeText(DetailArticleActivity.this,
                        R.string.will_be_updated_function, Toast.LENGTH_SHORT).show();
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
    public void onRequestDataSuccessfully(final Article result) {
        // Hide progress bar
        hideProgressBar();

        if (result.getCategoryParent() != null) {
            setToolbarTitle(result.getCategoryParent().getCategoryName());
        }

        // When request data successfully. We start sets up FloatActionButton and its click event
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start CommentActivity
                Intent intent = new Intent(DetailArticleActivity.this, CommentActivity.class);
                intent.putExtra(CommentActivity.TAG_ARTICLE_ID, result.getArticleId());
                intent.putExtra(CommentActivity.TAG_CATEGORY_ID, result.getCategoryParent().getCategoryID());
                intent.putExtra(CommentActivity.TAG_ARTICLE_TITLE, result.getTitle());
                startActivity(intent);
            }
        });
        // Since it is set to "INVISIBLE" in XML file. So we show it up
        fab.show();
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

    /**
     * Handles insert article into DB in background thread
     */
    private class ArticleSavingHandler extends AsyncQueryHandler {

        public ArticleSavingHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onInsertComplete(int token, Object cookie, Uri uri) {
            MenuItem item = (MenuItem) cookie;
            item.setIcon(R.drawable.ic_bookmark_check);
        }
    }
}
