package com.example.phat.vnexpressnews.util;

import com.example.phat.vnexpressnews.Config;
import com.example.phat.vnexpressnews.model.Category;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.example.phat.vnexpressnews.util.LogUtils.LOGD;
import static com.example.phat.vnexpressnews.util.LogUtils.LOGE;
import static com.example.phat.vnexpressnews.util.LogUtils.LOGI;
import static com.example.phat.vnexpressnews.util.LogUtils.makeLogTag;

/**
 * This builder helps to build an API Endpoint for each type of request like as requesting top news,
 * requesting full article, requesting comments,... The client even doesn't need to know about
 * business logic in creating API Endpoint.
 */
public class APIEndpointBuilder {
    private static final String TAG = makeLogTag(APIEndpoint.class);

    /**
     * This enum uses to indicates what kind of API Endpoint should be created
     */
    public enum APIEndpointType {
        API_FOLDERS_LIST, // Needs to provide nothing
        API_TOP_NEWS,   // Needs to provide category list
        API_PERSPECTIVE, // Needs to provide nothing
        API_TOP_PHOTO,  // Needs to provide nothing
        API_NEWS_WITH_CATEGORY, // Needs to provide categoryId
        API_DETAIL_FROM_ARTICLE,    // Needs to provide articleId
        API_PERSPECTIVE_DETAIL_FROM_ARTICLE, // Needs to provide articleId
        API_GET_COMMENTS,   // Needs to provide siteId, articleId, categoryId, articleTitle
        API_POST_COMMENT,   // Needs to provide nothing
        API_GET_TOTAL_COMMENTS, // Needs to provide articleId
        API_SEARCH_NEWS,    // Needs to provide searchKeyword
        API_GET_POSTS_OF_AUTHOR // Needs to provide authorId
    }

    public static final int INVALID_ID = -1;

    private APIEndpointType mType;
    private int mArticleId = INVALID_ID;
    private int mCategoryId = INVALID_ID;
    private int mSiteId = INVALID_ID;
    private String mArticleTitle;
    private String mSearchKeyword;
    private int mAuthorId = INVALID_ID;
    private List<Category> mCategoryList;
    private APIEndpoint mAPIEndpoint;

    public APIEndpointBuilder(APIEndpointType type) {
        this.mType = type;
    }

    public APIEndpointBuilder setArticleId(int articleId) {
        mArticleId = articleId;
        return this;
    }

    public int getArticleId() {
        return mArticleId;
    }

    public APIEndpointBuilder setCategoryId(int categoryId) {
        mCategoryId = categoryId;
        return this;
    }

    public int getCategoryId() {
        return mArticleId;
    }

    public APIEndpointBuilder setCategoryList(List<Category> categoryList) {
        mCategoryList = categoryList;
        return this;
    }

    public List<Category> getCategoryList() {
        return mCategoryList;
    }

    public APIEndpointBuilder setAPIEndpointType(APIEndpointType type) {
        mType = type;
        return this;
    }

    public APIEndpointType getAPIEndpointType() {
        return mType;
    }

    public APIEndpointBuilder setSiteId(int siteId) {
        mSiteId = siteId;
        return this;
    }

    public int getSiteId() {
        return mSiteId;
    }

    public APIEndpointBuilder setArticleTitle(String title) {
        mArticleTitle = title != null ? title : "";
        return this;
    }

    public String getArticleTitle() {
        return mArticleTitle;
    }

    public APIEndpointBuilder setSearchKeyword(String keyword) {
        mSearchKeyword = keyword != null ? keyword : "";
        return this;
    }

    public String getSearchKeyword() {
        return mSearchKeyword;
    }

    public APIEndpointBuilder setAuthorId(int authorId) {
        mAuthorId = authorId;
        return this;
    }

    public int getAuthorId() {
        return mAuthorId;
    }

    public APIEndpoint getAPIEndpoint() {
        return mAPIEndpoint;
    }

    /**
     * This method builds a desired APIEndpoint based on APIEndpointType.
     */
    public String build() {

        // USe switch to choose right type of APIEndpoint based on APIEndpointType
        switch (mType) {
            case API_FOLDERS_LIST:
                mAPIEndpoint = new ApiFolderList();
                LOGI(TAG, "Creates a new ApiFolderList instance");
                break;
            case API_TOP_NEWS:
                mAPIEndpoint = new ApiTopNews();
                LOGI(TAG, "Creates a new ApiTopNews instance");
                break;
            case API_PERSPECTIVE:
                mAPIEndpoint = new ApiPerspective();
                LOGI(TAG, "Creates a new ApiPerspective instance");
                break;
            case API_TOP_PHOTO:
                mAPIEndpoint = new ApiTopPhoto();
                LOGI(TAG, "Creates a new ApiTopPhoto instance");
                break;
            case API_NEWS_WITH_CATEGORY:
                mAPIEndpoint = new ApiNewsWithCategory();
                LOGI(TAG, "Creates a new ApiNewsWithCategory instance");
                break;
            case API_DETAIL_FROM_ARTICLE:
                mAPIEndpoint = new ApiDetailFromArticle();
                LOGI(TAG, "Creates a new ApiDetailFromArticle instance");
                break;
            case API_PERSPECTIVE_DETAIL_FROM_ARTICLE:
                mAPIEndpoint = new ApiPerspectiveDetailFromArticle();
                LOGI(TAG, "Creates a new ApiPerspectiveDetailFromArticle instance");
                break;
            case API_GET_COMMENTS:
                mAPIEndpoint = new ApiGetComments();
                LOGI(TAG, "Creates a new ApiGetComments instance");
                break;
            case API_POST_COMMENT:
                mAPIEndpoint = new ApiPostComment();
                LOGI(TAG, "Creates a new ApiPostComment instance");
                break;
            case API_GET_TOTAL_COMMENTS:
                mAPIEndpoint = new ApiGetTotalComments();
                LOGI(TAG, "Creates a new ApiGetTotalComments instance");
                break;
            case API_SEARCH_NEWS:
                mAPIEndpoint = new ApiSearchNews();
                LOGI(TAG, "Creates a new ApiSearchNews instance");
                break;
            case API_GET_POSTS_OF_AUTHOR:
                mAPIEndpoint = new ApiGetPostsOfAuthor();
                LOGI(TAG, "Creates a new ApiGetPostsOfAuthor instance");
                break;
            default:
                throw new IllegalArgumentException("Can not found this argument in APIEndpointType: Argument: " + mType);
        }
        String result = mAPIEndpoint.buildAPIEndpoint();
        return result;
    }

    /**
     * The abstract class includes some productive constants, and most important thing is #buildAPIEndpoint method,
     * that class is marked as abstraction. Its child class will extend and implement #buildAPIEndpoint method
     * for each type of APIEndpoint.
     */
    private abstract class APIEndpoint {
        protected static final String BASE_URL = Config.BASE_URL_API;
        protected String mUrl;

        protected static final String TYPE_NAME = "type";
        protected static final String SHOWED_AREA_NAME = "showed_area";
        protected static final String CATE_ID_NAME = "cate_id";
        protected static final String ARTICLE_ID_NAME = "article_id";
        protected static final String LIMIT_NAME = "limit";
        protected static final String OPTION_NAME = "option";
        protected static final String SITE_ID_NAME = "site_id";
        protected static final String SHOW_FOLDER_NAME = "show_folder";
        protected static final String APP_DETAIL_NAME = "app_detail";
        protected static final String DEEP_MENU_NAME = "deep";
        protected static final String SEARCH_KEYWORD_NAME = "keyword";
        protected static final String AUTHOR_ID_NAME = "author_id";
        protected static final String APP_ID_NAME = "app_id";

        protected static final int SITE_ID_VALUE = 1000000;
        protected static final int SHOW_FOLDER_VALUE = 1;
        protected static final int APP_DETAIL_VALUE = 1;
        protected static final int DEEP_MENU_VALUE = 1;
        protected static final String GET_TOP_STORY_TYPE_VALUE = "get_topstory";
        protected static final String GET_RULE_2_TYPE_VALUE = "get_rule_2";
        protected static final String VNE_VIDEO_VALUE = "vne_video";
        protected static final String VNE_PHOTO_VALUE = "vne_anh";
        protected static final String GET_FULL_TYPE_VALUE = "get_full";
        protected static final String SEARCH_TYPE_VALUE = "search";
        protected static final String GET_AUTHOR_TYPE_VALUE = "get_author";

        protected static final String GROUP_PATH = "api/group";
        protected static final String ARTICLE_PATH = "api/article";
        protected static final String CATEGORY_PATH = "api/category";

        protected APIEndpoint(String path) {
            try {
                mUrl = URLUtils.buildURL(BASE_URL, path);
            } catch (URISyntaxException e) {
                LOGE(TAG, "Met an error when creating API Endpoint with a path", e.getCause());
            }
        }

        protected APIEndpoint(String customUrl, String path) {
            try {
                mUrl = URLUtils.buildURL(customUrl, path);
            } catch (URISyntaxException e) {
                LOGE(TAG, "Met an error when creating API Endpoint with a custom url and a path", e.getCause());
            }
        }

        public abstract String buildAPIEndpoint();
    }

    private class ApiFolderList extends APIEndpoint {

        protected ApiFolderList() {
            super(CATEGORY_PATH);
        }

        @Override
        public String buildAPIEndpoint() {
            // Build request parameters
            Map<String, String> requestParameters = new LinkedHashMap<>(6);
            requestParameters.put(TYPE_NAME, "get_menu");
            requestParameters.put(SITE_ID_NAME, String.valueOf(SITE_ID_VALUE));
            requestParameters.put(SHOW_FOLDER_NAME, String.valueOf(SHOW_FOLDER_VALUE));
            requestParameters.put(APP_DETAIL_NAME, String.valueOf(APP_DETAIL_VALUE));
            requestParameters.put(DEEP_MENU_NAME, String.valueOf(DEEP_MENU_VALUE));
            requestParameters.put(APP_ID_NAME, Config.APP_ID);

            try {
                String finalUrl = URLUtils.buildURL(mUrl, requestParameters);
                LOGI(TAG, "Build Folder List API Endpoint successful. Built URL: " + finalUrl);
                return finalUrl;
            } catch (URISyntaxException e) {
                LOGE(TAG, "Passed URL is not valid. URL: " + mUrl, e.getCause());
                return null;
            }
        }
    }

    private class ApiTopNews extends APIEndpoint {
        private static final String mRequestParameterName = "api[]";
        private static final String mSubPath = "article";

        protected ApiTopNews() {
            super(GROUP_PATH);
        }

        @Override
        public String buildAPIEndpoint() {

            try {
                // This map for creating API Endpoint
                Map<String, List<String>> requestParameters = new LinkedHashMap<>(4);
                // We will save values of "api[]" name in this list
                List<String> valuesApiRequest = new ArrayList<>(5);

                // Build query string for requesting top story
                // This map for sub url. This map differ from above map. This map for creating a sub
                // request string. Otherwise, above map for creating API Endpoint request string
                Map<String, String> subRequestParameters = new LinkedHashMap<>(6);
                subRequestParameters.put(TYPE_NAME, GET_TOP_STORY_TYPE_VALUE);
                subRequestParameters.put(SHOWED_AREA_NAME, "trangchu_beta");
                subRequestParameters.put(CATE_ID_NAME, String.valueOf(SITE_ID_VALUE));
                subRequestParameters.put(LIMIT_NAME, String.valueOf(Config.NUMBER_OF_TOP_STORY_ARTICLE_));
                String topStoryQueryString = URLUtils.buildSubURL(mSubPath, subRequestParameters);
                LOGD(TAG, "Build top story query string successful. Query string: " + topStoryQueryString);
                valuesApiRequest.add(topStoryQueryString);

                // Build query string for requesting perspective category
                subRequestParameters.clear(); // reuse above map instance.
                subRequestParameters.put(TYPE_NAME, GET_RULE_2_TYPE_VALUE);
                subRequestParameters.put(CATE_ID_NAME, String.valueOf(Config.DEFAULT_CATEGORY_ID_PERSPECTIVE));
                subRequestParameters.put(LIMIT_NAME, String.valueOf(Config.NUMBER_OF_PERSPECTIVE_ARTICLE));
                subRequestParameters.put(OPTION_NAME, "object");
                String perspectiveQueryString = URLUtils.buildSubURL(mSubPath, subRequestParameters);
                LOGD(TAG, "Build perspective category query string successful. Query string: " + perspectiveQueryString);
                valuesApiRequest.add(perspectiveQueryString);

                // Build query string for requesting video
                subRequestParameters.clear(); // reuse above map instance.
                subRequestParameters.put(TYPE_NAME, GET_TOP_STORY_TYPE_VALUE);
                subRequestParameters.put(SHOWED_AREA_NAME, VNE_VIDEO_VALUE);
                subRequestParameters.put(CATE_ID_NAME, String.valueOf(SITE_ID_VALUE));
                subRequestParameters.put(LIMIT_NAME, String.valueOf(Config.NUMBER_OF_VNE_VIDEO_ARTICLE));
                String videoQueryString = URLUtils.buildSubURL(mSubPath, subRequestParameters);
                LOGD(TAG, "Build video query string successful. Query string: " + videoQueryString);
                valuesApiRequest.add(videoQueryString);

                // Build query string for other category
                String categoryIds = buildCategoryIds(mCategoryList);
                subRequestParameters.clear(); // reuse above map instance.
                subRequestParameters.put(TYPE_NAME, GET_RULE_2_TYPE_VALUE);
                subRequestParameters.put(CATE_ID_NAME, categoryIds);
                subRequestParameters.put(LIMIT_NAME, String.valueOf(Config.NUMBER_OF_EACH_OTHER_ARTICLE));
                String otherCategoryQueryString = URLUtils.buildSubURL(mSubPath, subRequestParameters);
                LOGD(TAG, "Build other categories query string successful. Query string: " + otherCategoryQueryString);
                valuesApiRequest.add(otherCategoryQueryString);

                // Build query string for requesting photo
                subRequestParameters.clear(); // reuse above map instance.
                subRequestParameters.put(TYPE_NAME, GET_TOP_STORY_TYPE_VALUE);
                subRequestParameters.put(SHOWED_AREA_NAME, VNE_PHOTO_VALUE);
                subRequestParameters.put(CATE_ID_NAME, String.valueOf(SITE_ID_VALUE));
                subRequestParameters.put(LIMIT_NAME, String.valueOf(Config.NUMBER_OF_VNE_PHOTO_ARTICLE));
                String photoQueryString = URLUtils.buildSubURL(mSubPath, subRequestParameters);
                LOGD(TAG, "Build photo query string successful. Query string: " + photoQueryString);
                valuesApiRequest.add(photoQueryString);

                // Creates first name and value pair of query string. It will format like: api[]=....&api[]=....
                // In that, "..." is value of "api[]" name
                requestParameters.put(mRequestParameterName, valuesApiRequest);
                // Add APP_ID to query string
                requestParameters.put(APP_ID_NAME, Collections.singletonList(Config.APP_ID));

                // Finally, build API Endpoint
                String finalUrl = URLUtils.buildComplexURL(mUrl, requestParameters);
                LOGI(TAG, "Build Top News API Endpoint successful. API Endpoint: " + finalUrl);
                return finalUrl;


            } catch (URISyntaxException e) {
                LOGE(TAG, "Met an error when building an API Endpoint. Input: " + e.getInput() + ", " +
                        "Reason: " + e.getReason() + ", Index: " + e.getIndex(), e.getCause());
            } catch (IllegalStateException e) {
                LOGE(TAG, "Met an error when trying to pass a null or empty String to first argument" +
                        " of URIUtils#buildSubURL method.", e.getCause());
            }

            return null;
        }

        /**
         * This helper method help to build an composite category ids.
         * Example: We have three independent category ids: 1001; 1002; 1003. After building,
         * We will have a string format like this: "1001,1002,1003"
         *
         * @param categories list of category
         * @return A composition of category id.
         */
        private String buildCategoryIds(List<Category> categories) {
            // A list only include categoryId
            List<Integer> categoryIdList;

            if (categories == null || categories.isEmpty()) {
                // Creates a default category list based on default Config value
                categoryIdList = new ArrayList<>(18);
                categoryIdList.add(Config.DEFAULT_CATEGORY_ID_NEWS);
                categoryIdList.add(Config.DEFAULT_CATEGORY_ID_THE_WORLD);
                categoryIdList.add(Config.DEFAULT_CATEGORY_ID_PERSPECTIVE);
                categoryIdList.add(Config.DEFAULT_CATEGORY_ID_BUSINESS);
                categoryIdList.add(Config.DEFAULT_CATEGORY_ID_ENTERTAINMENT);
                categoryIdList.add(Config.DEFAULT_CATEGORY_ID_SPORT);
                categoryIdList.add(Config.DEFAULT_CATEGORY_ID_LAW);
                categoryIdList.add(Config.DEFAULT_CATEGORY_ID_EDUCATION);
                categoryIdList.add(Config.DEFAULT_CATEGORY_ID_HEALTH);
                categoryIdList.add(Config.DEFAULT_CATEGORY_ID_FAMILY);
                categoryIdList.add(Config.DEFAULT_CATEGORY_ID_TRAVEL);
                categoryIdList.add(Config.DEFAULT_CATEGORY_ID_SCIENCE);
                categoryIdList.add(Config.DEFAULT_CATEGORY_ID_DIGITIZATION);
                categoryIdList.add(Config.DEFAULT_CATEGORY_ID_VEHICLE);
                categoryIdList.add(Config.DEFAULT_CATEGORY_ID_COMMUNITY);
                categoryIdList.add(Config.DEFAULT_CATEGORY_ID_CONFIDENCE);
                categoryIdList.add(Config.DEFAULT_CATEGORY_ID_VIDEO);
                categoryIdList.add(Config.DEFAULT_CATEGORY_ID_FUNNY);
            } else {
                // Convert list of category to list include only categoryID
                categoryIdList = new ArrayList<>(categories.size());
                for (Category c : categories) {
                    categoryIdList.add(c.getCategoryID());
                }
            }

            String categoryIdsString = "";
            int i = 0;
            for (Integer id : categoryIdList) {
                if (!(id == Config.DEFAULT_CATEGORY_ID_PERSPECTIVE ||      // Don't want include perspective category id and top news category id
                        id == Config.DEFAULT_CATEGORY_ID_TOP_NEWS ||       // because it was indeed requested in other request string
                        id == Config.DEFAULT_CATEGORY_ID_WANT_AD)) {       // We also don't want include a want-ad category in request string
                    categoryIdsString += id + (i < categoryIdList.size() + -1 ? "," : "");
                }
                i++;
            }
            return categoryIdsString;
        }
    }

    private class ApiPerspective extends APIEndpoint {

        protected ApiPerspective() {
            super(ARTICLE_PATH);
        }

        @Override
        public String buildAPIEndpoint() {
            // Build request parameters
            Map<String, String> requestParameters = new LinkedHashMap<>(6);
            requestParameters.put(TYPE_NAME, GET_RULE_2_TYPE_VALUE);
            requestParameters.put(CATE_ID_NAME, String.valueOf(Config.DEFAULT_CATEGORY_ID_PERSPECTIVE));
            requestParameters.put(LIMIT_NAME, String.valueOf(Config.DEFAULT_NUMBER_OF_ARTICLE_WITH_CATEGORY));
            requestParameters.put(OPTION_NAME, "object");
            requestParameters.put(APP_ID_NAME, Config.APP_ID);

            try {
                String finalUrl = URLUtils.buildURL(mUrl, requestParameters);
                LOGI(TAG, "Build Perspective API Endpoint successful. Built URL: " + finalUrl);
                return finalUrl;
            } catch (URISyntaxException e) {
                LOGE(TAG, "Passed URL is not valid. URL: " + mUrl, e.getCause());
                return null;
            }
        }
    }

    private class ApiTopPhoto extends APIEndpoint {

        protected ApiTopPhoto() {
            super(ARTICLE_PATH);
        }

        @Override
        public String buildAPIEndpoint() {
            // Build request parameters
            Map<String, String> requestParameters = new LinkedHashMap<>(6);
            requestParameters.put(TYPE_NAME, GET_TOP_STORY_TYPE_VALUE);
            requestParameters.put(SHOWED_AREA_NAME, VNE_PHOTO_VALUE);
            requestParameters.put(CATE_ID_NAME, String.valueOf(SITE_ID_VALUE));
            requestParameters.put(LIMIT_NAME, String.valueOf(Config.DEFAULT_NUMBER_OF_ARTICLE_WITH_CATEGORY));
            requestParameters.put(APP_ID_NAME, Config.APP_ID);

            try {
                String finalUrl = URLUtils.buildURL(mUrl, requestParameters);
                LOGI(TAG, "Build Top Photos API Endpoint successful. Built URL: " + finalUrl);
                return finalUrl;
            } catch (URISyntaxException e) {
                LOGE(TAG, "Passed URL is not valid. URL: " + mUrl, e.getCause());
                return null;
            }
        }
    }

    private class ApiNewsWithCategory extends APIEndpoint {

        protected ApiNewsWithCategory() {
            super(ARTICLE_PATH);
        }

        @Override
        public String buildAPIEndpoint() {

            // The first, check mCategory property to ensure it was set
            if (mCategoryId == INVALID_ID) {
                throw new IllegalStateException("The category ID has not set. Must set category ID" +
                        " before build ApiNewsWithCategory endpoint.");
            }

            // Build request parameters
            Map<String, String> requestParameters = new LinkedHashMap<>(4);
            requestParameters.put(TYPE_NAME, GET_RULE_2_TYPE_VALUE);
            requestParameters.put(CATE_ID_NAME, String.valueOf(mCategoryId));
            requestParameters.put(LIMIT_NAME, String.valueOf(Config.DEFAULT_NUMBER_OF_ARTICLE_WITH_CATEGORY));
            requestParameters.put(APP_ID_NAME, Config.APP_ID);

            try {
                String finalUrl = URLUtils.buildURL(mUrl, requestParameters);
                LOGI(TAG, "Build NewsWithCategory API Endpoint successful. Built URL: " + finalUrl);
                return finalUrl;
            } catch (URISyntaxException e) {
                LOGE(TAG, "Passed URL is not valid. URL: " + mUrl, e.getCause());
                return null;
            }
        }
    }

    private class ApiDetailFromArticle extends APIEndpoint {

        protected ApiDetailFromArticle() {
            super(ARTICLE_PATH);
        }

        @Override
        public String buildAPIEndpoint() {

            // The first, check article Id to make sure it was set
            if (mArticleId == INVALID_ID) {
                throw new IllegalStateException("The article ID has not set. Must set article ID" +
                        " before build ApiDetailFromArticle endpoint.");
            }

            // Build request parameters
            Map<String, String> requestParameters = new LinkedHashMap<>(4);
            requestParameters.put(TYPE_NAME, GET_FULL_TYPE_VALUE);
            requestParameters.put(ARTICLE_ID_NAME, String.valueOf(mArticleId));
            requestParameters.put(OPTION_NAME, "list_reference,author_article,cate_parent,mode_view,object");
            requestParameters.put(APP_ID_NAME, Config.APP_ID);

            try {
                String finalUrl = URLUtils.buildURL(mUrl, requestParameters);
                LOGI(TAG, "Build DetailFromArticle API Endpoint successful. Built URL: " + finalUrl);
                return finalUrl;
            } catch (URISyntaxException e) {
                LOGE(TAG, "Passed URL is not valid. URL: " + mUrl, e.getCause());
                return null;
            }
        }
    }

    private class ApiPerspectiveDetailFromArticle extends APIEndpoint {

        protected ApiPerspectiveDetailFromArticle() {
            super(ARTICLE_PATH);
        }

        @Override
        public String buildAPIEndpoint() {

            // The first, check article Id to make sure it was set
            if (mArticleId == INVALID_ID) {
                throw new IllegalStateException("The article ID has not set. Must set article ID" +
                        " before build ApiPerspectiveDetailFromArticle endpoint.");
            }

            // Build request parameters
            Map<String, String> requestParameters = new LinkedHashMap<>(5);
            requestParameters.put(TYPE_NAME, GET_FULL_TYPE_VALUE);
            requestParameters.put(ARTICLE_ID_NAME, String.valueOf(mArticleId));
            requestParameters.put(OPTION_NAME, "list_reference,author_article,cate_parent,object");
            requestParameters.put("limit_author_article", String.valueOf(2));
            requestParameters.put(APP_ID_NAME, Config.APP_ID);

            try {
                String finalUrl = URLUtils.buildURL(mUrl, requestParameters);
                LOGI(TAG, "Build PerspectiveDetailFromArticle API Endpoint successful. Built URL: " + finalUrl);
                return finalUrl;
            } catch (URISyntaxException e) {
                LOGE(TAG, "Passed URL is not valid. URL: " + mUrl, e.getCause());
                return null;
            }
        }
    }

    private class ApiGetComments extends APIEndpoint {

        protected ApiGetComments() {
            super(Config.BASE_COMMENT_URL_API, "widget/mcomment");
        }

        @Override
        public String buildAPIEndpoint() {

            // The first, check needful properties were set or not
            if (mSiteId == INVALID_ID) {
                // Set to default siteId
                mSiteId = SITE_ID_VALUE;
            }

            if (mArticleId == INVALID_ID || mCategoryId == INVALID_ID) {
                throw new IllegalStateException("The article ID or category ID has not set. " +
                        "Must set article ID or category ID before build ApiGetComments endpoint.");
            }

            // Since article title doesn't make request fail. So it doesn't matter whether it was set or not
            // We check to make sure it doesn't throw a NullPointerException.
            if (mArticleTitle == null) {
                // Set article title to empty if it is null
                mArticleTitle = "";
            }

            // Build request parameters
            Map<String, String> requestParameters = new LinkedHashMap<>(5);
            requestParameters.put("siteid", String.valueOf(mSiteId));
            requestParameters.put("objectid", String.valueOf(mArticleId));
            requestParameters.put("objecttype", String.valueOf(1));
            requestParameters.put("categoryid", String.valueOf(mCategoryId));
            requestParameters.put("title", mArticleTitle);
            requestParameters.put(APP_ID_NAME, Config.APP_ID);

            try {
                String finalUrl = URLUtils.buildURL(mUrl, requestParameters);
                LOGI(TAG, "Build GetComments API Endpoint successful. Built URL: " + finalUrl);
                return finalUrl;
            } catch (URISyntaxException e) {
                LOGE(TAG, "Passed URL is not valid. URL: " + mUrl, e.getCause());
                return null;
            }
        }
    }

    private class ApiPostComment extends APIEndpoint {

        protected ApiPostComment() {
            super(Config.BASE_COMMENT_URL_API, "index/add");
        }

        @Override
        public String buildAPIEndpoint() {
            // Just return url, since this API Endpoint is POST type.
            // All parameters will added in data-form. So, we just need return url
            LOGI(TAG, "Build PostComment API Endpoint successful. Built URL: " + mUrl);
            return mUrl;
        }
    }

    private class ApiGetTotalComments extends APIEndpoint {

        protected ApiGetTotalComments() {
            super(Config.BASE_COMMENT_URL_API, "widget/statistic");
        }

        @Override
        public String buildAPIEndpoint() {

            if (mArticleId == INVALID_ID) {
                throw new IllegalStateException("The article ID has not set. Must set article ID" +
                        " before build ApiGetTotalComments endpoint.");
            }

            // Build request parameters
            Map<String, String> requestParameters = new LinkedHashMap<>(3);
            requestParameters.put("objectid", String.valueOf(mArticleId));
            requestParameters.put("objecttype", String.valueOf(1));
            requestParameters.put(APP_ID_NAME, Config.APP_ID);

            try {
                String finalUrl = URLUtils.buildURL(mUrl, requestParameters);
                LOGI(TAG, "Build GetTotalComments successful. Built URL: " + finalUrl);
                return finalUrl;
            } catch (URISyntaxException e) {
                LOGE(TAG, "Passed URL is not valid. URL: " + mUrl, e.getCause());
                return null;
            }
        }
    }

    private class ApiSearchNews extends APIEndpoint {

        protected ApiSearchNews() {
            super(ARTICLE_PATH);
        }

        @Override
        public String buildAPIEndpoint() {
            // Build request parameters
            Map<String, String> requestParameters = new LinkedHashMap<>(5);
            requestParameters.put(TYPE_NAME, SEARCH_TYPE_VALUE);
            requestParameters.put(SEARCH_KEYWORD_NAME, mSearchKeyword != null ? mSearchKeyword : "");
            requestParameters.put(LIMIT_NAME, String.valueOf(Config.DEFAULT_NUMBER_OF_ARTICLE_WITH_CATEGORY));
            requestParameters.put(APP_ID_NAME, Config.APP_ID);

            try {
                String finalUrl = URLUtils.buildURL(mUrl, requestParameters);
                LOGI(TAG, "Build SearchNews API Endpoint successful. Built URL: " + finalUrl);
                return finalUrl;
            } catch (URISyntaxException e) {
                LOGE(TAG, "Passed URL is not valid. URL: " + mUrl, e.getCause());
                return null;
            }
        }
    }

    private class ApiGetPostsOfAuthor extends APIEndpoint {

        protected ApiGetPostsOfAuthor() {
            super(ARTICLE_PATH);
        }

        @Override
        public String buildAPIEndpoint() {

            if (mAuthorId == INVALID_ID) {
                throw new IllegalStateException("The author ID has not set. Must set author ID" +
                        " before build ApiGetPostsOfAuthor endpoint.");
            }

            // Build request parameters
            Map<String, String> requestParameters = new LinkedHashMap<>(5);
            requestParameters.put(TYPE_NAME, GET_AUTHOR_TYPE_VALUE);
            requestParameters.put(AUTHOR_ID_NAME, String.valueOf(mAuthorId));
            requestParameters.put(LIMIT_NAME, String.valueOf(Config.DEFAULT_NUMBER_OF_ARTICLE_WITH_CATEGORY));
            requestParameters.put(APP_ID_NAME, Config.APP_ID);

            try {
                String finalUrl = URLUtils.buildURL(mUrl, requestParameters);
                LOGI(TAG, "Build GetPostsOfAuthor API Endpoint successful. Built URL: " + finalUrl);
                return finalUrl;
            } catch (URISyntaxException e) {
                LOGE(TAG, "Passed URL is not valid. URL: " + mUrl, e.getCause());
                return null;
            }
        }
    }

}
