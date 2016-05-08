package com.phattn.vnexpressnews.util;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import com.phattn.vnexpressnews.Config;
import com.phattn.vnexpressnews.model.Category;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * The class tests all public methods in {@link APIEndpointBuilder} class.
 * We create APIEndpointBuilder instance in each tests to ensure each tests is independent.
 *
 * In some test cases, we will provide all properties of {@code APIEndpointBuilder} to ensure
 * providing all properties does not affect to result. Of course, except APIEndpointType property,
 * we won't set that property, because that action will changes behaviour of builder.
 * Instead of creating APIEndpoint we want to test, it will creates based on the type which is set to.
 * That is unexpected behaviour of tests.
 */
@SmallTest
@RunWith(AndroidJUnit4.class)
public class APIEndpointBuilderTest {

    private static final int MOCK_CATEGORY_ID = 435453;
    private static final int MOCK_ARTICLE_ID = 6363453;
    private static final int MOCK_SITE_ID = 9572010;
    private static final int MOCK_AUTHOR_ID = 2947852;
    private static final String MOCK_ARTICLE_TITLE = "Bổ nhiệm lãnh đạo trẻ cần sự minh bạch";
    private static final String MOCK_ARTICLE_TITLE_ENCODED = "B%E1%BB%95%20nhi%E1%BB%87m%20l%C3%A3nh" +
            "%20%C4%91%E1%BA%A1o%20tr%E1%BA%BB%20c%E1%BA%A7n%20s%E1%BB%B1%20minh%20b%E1%BA%A1ch";
    private static final String MOCK_SEARCH_KEYWORD = "TP HCM";
    private static final String MOCK_SEARCH_KEYWORD_ENCODED = "TP%20HCM";
    private static final APIEndpointBuilder.APIEndpointType MOCK_API_ENDPOINT_TYPE =
            APIEndpointBuilder.APIEndpointType.API_NEWS_WITH_CATEGORY;
    private static final List<Category> MOCK_CATEGORY_LIST;

    static {
        MOCK_CATEGORY_LIST = new ArrayList<>(6);
        int[] categoryIds = {1001005, 1001002, 1003450, 1003159, 1002691, 1002565};

        for(Integer id : categoryIds) {
            Category temp = new Category();
            temp.setCategoryID(id);
            MOCK_CATEGORY_LIST.add(temp);
        }
    }

    @Test
    public void createNewAPIEndpointBuilder_ReturnsTrue() {
        APIEndpointBuilder builder = new APIEndpointBuilder(MOCK_API_ENDPOINT_TYPE);
        assertTrue(MOCK_API_ENDPOINT_TYPE == builder.getAPIEndpointType());
    }

    /**
     * Creating APIEndPoint ApiFolderList does not need to provide any properties. It creates based on
     * configuration of app.
     */
    @Test
    public void createApiFolderList_ProvidedNothing_equalsExpectedResult() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_FOLDERS_LIST);

        final String expectedResult = "http://api3.vnexpress.net/api/category?type=get_menu&" +
                "site_id=1000000&show_folder=1&app_detail=1&deep=1&app_id=" + Config.APP_ID;
        final String actualResult = builder.build();
        assertThat(actualResult, is(equalTo(expectedResult))); // You can use is(expectedResult). I use is(equalTo(expectedResult) for readable and understandable
    }

    /**
     * Creating APIEndPoint ApiFolderList does not need to provide any property. It creates based on
     * configuration of app.
     * But try to test providing all properties (of course, except APIEndpointType, because that action
     * will changes behaviour of builder. Instead of creating ApiFolderList, it will creates based on type
     * which is set to) to ensure providing property does not affect to result
     */
    @Test
    public void createApiFolderList_ProvidedAllProperties_equalsExpectedResult() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_FOLDERS_LIST);

        builder.setArticleId(MOCK_ARTICLE_ID);
        builder.setCategoryId(MOCK_CATEGORY_ID);
        builder.setSiteId(MOCK_SITE_ID);
        builder.setAuthorId(MOCK_AUTHOR_ID);
        builder.setArticleTitle(MOCK_ARTICLE_TITLE);
        builder.setSearchKeyword(MOCK_SEARCH_KEYWORD);
        builder.setCategoryList(MOCK_CATEGORY_LIST);

        final String expectedResult = "http://api3.vnexpress.net/api/category?type=get_menu&" +
                "site_id=1000000&show_folder=1&app_detail=1&deep=1&app_id=" + Config.APP_ID;
        final String actualResult = builder.build();
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    /**
     * In order to create ApiTopNews, we need to provide category list. But if we don't.
     * It will uses default category list in {@link com.phattn.vnexpressnews.Config} class.
     */
    @Test
    public void createApiTopNews_ProvideNothing_equalsExpectedResult() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_TOP_NEWS);

        // The complex URL :D
        final String expectedResult = "http://api3.vnexpress.net/api/group?api%5B%5D=article%3Ftype" +
                "%3Dget_topstory%26showed_area%3Dtrangchu_beta%26cate_id%3D1000000%26limit%3D15&" +
                "api%5B%5D=article%3Ftype%3Dget_rule_2%26cate_id%3D1003450%26limit%3D1%26option%3D" +
                "object&api%5B%5D=article%3Ftype%3Dget_topstory%26showed_area%3Dvne_video%26cate_id%3D" +
                "1000000%26limit%3D3&api%5B%5D=article%3Ftype%3Dget_rule_2%26cate_id%3D1001005%252C1001002%252C" +
                "1003159%252C1002691%252C1002565%252C1001007%252C1003497%252C1003750%252C1002966%252C" +
                "1003231%252C1001009%252C1002592%252C1001006%252C1001012%252C1001014%252C1001019%252C" +
                "1001011%26limit%3D5&api%5B%5D=article%3Ftype%3Dget_topstory%26showed_area%3Dvne_anh%26" +
                "cate_id%3D1000000%26limit%3D6&app_id=" + Config.APP_ID;

        final String actualResult = builder.build();
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    @Test
    public void createApiTopNews_ProvideCategoryList_equalsExpectedResult() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_TOP_NEWS);

        builder.setCategoryList(MOCK_CATEGORY_LIST);

        // The complex URL :D
        final String expectedResult = "http://api3.vnexpress.net/api/group?api%5B%5D=article%3Ftype" +
                "%3Dget_topstory%26showed_area%3Dtrangchu_beta%26cate_id%3D1000000%26limit%3D15&" +
                "api%5B%5D=article%3Ftype%3Dget_rule_2%26cate_id%3D1003450%26limit%3D1%26option%3D" +
                "object&api%5B%5D=article%3Ftype%3Dget_topstory%26showed_area%3Dvne_video%26cate_id%3D" +
                "1000000%26limit%3D3&api%5B%5D=article%3Ftype%3Dget_rule_2%26cate_id%3D1001005%252C1001002%252C" +
                "1003159%252C1002691%252C1002565%26limit%3D5&api%5B%5D=article%3Ftype%3Dget_topstory%26" +
                "showed_area%3Dvne_anh%26cate_id%3D1000000%26limit%3D6&app_id=" + Config.APP_ID;

        final String actualResult = builder.build();
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    @Test
    public void createApiTopNews_ProvideAllProperties_equalsExpectedResult() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_TOP_NEWS);

        builder.setArticleId(MOCK_ARTICLE_ID);
        builder.setCategoryId(MOCK_CATEGORY_ID);
        builder.setSiteId(MOCK_SITE_ID);
        builder.setAuthorId(MOCK_AUTHOR_ID);
        builder.setArticleTitle(MOCK_ARTICLE_TITLE);
        builder.setSearchKeyword(MOCK_SEARCH_KEYWORD);
        builder.setCategoryList(MOCK_CATEGORY_LIST);

        // The complex URL :D
        final String expectedResult = "http://api3.vnexpress.net/api/group?api%5B%5D=article%3Ftype" +
                "%3Dget_topstory%26showed_area%3Dtrangchu_beta%26cate_id%3D1000000%26limit%3D15&" +
                "api%5B%5D=article%3Ftype%3Dget_rule_2%26cate_id%3D1003450%26limit%3D1%26option%3D" +
                "object&api%5B%5D=article%3Ftype%3Dget_topstory%26showed_area%3Dvne_video%26cate_id%3D" +
                "1000000%26limit%3D3&api%5B%5D=article%3Ftype%3Dget_rule_2%26cate_id%3D1001005%252C1001002%252C" +
                "1003159%252C1002691%252C1002565%26limit%3D5&api%5B%5D=article%3Ftype%3Dget_topstory%26" +
                "showed_area%3Dvne_anh%26cate_id%3D1000000%26limit%3D6&app_id=" + Config.APP_ID;

        final String actualResult = builder.build();
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    /**
     * Creating an API Perspective Endpoint doesn't need to provide any property. So, it will
     * builds successfully.
     */
    @Test
    public void createApiPerspective_ProvideNothing_equalsExpectedResult() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_PERSPECTIVE);

        final String expectedResult = "http://api3.vnexpress.net/api/article?type=get_rule_2&" +
                "cate_id=1003450&limit=30&option=object&app_id=" + Config.APP_ID;
        final String actualResult = builder.build();
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    /**
     * Try to provide all property to ensure it does not affect to result
     */
    @Test
    public void createApiPerspective_ProvideAllProperties_equalsExpectedResult() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_PERSPECTIVE);

        builder.setArticleId(MOCK_ARTICLE_ID);
        builder.setCategoryId(MOCK_CATEGORY_ID);
        builder.setSiteId(MOCK_SITE_ID);
        builder.setAuthorId(MOCK_AUTHOR_ID);
        builder.setArticleTitle(MOCK_ARTICLE_TITLE);
        builder.setSearchKeyword(MOCK_SEARCH_KEYWORD);
        builder.setCategoryList(MOCK_CATEGORY_LIST);

        final String expectedResult = "http://api3.vnexpress.net/api/article?type=get_rule_2&" +
                "cate_id=1003450&limit=30&option=object&app_id=" + Config.APP_ID;
        final String actualResult = builder.build();
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    /**
     * Creating an API Top Photo Endpoint doesn't need to provide any property. So, it will
     * builds successfully.
     */
    @Test
    public void createApiTopPhoto_ProvideNothing_equalsExpectedResult() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_TOP_PHOTO);

        final String expectedResult = "http://api3.vnexpress.net/api/article?type=get_topstory&" +
                "showed_area=vne_anh&cate_id=1000000&limit=30&app_id=" + Config.APP_ID;
        final String actualResult = builder.build();
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    /**
     * Try to provide all property to ensure it does not affect to result
     */
    @Test
    public void createApiTopPhoto_ProvideAllProperties_equalsExpectedResult() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_TOP_PHOTO);

        builder.setArticleId(MOCK_ARTICLE_ID);
        builder.setCategoryId(MOCK_CATEGORY_ID);
        builder.setSiteId(MOCK_SITE_ID);
        builder.setAuthorId(MOCK_AUTHOR_ID);
        builder.setArticleTitle(MOCK_ARTICLE_TITLE);
        builder.setSearchKeyword(MOCK_SEARCH_KEYWORD);
        builder.setCategoryList(MOCK_CATEGORY_LIST);

        final String expectedResult = "http://api3.vnexpress.net/api/article?type=get_topstory&" +
                "showed_area=vne_anh&cate_id=1000000&limit=30&app_id=" + Config.APP_ID;
        final String actualResult = builder.build();
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    /**
     * In order to build an API News With Category Endpoint, we need to provide categoryId.
     * But we try to do not provide and expect it throws an exception
     */
    @Test(expected = IllegalStateException.class)
    public void createApiNewsWithCategory_ProvideNothing_ThrowsISE() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_NEWS_WITH_CATEGORY);
        builder.build();
    }

    @Test
    public void createApiNewsWithCategory_ProvideNeedfulProperties_equalsExpectedResult() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_NEWS_WITH_CATEGORY);

        builder.setCategoryId(MOCK_CATEGORY_ID);

        final String expectedResult = "http://api3.vnexpress.net/api/article?type=get_rule_2" +
                "&cate_id=" + MOCK_CATEGORY_ID + "&limit=30&app_id=" + Config.APP_ID;
        final String actualResult = builder.build();
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    /**
     * Try to provide all property to ensure it does not affect to result
     */
    @Test
    public void createApiNewsWithCategory_ProvideAllProperties_equalsExpectedResult() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_NEWS_WITH_CATEGORY);

        builder.setArticleId(MOCK_ARTICLE_ID);
        builder.setCategoryId(MOCK_CATEGORY_ID);
        builder.setSiteId(MOCK_SITE_ID);
        builder.setAuthorId(MOCK_AUTHOR_ID);
        builder.setArticleTitle(MOCK_ARTICLE_TITLE);
        builder.setSearchKeyword(MOCK_SEARCH_KEYWORD);
        builder.setCategoryList(MOCK_CATEGORY_LIST);

        final String expectedResult = "http://api3.vnexpress.net/api/article?type=get_rule_2" +
                "&cate_id=" + MOCK_CATEGORY_ID + "&limit=30&app_id=" + Config.APP_ID;
        final String actualResult = builder.build();
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    /**
     * In order to build an API Detail From Article Endpoint, we need to provide articleId.
     * But we try to do not provide and expect it throws an exception
     */
    @Test(expected = IllegalStateException.class)
    public void createApiDetailFromArticle_ProvideNothing_ThrowsISE() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_DETAIL_FROM_ARTICLE);
        builder.build();
    }

    @Test
    public void createApiDetailFromArticle_ProvideNeedfulProperties_equalsExpectedResult() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_DETAIL_FROM_ARTICLE);

        builder.setArticleId(MOCK_ARTICLE_ID);

        final String expectedResult = "http://api3.vnexpress.net/api/article?type=get_full&article_id="
                + MOCK_ARTICLE_ID + "&option=list_reference%2Cauthor_article%2Ccate_parent%2Cmode_view%2Cobject" +
                "&app_id=" + Config.APP_ID;
        final String actualResult = builder.build();
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    /**
     * Try to provide all property to ensure it does not affect to result
     */
    @Test
    public void createApiDetailFromArticle_ProvideAllProperties_equalsExpectedResult() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_DETAIL_FROM_ARTICLE);

        builder.setArticleId(MOCK_ARTICLE_ID);
        builder.setCategoryId(MOCK_CATEGORY_ID);
        builder.setSiteId(MOCK_SITE_ID);
        builder.setAuthorId(MOCK_AUTHOR_ID);
        builder.setArticleTitle(MOCK_ARTICLE_TITLE);
        builder.setSearchKeyword(MOCK_SEARCH_KEYWORD);
        builder.setCategoryList(MOCK_CATEGORY_LIST);

        final String expectedResult = "http://api3.vnexpress.net/api/article?type=get_full&article_id="
                + MOCK_ARTICLE_ID + "&option=list_reference%2Cauthor_article%2Ccate_parent%2Cmode_view%2Cobject" +
                "&app_id=" + Config.APP_ID;
        final String actualResult = builder.build();
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    /**
     * In order to build an API Perspective Detail From Article Endpoint, we need to provide articleId.
     * But we try to do not provide and expect it throws an exception
     */
    @Test(expected = IllegalStateException.class)
    public void createApiPerspectiveDetailFromArticle_ProvideNothing_ThrowsISE() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_PERSPECTIVE_DETAIL_FROM_ARTICLE);
        builder.build();
    }

    @Test
    public void createApiPerspectiveDetailFromArticle_ProvideNeedfulProperties_equalsExpectedResult() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_PERSPECTIVE_DETAIL_FROM_ARTICLE);

        builder.setArticleId(MOCK_ARTICLE_ID);

        final String expectedResult = "http://api3.vnexpress.net/api/article?type=get_full&article_id="
                + MOCK_ARTICLE_ID + "&option=list_reference%2Cauthor_article%2Ccate_parent%2Cobject" +
                "&limit_author_article=2&app_id=" + Config.APP_ID;
        final String actualResult = builder.build();
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    /**
     * Try to provide all property to ensure it does not affect to result
     */
    @Test
    public void createApiPerspectiveDetailFromArticle_ProvideAllProperties_equalsExpectedResult() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_PERSPECTIVE_DETAIL_FROM_ARTICLE);

        builder.setArticleId(MOCK_ARTICLE_ID);
        builder.setCategoryId(MOCK_CATEGORY_ID);
        builder.setSiteId(MOCK_SITE_ID);
        builder.setAuthorId(MOCK_AUTHOR_ID);
        builder.setArticleTitle(MOCK_ARTICLE_TITLE);
        builder.setSearchKeyword(MOCK_SEARCH_KEYWORD);
        builder.setCategoryList(MOCK_CATEGORY_LIST);

        final String expectedResult = "http://api3.vnexpress.net/api/article?type=get_full&article_id="
                + MOCK_ARTICLE_ID + "&option=list_reference%2Cauthor_article%2Ccate_parent%2Cobject" +
                "&limit_author_article=2&app_id=" + Config.APP_ID;
        final String actualResult = builder.build();
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    /**
     * In order to build an API Get Comments Endpoint, we need to provide articleId.
     * But we try to do not provide and expect it throws an exception
     */
    @Test(expected = IllegalStateException.class)
    public void createApiGetComments_ProvideNothing_ThrowsISE() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_GET_COMMENTS);
        builder.build();
    }

    /**
     * Needs to provide 4 properties: siteId, articleId, categoryId, articleTitle
     */
    @Test
    public void createApiGetComments_ProvideNeedfulProperties_equalsExpectedResult() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_GET_COMMENTS);

        builder.setSiteId(MOCK_SITE_ID);
        builder.setArticleId(MOCK_ARTICLE_ID);
        builder.setCategoryId(MOCK_CATEGORY_ID);
        builder.setArticleTitle(MOCK_ARTICLE_TITLE);

        final String expectedResult = "http://usi.saas.vnexpress.net/widget/mcomment?siteid="
                + MOCK_SITE_ID + "&objectid=" + MOCK_ARTICLE_ID + "&objecttype=1&categoryid="
                + MOCK_CATEGORY_ID + "&title=" + MOCK_ARTICLE_TITLE_ENCODED + "&app_id=" + Config.APP_ID;
        final String actualResult = builder.build();
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    /**
     * Because we must provide article ID when creating ApiGetComments. So it will throws exception
     * in this test
     */
    @Test(expected = IllegalStateException.class)
    public void createApiGetComments_NotProvideArticleId_ThrowsISE() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_GET_COMMENTS);

        builder.setSiteId(MOCK_SITE_ID);
        builder.setCategoryId(MOCK_CATEGORY_ID);
        builder.setArticleTitle(MOCK_ARTICLE_TITLE);

        builder.build();
    }

    /**
     * Because we must provide category ID when creating ApiGetComments. So it will throws exception
     * in this test
     */
    @Test(expected = IllegalStateException.class)
    public void createApiGetComments_NotProvideCategoryId_ThrowsISE() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_GET_COMMENTS);

        builder.setSiteId(MOCK_SITE_ID);
        builder.setArticleId(MOCK_ARTICLE_ID);
        builder.setArticleTitle(MOCK_ARTICLE_TITLE);

        builder.build();
    }

    /**
     * The site Id can miss when creating ApiGetComments because it will be set to default value
     */
    @Test
    public void createApiGetComments_NotProvideSiteId_equalsExpectedResult() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_GET_COMMENTS);

        builder.setArticleId(MOCK_ARTICLE_ID);
        builder.setCategoryId(MOCK_CATEGORY_ID);
        builder.setArticleTitle(MOCK_ARTICLE_TITLE);

        final String expectedResult = "http://usi.saas.vnexpress.net/widget/mcomment?siteid=1000000" +
                "&objectid=" + MOCK_ARTICLE_ID + "&objecttype=1&categoryid="
                + MOCK_CATEGORY_ID + "&title=" + MOCK_ARTICLE_TITLE_ENCODED + "&app_id=" + Config.APP_ID;
        final String actualResult = builder.build();
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    /**
     * The article title can miss when creating ApiGetComments because it will be set to empty string
     */
    @Test
    public void createApiGetComments_NotProvideArticleTitle_equalsExpectedResult() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_GET_COMMENTS);

        builder.setSiteId(MOCK_SITE_ID);
        builder.setArticleId(MOCK_ARTICLE_ID);
        builder.setCategoryId(MOCK_CATEGORY_ID);

        final String expectedResult = "http://usi.saas.vnexpress.net/widget/mcomment?siteid=" +
                MOCK_SITE_ID + "&objectid=" + MOCK_ARTICLE_ID + "&objecttype=1&categoryid="
                + MOCK_CATEGORY_ID + "&title=&app_id=" + Config.APP_ID;
        final String actualResult = builder.build();
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    /**
     * Try to provide all property to ensure it does not affect to result
     */
    @Test
    public void createApiGetComments_ProvideAllProperties_equalsExpectedResult() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_GET_COMMENTS);

        builder.setArticleId(MOCK_ARTICLE_ID);
        builder.setCategoryId(MOCK_CATEGORY_ID);
        builder.setSiteId(MOCK_SITE_ID);
        builder.setAuthorId(MOCK_AUTHOR_ID);
        builder.setArticleTitle(MOCK_ARTICLE_TITLE);
        builder.setSearchKeyword(MOCK_SEARCH_KEYWORD);
        builder.setCategoryList(MOCK_CATEGORY_LIST);

        final String expectedResult = "http://usi.saas.vnexpress.net/widget/mcomment?siteid="
                + MOCK_SITE_ID + "&objectid=" + MOCK_ARTICLE_ID + "&objecttype=1&categoryid="
                + MOCK_CATEGORY_ID + "&title=" + MOCK_ARTICLE_TITLE_ENCODED + "&app_id=" + Config.APP_ID;
        final String actualResult = builder.build();
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    /**
     * Creating an API Post Comment Endpoint doesn't need to provide any property. So, it will
     * builds successfully.
     */
    @Test
    public void createApiPostComment_ProvideNothing_equalsExpectedResult() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_POST_COMMENT);

        final String expectedResult = "http://usi.saas.vnexpress.net/index/add";
        final String actualResult = builder.build();
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    /**
     * Try to provide all property to ensure it does not affect to result
     */
    @Test
    public void createApiPostComment_ProvideAllProperties_equalsExpectedResult() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_POST_COMMENT);

        builder.setArticleId(MOCK_ARTICLE_ID);
        builder.setCategoryId(MOCK_CATEGORY_ID);
        builder.setSiteId(MOCK_SITE_ID);
        builder.setAuthorId(MOCK_AUTHOR_ID);
        builder.setArticleTitle(MOCK_ARTICLE_TITLE);
        builder.setSearchKeyword(MOCK_SEARCH_KEYWORD);
        builder.setCategoryList(MOCK_CATEGORY_LIST);

        final String expectedResult = "http://usi.saas.vnexpress.net/index/add";
        final String actualResult = builder.build();
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    /**
     * Because we must provide article ID when creating ApiGetTotalComments. So it will throws exception
     * in this test
     */
    @Test(expected = IllegalStateException.class)
    public void createApiGetTotalComments_ProvideNothing_ThrowsISE() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_GET_TOTAL_COMMENTS);

        builder.build();
    }

    /**
     * Creating ApiGetTotalComments only needs one property. That is articleId. So we set it
     */
    @Test
    public void createApiGetTotalComments_ProvideNeedfulProperties_equalsExpectedResult() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_GET_TOTAL_COMMENTS);

        builder.setArticleId(MOCK_ARTICLE_ID);

        final String expectedResult = "http://usi.saas.vnexpress.net/widget/statistic?objectid="
                + MOCK_ARTICLE_ID + "&objecttype=1&app_id=" + Config.APP_ID;
        final String actualResult = builder.build();
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    /**
     * Try to provide all property to ensure it does not affect to result
     */
    @Test
    public void createApiTotalComments_ProvideAllProperties_equalsExpectedResult() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_GET_TOTAL_COMMENTS);

        builder.setArticleId(MOCK_ARTICLE_ID);
        builder.setCategoryId(MOCK_CATEGORY_ID);
        builder.setSiteId(MOCK_SITE_ID);
        builder.setAuthorId(MOCK_AUTHOR_ID);
        builder.setArticleTitle(MOCK_ARTICLE_TITLE);
        builder.setSearchKeyword(MOCK_SEARCH_KEYWORD);
        builder.setCategoryList(MOCK_CATEGORY_LIST);

        final String expectedResult = "http://usi.saas.vnexpress.net/widget/statistic?objectid="
                + MOCK_ARTICLE_ID + "&objecttype=1&app_id=" + Config.APP_ID;
        final String actualResult = builder.build();
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    /**
     * Creating API Search News Endpoint needs to provide only one property. That is search keyword.
     * If we don't provide. It will set to empty string.
     */
    @Test
    public void createApiSearchNews_ProvideNothing_equalsExpectedResult() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_SEARCH_NEWS);

        final String expectedResult = "http://api3.vnexpress.net/api/article?type=search" +
                "&keyword=&limit=30&app_id=" + Config.APP_ID;
        final String actualResult = builder.build();

        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    /**
     * Creating ApiGetTotalComments only needs one property. That is search keyword. So we set it
     */
    @Test
    public void createApiSearchNews_ProvideNeedfulProperties_equalsExpectedResult() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_SEARCH_NEWS);

        builder.setSearchKeyword(MOCK_SEARCH_KEYWORD);

        final String expectedResult = "http://api3.vnexpress.net/api/article?type=search" +
                "&keyword=" + MOCK_SEARCH_KEYWORD_ENCODED + "&limit=30&app_id=" + Config.APP_ID;
        final String actualResult = builder.build();
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    /**
     * Try to provide all property to ensure it does not affect to result
     */
    @Test
    public void createApiSearchNews_ProvideAllProperties_equalsExpectedResult() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_SEARCH_NEWS);

        builder.setArticleId(MOCK_ARTICLE_ID);
        builder.setCategoryId(MOCK_CATEGORY_ID);
        builder.setSiteId(MOCK_SITE_ID);
        builder.setAuthorId(MOCK_AUTHOR_ID);
        builder.setArticleTitle(MOCK_ARTICLE_TITLE);
        builder.setSearchKeyword(MOCK_SEARCH_KEYWORD);
        builder.setCategoryList(MOCK_CATEGORY_LIST);

        final String expectedResult = "http://api3.vnexpress.net/api/article?type=search" +
                "&keyword=" + MOCK_SEARCH_KEYWORD_ENCODED + "&limit=30&app_id=" + Config.APP_ID;
        final String actualResult = builder.build();
        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    /**
     * In order to create ApiGetPostsOfAuthor, we must provide author ID. If we don't, it will throw
     * an exception
     */
    @Test(expected = IllegalStateException.class)
    public void createApiGetPostsOfAuthor_ProvideNothing_ThrowsISE() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_GET_POSTS_OF_AUTHOR);
        builder.build();
    }

    /**
     * Provides authorID for tests
     */
    @Test
    public void createApiGetPostsOfAuthor_ProvideNeedfulProperties_equalsExpectedResult() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_GET_POSTS_OF_AUTHOR);

        builder.setAuthorId(MOCK_AUTHOR_ID);

        final String expectedResult = "http://api3.vnexpress.net/api/article?type=get_author&" +
                "author_id=" + MOCK_AUTHOR_ID + "&limit=30&app_id=" + Config.APP_ID;
        final String actualResult = builder.build();

        assertThat(actualResult, is(equalTo(expectedResult)));
    }

    /**
     * Try to provide all property to ensure it does not affect to result
     */
    @Test
    public void createApiGetPostsOfAuthor_ProvideAllProperties_equalsExpectedResult() {
        APIEndpointBuilder builder = new APIEndpointBuilder(
                APIEndpointBuilder.APIEndpointType.API_GET_POSTS_OF_AUTHOR);

        builder.setArticleId(MOCK_ARTICLE_ID);
        builder.setCategoryId(MOCK_CATEGORY_ID);
        builder.setSiteId(MOCK_SITE_ID);
        builder.setAuthorId(MOCK_AUTHOR_ID);
        builder.setArticleTitle(MOCK_ARTICLE_TITLE);
        builder.setSearchKeyword(MOCK_SEARCH_KEYWORD);
        builder.setCategoryList(MOCK_CATEGORY_LIST);

        final String expectedResult = "http://api3.vnexpress.net/api/article?type=get_author&" +
                "author_id=" + MOCK_AUTHOR_ID + "&limit=30&app_id=" + Config.APP_ID;
        final String actualResult = builder.build();

        assertThat(actualResult, is(equalTo(expectedResult)));
    }

}
