package com.example.phat.vnexpressnews.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.MediumTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class tests the {@link ArticleDatabase} class work correctly
 */
@MediumTest
@RunWith(AndroidJUnit4.class)
public class ArticleDatabaseTest {

    private Context mContext;

    private Cursor cursor;

    private SQLiteDatabase db;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getTargetContext();
        // Starts with clean state
        deleteTheDatabase();
        db = new ArticleDatabase(mContext).getWritableDatabase();

    }

    @After
    public void tearDown() {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        if (db != null && db.isOpen()) {
            db.close();
        }
    }


    // Since we want each test to start with a clean state
    void deleteTheDatabase() {
        mContext.deleteDatabase(ArticleDatabase.DATABASE_NAME);
    }

    @Test
    public void databaseShouldBeCreated_ReturnsTrue() {
        assertTrue(db.isOpen());
    }

    /**
     * Tests all expected tables were created.
     */
    @Test
    public void expectedTablesShouldBeCreated_ReturnsTrue() {
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(ArticleDatabase.Tables.ARTICLES);
        tableNameHashSet.add(ArticleDatabase.Tables.LINKED_ARTICLES);
        tableNameHashSet.add(ArticleDatabase.Tables.REFERENCE_ARTICLES);
        tableNameHashSet.add(ArticleDatabase.Tables.CATEGORIES);
        tableNameHashSet.add(ArticleDatabase.Tables.PHOTOS);
        tableNameHashSet.add(ArticleDatabase.Tables.VIDEOS);

        cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: The database has not been created correctly", cursor.moveToFirst());

        // Verify that the tables have been created
        do {
            tableNameHashSet.remove(cursor.getString(0));
        } while (cursor.moveToNext());

        assertTrue("Error: The database was created but it did not create all expected tables",
                tableNameHashSet.isEmpty());

        cursor.close();
    }

    /**
     * Tests all columns in {@code Articles} table were created
     */
    @Test
    public void articlesTableShouldBeCreated_ReturnsTrue() {
        // Holds column names of tables
        final HashSet<String> tableColumnSet = new HashSet<>();

        // Test all columns of {@code Article} table was created
        tableColumnSet.add(ArticleContract.Articles._ID);
        tableColumnSet.add(ArticleContract.ArticlesColumns.ARTICLE_ID);
        tableColumnSet.add(ArticleContract.ArticlesColumns.ARTICLE_TYPE);
        tableColumnSet.add(ArticleContract.ArticlesColumns.ORIGINAL_CATEGORY);
        tableColumnSet.add(ArticleContract.ArticlesColumns.TITLE);
        tableColumnSet.add(ArticleContract.ArticlesColumns.LEAD);
        tableColumnSet.add(ArticleContract.ArticlesColumns.SHARE_URL);
        tableColumnSet.add(ArticleContract.ArticlesColumns.THUMBNAIL_URL);
        tableColumnSet.add(ArticleContract.ArticlesColumns.PRIVACY);
        tableColumnSet.add(ArticleContract.ArticlesColumns.TOTAL_COMMENT);
        tableColumnSet.add(ArticleContract.ArticlesColumns.TOTAL_PAGE);
        tableColumnSet.add(ArticleContract.ArticlesColumns.PUBLISH_TIME);
        tableColumnSet.add(ArticleContract.ArticlesColumns.SITE_ID);
        tableColumnSet.add(ArticleContract.ArticlesColumns.LIST_REFERENCE);
        tableColumnSet.add(ArticleContract.ArticlesColumns.CONTENT);
        tableColumnSet.add(ArticleContract.ArticlesColumns.PHOTOS);
        tableColumnSet.add(ArticleContract.ArticlesColumns.MODE_VIEW);
        tableColumnSet.add(ArticleContract.ArticlesColumns.CATEGORY_PARENT);
        tableColumnSet.add(ArticleContract.ArticlesColumns.VIDEOS);

        // Gets info of Article table
        cursor = db.rawQuery("PRAGMA table_info(" + ArticleDatabase.Tables.ARTICLES + ")", null);
        assertTrue("Error: Can not query article table information of the database.",
                cursor.moveToFirst());

        int columnNameIndex = cursor.getColumnIndex("name");
        do {
            String columnName = cursor.getString(columnNameIndex);
            tableColumnSet.remove(columnName);
        } while (cursor.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required " +
                "Article table columns", tableColumnSet.isEmpty());
    }

    /**
     * Tests all columns in {@code ReferenceArticles} table were created
     */
    @Test
    public void referenceArticlesTableShouldBeCreated_ReturnsTrue() {
        // Holds column names of tables
        final HashSet<String> tableColumnSet = new HashSet<>();

        // Test all columns of Reference Article table was created
        tableColumnSet.add(ArticleContract.ReferenceArticles._ID);
        tableColumnSet.add(ArticleContract.ReferenceArticlesColumns.ARTICLE_ID);
        tableColumnSet.add(ArticleContract.ReferenceArticlesColumns.ARTICLE_TYPE);
        tableColumnSet.add(ArticleContract.ReferenceArticlesColumns.ORIGINAL_CATEGORY);
        tableColumnSet.add(ArticleContract.ReferenceArticlesColumns.SHARE_URL);
        tableColumnSet.add(ArticleContract.ReferenceArticlesColumns.THUMBNAIL_URL);

        // Gets info of ReferenceArticle table
        cursor = db.rawQuery("PRAGMA table_info(" + ArticleDatabase.Tables.REFERENCE_ARTICLES + ")", null);
        assertTrue("Error: Can not query reference article table info of the database",
                cursor.moveToFirst());
        int columnNameIndex = cursor.getColumnIndex("name");
        do {
            String columnName = cursor.getString(columnNameIndex);
            tableColumnSet.remove(columnName);
        } while (cursor.moveToNext());
        assertTrue("Error: The database doesn't contain all of the required Reference Article" +
                " table column.", tableColumnSet.isEmpty());
    }

    /**
     * Tests all columns in {@code Categories} table were created
     */
    @Test
    public void categoriesTableShouldBeCreated_ReturnsTrue() {
        // Holds column names of tables
        final HashSet<String> tableColumnSet = new HashSet<>();

        // Test all columns of Categories table was created
        tableColumnSet.add(ArticleContract.Categories._ID);
        tableColumnSet.add(ArticleContract.CategoriesColumns.CATEGORY_ID);
        tableColumnSet.add(ArticleContract.CategoriesColumns.CATEGORY_NAME);
        tableColumnSet.add(ArticleContract.CategoriesColumns.CATEGORY_CODE);
        tableColumnSet.add(ArticleContract.CategoriesColumns.PARENT_ID);
        tableColumnSet.add(ArticleContract.CategoriesColumns.FULL_PARENT);
        tableColumnSet.add(ArticleContract.CategoriesColumns.SHOW_FOLDER);
        tableColumnSet.add(ArticleContract.CategoriesColumns.DISPLAY_ORDER);

        // Gets info of Categories table
        cursor = db.rawQuery("PRAGMA table_info(" + ArticleDatabase.Tables.CATEGORIES + ")", null);
        assertTrue("Error: Can not query categories table info.", cursor.moveToFirst());

        int columnNameIndex = cursor.getColumnIndex("name");
        do {
            String columnName = cursor.getString(columnNameIndex);
            tableColumnSet.remove(columnName);
        } while (cursor.moveToNext());
        assertTrue("Error: The database doesn't contain all of the required Categories " +
                "table columns.", tableColumnSet.isEmpty());
    }

    /**
     * Tests all columns in {@code Photos} table were created
     */
    @Test
    public void photosTableShouldBeCreated_ReturnsTrue() {
        // Holds column names of tables
        final HashSet<String> tableColumnSet = new HashSet<>();

        // Test all columns of Photos table was created
        tableColumnSet.add(ArticleContract.Photos._ID);
        tableColumnSet.add(ArticleContract.PhotosColumns.PHOTO_ID);
        tableColumnSet.add(ArticleContract.PhotosColumns.ARTICLE_ID);
        tableColumnSet.add(ArticleContract.PhotosColumns.THUMBNAIL_URL);
        tableColumnSet.add(ArticleContract.PhotosColumns.CAPTION);

        // Gets info of Photos table
        cursor = db.rawQuery("PRAGMA table_info(" + ArticleDatabase.Tables.PHOTOS + ")", null);
        assertTrue("Error: Can not query Photos table info.", cursor.moveToFirst());

        int columnNameIndex = cursor.getColumnIndex("name");
        do {
            String columnName = cursor.getString(columnNameIndex);
            tableColumnSet.remove(columnName);
        } while (cursor.moveToNext());
        assertTrue("Error: The database doesn't contain all of the required Photos " +
                "table columns.", tableColumnSet.isEmpty());
    }

    /**
     * Tests all columns in {@code Videos} table were created
     */
    @Test
    public void videosTableShouldBeCreated_ReturnsTrue() {
        // Holds column names of tables
        final HashSet<String> tableColumnSet = new HashSet<>();

        // Test all columns of Videos table was created
        tableColumnSet.add(ArticleContract.Videos._ID);
        tableColumnSet.add(ArticleContract.VideosColumns.VIDEO_ID);
        tableColumnSet.add(ArticleContract.VideosColumns.ARTICLE_ID);
        tableColumnSet.add(ArticleContract.VideosColumns.URL);
        tableColumnSet.add(ArticleContract.VideosColumns.CAPTION);
        tableColumnSet.add(ArticleContract.VideosColumns.DESCRIPTION);
        tableColumnSet.add(ArticleContract.VideosColumns.THUMBNAIL_URL);
        tableColumnSet.add(ArticleContract.VideosColumns.SIZE_FORMAT);

        // Gets info of Videos table
        cursor = db.rawQuery("PRAGMA table_info(" + ArticleDatabase.Tables.VIDEOS + ")", null);
        assertTrue("Error: Can not query Videos table info.", cursor.moveToFirst());

        int columnNameIndex = cursor.getColumnIndex("name");
        do {
            String columnName = cursor.getString(columnNameIndex);
            tableColumnSet.remove(columnName);
        } while (cursor.moveToNext());
        assertTrue("Error: The database doesn't contain all of the required Videos " +
                "table columns.", tableColumnSet.isEmpty());
    }

    /**
     * Tests all columns in {@code LinkedArticles} table were created
     */
    @Test
    public void linkedArticlesTableShouldBeCreated_ReturnsTrue() {
        // Holds column names of tables
        final HashSet<String> tableColumnSet = new HashSet<>();

        // Test all columns of Linked Articles table was created
        tableColumnSet.add(ArticleDatabase.LinkedArticles.ARTICLE_ID);
        tableColumnSet.add(ArticleDatabase.LinkedArticles.REFERENCE_ID);

        // Gets info of Link Articles table
        cursor = db.rawQuery("PRAGMA table_info(" + ArticleDatabase.Tables.LINKED_ARTICLES + ")", null);
        assertTrue("Error: Can not query Linked Articles table info.", cursor.moveToFirst());

        int columnNameIndex = cursor.getColumnIndex("name");
        do {
            String columnName = cursor.getString(columnNameIndex);
            tableColumnSet.remove(columnName);
        } while (cursor.moveToNext());
        assertTrue("Error: The database doesn't contain all of the required Linked Articles " +
                "table columns.", tableColumnSet.isEmpty());
    }

    /**
     * Tests linked_articles_delete trigger should be created and work correctly.
     * Explains about linked_articles_delete: When we delete an article, linked_articles_delete trigger
     * also delete related linked_article.
     */
    @Test
    public void linkedArticlesDeleteTrigger_shouldBeCreated() {
        ContentValues articleValues = TestUtilities.createArticleValues();
        ContentValues linkedArticlesValue = TestUtilities.createLinkedArticleValue();

        int articleId = articleValues.getAsInteger(ArticleContract.Articles.ARTICLE_ID);

        // Insert an articles and a linked_article into db
        long rowId = db.insert(ArticleDatabase.Tables.ARTICLES, null, articleValues);
        assertThat("Unable to Insert an article into the DB", rowId != -1);
        rowId = db.insert(ArticleDatabase.Tables.LINKED_ARTICLES, null, linkedArticlesValue);
        assertThat("Unable to Insert a linked_article into the DB", rowId != -1);

        // Query an article to assert that it was already inserted in DB
        cursor = db.query(ArticleDatabase.Tables.ARTICLES,
                null,
                ArticleContract.Articles.ARTICLE_ID + "=?",
                new String[]{String.valueOf(articleId)},
                null,
                null,
                null);
        TestUtilities.validateCursor(null, cursor, articleValues);

        // Query a linked_article to assert that it was already inserted in DB
        cursor = db.query(ArticleDatabase.Tables.LINKED_ARTICLES,
                null,
                ArticleDatabase.LinkedArticles.ARTICLE_ID + "=?",
                new String[]{String.valueOf(articleId)},
                null,
                null,
                null);
        TestUtilities.validateCursor(null, cursor, linkedArticlesValue);

        // Finally, delete the article to check that the linked_article also deletes in behind
        int count = db.delete(ArticleDatabase.Tables.ARTICLES,
                ArticleContract.Articles.ARTICLE_ID + "=?",
                new String[] {String.valueOf(articleId)});
        assertThat(count, is(1));

        cursor = db.query(ArticleDatabase.Tables.ARTICLES,
                null,
                ArticleContract.Articles.ARTICLE_ID + "=?",
                new String[]{String.valueOf(articleId)},
                null,
                null,
                null);
        assertThat("Error: Delete an article unsuccessful", !cursor.moveToFirst());

        cursor = db.query(ArticleDatabase.Tables.LINKED_ARTICLES,
                null,
                ArticleDatabase.LinkedArticles.ARTICLE_ID + "=?",
                new String[] {String.valueOf(articleId)},
                null,
                null,
                null);
        assertThat("Error: linked_articles_delete trigger didn't work.", !cursor.moveToFirst());
    }

    /**
     * Tests articles_photos_delete trigger should be created and work correctly
     * Explains about articles_photos_delete: When we delete an article, articles_photos_delete trigger
     * also delete related photos.
     */
    @Test
    public void articlesPhotosDeleteTrigger_shouldBeCreated() {
        ContentValues articleValues = TestUtilities.createArticleValues();
        ContentValues photoValues = TestUtilities.createPhotoValues();

        int articleId = articleValues.getAsInteger(ArticleContract.Articles.ARTICLE_ID);
        int photoId = photoValues.getAsInteger(ArticleContract.Photos.PHOTO_ID);

        // Insert an articles and a related photo into db
        long rowId = db.insert(ArticleDatabase.Tables.ARTICLES, null, articleValues);
        assertThat("Unable to Insert an article into the DB", rowId != -1);
        rowId = db.insert(ArticleDatabase.Tables.PHOTOS, null, photoValues);
        assertThat("Unable to Insert a photo into the DB", rowId != -1);

        // Query an article to assert that it was already inserted in DB
        cursor = db.query(ArticleDatabase.Tables.ARTICLES,
                null,
                ArticleContract.Articles.ARTICLE_ID + "=?",
                new String[]{String.valueOf(articleId)},
                null,
                null,
                null);
        TestUtilities.validateCursor(null, cursor, articleValues);

        // Query a photo to assert that it was already inserted in DB
        cursor = db.query(ArticleDatabase.Tables.PHOTOS,
                null,
                ArticleContract.Photos.PHOTO_ID + "=?",
                new String[]{String.valueOf(photoId)},
                null,
                null,
                null);
        TestUtilities.validateCursor(null, cursor, photoValues);

        // Finally, delete the article to check that the photo also deletes in behind
        int count = db.delete(ArticleDatabase.Tables.ARTICLES,
                ArticleContract.Articles.ARTICLE_ID + "=?",
                new String[] {String.valueOf(articleId)});
        assertThat(count, is(1));

        cursor = db.query(ArticleDatabase.Tables.ARTICLES,
                null,
                ArticleContract.Articles.ARTICLE_ID + "=?",
                new String[] {String.valueOf(articleId)},
                null,
                null,
                null);
        assertThat("Error: Delete an article unsuccessful", !cursor.moveToFirst());

        cursor = db.query(ArticleDatabase.Tables.PHOTOS,
                null,
                ArticleContract.Photos.PHOTO_ID + "=?",
                new String[] {String.valueOf(photoId)},
                null,
                null,
                null);
        assertThat("Error: articles_photos_delete trigger didn't work.", !cursor.moveToFirst());
    }

    /**
     * Tests articles_videos_delete trigger should be created and work correctly
     * Explains about articles_videos_delete: When we delete an article, articles_videos_delete trigger
     * also delete related videos.
     */
    @Test
    public void articlesVideosDeleteTrigger_shouldBeCreated() {
        ContentValues articleValues = TestUtilities.createArticleValues();
        ContentValues videoValues = TestUtilities.createVideoValue();

        int articleId = articleValues.getAsInteger(ArticleContract.Articles.ARTICLE_ID);
        int videoId = videoValues.getAsInteger(ArticleContract.Videos.VIDEO_ID);

        // Insert an articles and a related video into db
        long rowId = db.insert(ArticleDatabase.Tables.ARTICLES, null, articleValues);
        assertThat("Unable to Insert an article into the DB", rowId != -1);
        rowId = db.insert(ArticleDatabase.Tables.VIDEOS, null, videoValues);
        assertThat("Unable to Insert a video into the DB", rowId != -1);

        // Query an article to assert that it was already inserted in DB
        cursor = db.query(ArticleDatabase.Tables.ARTICLES,
                null,
                ArticleContract.Articles.ARTICLE_ID + "=?",
                new String[]{String.valueOf(articleId)},
                null,
                null,
                null);
        TestUtilities.validateCursor(null, cursor, articleValues);

        // Query a video to assert that it was already inserted in DB
        cursor = db.query(ArticleDatabase.Tables.VIDEOS,
                null,
                ArticleContract.Videos.VIDEO_ID + "=?",
                new String[]{String.valueOf(videoId)},
                null,
                null,
                null);
        TestUtilities.validateCursor(null, cursor, videoValues);

        // Finally, delete the article to check that the video also deletes in behind
        int count = db.delete(ArticleDatabase.Tables.ARTICLES,
                ArticleContract.Articles.ARTICLE_ID + "=?",
                new String[] {String.valueOf(articleId)});
        assertThat(count, is(1));

        cursor = db.query(ArticleDatabase.Tables.ARTICLES,
                null,
                ArticleContract.Articles.ARTICLE_ID + "=?",
                new String[] {String.valueOf(articleId)},
                null,
                null,
                null);
        assertThat("Error: Delete an article unsuccessful", !cursor.moveToFirst());

        cursor = db.query(ArticleDatabase.Tables.VIDEOS,
                null,
                ArticleContract.Videos.VIDEO_ID + "=?",
                new String[] {String.valueOf(videoId)},
                null,
                null,
                null);
        assertThat("Error: articles_videos_delete trigger didn't work.", !cursor.moveToFirst());
    }

    /**
     * Tests reference_articles_delete trigger should be created and work correctly
     * Explains about reference_articles_delete: When we delete a linked_article, reference_articles_delete trigger
     * will check if related reference_article doesn't link to any other linked_articles. It will deletes that reference_article.
     */
    @Test
    public void referenceArticlesDeleteTrigger_shouldBeCreated() {
        ContentValues referenceArticlesValues = TestUtilities.createReferenceArticleValues();
        ContentValues linkedArticleValues = TestUtilities.createLinkedArticleValue();

        int referenceArticleId = referenceArticlesValues.getAsInteger(ArticleContract.ReferenceArticles.ARTICLE_ID);
        int referenceId = linkedArticleValues.getAsInteger(ArticleDatabase.LinkedArticles.REFERENCE_ID);

        // Insert a reference_article and a related linked_article into db
        long rowId = db.insert(ArticleDatabase.Tables.REFERENCE_ARTICLES, null, referenceArticlesValues);
        assertThat("Unable to Insert a reference_article into the DB", rowId != -1);
        rowId = db.insert(ArticleDatabase.Tables.LINKED_ARTICLES, null, linkedArticleValues);
        assertThat("Unable to Insert a linked_article into the DB", rowId != -1);

        // Query an article to assert that it was already inserted in DB
        cursor = db.query(ArticleDatabase.Tables.REFERENCE_ARTICLES,
                null,
                ArticleContract.ReferenceArticles.ARTICLE_ID + "=?",
                new String[]{String.valueOf(referenceArticleId)},
                null,
                null,
                null);
        TestUtilities.validateCursor(null, cursor, referenceArticlesValues);

        // Query a linked_article to assert that it was already inserted in DB
        cursor = db.query(ArticleDatabase.Tables.LINKED_ARTICLES,
                null,
                ArticleDatabase.LinkedArticles.REFERENCE_ID + "=?",
                new String[]{String.valueOf(referenceId)},
                null,
                null,
                null);
        TestUtilities.validateCursor(null, cursor, linkedArticleValues);

        // Finally, delete the linked_article to check that the reference_article also deletes in behind
        int count = db.delete(ArticleDatabase.Tables.LINKED_ARTICLES,
                ArticleDatabase.LinkedArticles.REFERENCE_ID + "=?",
                new String[] {String.valueOf(referenceId)});
        assertThat(count, is(1));

        cursor = db.query(ArticleDatabase.Tables.LINKED_ARTICLES,
                null,
                ArticleDatabase.LinkedArticles.REFERENCE_ID + "=?",
                new String[] {String.valueOf(referenceId)},
                null,
                null,
                null);
        assertThat("Error: Delete an article unsuccessful", !cursor.moveToFirst());

        cursor = db.query(ArticleDatabase.Tables.REFERENCE_ARTICLES,
                null,
                ArticleContract.ReferenceArticles.ARTICLE_ID + "=?",
                new String[] {String.valueOf(referenceArticleId)},
                null,
                null,
                null);
        assertThat("Error: reference_article_delete trigger didn't work.", !cursor.moveToFirst());
    }

    /**
     * <em>Note:</em> This test <i>does not</i> check {@code insert} function
     * of {@code ContentProvider}. The test only asserts that all columns were created
     * with correct data type. So, in this test, we use pure {@code insert} function of
     * {@code SQLiteDatabase}
     */
    @Test
    public void checkInsertIntoArticlesTable() {
        ContentValues testValues = TestUtilities.createArticleValues();

        long rowId = db.insert(ArticleDatabase.Tables.ARTICLES, null, testValues);

        assertTrue(rowId != -1);

        cursor = db.query(
                ArticleDatabase.Tables.ARTICLES,
                null, // column
                null, // selections
                null, // selection args
                null, // groupBy
                null, // having
                null); // orderBy

        assertTrue("Error: No records returned from Articles table", cursor.moveToFirst());
        TestUtilities.validateCurrentRecord("Error: Article query validation failed",
                cursor, testValues);

        assertFalse("Error: More than one record return from Articles query", cursor.moveToNext());

    }

    /**
     * <em>Note:</em> This test <i>does not</i> check {@code insert} function
     * of {@code ContentProvider}. The test only asserts that all columns were created
     * with correct data type. So, in this test, we use pure {@code insert} function of
     * {@code SQLiteDatabase}
     */
    @Test
    public void checkInsertIntoReferenceArticlesTable() {
        ContentValues testValue = TestUtilities.createReferenceArticleValues();

        long rowId = db.insert(ArticleDatabase.Tables.REFERENCE_ARTICLES, null, testValue);

        assertTrue(rowId != -1);

        cursor = db.query(
                ArticleDatabase.Tables.REFERENCE_ARTICLES,
                null, // column
                null, // selections
                null, // selection args
                null, // groupBy
                null, // having
                null); // orderBy

        assertTrue("Error: No records returned from Reference_Articles table", cursor.moveToFirst());
        TestUtilities.validateCurrentRecord("Error: Reference_Articles query validation failed",
                cursor, testValue);

        assertFalse("Error: More than one record return from Reference_Articles query", cursor.moveToNext());
    }

    /**
     * <em>Note:</em> This test <i>does not</i> check {@code insert} function
     * of {@code ContentProvider}. The test only asserts that all columns were created
     * with correct data type. So, in this test, we use pure {@code insert} function of
     * {@code SQLiteDatabase}
     */
    @Test
    public void checkInsertIntoCategoriesTable() {
        ContentValues testValue = TestUtilities.createCategoriesValues();

        long rowId = db.insert(ArticleDatabase.Tables.CATEGORIES, null, testValue);

        assertTrue(rowId != -1);

        cursor = db.query(
                ArticleDatabase.Tables.CATEGORIES,
                null, // column
                null, // selections
                null, // selection args
                null, // groupBy
                null, // having
                null); // orderBy

        assertTrue("Error: No records returned from Categories table", cursor.moveToFirst());
        TestUtilities.validateCurrentRecord("Error: Categories query validation failed",
                cursor, testValue);

        assertFalse("Error: More than one record return from Categories query", cursor.moveToNext());
    }

    /**
     * <em>Note:</em> This test <i>does not</i> check {@code insert} function
     * of {@code ContentProvider}. The test only asserts that all columns were created
     * with correct data type. So, in this test, we use pure {@code insert} function of
     * {@code SQLiteDatabase}
     */
    @Test
    public void checkInsertIntoPhotosTable() {
        ContentValues testValue = TestUtilities.createPhotoValues();

        long rowId = db.insert(ArticleDatabase.Tables.PHOTOS, null, testValue);

        assertTrue(rowId != -1);

        cursor = db.query(
                ArticleDatabase.Tables.PHOTOS,
                null, // column
                null, // selections
                null, // selection args
                null, // groupBy
                null, // having
                null); // orderBy

        assertTrue("Error: No records returned from Photos table", cursor.moveToFirst());
        TestUtilities.validateCurrentRecord("Error: Photos query validation failed",
                cursor, testValue);

        assertFalse("Error: More than one record return from Photos query", cursor.moveToNext());
    }

    /**
     * <em>Note:</em> This test <i>does not</i> check {@code insert} function
     * of {@code ContentProvider}. The test only asserts that all columns were created
     * with correct data type. So, in this test, we use pure {@code insert} function of
     * {@code SQLiteDatabase}
     */
    @Test
    public void checkInsertIntoVideosTable() {
        ContentValues testValue = TestUtilities.createVideoValue();

        long rowId = db.insert(ArticleDatabase.Tables.VIDEOS, null, testValue);

        assertTrue(rowId != -1);

        cursor = db.query(
                ArticleDatabase.Tables.VIDEOS,
                null, // column
                null, // selections
                null, // selection args
                null, // groupBy
                null, // having
                null); // orderBy

        assertTrue("Error: No records returned from Videos table", cursor.moveToFirst());
        TestUtilities.validateCurrentRecord("Error: Videos query validation failed",
                cursor, testValue);

        assertFalse("Error: More than one record return from Videos query", cursor.moveToNext());
    }

    /**
     * <em>Note:</em> This test <i>does not</i> check {@code insert} function
     * of {@code ContentProvider}. The test only asserts that all columns were created
     * with correct data type. So, in this test, we use pure {@code insert} function of
     * {@code SQLiteDatabase}
     */
    @Test
    public void checkInsertIntoLinkedArticlesTable() {
        ContentValues testValue = TestUtilities.createLinkedArticleValue();

        long rowId = db.insert(ArticleDatabase.Tables.LINKED_ARTICLES, null, testValue);

        assertTrue(rowId != -1);

        cursor = db.query(
                ArticleDatabase.Tables.LINKED_ARTICLES,
                null, // column
                null, // selections
                null, // selection args
                null, // groupBy
                null, // having
                null); // orderBy

        assertTrue("Error: No records returned from Linked Articles table", cursor.moveToFirst());
        TestUtilities.validateCurrentRecord("Error: Linked Articles query validation failed",
                cursor, testValue);

        assertFalse("Error: More than one record return from Linked Articles query", cursor.moveToNext());
    }

}
