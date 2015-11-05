package com.example.phat.vnexpressnews.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.example.phat.vnexpressnews.provider.ArticleContract.Articles;
import com.example.phat.vnexpressnews.provider.ArticleContract.Categories;
import com.example.phat.vnexpressnews.provider.ArticleContract.Photos;
import com.example.phat.vnexpressnews.provider.ArticleContract.ReferenceArticles;
import com.example.phat.vnexpressnews.provider.ArticleContract.Videos;

import static com.example.phat.vnexpressnews.util.LogUtils.LOGV;
import static com.example.phat.vnexpressnews.util.LogUtils.makeLogTag;

/**
 * Helper for managing {@link android.database.sqlite.SQLiteDatabase} that stores data
 * for {@link ArticleProvider}.
 */
public class ArticleDatabase extends SQLiteOpenHelper {
    private static final String TAG = makeLogTag(ArticleDatabase.class);

    public static final String DATABASE_NAME = "vn_express.db";

    private static final int VER_1_RELEASE_A = 100;
    private static final int CUR_DATABASE_VERSION = VER_1_RELEASE_A;

    private final Context mContext;

    public ArticleDatabase(Context context) {
        super(context, DATABASE_NAME, null, CUR_DATABASE_VERSION);
        mContext = context;
    }

    interface Tables {
        String ARTICLES = "articles";
        /** This table saves related-short-version articles of articles in Article table */
        String REFERENCE_ARTICLES = "reference_articles";
        /** This table links articles table with reference_articles table.
         * A article could have many reference-articles and a reference-article could link to many articles,
         * so we create third table to link articles table and reference-articles table */
        String LINKED_ARTICLES = "linked_articles";
        String PHOTOS = "photos";
        String CATEGORIES = "categories";
        String VIDEOS = "videos";

        String ARTICLES_JOIN_CATEGORIES = ARTICLES
                + " LEFT OUTER JOIN " + CATEGORIES + " ON "
                + ARTICLES + "." + Articles.CATEGORY_PARENT + "=" + CATEGORIES + "." + Categories.CATEGORY_ID;

        String ARTICLES_JOIN_PHOTOS = ARTICLES
                + " LEFT OUTER JOIN " + PHOTOS + " ON "
                + Qualified.ARTICLE_ARTICLE_ID + "=" + Qualified.PHOTOS_ARTICLE_ID;

        String ARTICLES_JOIN_VIDEOS = ARTICLES
                + " LEFT OUTER JOIN " + VIDEOS + " ON "
                + Qualified.ARTICLE_ARTICLE_ID + "=" + Qualified.VIDEOS_ARTICLE_ID;

        String LINKED_ARTICLES_JOIN_REFERENCE_ARTICLES = LINKED_ARTICLES
                + " LEFT OUTER JOIN " + REFERENCE_ARTICLES + " ON "
                + Qualified.LINKED_ARTICLES_REFERENCE_ID + "=" + Qualified.REFERENCE_ARTICLES_ARTICLE_ID;
    }

    private interface Triggers {
        // Deletes from dependent tables when corresponding articles are deleted.
        String LINKED_ARTICLES_DELETE = "linked_articles_delete";
        String ARTICLES_PHOTOS_DELETE = "articles_photos_delete";
        String ARTICLES_VIDEOS_DELETE = "videos_videos_delete";

        // Deletes references when there are no any articles refer to it.
        String REFERENCE_ARTICLES_DELETE = "references_articles_delete";
    }

    /**
     * This table links articles table with its reference_article table
     */
    public interface LinkedArticles {
        String ARTICLE_ID = "article_id";
        String REFERENCE_ID = "reference_id";
    }

    /** Fully-qualified field names. */
    private interface Qualified {
        String ARTICLE_ARTICLE_ID = Tables.ARTICLES + "." + Articles.ARTICLE_ID;

        String LINKED_ARTICLES_ARTICLE_ID = Tables.LINKED_ARTICLES + "."
                + LinkedArticles.ARTICLE_ID;

        String LINKED_ARTICLES_REFERENCE_ID = Tables.LINKED_ARTICLES + "."
                + LinkedArticles.REFERENCE_ID;

        String REFERENCE_ARTICLES_ARTICLE_ID = Tables.REFERENCE_ARTICLES + "."
                + ReferenceArticles.ARTICLE_ID;

        String PHOTOS_ARTICLE_ID = Tables.PHOTOS + "." + Photos.ARTICLE_ID;

        String VIDEOS_ARTICLE_ID = Tables.VIDEOS + "." + Videos.ARTICLE_ID;
    }

    /** {@code REFERENCE_ARTICLES} clauses */
    private interface References {
        String CATEGORY_ID = "REFERENCES " + Tables.CATEGORIES + "(" + Categories.CATEGORY_ID + ")";
        String ARTICLE_ID = "REFERENCES " + Tables.ARTICLES + "(" + Articles.ARTICLE_ID + ")";
        String REFERENCE_ARTICLE_ID = "REFERENCES " + Tables.REFERENCE_ARTICLES + "(" + ReferenceArticles.ARTICLE_ID + ")";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.ARTICLES + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Articles.ARTICLE_ID + " INTEGER NOT NULL,"
                + Articles.ARTICLE_TYPE + " INTEGER,"
                + Articles.ORIGINAL_CATEGORY + " INTEGER,"
                + Articles.TITLE + " TEXT NOT NULL,"
                + Articles.LEAD + " TEXT NOT NULL,"
                + Articles.SHARE_URL + " TEXT,"
                + Articles.THUMBNAIL_URL + " TEXT NOT NULL,"
                + Articles.PRIVACY + " INTEGER,"
                + Articles.TOTAL_PAGE + " INTEGER,"
                + Articles.TOTAL_COMMENT + " INTEGER NOT NULL,"
                + Articles.PUBLISH_TIME + " INTEGER NOT NULL,"
                + Articles.SITE_ID + " INTEGER,"
                + Articles.LIST_REFERENCE + " TEXT,"
                + Articles.CONTENT + " TEXT NOT NULL,"
                + Articles.PHOTOS + " TEXT,"
                + Articles.MODE_VIEW + " INTEGER,"
                + Articles.CATEGORY_PARENT + " INTEGER NOT NULL " + References.CATEGORY_ID + ","
                + Articles.VIDEOS + " TEXT,"
                + "UNIQUE (" + Articles.ARTICLE_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.REFERENCE_ARTICLES + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ReferenceArticles.ARTICLE_ID + " INTEGER NOT NULL,"
                + ReferenceArticles.ARTICLE_TYPE + " INTEGER,"
                + ReferenceArticles.ORIGINAL_CATEGORY + " INTEGER,"
                + ReferenceArticles.TITLE + " TEXT NOT NULL,"
                + ReferenceArticles.SHARE_URL + " TEXT,"
                + ReferenceArticles.THUMBNAIL_URL + " TEXT NOT NULL,"
                + "UNIQUE (" + ReferenceArticles.ARTICLE_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.PHOTOS + "("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Photos.PHOTO_ID + " INTEGER NOT NULL " + References.ARTICLE_ID + ","
                + Photos.ARTICLE_ID + " INTEGER NOT NULL,"
                + Photos.THUMBNAIL_URL + " TEXT NOT NULL,"
                + Photos.CAPTION + " TEXT,"
                + "UNIQUE (" + Photos.PHOTO_ID + ","
                + Photos.ARTICLE_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.CATEGORIES + "("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Categories.CATEGORY_ID + " INTEGER NOT NULL,"
                + Categories.CATEGORY_NAME + " TEXT NOT NULL,"
                + Categories.CATEGORY_CODE + " TEXT NOT NULL,"
                + Categories.PARENT_ID + " INTEGER,"
                + Categories.FULL_PARENT + " INTEGER,"
                + Categories.SHOW_FOLDER + " INTEGER ,"
                + Categories.DISPLAY_ORDER + " INTEGER,"
                + "UNIQUE (" + Categories.CATEGORY_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.VIDEOS + "("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Videos.VIDEO_ID + " INTEGER NOT NULL,"
                + Videos.ARTICLE_ID + " INTEGER NOT NULL " + References.ARTICLE_ID + ","
                + Videos.URL + " TEXT NOT NULL,"
                + Videos.CAPTION + " TEXT,"
                + Videos.DESCRIPTION + " TEXT,"
                + Videos.THUMBNAIL_URL + " TEXT NOT NULL,"
                + Videos.SIZE_FORMAT + " TEXT,"
                + "UNIQUE (" + Videos.VIDEO_ID + ","
                + Videos.ARTICLE_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.LINKED_ARTICLES + "("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + LinkedArticles.ARTICLE_ID + " INTEGER NOT NULL " + References.ARTICLE_ID + ","
                + LinkedArticles.REFERENCE_ID + " INTEGER NOT NULL " + References.REFERENCE_ARTICLE_ID + ","
                + "UNIQUE (" + LinkedArticles.ARTICLE_ID + ","
                + LinkedArticles.REFERENCE_ID + ") ON CONFLICT REPLACE)");

        // Article deletion triggers
        db.execSQL("CREATE TRIGGER " + Triggers.LINKED_ARTICLES_DELETE + " AFTER DELETE ON "
                + Tables.ARTICLES + " BEGIN DELETE FROM " + Tables.LINKED_ARTICLES
                + " WHERE " + Qualified.LINKED_ARTICLES_ARTICLE_ID + "=old." + Articles.ARTICLE_ID
                + "; END;");

        db.execSQL("CREATE TRIGGER " + Triggers.ARTICLES_PHOTOS_DELETE + " AFTER DELETE ON "
                + Tables.ARTICLES + " BEGIN DELETE FROM " + Tables.PHOTOS
                + " WHERE " + Qualified.PHOTOS_ARTICLE_ID + "=old." + Articles.ARTICLE_ID
                + "; END;");

        db.execSQL("CREATE TRIGGER " + Triggers.ARTICLES_VIDEOS_DELETE + " AFTER DELETE ON "
                + Tables.ARTICLES + " BEGIN DELETE FROM " + Tables.VIDEOS
                + " WHERE " + Qualified.VIDEOS_ARTICLE_ID + "=old." + Articles.ARTICLE_ID
                + "; END;");

        db.execSQL("CREATE TRIGGER " + Triggers.REFERENCE_ARTICLES_DELETE + " AFTER DELETE ON "
                + Tables.LINKED_ARTICLES + " BEGIN DELETE FROM " + Tables.REFERENCE_ARTICLES
                + " WHERE (SELECT COUNT(" + LinkedArticles.ARTICLE_ID + ") FROM " + Tables.LINKED_ARTICLES
                + " WHERE " + LinkedArticles.REFERENCE_ID + "=old." + ReferenceArticles.ARTICLE_ID
                + ")=0; END;");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TRIGGER IF EXISTS " + Triggers.LINKED_ARTICLES_DELETE);
        db.execSQL("DROP TRIGGER IF EXISTS " + Triggers.ARTICLES_PHOTOS_DELETE);
        db.execSQL("DROP TRIGGER IF EXISTS " + Triggers.ARTICLES_VIDEOS_DELETE);
        db.execSQL("DROP TRIGGER IF EXISTS " + Triggers.REFERENCE_ARTICLES_DELETE);

        db.execSQL("DROP TABLE IF EXISTS " + Tables.ARTICLES);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.LINKED_ARTICLES);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.REFERENCE_ARTICLES);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.PHOTOS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.VIDEOS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.CATEGORIES);

        onCreate(db);
    }

    public static void deleteDatabase(Context context) {
        LOGV(TAG, "Delete the database " + DATABASE_NAME);
        context.deleteDatabase(DATABASE_NAME);
    }
}
