package com.example.phat.vnexpressnews.activities.eventhandler;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.phat.vnexpressnews.R;
import com.example.phat.vnexpressnews.activities.BrowseNewsActivity;
import com.example.phat.vnexpressnews.activities.DetailArticleActivity;
import com.example.phat.vnexpressnews.adapters.BriefArticleAdapter;
import com.example.phat.vnexpressnews.model.BriefArticle;
import com.example.phat.vnexpressnews.util.ItemClickSupport;

import static com.example.phat.vnexpressnews.util.LogUtils.LOGI;
import static com.example.phat.vnexpressnews.util.LogUtils.makeLogTag;

/**
 * Handles when a brief article in recycler view is clicked.
 */
public class OnBriefArticleClickedHandler implements ItemClickSupport.OnItemClickListener,
        BrowseNewsActivity.OnMainArticleClickListener {
    private static final String TAG = makeLogTag(OnBriefArticleClickedHandler.class);

    private Activity mActivity;

    public OnBriefArticleClickedHandler(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        // Gets item which was clicked
        BriefArticle item = ((BriefArticleAdapter) recyclerView.getAdapter()).getItem(position);

        // Start a DetailArticleActivity
        startDetailArticleActivity(v, item);
    }

    @Override
    public void onMainArticleClicked(View v, BriefArticle briefArticle) {
        startDetailArticleActivity(v, briefArticle);
    }

    private void startDetailArticleActivity(View v, BriefArticle briefArticle) {
        Intent intent = new Intent(mActivity, DetailArticleActivity.class);
        intent.putExtra(DetailArticleActivity.TAG_ARTICLE_ID, briefArticle.getArticleId());
        intent.putExtra(DetailArticleActivity.TAG_ARTICLE_TYPE, briefArticle.getArticleType());
        intent.putExtra(DetailArticleActivity.TAG_ARTICLE_THUMBNAIL_URL, briefArticle.getThumbnailUrl());

        // Indicates shared elements
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                mActivity,
                new Pair<>(v.findViewById(R.id.brief_article_thumbnail),
                        mActivity.getString(R.string.transition_name_article_thumbnail))
        );

        // Start DetailArticleActivity with transition animation
        ActivityCompat.startActivity(mActivity, intent, options.toBundle());
        LOGI(TAG, "Start DetailArticleActivity with articleTitle: " + briefArticle.getTitle()
                + ", articleId:" + briefArticle.getArticleId());
    }
}
