package com.phattn.vnexpressnews.provider;

import android.content.ContentValues;
import android.database.Cursor;

import com.phattn.vnexpressnews.provider.ArticleContract.Articles;
import com.phattn.vnexpressnews.provider.ArticleContract.Categories;
import com.phattn.vnexpressnews.provider.ArticleContract.Photos;
import com.phattn.vnexpressnews.provider.ArticleContract.ReferenceArticles;
import com.phattn.vnexpressnews.provider.ArticleContract.Videos;
import com.phattn.vnexpressnews.provider.ArticleDatabase.LinkedArticles;
import com.phattn.vnexpressnews.provider.ArticleDatabase.Tables;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class provides some helper methods for testing in this package.
 */
public class TestUtilities {

    static final String THUMBNAIL_URL_ALIAS = "source_url";

    static final String[] ARTICLES_COLUMN_NAMES = new String[] {
            buildFullQualifier(Tables.ARTICLES, Articles.ARTICLE_ID),
            buildFullQualifier(Tables.ARTICLES, Articles.ARTICLE_TYPE),
            buildFullQualifier(Tables.ARTICLES, Articles.ORIGINAL_CATEGORY),
            buildFullQualifier(Tables.ARTICLES, Articles.TITLE),
            buildFullQualifier(Tables.ARTICLES, Articles.LEAD),
            buildFullQualifier(Tables.ARTICLES, Articles.SHARE_URL),
            buildFullQualifier(Tables.ARTICLES, Articles.THUMBNAIL_URL),
            buildFullQualifier(Tables.ARTICLES, Articles.PRIVACY),
            buildFullQualifier(Tables.ARTICLES, Articles.TOTAL_PAGE),
            buildFullQualifier(Tables.ARTICLES, Articles.TOTAL_COMMENT),
            buildFullQualifier(Tables.ARTICLES, Articles.PUBLISH_TIME),
            buildFullQualifier(Tables.ARTICLES, Articles.SITE_ID),
            buildFullQualifier(Tables.ARTICLES, Articles.LIST_REFERENCE),
            buildFullQualifier(Tables.ARTICLES, Articles.CONTENT),
            buildFullQualifier(Tables.ARTICLES, Articles.PHOTOS),
            buildFullQualifier(Tables.ARTICLES, Articles.MODE_VIEW),
            buildFullQualifier(Tables.ARTICLES, Articles.CATEGORY_PARENT),
            buildFullQualifier(Tables.ARTICLES, Articles.VIDEOS)
    };

    static final String[] PHOTOS_COLUMN_NAMES = new String[] {
            buildFullQualifier(Tables.PHOTOS, Photos.PHOTO_ID),
            buildFullQualifier(Tables.PHOTOS, Photos.ARTICLE_ID),
            buildFullQualifier(Tables.PHOTOS, Photos.THUMBNAIL_URL) + " AS " + THUMBNAIL_URL_ALIAS,
            buildFullQualifier(Tables.PHOTOS, Photos.CAPTION)
    };

    static final String[] VIDEOS_COLUMN_NAMES = new String[] {
            buildFullQualifier(Tables.VIDEOS, Videos.VIDEO_ID),
            buildFullQualifier(Tables.VIDEOS, Videos.ARTICLE_ID),
            buildFullQualifier(Tables.VIDEOS, Videos.THUMBNAIL_URL) + " AS " + THUMBNAIL_URL_ALIAS,
            buildFullQualifier(Tables.VIDEOS, Videos.URL),
            buildFullQualifier(Tables.VIDEOS, Videos.DESCRIPTION),
            buildFullQualifier(Tables.VIDEOS, Videos.CAPTION),
            buildFullQualifier(Tables.VIDEOS, Videos.SIZE_FORMAT)

    };

    static final String[] REFERENCE_ARTICLES_COLUMN_NAMES = new String[] {
            buildFullQualifier(Tables.REFERENCE_ARTICLES, ReferenceArticles.ARTICLE_ID),
            buildFullQualifier(Tables.REFERENCE_ARTICLES, ReferenceArticles.ARTICLE_TYPE),
            buildFullQualifier(Tables.REFERENCE_ARTICLES, ReferenceArticles.ORIGINAL_CATEGORY),
            buildFullQualifier(Tables.REFERENCE_ARTICLES, ReferenceArticles.TITLE),
            buildFullQualifier(Tables.REFERENCE_ARTICLES, ReferenceArticles.SHARE_URL),
            buildFullQualifier(Tables.REFERENCE_ARTICLES, ReferenceArticles.THUMBNAIL_URL)
    };

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int index = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, index == -1);
            String expectedValue = entry.getValue().toString();
            String actualValue = valueCursor.getString(index);
            assertThat("Column '" + columnName + "' has not same value. " + error, actualValue, is(expectedValue));
        }
    }

    private static String buildFullQualifier(String tableName, String columnName) {
        return tableName + "." + columnName;
    }

    static String[] concatenate(String[] arr1, String[] arr2) {
        String[] result = new String[arr1.length + arr2.length];

        System.arraycopy(arr1, 0, result, 0, arr1.length);
        System.arraycopy(arr2, 0, result, arr1.length, arr2.length);

        return result;
    }
    /**
     *  Creates default article values for database tests
     */
    static ContentValues createArticleValues() {
        ContentValues values = new ContentValues();
        values.put(Articles.ARTICLE_ID, 3278628);
        values.put(Articles.ARTICLE_TYPE, 5);
        values.put(Articles.ORIGINAL_CATEGORY, 1003001);
        values.put(Articles.TITLE, "Djokovic hạ Federer, vô địch Mỹ Mở rộng");
        values.put(Articles.LEAD, "Tay vợt người Serbia thể hiện bản lĩnh, sự lạnh lùng");
        values.put(Articles.SHARE_URL, "http://example.com/demo");
        values.put(Articles.THUMBNAIL_URL, "https://example.com/sample");
        values.put(Articles.PRIVACY, 16386);
        values.put(Articles.TOTAL_PAGE, 2);
        values.put(Articles.TOTAL_COMMENT, 156);
        values.put(Articles.PUBLISH_TIME, 1442185088);
        values.put(Articles.SITE_ID, 1002565);
        values.put(Articles.LIST_REFERENCE, "3278939,3244353,7842842");
        values.put(Articles.CONTENT, "<p>Cơn mưa lớn ở New York khiến trận đấu được mong chờ giữa Roger Federer và Novak Djokovi</p>");
        values.put(Articles.PHOTOS, "4235256,5265470,9653923");
        values.put(Articles.MODE_VIEW, 1);
        values.put(Articles.CATEGORY_PARENT, 1002565);
        values.put(Articles.VIDEOS, "1953724,1842056,6799209");

        return values;
    }

    /**
     *  Creates default reference-article values for database tests
     */
    static ContentValues createReferenceArticleValues() {
        ContentValues values = new ContentValues();
        values.put(ReferenceArticles.ARTICLE_ID, 3278939);
        values.put(ReferenceArticles.ARTICLE_TYPE, 1);
        values.put(ReferenceArticles.ORIGINAL_CATEGORY, 0);
        values.put(ReferenceArticles.TITLE, "Federer hẹn trở lại Mỹ Mở rộng");
        values.put(ReferenceArticles.SHARE_URL, "http://vnexpress.net/demo");
        values.put(ReferenceArticles.THUMBNAIL_URL, "http://vnexpress.net/sample");

        return values;
    }

    /**
     *  Creates default category values for database tests
     */
    static ContentValues createCategoriesValues() {
        ContentValues values = new ContentValues();
        values.put(Categories.CATEGORY_ID, 1002565);
        values.put(Categories.CATEGORY_CODE, "thethao");
        values.put(Categories.CATEGORY_NAME, "Thể Thao");
        values.put(Categories.PARENT_ID, 0);
        values.put(Categories.FULL_PARENT, 1002565);
        values.put(Categories.SHOW_FOLDER, 1);
        values.put(Categories.DISPLAY_ORDER, 5);

        return values;
    }

    /**
     *  Creates default photo values for database tests
     */
    static ContentValues createPhotoValues() {
        ContentValues values = new ContentValues();
        values.put(Photos.PHOTO_ID, 23075971);
        values.put(Photos.ARTICLE_ID, 3278628);
        values.put(Photos.THUMBNAIL_URL, "http://image.cdn.example.com/demo");
        values.put(Photos.CAPTION, "<p>Federer</p>");

        return values;
    }

    /**
     *  Creates default video values for database tests
     */
    static ContentValues createVideoValue() {
        ContentValues values = new ContentValues();
        values.put(Videos.VIDEO_ID, 66027);
        values.put(Videos.ARTICLE_ID, 3278628);
        values.put(Videos.URL, "http://example.com/demo");
        values.put(Videos.CAPTION, "Novak Djokovic 3-1 Roger Federer");
        values.put(Videos.DESCRIPTION, "Chiến thắng trước Federer với tỷ số 6-4, 5-7, 6-4 và 6-4 giúp Djokovic đăng quang");
        values.put(Videos.THUMBNAIL_URL, "http://video.cdn.example.com/demo");
        values.put(Videos.SIZE_FORMAT, "{\"240\": \"http://video.cnd.example.com/demo/240/\"" +
                "\"360\": \"http://video.cnd.example.com/demo/360/\"" +
                "\"480\": \"http://video.cnd.example.com/demo/480/\"" +
                "\"720\": \"http://video.cnd.example.com/demo/720/\"}");

        return values;
    }

    /**
     * Creates default linked articles for database tests
     */
    static ContentValues createLinkedArticleValue() {
        ContentValues values = new ContentValues();
        values.put(LinkedArticles.ARTICLE_ID, 3278628);
        values.put(LinkedArticles.REFERENCE_ID, 3278939);

        return values;
    }
}
