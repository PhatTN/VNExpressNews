package com.phattn.vnexpressnews.util;

import android.content.ContentValues;

import com.phattn.vnexpressnews.model.Article;
import com.phattn.vnexpressnews.provider.ArticleContract;

/**
 * Helps to convert POJO class to {@link android.content.ContentValues}
 */
public class ContentValuesHelper {

    public static ContentValues convertFrom(Article article) {
        ContentValues values = new ContentValues();
        values.put(ArticleContract.Articles.ARTICLE_ID, article.getArticleId());
        values.put(ArticleContract.Articles.ARTICLE_TYPE, article.getArticleType());
        values.put(ArticleContract.Articles.ORIGINAL_CATEGORY, article.getOriginalCategory());
        values.put(ArticleContract.Articles.TITLE, article.getTitle());
        values.put(ArticleContract.Articles.LEAD, article.getLead());
        values.put(ArticleContract.Articles.SHARE_URL, article.getShareUrl());
        values.put(ArticleContract.Articles.THUMBNAIL_URL, article.getThumbnailUrl());
        values.put(ArticleContract.Articles.PRIVACY, article.getPrivacy());
        values.put(ArticleContract.Articles.TOTAL_PAGE, article.getTotalPage());
        values.put(ArticleContract.Articles.TOTAL_COMMENT, article.getTotalComment());
        values.put(ArticleContract.Articles.PUBLISH_TIME, article.getPublishTime());
        values.put(ArticleContract.Articles.SITE_ID, article.getSiteId());
        values.put(ArticleContract.Articles.LIST_REFERENCE, "");
        values.put(ArticleContract.Articles.CONTENT, article.getContent());
        values.put(ArticleContract.Articles.PHOTOS, "");
        values.put(ArticleContract.Articles.MODE_VIEW, article.getModeView());
        values.put(ArticleContract.Articles.CATEGORY_PARENT, article.getCategoryParent().getCategoryID());
        values.put(ArticleContract.Articles.VIDEOS, "");

        return values;
    }

}
