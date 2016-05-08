package com.phattn.vnexpressnews.provider;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.MediumTest;

import com.phattn.vnexpressnews.provider.ArticleContract.Articles;
import com.phattn.vnexpressnews.provider.ArticleContract.Categories;
import com.phattn.vnexpressnews.provider.ArticleContract.Photos;
import com.phattn.vnexpressnews.provider.ArticleContract.ReferenceArticles;
import com.phattn.vnexpressnews.provider.ArticleContract.Videos;
import com.phattn.vnexpressnews.provider.ArticleDatabase.Tables;
import com.phattn.vnexpressnews.provider.ArticleDatabase.LinkedArticles;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Set of tests of the {@link ArticleProvider}, it does test basic functionality
 * has been implemented correctly
 */
@MediumTest
@RunWith(AndroidJUnit4.class)
public class ArticleProviderTest {

    private SQLiteDatabase db;
    private Cursor mCursor;
    private Context mContext;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getTargetContext();
        db = new ArticleDatabase(mContext).getWritableDatabase();
        // Starts with clean state
        deleteAllRecordsFromDB();

    }

    @After
    public void tearDown() {
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    /**
     * This helper method deletes all records from all database tables using the database
     * functions only. This is designed to be used to reset the state of the database until
     * the delete functionality of {@code ContentProvider} is available
     */
    private void deleteAllRecordsFromDB() {
        db.delete(Tables.ARTICLES, null, null);
        db.delete(Tables.REFERENCE_ARTICLES, null, null);
        db.delete(Tables.LINKED_ARTICLES, null, null);
        db.delete(Tables.CATEGORIES, null, null);
        db.delete(Tables.PHOTOS, null, null);
        db.delete(Tables.VIDEOS, null, null);
    }

    /**
     * This test checks to make sure that the {@code ContentProvider} is registered correctly.
     */
    @Test
    public void providerShouldAlreadyRegistered() {
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                ArticleProvider.class.getName());

        try {
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            assertThat("Error: ArticleProvider registered with authority: " + providerInfo.authority
                + " instead of authority: " + ArticleContract.CONTENT_AUTHORITY,
                    providerInfo.authority, is(equalTo(ArticleContract.CONTENT_AUTHORITY)));
        } catch (PackageManager.NameNotFoundException e) {
            assertThat("Error: ArticleProvider not registered at " + mContext.getPackageName(),
                    true, is(false));
        }
    }

    @Test
    public void query_articlesTable() {
        ContentValues testValues = TestUtilities.createArticleValues();

        // First, insert default article into the DB using database insert function.
        long rowId = db.insert(Tables.ARTICLES, null, testValues);
        assertThat("Unable to Insert an article into the DB", rowId != -1);

        db.close();

        // Tests query all articles in db. In this case, only one article in db
        mCursor = mContext.getContentResolver().query(
                Articles.CONTENT_URI,
                null, // columns
                null, // selection
                null, // selectionArgs
                null // sortOrder
        );

        // Make sure we get the correct value
        TestUtilities.validateCursor("Can not query an article.", mCursor, testValues);
    }

    @Test
    public void query_categoriesTable() {
        ContentValues testValues = TestUtilities.createCategoriesValues();

        // First, insert default category into the DB using database insert function.
        long rowId = db.insert(Tables.CATEGORIES, null, testValues);
        assertThat("Unable to Insert a category into the DB", rowId != -1);

        db.close();

        mCursor = mContext.getContentResolver().query(Categories.CONTENT_URI, null, null, null, null);

        // Make sure we get the correct value
        TestUtilities.validateCursor("Can not query a category.", mCursor, testValues);
    }

    @Test
    public void query_PhotosTable() {
        ContentValues testValues = TestUtilities.createPhotoValues();

        // First, insert default photo into the DB using database insert function.
        long rowId = db.insert(Tables.PHOTOS, null, testValues);
        assertThat("Unable to Insert a photo into the DB", rowId != -1);

        db.close();

        mCursor = mContext.getContentResolver().query(Photos.CONTENT_URI, null, null, null, null);

        // Make sure we get the correct value
        TestUtilities.validateCursor("Can not query a photo.", mCursor, testValues);
    }

    @Test
    public void query_VideosTable() {
        ContentValues testValues = TestUtilities.createVideoValue();

        // First, insert default photo into the DB using database insert function.
        long rowId = db.insert(Tables.VIDEOS, null, testValues);
        assertThat("Unable to Insert a video into the DB", rowId != -1);

        db.close();

        mCursor = mContext.getContentResolver().query(Videos.CONTENT_URI, null, null, null, null);

        // Make sure we get the correct value
        TestUtilities.validateCursor("Can not query a video.", mCursor, testValues);
    }

    @Test
    public void query_ReferenceArticlesTable() {
        ContentValues testValues = TestUtilities.createReferenceArticleValues();

        // First, insert default photo into the DB using database insert function.
        long rowId = db.insert(Tables.REFERENCE_ARTICLES, null, testValues);
        assertThat("Unable to Insert a reference_article into the DB", rowId != -1);

        db.close();

        mCursor = mContext.getContentResolver().query(ReferenceArticles.CONTENT_URI, null, null, null, null);

        // Make sure we get the correct value
        TestUtilities.validateCursor("Can not query a reference_article.", mCursor, testValues);
    }

    @Test
    public void query_articlesWithCategories() {
        ContentValues categoryValues = TestUtilities.createCategoriesValues();

        long rowId = db.insert(Tables.CATEGORIES, null, categoryValues);
        assertThat("Unable to Insert a category into the DB", rowId != -1);

        ContentValues articleValues = TestUtilities.createArticleValues();
        rowId = db.insert(Tables.ARTICLES, null, articleValues);
        assertThat("Unable to Insert an article into the DB", rowId != -1);

        db.close();

        int articleId = articleValues.getAsInteger(Articles.ARTICLE_ID);

        mCursor = mContext.getContentResolver().query(Articles.buildCategoriesItemUri(articleId), null, null, null, null);

        ContentValues allValues = new ContentValues();
        allValues.putAll(categoryValues);
        allValues.putAll(articleValues);

        // Make sure we get the correct value
        TestUtilities.validateCursor("Can not query an article with category", mCursor, allValues);
    }

    @Test
    public void query_articlesWithReferenceArticles() {
        ContentValues referenceArticlesValues = TestUtilities.createReferenceArticleValues();

        long rowId = db.insert(Tables.REFERENCE_ARTICLES, null, referenceArticlesValues);
        assertThat("Unable to Insert a reference_article into the DB", rowId != -1);

        ContentValues articleValues = TestUtilities.createArticleValues();
        rowId = db.insert(Tables.ARTICLES, null, articleValues);
        assertThat("Unable to Insert an article into the DB", rowId != -1);

        ContentValues linkedArticleValues = TestUtilities.createLinkedArticleValue();
        rowId = db.insert(Tables.LINKED_ARTICLES, null, linkedArticleValues);
        assertThat("Unable to Insert a linked_article into the DB", rowId != -1);

        db.close();

        int articleId = articleValues.getAsInteger(Articles.ARTICLE_ID);

        String[] columns = TestUtilities.REFERENCE_ARTICLES_COLUMN_NAMES;
        mCursor = mContext.getContentResolver().query(Articles.buildReferenceArticlesDirUri(articleId), columns, null, null, null);

        ContentValues allValues = new ContentValues();
        allValues.putAll(referenceArticlesValues);

        // Make sure we get the correct value
        TestUtilities.validateCursor("Can not query an article with reference_article", mCursor, allValues);
    }

    @Test
    public void query_articlesWithPhotos() {
        ContentValues photoValues = TestUtilities.createPhotoValues();

        long rowId = db.insert(Tables.PHOTOS, null, photoValues);
        assertThat("Unable to Insert a photo into the DB", rowId != -1);

        ContentValues articleValues = TestUtilities.createArticleValues();
        rowId = db.insert(Tables.ARTICLES, null, articleValues);
        assertThat("Unable to Insert an article into the DB", rowId != -1);

        db.close();

        int articleId = articleValues.getAsInteger(Articles.ARTICLE_ID);

        String[] columns = TestUtilities.concatenate(TestUtilities.ARTICLES_COLUMN_NAMES, TestUtilities.PHOTOS_COLUMN_NAMES);
        mCursor = mContext.getContentResolver().query(Articles.buildPhotosDirUri(articleId), columns, null, null, null);

        ContentValues allValues = new ContentValues();
        // Since two entries have same thumbnail_url column name, so we using "source_url" alias
        // for photos.thumbnail_url
        String thumbnail_url = photoValues.getAsString(Photos.THUMBNAIL_URL);
        photoValues.remove(Photos.THUMBNAIL_URL);
        photoValues.put(TestUtilities.THUMBNAIL_URL_ALIAS, thumbnail_url);
        allValues.putAll(photoValues);
        allValues.putAll(articleValues);

        // Make sure we get the correct value
        TestUtilities.validateCursor("Can not query an article with photo", mCursor, allValues);
    }

    @Test
    public void query_articlesWithVideos() {
        ContentValues videoValues = TestUtilities.createVideoValue();

        long rowId = db.insert(Tables.VIDEOS, null, videoValues);
        assertThat("Unable to Insert a video into the DB", rowId != -1);

        ContentValues articleValues = TestUtilities.createArticleValues();
        rowId = db.insert(Tables.ARTICLES, null, articleValues);
        assertThat("Unable to Insert an article into the DB", rowId != -1);

        db.close();

        int articleId = articleValues.getAsInteger(Articles.ARTICLE_ID);

        String[] columns = TestUtilities.concatenate(TestUtilities.ARTICLES_COLUMN_NAMES, TestUtilities.VIDEOS_COLUMN_NAMES);
        mCursor = mContext.getContentResolver().query(Articles.buildVideosDirUri(articleId), columns, null, null, null);

        ContentValues allValues = new ContentValues();
        // Since two entries have same thumbnail_url column name, so we using "source_url" alias
        // for videos.thumbnail_url
        String thumbnail_uri = videoValues.getAsString(Videos.THUMBNAIL_URL);
        videoValues.remove(Videos.THUMBNAIL_URL);
        videoValues.put(TestUtilities.THUMBNAIL_URL_ALIAS, thumbnail_uri);
        allValues.putAll(videoValues);
        allValues.putAll(articleValues);

        // Make sure we get the correct value
        TestUtilities.validateCursor("Can not query an article with video", mCursor, allValues);
    }

    @Test
    public void insert_articlesTable() {
        ContentValues testValues = TestUtilities.createArticleValues();

        Uri uri = mContext.getContentResolver().insert(Articles.CONTENT_URI, testValues);
        int articleId = Articles.getArticleId(uri);
        assertThat("Error: The returned articleId is not equals to the passed articleId",
                articleId, is(testValues.getAsInteger(Articles.ARTICLE_ID)));

        Cursor cursor = db.query(Tables.ARTICLES, null, null, null, null, null, null);

        TestUtilities.validateCursor("Error: The returned article is not equal to inserted article",
                cursor, testValues);
    }

    @Test
    public void insert_referenceArticlesTable() {
        ContentValues testValues = TestUtilities.createReferenceArticleValues();

        Uri uri = mContext.getContentResolver().insert(ReferenceArticles.CONTENT_URI, testValues);
        int referenceArticleId = ReferenceArticles.getReferenceArticleId(uri);
        assertThat("Error: The returned reference_articleId is not equals to the passed reference_articleId",
                referenceArticleId, is(testValues.getAsInteger(ReferenceArticles.ARTICLE_ID)));

        Cursor cursor = db.query(Tables.REFERENCE_ARTICLES, null, null, null, null, null, null);

        TestUtilities.validateCursor("Error: The returned reference_article is not equal to inserted reference_article",
                cursor, testValues);
    }

    @Test
    public void insert_categoriesTable() {
        ContentValues testValues = TestUtilities.createCategoriesValues();

        Uri uri = mContext.getContentResolver().insert(Categories.CONTENT_URI, testValues);
        int categoryId = Categories.getCategoryId(uri);
        assertThat("Error: The returned categoryId is not equals to the passed categoryId",
                categoryId, is(testValues.getAsInteger(Categories.CATEGORY_ID)));

        Cursor cursor = db.query(Tables.CATEGORIES, null, null, null, null, null, null);

        TestUtilities.validateCursor("Error: The returned category is not equal to inserted category",
                cursor, testValues);
    }

    @Test
    public void insert_photosTable() {
        ContentValues testValues = TestUtilities.createPhotoValues();

        Uri uri = mContext.getContentResolver().insert(Photos.CONTENT_URI, testValues);
        int photoId = Photos.getPhotoId(uri);
        assertThat("Error: The returned photoId is not equals to the passed photoId",
                photoId, is(testValues.getAsInteger(Photos.PHOTO_ID)));

        Cursor cursor = db.query(Tables.PHOTOS, null, null, null, null, null, null);

        TestUtilities.validateCursor("Error: The returned photo is not equal to inserted photo",
                cursor, testValues);
    }

    @Test
    public void insert_videosTable() {
        ContentValues testValues = TestUtilities.createVideoValue();

        Uri uri = mContext.getContentResolver().insert(Videos.CONTENT_URI, testValues);
        int videoId = Videos.getVideoId(uri);
        assertThat("Error: The returned videoId is not equals to the passed videoId",
                videoId, is(testValues.getAsInteger(Videos.VIDEO_ID)));

        Cursor cursor = db.query(Tables.VIDEOS, null, null, null, null, null, null);

        TestUtilities.validateCursor("Error: The returned video is not equal to inserted video",
                cursor, testValues);
    }

    @Test
    public void insert_linkedArticlesTable() {
        ContentValues testValues = TestUtilities.createLinkedArticleValue();

        int articleId = testValues.getAsInteger(LinkedArticles.ARTICLE_ID);

        Uri uri = mContext.getContentResolver().insert(Articles.buildReferenceArticlesDirUri(articleId), testValues);
        int resultArticleId = Articles.getArticleId(uri);
        assertThat("Error: The returned articleId is not equals to the passed articleId",
                resultArticleId, is(articleId));

        Cursor cursor = db.query(Tables.LINKED_ARTICLES, null, null, null, null, null, null);

        TestUtilities.validateCursor("Error: The returned linked_article is not equal to inserted linked_article",
                cursor, testValues);
    }

    @Test
    public void update_articlesTable() {
        ContentValues values = TestUtilities.createArticleValues();

        long rowId = db.insert(Tables.ARTICLES, null, values);
        assertThat("Unable to Insert an article into the DB", rowId != -1);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(Articles.ARTICLE_TYPE, 2);
        updatedValues.put(Articles.THUMBNAIL_URL, "https://changedUrl.com");

        int count = mContext.getContentResolver()
                .update(Articles.buildArticleUri(updatedValues.getAsInteger(Articles.ARTICLE_ID)), updatedValues, null, null);
        assertThat(count, is(1));

        mCursor = db.query(Tables.ARTICLES,
                null,                           // columns
                Articles.ARTICLE_ID + "=?",
                new String[] {updatedValues.getAsString(Articles.ARTICLE_ID)},
                null,
                null,
                null,
                null);

        TestUtilities.validateCursor("Error validating article entry update.", mCursor, updatedValues);
    }

    @Test
    public void update_referenceArticlesTable() {
        ContentValues values = TestUtilities.createReferenceArticleValues();

        long rowId = db.insert(Tables.REFERENCE_ARTICLES, null, values);
        assertThat("Unable to Insert a reference_article into the DB", rowId != -1);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(ReferenceArticles.ARTICLE_TYPE, 2);
        updatedValues.put(ReferenceArticles.THUMBNAIL_URL, "https://changedUrl.com");

        int count = mContext.getContentResolver()
                .update(ReferenceArticles.buildReferenceArticleUri(updatedValues.getAsInteger(ReferenceArticles.ARTICLE_ID)),
                        updatedValues, null, null);
        assertThat(count, is(1));

        mCursor = db.query(Tables.REFERENCE_ARTICLES,
                null,                           // columns
                ReferenceArticles.ARTICLE_ID + "=?",
                new String[] {updatedValues.getAsString(ReferenceArticles.ARTICLE_ID)},
                null,
                null,
                null,
                null);

        TestUtilities.validateCursor("Error validating reference_article entry update.", mCursor, updatedValues);
    }

    @Test
    public void update_categoriesTable() {
        ContentValues values = TestUtilities.createCategoriesValues();

        long rowId = db.insert(Tables.CATEGORIES, null, values);
        assertThat("Unable to Insert a category into the DB", rowId != -1);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(Categories.DISPLAY_ORDER, 20);
        updatedValues.put(Categories.SHOW_FOLDER, 0);

        int count = mContext.getContentResolver()
                .update(Categories.buildCategoryUri(updatedValues.getAsInteger(Categories.CATEGORY_ID)),
                        updatedValues, null, null);
        assertThat(count, is(1));

        mCursor = db.query(Tables.CATEGORIES,
                null,                           // columns
                Categories.CATEGORY_ID + "=?",
                new String[] {updatedValues.getAsString(Categories.CATEGORY_ID)},
                null,
                null,
                null,
                null);

        TestUtilities.validateCursor("Error validating categories entry update.", mCursor, updatedValues);
    }

    @Test
    public void update_PhotosTable() {
        ContentValues values = TestUtilities.createPhotoValues();

        long rowId = db.insert(Tables.PHOTOS, null, values);
        assertThat("Unable to Insert a photo into the DB", rowId != -1);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(Photos.CAPTION, "Changed caption");
        updatedValues.put(Photos.THUMBNAIL_URL, "https://changedUrl.com");

        int count = mContext.getContentResolver()
                .update(Photos.buildPhotoUri(updatedValues.getAsInteger(Photos.PHOTO_ID)),
                        updatedValues, null, null);
        assertThat(count, is(1));

        mCursor = db.query(Tables.PHOTOS,
                null,                           // columns
                Photos.PHOTO_ID + "=?",
                new String[] {updatedValues.getAsString(Photos.PHOTO_ID)},
                null,
                null,
                null,
                null);

        TestUtilities.validateCursor("Error validating photo entry update.", mCursor, updatedValues);
    }

    @Test
    public void update_VideosTable() {
        ContentValues values = TestUtilities.createVideoValue();

        long rowId = db.insert(Tables.VIDEOS, null, values);
        assertThat("Unable to Insert a video into the DB", rowId != -1);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(Videos.CAPTION, "Changed caption");
        updatedValues.put(Videos.THUMBNAIL_URL, "https://changedUrl.com");

        int count = mContext.getContentResolver()
                .update(Videos.buildVideoUri(updatedValues.getAsInteger(Videos.VIDEO_ID)),
                        updatedValues, null, null);
        assertThat(count, is(1));

        mCursor = db.query(Tables.VIDEOS,
                null,                           // columns
                Videos.VIDEO_ID + "=?",
                new String[] {updatedValues.getAsString(Videos.VIDEO_ID)},
                null,
                null,
                null,
                null);

        TestUtilities.validateCursor("Error validating video entry update.", mCursor, updatedValues);
    }

    @Test
    public void delete_articlesTable() {
        ContentValues values = TestUtilities.createArticleValues();
        int articleId = values.getAsInteger(Articles.ARTICLE_ID);

        long rowId = db.insert(Tables.ARTICLES, null, values);
        assertThat("Unable to Insert an article into the DB", rowId != -1);

        int count = mContext.getContentResolver().delete(Articles.buildArticleUri(articleId), null, null);
        assertThat(count, is(1));

        mCursor = db.query(
                Tables.ARTICLES,                            // tables
                null,                                       // columns
                Articles.ARTICLE_ID + "=?",                 // selection
                new String[] {String.valueOf(articleId)},   // selectionArgs
                null,                                       // groupBy
                null,                                       // having
                null);                                      // orderBy
        assertThat("Error: Delete an article unsuccessful", !mCursor.moveToFirst());
    }

    @Test
    public void delete_referenceArticlesTable() {
        ContentValues values = TestUtilities.createReferenceArticleValues();
        int referenceArticlesId = values.getAsInteger(ReferenceArticles.ARTICLE_ID);

        long rowId = db.insert(Tables.REFERENCE_ARTICLES, null, values);
        assertThat("Unable to Insert an reference_article into the DB", rowId != -1);

        int count = mContext.getContentResolver()
                .delete(ReferenceArticles.buildReferenceArticleUri(referenceArticlesId), null, null);
        assertThat(count, is(1));

        mCursor = db.query(
                Tables.REFERENCE_ARTICLES,                           // tables
                null,                                                // columns
                ReferenceArticles.ARTICLE_ID + "=?",                 // selection
                new String[] {String.valueOf(referenceArticlesId)},  // selectionArgs
                null,                                                // groupBy
                null,                                                // having
                null);                                               // orderBy
        assertThat("Error: Delete a reference_article unsuccessful", !mCursor.moveToFirst());
    }

    @Test
    public void delete_categoriesTable() {
        ContentValues values = TestUtilities.createCategoriesValues();
        int categoryId = values.getAsInteger(Categories.CATEGORY_ID);

        long rowId = db.insert(Tables.CATEGORIES, null, values);
        assertThat("Unable to Insert a category into the DB", rowId != -1);

        int count = mContext.getContentResolver()
                .delete(Categories.buildCategoryUri(categoryId), null, null);
        assertThat(count, is(1));

        mCursor = db.query(
                Tables.CATEGORIES,                          // tables
                null,                                       // columns
                Categories.CATEGORY_ID + "=?",              // selection
                new String[] {String.valueOf(categoryId)},  // selectionArgs
                null,                                       // groupBy
                null,                                       // having
                null);                                      // orderBy
        assertThat("Error: Delete a category unsuccessful", !mCursor.moveToFirst());
    }

    @Test
    public void delete_photosTable() {
        ContentValues values = TestUtilities.createPhotoValues();
        int photoId = values.getAsInteger(Photos.PHOTO_ID);

        long rowId = db.insert(Tables.PHOTOS, null, values);
        assertThat("Unable to Insert a photo into the DB", rowId != -1);

        int count = mContext.getContentResolver()
                .delete(Photos.buildPhotoUri(photoId), null, null);
        assertThat(count, is(1));

        mCursor = db.query(
                Tables.PHOTOS,                           // tables
                null,                                    // columns
                Photos.PHOTO_ID + "=?",                  // selection
                new String[] {String.valueOf(photoId)},  // selectionArgs
                null,                                    // groupBy
                null,                                    // having
                null);                                   // orderBy
        assertThat("Error: Delete a photo unsuccessful", !mCursor.moveToFirst());
    }

    @Test
    public void delete_videosTable() {
        ContentValues values = TestUtilities.createVideoValue();
        int videoId = values.getAsInteger(Videos.VIDEO_ID);

        long rowId = db.insert(Tables.VIDEOS, null, values);
        assertThat("Unable to Insert a video into the DB", rowId != -1);

        int count = mContext.getContentResolver()
                .delete(Videos.buildVideoUri(videoId), null, null);
        assertThat(count, is(1));

        mCursor = db.query(
                Tables.VIDEOS,                           // tables
                null,                                    // columns
                Videos.VIDEO_ID + "=?",                  // selection
                new String[] {String.valueOf(videoId)},  // selectionArgs
                null,                                    // groupBy
                null,                                    // having
                null);                                   // orderBy
        assertThat("Error: Delete a video unsuccessful", !mCursor.moveToFirst());
    }

    @Test
    public void getType_articleType_equalsExpectedResult() {
        final Uri uri = Articles.CONTENT_URI;
        final String expectedResult = ArticleContract.makeContentType(Articles.CONTENT_TYPE_ID);
        final String actualResult = mContext.getContentResolver().getType(uri);
        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void getType_articleIdType_equalsExpectedResult() {
        final Uri uri = Articles.buildArticleUri(425435);
        final String expectedResult = ArticleContract.makeContentItemType(Articles.CONTENT_TYPE_ID);
        final String actualResult = mContext.getContentResolver().getType(uri);
        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void getType_referenceArticleDirType_equalsExpectedResult() {
        final Uri uri = Articles.buildReferenceArticlesDirUri(25234);
        final String expectedResult = ArticleContract.makeContentType(ReferenceArticles.CONTENT_TYPE_ID);
        final String actualResult = mContext.getContentResolver().getType(uri);
        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void getType_categoriesItemType_equalsExpectedResult() {
        final Uri uri = Articles.buildCategoriesItemUri(25234);
        final String expectedResult = ArticleContract.makeContentItemType(Categories.CONTENT_TYPE_ID);
        final String actualResult = mContext.getContentResolver().getType(uri);
        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void getType_photosDirType_equalsExpectedResult() {
        final Uri uri = Articles.buildPhotosDirUri(25234);
        final String expectedResult = ArticleContract.makeContentType(Photos.CONTENT_TYPE_ID);
        final String actualResult = mContext.getContentResolver().getType(uri);
        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void getType_videosDirType_equalsExpectedResult() {
        final Uri uri = Articles.buildVideosDirUri(25234);
        final String expectedResult = ArticleContract.makeContentType(Videos.CONTENT_TYPE_ID);
        final String actualResult = mContext.getContentResolver().getType(uri);
        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void getType_referenceArticleType_equalsExpectedResult() {
        final Uri uri = ReferenceArticles.CONTENT_URI;
        final String expectedResult = ArticleContract.makeContentType(ReferenceArticles.CONTENT_TYPE_ID);
        final String actualResult = mContext.getContentResolver().getType(uri);
        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void getType_referenceArticleIdType_equalsExpectedResult() {
        final Uri uri = ReferenceArticles.buildReferenceArticleUri(53543);
        final String expectedResult = ArticleContract.makeContentItemType(ReferenceArticles.CONTENT_TYPE_ID);
        final String actualResult = mContext.getContentResolver().getType(uri);
        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void getType_categoriesType_equalsExpectedResult() {
        final Uri uri = Categories.CONTENT_URI;
        final String expectedResult = ArticleContract.makeContentType(Categories.CONTENT_TYPE_ID);
        final String actualResult = mContext.getContentResolver().getType(uri);
        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void getType_categoryIdType_equalsExpectedResult() {
        final Uri uri = Categories.buildCategoryUri(42535);
        final String expectedResult = ArticleContract.makeContentItemType(Categories.CONTENT_TYPE_ID);
        final String actualResult = mContext.getContentResolver().getType(uri);
        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void getType_photosType_equalsExpectedResult() {
        final Uri uri = Photos.CONTENT_URI;
        final String expectedResult = ArticleContract.makeContentType(Photos.CONTENT_TYPE_ID);
        final String actualResult = mContext.getContentResolver().getType(uri);
        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void getType_photoIdType_equalsExpectedResult() {
        final Uri uri = Photos.buildPhotoUri(424234);
        final String expectedResult = ArticleContract.makeContentItemType(Photos.CONTENT_TYPE_ID);
        final String actualResult = mContext.getContentResolver().getType(uri);
        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void getType_videosType_equalsExpectedResult() {
        final Uri uri = Videos.CONTENT_URI;
        final String expectedResult = ArticleContract.makeContentType(Videos.CONTENT_TYPE_ID);
        final String actualResult = mContext.getContentResolver().getType(uri);
        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void getType_videoIdType_equalsExpectedResult() {
        final Uri uri = Videos.buildVideoUri(424234);
        final String expectedResult = ArticleContract.makeContentItemType(Videos.CONTENT_TYPE_ID);
        final String actualResult = mContext.getContentResolver().getType(uri);
        assertThat(actualResult, is(expectedResult));
    }
}
