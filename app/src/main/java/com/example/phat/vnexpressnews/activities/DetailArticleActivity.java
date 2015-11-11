package com.example.phat.vnexpressnews.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.phat.vnexpressnews.Config;
import com.example.phat.vnexpressnews.R;

public class DetailArticleActivity extends BaseActivity {

    /** The tag is used to pass/receive data to/from Intent */
    public static final String TAG_ARTICLE_ID = Config.PACKAGE_NAME
            + ".activities.TAG_ARTICLE_ID";
    public static final String TAG_ARTICLE_TITLE = Config.PACKAGE_NAME
            + ".activities.TAG_ARTICLE_TITLE";
    public static final String TAG_ARTICLE_THUMBNAIL_URL = Config.PACKAGE_NAME
            + ".activities.TAG_ARTICLE_THUMBNAIL_URL";
    public static final String TAG_ARTICLE_LEAD = Config.PACKAGE_NAME
            + ".activities.TAG_ARTICLE_LEAD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_article);

        int articleId = getIntent().getIntExtra(TAG_ARTICLE_ID, -1);
        String articleTitle = getIntent().getStringExtra(TAG_ARTICLE_TITLE);
        String articleThumbnail = getIntent().getStringExtra(TAG_ARTICLE_THUMBNAIL_URL);
        String articleLead = getIntent().getStringExtra(TAG_ARTICLE_LEAD);

        ImageView thumbnailView = (ImageView) findViewById(R.id.article_thumbnail);
        TextView titleView = (TextView) findViewById(R.id.article_title);
        mImageLoader.loadImage(articleThumbnail, thumbnailView, false);
        titleView.setText(articleTitle);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setTitleEnabled(false);
    }

    @Override
    protected void onCategoryItemSelected(int categoryId) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
}
