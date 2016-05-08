package com.phattn.vnexpressnews.provider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.phattn.vnexpressnews.provider.ArticleContract.Articles;
import com.phattn.vnexpressnews.provider.ArticleContract.Categories;
import com.phattn.vnexpressnews.provider.ArticleContract.Photos;
import com.phattn.vnexpressnews.provider.ArticleContract.ReferenceArticles;
import com.phattn.vnexpressnews.provider.ArticleContract.Videos;
import com.phattn.vnexpressnews.provider.ArticleDatabase.Tables;
import com.phattn.vnexpressnews.provider.ArticleDatabase.LinkedArticles;
import com.phattn.vnexpressnews.util.SelectionBuilder;

import java.util.ArrayList;
import java.util.Arrays;

import static com.phattn.vnexpressnews.util.LogUtils.LOGV;
import static com.phattn.vnexpressnews.util.LogUtils.makeLogTag;

/**
 * {@link android.content.ContentProvider} that stores {@link ArticleProvider} data.
 */
public class ArticleProvider extends ContentProvider {

    private static final String TAG = makeLogTag(ArticleProvider.class);

    private ArticleDatabase mOpenHelper;

    private ArticleProviderUriMatcher mUriMatcher;

    @Override
    public boolean onCreate() {
        mOpenHelper = new ArticleDatabase(getContext());
        mUriMatcher = new ArticleProviderUriMatcher();
        return true;
    }

    private void deleteDatabase() {
        mOpenHelper.close();
        Context context = getContext();
        ArticleDatabase.deleteDatabase(context);
        mOpenHelper = new ArticleDatabase(getContext());
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        ArticleUriEnum matchingUriEnum = mUriMatcher.matchUri(uri);
        return matchingUriEnum.contentType;
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        ArticleUriEnum matchingUriEnum = mUriMatcher.matchUri(uri);

        LOGV(TAG, "Uri=" + uri + " code=" + matchingUriEnum.contentType + " project=" +
                Arrays.toString(projection) + " selection=" + selection + " args=" +
                Arrays.toString(selectionArgs));

        boolean distinct = ArticleContractHelper.isQueryDistinct(uri);

        final SelectionBuilder builder = buildSelection(uri);

        Cursor cursor = builder.where(selection, selectionArgs)
                .query(db, distinct, projection, sortOrder, null);

        Context context = getContext();
        if (null != context) {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }
        return cursor;
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        LOGV(TAG, "insert(uri=" + uri + ", values=" + values.toString() + ")");

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        ArticleUriEnum matchingUriEnum = mUriMatcher.matchUri(uri);
        if (matchingUriEnum.table != null) {
            db.insertOrThrow(matchingUriEnum.table, null, values);
            notifyChange(uri);
        }

        switch (matchingUriEnum) {
            case ARTICLES: {
                return Articles.buildArticleUri(values.getAsInteger(Articles.ARTICLE_ID));
            }
            case REFERENCE_ARTICLE: {
                return ReferenceArticles.buildReferenceArticleUri(
                        values.getAsInteger(ReferenceArticles.ARTICLE_ID));
            }
            case PHOTOS: {
                return Photos.buildPhotoUri(values.getAsInteger(Photos.PHOTO_ID));
            }
            case CATEGORIES: {
                return Categories.buildCategoryUri(values.getAsInteger(Categories.CATEGORY_ID));
            }
            case VIDEOS: {
                return Videos.buildVideoUri(values.getAsInteger(Videos.VIDEO_ID));
            }
            case LINKED_ARTICLES:
                return Articles.buildReferenceArticlesDirUri(values.getAsInteger(LinkedArticles.ARTICLE_ID));
            default: {
                throw new UnsupportedOperationException("Unknown insert uri: " + uri);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        LOGV(TAG, "delete(uri=" + uri + ", selection=" + selection
                + ", selectionArgs=" + Arrays.toString(selectionArgs));

        if (uri == ArticleContract.BASE_CONTENT_URI) {
            // Handle whole database deletes
            deleteDatabase();
            notifyChange(uri);
            return 1;
        }

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSelection(uri);

        int retVal = builder.where(selection, selectionArgs).delete(db);
        notifyChange(uri);
        return retVal;
    }

    /** {@inheritDoc} */
    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        LOGV(TAG, "update(uri=" + uri + ", values=" + values.toString()
                + ", selection=" + selection + ", selectionArgs=" + Arrays.toString(selectionArgs));

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSelection(uri);

        int retVal = builder.where(selection, selectionArgs).update(db, values);
        notifyChange(uri);
        return retVal;
    }

    @NonNull
    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Returns a tuple of question marks. For example, if {@code count} is 3, returns "(?,?,?)"
     */
    private String makeQuestionMarkTuple(int count) {
        if (count < 1) {
            return "()";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(?");
        for (int i = 1; i < count; i++) {
            stringBuilder.append(",?");
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    /**
     * Build a simple {@link SelectionBuilder} to match the requested {@link Uri}.
     * This is usually enough to support {@link #query}, {@link #insert}, {@link #update}
     * and {@link #delete} operations of this app.
     */
    private SelectionBuilder buildSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        ArticleUriEnum matchingUriEnum = mUriMatcher.matchUri(uri);

        switch (matchingUriEnum) {
            // The main Uris, corresponding to the root of each type of Uri, do not have any selection
            // criteria so the full table is used. The others apply a selection criteria.
            case ARTICLES:
            case REFERENCE_ARTICLE:
            case PHOTOS:
            case CATEGORIES:
            case VIDEOS:
                return builder.table(matchingUriEnum.table);
            case ARTICLES_ID: {
                final int articleId = Articles.getArticleId(uri);
                return builder.table(Tables.ARTICLES)
                        .where(Articles.ARTICLE_ID + "=?", String.valueOf(articleId));
            }
            case REFERENCE_ARTICLE_ID: {
                final int referenceArticleId = ReferenceArticles.getReferenceArticleId(uri);
                return builder.table(Tables.REFERENCE_ARTICLES)
                        .where(ReferenceArticles.ARTICLE_ID + "=?", String.valueOf(referenceArticleId));
            }
            case PHOTOS_ID: {
                final int photoId = Photos.getPhotoId(uri);
                return builder.table(Tables.PHOTOS)
                        .where(Photos.PHOTO_ID + "=?", String.valueOf(photoId));
            }
            case CATEGORIES_ID: {
                final int categoryId = Categories.getCategoryId(uri);
                return builder.table(Tables.CATEGORIES)
                        .where(Categories.CATEGORY_ID + "=?", String.valueOf(categoryId));
            }
            case VIDEOS_ID: {
                final int videoId = Videos.getVideoId(uri);
                return builder.table(Tables.VIDEOS)
                        .where(Videos.VIDEO_ID + "=?", String.valueOf(videoId));
            }
            case LINKED_ARTICLES: {
                final int articleId = Articles.getArticleId(uri);
                return builder.table(Tables.LINKED_ARTICLES_JOIN_REFERENCE_ARTICLES)
                        .mapToTable(ReferenceArticles._ID, Tables.REFERENCE_ARTICLES)
                        .mapToTable(ReferenceArticles.ARTICLE_ID, Tables.REFERENCE_ARTICLES)
                        .where(Qualified.LINKED_ARTICLES_ARTICLE_ID + "=?", String.valueOf(articleId));
            }
            case ARTICLES_ID_CATEGORIES: {
                final int articleId = Articles.getArticleId(uri);
                return builder.table(Tables.ARTICLES_JOIN_CATEGORIES)
                        .mapToTable(Categories._ID, Tables.CATEGORIES)
                        .where(Qualified.ARTICLES_ARTICLE_ID + "=?", String.valueOf(articleId));
            }
            case ARTICLES_ID_PHOTOS: {
                final int articleId = Articles.getArticleId(uri);
                return builder.table(Tables.ARTICLES_JOIN_PHOTOS)
                        .mapToTable(Photos._ID, Tables.PHOTOS)
                        .mapToTable(Articles.ARTICLE_ID, Tables.ARTICLES)
                        .where(Qualified.ARTICLES_ARTICLE_ID + "=?", String.valueOf(articleId));
            }
            case ARTICLES_ID_VIDEOS: {
                final int articleId = Articles.getArticleId(uri);
                return builder.table(Tables.ARTICLES_JOIN_VIDEOS)
                        .mapToTable(Videos._ID, Tables.VIDEOS)
                        .mapToTable(Articles.ARTICLE_ID, Tables.ARTICLES)
                        .where(Qualified.ARTICLES_ARTICLE_ID + "=?", String.valueOf(articleId));
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri for " + uri);
            }
        }
    }

    private void notifyChange(Uri uri) {
        Context context = getContext();
        if (null != context) {
            context.getContentResolver().notifyChange(uri, null);
        }
    }

    /** Fully-qualified field names. Used when needed to work around SQL ambiguity */
    private interface Qualified {
        String LINKED_ARTICLES_ARTICLE_ID = Tables.LINKED_ARTICLES + "." + LinkedArticles.ARTICLE_ID;
        String ARTICLES_ARTICLE_ID = Tables.ARTICLES + "." + Articles.ARTICLE_ID;
    }
}
