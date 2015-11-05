package com.example.phat.vnexpressnews.provider;

import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import com.example.phat.vnexpressnews.provider.ArticleContract.Articles;
import com.example.phat.vnexpressnews.provider.ArticleContract.Categories;
import com.example.phat.vnexpressnews.provider.ArticleContract.Photos;
import com.example.phat.vnexpressnews.provider.ArticleContract.ReferenceArticles;
import com.example.phat.vnexpressnews.provider.ArticleContract.Videos;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * This class tests that the {@link ArticleProviderUriMatcher} work correcly.
 */
@SmallTest
@RunWith(AndroidJUnit4.class)
public class ArticleProviderUriMatcherTest {

    private ArticleProviderUriMatcher uriMatcher;

    private final static String BASE_CONTENT_URI = ArticleContract.BASE_CONTENT_URI.toString();

    @Before
    public void setUp() {
        uriMatcher = new ArticleProviderUriMatcher();
    }


    @Test
    public void matchCode_CorrectCode_equalsExpectedResult() {
        final ArticleUriEnum expectedResult = ArticleUriEnum.ARTICLES_ID;
        final ArticleUriEnum actualResult = uriMatcher.matchCode(expectedResult.code);
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void matchCode_IncorrectCode_ThrowsUOE() {
        final int code = 107;
        uriMatcher.matchCode(code);
    }

    @Test
    public void matchUri_CorrectUri_equalsExpectedResult() {
        final Uri uri = Uri.parse(BASE_CONTENT_URI + "/articles/1432");
        final ArticleUriEnum expectedResult = ArticleUriEnum.ARTICLES_ID;
        final ArticleUriEnum actualResult = uriMatcher.matchUri(uri);
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void matchUri_IncorrectUri_ThrowsUOE() {
        final Uri uri = Uri.parse(BASE_CONTENT_URI + "/incorrectpath/1432");
        uriMatcher.matchUri(uri);
    }

    @Test
    public void matchUri_ArticleUri_EqualsExpectedResult() {
        final Uri uri = Uri.parse(BASE_CONTENT_URI).buildUpon().appendPath(ArticleContract.PATH_ARTICLES).build();
        final ArticleUriEnum expectedResult = ArticleUriEnum.ARTICLES;
        final ArticleUriEnum actualResult = uriMatcher.matchUri(uri);
        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void matchUri_ArticleIdUri_EqualsExpectedResult() {
        final int articleId = 3278628;
        final Uri uri = Articles.buildArticleUri(articleId);
        final ArticleUriEnum expectedResult = ArticleUriEnum.ARTICLES_ID;
        final ArticleUriEnum actualResult = uriMatcher.matchUri(uri);
        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void matchUri_ArticleIdCategoriesUri_EqualsExpectedResult() {
        final int articleId = 3278628;
        final Uri uri = Articles.buildCategoriesItemUri(articleId);
        final ArticleUriEnum expectedResult = ArticleUriEnum.ARTICLES_ID_CATEGORIES;
        final ArticleUriEnum actualResult = uriMatcher.matchUri(uri);
        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void matchUri_ArticleIdPhotosUri_EqualsExpectedResult() {
        final int articleId = 3278628;
        final Uri uri = Articles.buildPhotosDirUri(articleId);
        final ArticleUriEnum expectedResult = ArticleUriEnum.ARTICLES_ID_PHOTOS;
        final ArticleUriEnum actualResult = uriMatcher.matchUri(uri);
        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void matchUri_ArticleIdVideosUri_EqualsExpectedResult() {
        final int articleId = 3278628;
        final Uri uri = Articles.buildVideosDirUri(articleId);
        final ArticleUriEnum expectedResult = ArticleUriEnum.ARTICLES_ID_VIDEOS;
        final ArticleUriEnum actualResult = uriMatcher.matchUri(uri);
        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void matchUri_ReferenceArticleUri_EqualsExpectedResult() {
        final Uri uri = Uri.parse(BASE_CONTENT_URI).buildUpon().appendPath(ArticleContract.PATH_REFERENCE_ARTICLES).build();
        final ArticleUriEnum expectedResult = ArticleUriEnum.REFERENCE_ARTICLE;
        final ArticleUriEnum actualResult = uriMatcher.matchUri(uri);
        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void matchUri_ReferenceArticleIdUri_EqualsExpectedResult() {
        final int referenceArticleId = 3278939;
        final Uri referenceArticleUri = ReferenceArticles.buildReferenceArticleUri(referenceArticleId);
        final ArticleUriEnum expectedResult = ArticleUriEnum.REFERENCE_ARTICLE_ID;
        final ArticleUriEnum actualResult = uriMatcher.matchUri(referenceArticleUri);
        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void matchUri_CategoriesUri_EqualsExpectedResult() {
        final Uri uri = Uri.parse(BASE_CONTENT_URI).buildUpon().appendPath(ArticleContract.PATH_CATEGORIES).build();
        final ArticleUriEnum expectedResult = ArticleUriEnum.CATEGORIES;
        final ArticleUriEnum actualResult = uriMatcher.matchUri(uri);
        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void matchUri_CategoryIdUri_EqualsExpectedResult() {
        final int categoryId = 1002565;
        final Uri uri = Categories.buildCategoryUri(categoryId);
        final ArticleUriEnum expectedResult = ArticleUriEnum.CATEGORIES_ID;
        final ArticleUriEnum actualResult = uriMatcher.matchUri(uri);
        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void matchUri_PhotosUri_EqualsExpectedResult() {
        final Uri uri = Uri.parse(BASE_CONTENT_URI).buildUpon().appendPath(ArticleContract.PATH_PHOTOS).build();
        final ArticleUriEnum expectedResult = ArticleUriEnum.PHOTOS;
        final ArticleUriEnum actualResult = uriMatcher.matchUri(uri);
        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void matchUri_PhotoIdUri_EqualsExpectedResult() {
        final int photoId = 23075971;
        final Uri uri = Photos.buildPhotoUri(photoId);
        final ArticleUriEnum expectedResult = ArticleUriEnum.PHOTOS_ID;
        final ArticleUriEnum actualResult = uriMatcher.matchUri(uri);
        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void matchUri_VideosUri_EqualsExpectedResult() {
        final Uri uri = Uri.parse(BASE_CONTENT_URI).buildUpon().appendPath(ArticleContract.PATH_VIDEOS).build();
        final ArticleUriEnum expectedResult = ArticleUriEnum.VIDEOS;
        final ArticleUriEnum actualResult = uriMatcher.matchUri(uri);
        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void matchUri_VideoIdUri_EqualsExpectedResult() {
        final int videoId = 23075971;
        final Uri uri = Videos.buildVideoUri(videoId);
        final ArticleUriEnum expectedResult = ArticleUriEnum.VIDEOS_ID;
        final ArticleUriEnum actualResult = uriMatcher.matchUri(uri);
        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void matchUri_LinkedArticleUri_EqualsExpectedResult() {
        final int articleId = 3278628;
        final Uri uri = Articles.buildReferenceArticlesDirUri(articleId);
        final ArticleUriEnum expectedResult = ArticleUriEnum.LINKED_ARTICLES;
        final ArticleUriEnum actualResult = uriMatcher.matchUri(uri);
        assertThat(actualResult, is(expectedResult));
    }

}
