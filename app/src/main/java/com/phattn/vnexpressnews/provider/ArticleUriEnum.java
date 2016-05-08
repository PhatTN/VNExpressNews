package com.phattn.vnexpressnews.provider;

import com.phattn.vnexpressnews.provider.ArticleContract.Articles;
import com.phattn.vnexpressnews.provider.ArticleContract.Categories;
import com.phattn.vnexpressnews.provider.ArticleContract.ReferenceArticles;
import com.phattn.vnexpressnews.provider.ArticleContract.Photos;
import com.phattn.vnexpressnews.provider.ArticleContract.Videos;
import com.phattn.vnexpressnews.provider.ArticleDatabase.Tables;

/**
 * The list of {@code Uri}s recognised by the {@code ContentProvider} of the app.
 */
public enum ArticleUriEnum {
    ARTICLES(100, ArticleContract.PATH_ARTICLES, Articles.CONTENT_TYPE_ID, false, Tables.ARTICLES),
    ARTICLES_ID(101, ArticleContract.PATH_ARTICLES + "/#", Articles.CONTENT_TYPE_ID, true, null),
    ARTICLES_ID_PHOTOS(102, ArticleContract.PATH_ARTICLES + "/#/" + ArticleContract.PATH_PHOTOS, Photos.CONTENT_TYPE_ID, false, Tables.PHOTOS),
    ARTICLES_ID_VIDEOS(103, ArticleContract.PATH_ARTICLES + "/#/" + ArticleContract.PATH_VIDEOS, Videos.CONTENT_TYPE_ID, false, Tables.VIDEOS),
    ARTICLES_ID_CATEGORIES(104, ArticleContract.PATH_ARTICLES + "/#/" + ArticleContract.PATH_CATEGORIES, Categories.CONTENT_TYPE_ID, true, null),
    LINKED_ARTICLES(105,  ArticleContract.PATH_ARTICLES + "/#/" + ArticleContract.PATH_REFERENCE_ARTICLES, ReferenceArticles.CONTENT_TYPE_ID, false, Tables.LINKED_ARTICLES),
    REFERENCE_ARTICLE(200, ArticleContract.PATH_REFERENCE_ARTICLES, ReferenceArticles.CONTENT_TYPE_ID, false, Tables.REFERENCE_ARTICLES),
    REFERENCE_ARTICLE_ID(201, ArticleContract.PATH_REFERENCE_ARTICLES + "/#", ReferenceArticles.CONTENT_TYPE_ID, true, null),
    PHOTOS(300, ArticleContract.PATH_PHOTOS, Photos.CONTENT_TYPE_ID, false, Tables.PHOTOS),
    PHOTOS_ID(301, ArticleContract.PATH_PHOTOS + "/#", Photos.CONTENT_TYPE_ID, true, null),
    CATEGORIES(400, ArticleContract.PATH_CATEGORIES, Categories.CONTENT_TYPE_ID, false, Tables.CATEGORIES),
    CATEGORIES_ID(401, ArticleContract.PATH_CATEGORIES + "/#", Categories.CONTENT_TYPE_ID, true, null),
    VIDEOS(500, ArticleContract.PATH_VIDEOS, Videos.CONTENT_TYPE_ID, false, Tables.VIDEOS),
    VIDEOS_ID(501, ArticleContract.PATH_VIDEOS + "/#", Videos.CONTENT_TYPE_ID, true, null);

    public int code;

    public String path;

    public String contentType;

    public String table;

    ArticleUriEnum(int code, String path, String contentTypeId, boolean item, String table) {
        this.code = code;
        this.path = path;
        this.contentType = item ? ArticleContract.makeContentItemType(contentTypeId)
                : ArticleContract.makeContentType(contentTypeId);
        this.table = table;
    }
}
