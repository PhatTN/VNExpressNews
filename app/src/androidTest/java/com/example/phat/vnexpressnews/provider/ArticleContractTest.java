package com.example.phat.vnexpressnews.provider;

import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * This class test {@link ArticleContract} class work correctly
 */
@SmallTest
@RunWith(AndroidJUnit4.class)
public class ArticleContractTest {

    private final static String BASE_CONTENT_URI = ArticleContract.BASE_CONTENT_URI.toString();

    @Test
    public void makeContentType_CorrectFormatId_equalsExpectedResult() {
        final String id = "936js23";
        final String expectedResult = "vnd.android.cursor.dir/vnd.vnexpressnews." + id;
        final String actualResult = ArticleContract.makeContentType(id);
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    @Test
    public void makeContentType_NullId_ReturnsNull() {
        final String id = null;
        final String actualResult = ArticleContract.makeContentType(id);
        assertThat(actualResult, is(nullValue()));
    }

    @Test
    public void makeContentType_EmptyId_equalsExpectedResult() {
        final String id = "";
        final String expectedResult = "vnd.android.cursor.dir/vnd.vnexpressnews.";
        final String actualResult = ArticleContract.makeContentType(id);
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    @Test
    public void makeContentItemType_CorrectFormatId_equalsExpectedResult() {
        final String id = "423jkfs";
        final String expectedResult = "vnd.android.cursor.item/vnd.vnexpressnews." + id;
        final String actualResult = ArticleContract.makeContentItemType(id);
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    @Test
    public void makeContentItemType_NullId_ReturnsNull() {
        final String id = null;
        final String actualResult = ArticleContract.makeContentItemType(id);
        assertThat(actualResult, is(nullValue()));
    }

    @Test
    public void makeContentItemType_EmptyId_equalsExpectedResult() {
        final String id = "";
        final String expectedResult = "vnd.android.cursor.item/vnd.vnexpressnews.";
        final String actualResult = ArticleContract.makeContentItemType(id);
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    @Test
    public void buildArticlesUri_equalsExpectedResult() {
        final int articleId = 83120;
        final String expectedResult = BASE_CONTENT_URI + "/articles/" + articleId;
        final String actualResult = ArticleContract.Articles.buildArticleUri(articleId).toString();
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    @Test
    public void getArticleId_equalsExpectedResult() {
        final int expectedResult = 84923480;
        final Uri uri = Uri.parse(BASE_CONTENT_URI + "/articles/" + expectedResult);
        final int actualValue = ArticleContract.Articles.getArticleId(uri);
        assertThat(actualValue, is(equalTo(expectedResult)));
    }

    @Test
    public void buildReferenceArticlesDirUri_equalsExpectedResult() {
        final int articleId = 83120;
        final String expectedResult = BASE_CONTENT_URI + "/articles/" + articleId + "/reference_articles";
        final String actualResult = ArticleContract.Articles.buildReferenceArticlesDirUri(articleId).toString();
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    @Test
    public void buildCategoriesItemUri_equalsExpectedResult() {
        final int articleId = 83120;
        final String expectedResult = BASE_CONTENT_URI + "/articles/" + articleId + "/categories";
        final String actualResult = ArticleContract.Articles.buildCategoriesItemUri(articleId).toString();
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    @Test
    public void buildPhotosDirUri_equalsExpectedResult() {
        final int articleId = 83120;
        final String expectedResult = BASE_CONTENT_URI + "/articles/" + articleId + "/photos";
        final String actualResult = ArticleContract.Articles.buildPhotosDirUri(articleId).toString();
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    @Test
    public void buildVideosDirUri_equalsExpectedResult() {
        final int articleId = 83120;
        final String expectedResult = BASE_CONTENT_URI + "/articles/" + articleId + "/videos";
        final String actualResult = ArticleContract.Articles.buildVideosDirUri(articleId).toString();
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    @Test
    public void buildReferenceArticlesUri_equalsExpectedResult() {
        final int referenceArticleId = 24334;
        final String expectedResult = BASE_CONTENT_URI + "/reference_articles/" + referenceArticleId;
        final String actualResult = ArticleContract.ReferenceArticles.
                buildReferenceArticleUri(referenceArticleId).toString();
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    @Test
    public void getReferenceArticleId_equalsExpectedResult() {
        final int expectedResult = 8403840;
        final Uri uri = Uri.parse(BASE_CONTENT_URI + "/reference_articles/" + expectedResult);
        final int actualValue = ArticleContract.ReferenceArticles.getReferenceArticleId(uri);
        assertThat(actualValue, is(expectedResult));
    }

    @Test
    public void buildPhotosUri_equalsExpectedResult() {
        final int photoId = 428340;
        final String expectedResult = BASE_CONTENT_URI + "/photos/" + photoId;
        final String actualResult = ArticleContract.Photos.buildPhotoUri(photoId).toString();
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    @Test
    public void getPhotoId_equalsExpectedResult() {
        final int expectedResult = 3019239;
        final Uri uri = Uri.parse(BASE_CONTENT_URI + "/photos/" + expectedResult);
        final int actualResult = ArticleContract.Photos.getPhotoId(uri);
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    @Test
    public void buildCategoriesUri_equalsExpectedResult() {
        final int categoryId = 80423;
        final String expectedResult = BASE_CONTENT_URI + "/categories/" + categoryId;
        final String actualResult = ArticleContract.Categories.buildCategoryUri(categoryId).toString();
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    @Test
    public void getCategoryId_equalsExpectedResult() {
        final int expectedResult = 84230;
        final Uri uri = Uri.parse(BASE_CONTENT_URI + "/categories/" + expectedResult);
        final int actualResult = ArticleContract.Categories.getCategoryId(uri);
        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void buildVideosUri_equalsExpectedResult() {
        final int videoId = 89328049;
        final String expectedResult = BASE_CONTENT_URI + "/videos/" + videoId;
        final String actualResult = ArticleContract.Videos.buildVideoUri(videoId).toString();
        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void getVideoId_equalsExpectedResult() {
        final int expectedResult = 8904203;
        final Uri uri = Uri.parse(BASE_CONTENT_URI + "/videos/" + expectedResult);
        final int actualResult = ArticleContract.Categories.getCategoryId(uri);
        assertThat(actualResult, is(equalTo(expectedResult)));
    }


}
