package com.phattn.vnexpressnews.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract class for interacting with {@link ArticleProvider}.
 */
public final class ArticleContract {

    private ArticleContract() {}

    public static final String CONTENT_TYPE_APP_BASE = "vnexpressnews.";

    public static final String CONTENT_TYPE_BASE = "vnd.android.cursor.dir/vnd."
            + CONTENT_TYPE_APP_BASE;

    public static final String CONTENT_ITEM_TYPE_BASE = "vnd.android.cursor.item/vnd."
            + CONTENT_TYPE_APP_BASE;

    interface ArticlesColumns {
        /** Unique int identifying this article. */
        String ARTICLE_ID = "article_id";
        /** Type of this article */
        String ARTICLE_TYPE = "article_type";
        /** Original category of this article */
        String ORIGINAL_CATEGORY = "original_cate";
        /** Articles title */
        String TITLE = "title";
        /** Brief content of this article */
        String LEAD = "lead";
        /** The url points to actual article on website */
        String SHARE_URL = "share_url";
        /** The url points to thumbnail photo */
        String THUMBNAIL_URL = "thumbnail_url";
        /** Privacy of this article */
        String PRIVACY = "privacy";
        /** Number of pages of this article */
        String TOTAL_PAGE = "total_page";
        /** Number of comments */
        String TOTAL_COMMENT = "total_comment";
        /** The time that this article was published */
        String PUBLISH_TIME = "publish_time";
        /** Site id of this article */
        String SITE_ID = "site_id";
        /** The set of relate articles with this article. This is a comma-separated list of ids. */
        String LIST_REFERENCE = "list_reference";
        /** Content of this article */
        String CONTENT = "content";
        /** The set of photo ids of this article. This is a comma-separated list of ids */
        String PHOTOS = "has_photos";
        /** Mode view of this article */
        String MODE_VIEW = "mode_view";
        /** Indicates category id which this article belongs to */
        String CATEGORY_PARENT = "cate_parent";
        /** The list of videos ids of this article. This is a comma-separated list of ids*/
        String VIDEOS = "has_list_video";
    }

    interface ReferenceArticlesColumns {
        /** Unique int identifying this article. */
        String ARTICLE_ID = "article_id";
        /** Type of this article */
        String ARTICLE_TYPE = "article_type";
        /** Original category of this article */
        String ORIGINAL_CATEGORY = "original_cate";
        /** Articles title */
        String TITLE = "title";
        /** The url points to actual article on website */
        String SHARE_URL = "share_url";
        /** The url points to thumbnail photo */
        String THUMBNAIL_URL = "thumbnail_url";
    }

    interface PhotosColumns {
        /** Unique int identifying this photo */
        String PHOTO_ID = "photo_id";
        /** The article Id which this photo belongs to */
        String ARTICLE_ID = "article_id";
        /** The URL points to actual photo */
        String THUMBNAIL_URL = "thumbnail_url";
        /** The caption describes this photo */
        String CAPTION = "caption";
    }

    interface CategoriesColumns {
        /** Unique int identifying this category */
        String CATEGORY_ID = "category_id";
        /** Name of this category */
        String CATEGORY_NAME = "category_name";
        /** Code of this category */
        String CATEGORY_CODE = "category_code";
        /** Parent category that this category belongs to */
        String PARENT_ID = "parent_id";
        /** Highest parent category that this category belongs to */
        String FULL_PARENT = "full_parent";
        /** Indicates this category should shows in menu or not */
        String SHOW_FOLDER = "show_folder";
        /** Indicates order of this category in menu */
        String DISPLAY_ORDER = "display_order";
    }

    interface VideosColumns {
        /** Unique int identifying this video */
        String VIDEO_ID = "video_id";
        /** The article Id which this photo belongs to */
        String ARTICLE_ID = "article_id";
        /** The URL points to video source */
        String URL = "url";
        /** Video title */
        String CAPTION = "caption";
        /** Describes video content */
        String DESCRIPTION = "description";
        /** The url points to a photo which is placeholder of video */
        String THUMBNAIL_URL = "thumbnail_url";
        /** A map of resolution and url source of video. JSON format */
        String SIZE_FORMAT = "size_format";
    }

    public static final String CONTENT_AUTHORITY = "com.phattn.vnexpressnews";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ARTICLES = "articles";

    public static final String PATH_REFERENCE_ARTICLES = "reference_articles";

    public static final String PATH_PHOTOS = "photos";

    public static final String PATH_CATEGORIES = "categories";

    public static final String PATH_VIDEOS = "videos";

    public static String makeContentType(String id) {
        if (id != null) {
            return CONTENT_TYPE_BASE + id;
        } else {
            return null;
        }
    }

    public static String makeContentItemType(String id) {
        if (id != null) {
            return CONTENT_ITEM_TYPE_BASE + id;
        } else {
            return null;
        }
    }

    public static class Articles implements ArticlesColumns, BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_ARTICLES).build();

        public static final String CONTENT_TYPE_ID = "articles";

        public static final String DEFAULT_SORT = ArticlesColumns.PUBLISH_TIME + " DESC";

        /** Build {@link Uri} for given article. */
        public static Uri buildArticleUri(int articleId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(articleId)).build();
        }

        /** Return Article ID given URI. */
        public static int getArticleId(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(1));
        }

        /** Build {@link Uri} that references any {@code ReferenceArticles} associated with
         * the requested {@code #ARTICLE_ID}
         */
        public static Uri buildReferenceArticlesDirUri(int articleId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(articleId))
                    .appendPath(PATH_REFERENCE_ARTICLES).build();
        }

        /**
         * Build {@link Uri} that references a {@code Categories} associated with the
         * requested {@code #ARTICLE_ID}
         */
        public static Uri buildCategoriesItemUri(int articleId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(articleId))
                    .appendPath(PATH_CATEGORIES).build();
        }

        /**
         * Build {@link Uri} that references any {@code Photos} associated with the
         * requested {@code #ARTICLE_ID}
         */
        public static Uri buildPhotosDirUri(int articleId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(articleId))
                    .appendPath(PATH_PHOTOS).build();
        }

        /**
         * Build {@link Uri} that references any {@code Videos} associated with the
         * requested {@code #ARTICLE_ID}
         */
        public static Uri buildVideosDirUri(int articleId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(articleId))
                    .appendPath(PATH_VIDEOS).build();
        }
    }

    public static class ReferenceArticles implements ReferenceArticlesColumns, BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_REFERENCE_ARTICLES).build();

        public static final String CONTENT_TYPE_ID ="reference_article";

        /** Build {@link Uri} for given reference article. */
        public static Uri buildReferenceArticleUri(int referenceArticleId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(referenceArticleId)).build();
        }

        /** Return reference article id given uri */
        public static int getReferenceArticleId(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(1));
        }

    }

    public static class Photos implements PhotosColumns, BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_PHOTOS).build();

        public static final String CONTENT_TYPE_ID = "photo";

        /** Build {@link Uri} for given photo. */
        public static Uri buildPhotoUri(int photoId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(photoId)).build();
        }

        /** Return photo id given uri */
        public static int getPhotoId(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(1));
        }
    }

    public static class Categories implements CategoriesColumns, BaseColumns {
        public static final Uri CONTENT_URI =  BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_CATEGORIES).build();

        public static final String CONTENT_TYPE_ID = "category";

        /** Build {@link Uri} for given category. */
        public static Uri buildCategoryUri(int categoryId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(categoryId)).build();
        }

        /** Return category id given uri */
        public static int getCategoryId(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(1));
        }
    }

    public static class Videos implements VideosColumns, BaseColumns {
        public static final Uri CONTENT_URI =  BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_VIDEOS).build();

        public static final String CONTENT_TYPE_ID = "video";

        /** Build {@link Uri} for given video. */
        public static Uri buildVideoUri(int videoId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(videoId)).build();
        }

        /** Return video id given uri */
        public static int getVideoId(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(1));
        }
    }
}
