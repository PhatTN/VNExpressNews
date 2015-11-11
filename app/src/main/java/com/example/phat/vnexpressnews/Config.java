package com.example.phat.vnexpressnews;

import com.example.phat.vnexpressnews.fragments.BrowseNewsFragment;

/**
 * General Configuration
 */
public class Config {

    /** Is this an internal dogfood build? */
    public static final boolean IS_DOGFOOD_BUILD = false;

    public static final String PACKAGE_NAME = "com.example.phat.vnexpressnews";

    /** Need this to access API */
    public static final String APP_ID = "c31b32";

    /** A base url to build other end point API */
    public static final String BASE_URL_API = "http://api3.vnexpress.net/";
    public static final String BASE_COMMENT_URL_API = "http://usi.saas.vnexpress.net/";

    /** List of default category id */
    public static final int DEFAULT_CATEGORY_ID_TOP_NEWS = -1;
    public static final int DEFAULT_CATEGORY_SETTINGS_ID = -2;
    public static final int DEFAULT_CATEGORY_LOGIN_ID = -3;
    public static final int DEFAULT_CATEGORY_LOGOUT_ID = -4;
    public static final int DEFAULT_CATEGORY_ID_NEWS = 1001005;
    public static final int DEFAULT_CATEGORY_ID_THE_WORLD = 1001002;
    public static final int DEFAULT_CATEGORY_ID_PERSPECTIVE = 1003450;
    public static final int DEFAULT_CATEGORY_ID_BUSINESS = 1003159;
    public static final int DEFAULT_CATEGORY_ID_ENTERTAINMENT = 1002691;
    public static final int DEFAULT_CATEGORY_ID_SPORT = 1002565;
    public static final int DEFAULT_CATEGORY_ID_LAW = 1001007;
    public static final int DEFAULT_CATEGORY_ID_EDUCATION = 1003497;
    public static final int DEFAULT_CATEGORY_ID_HEALTH = 1003750;
    public static final int DEFAULT_CATEGORY_ID_FAMILY = 1002966;
    public static final int DEFAULT_CATEGORY_ID_TRAVEL = 1003231;
    public static final int DEFAULT_CATEGORY_ID_SCIENCE = 1001009;
    public static final int DEFAULT_CATEGORY_ID_DIGITIZATION = 1002592;
    public static final int DEFAULT_CATEGORY_ID_VEHICLE = 1001006;
    public static final int DEFAULT_CATEGORY_ID_COMMUNITY = 1001012;
    public static final int DEFAULT_CATEGORY_ID_CONFIDENCE = 1001014;
    public static final int DEFAULT_CATEGORY_ID_VIDEO = 1001019;
    public static final int DEFAULT_CATEGORY_ID_WANT_AD = 1001025;
    public static final int DEFAULT_CATEGORY_ID_FUNNY = 1001011;

    /** Number of top story article will be loaded into home screen */
    public static final int NUMBER_OF_TOP_STORY_ARTICLE_ = 15;
    /** Number of perspective(translate: Góc Nhìn, I don't know this term is correct or not :D)
     * article will be loaded into home screen */
    public static final int NUMBER_OF_PERSPECTIVE_ARTICLE = 1;
    /** Number of video article will be loaded into home screen */
    public static final int NUMBER_OF_VNE_VIDEO_ARTICLE = 3;
    /** Number of photo article will be loaded into home screen */
    public static final int NUMBER_OF_VNE_PHOTO_ARTICLE = 6;
    /** Number of other articles will be loaded into home screen */
    public static final int NUMBER_OF_EACH_OTHER_ARTICLE = 5;
    /** Number of articles will be loaded when selecting a category */
    public static final int DEFAULT_NUMBER_OF_ARTICLE_WITH_CATEGORY = 30;

    /** Number of brief articles columns will be displayed in {@link BrowseNewsFragment} */
    public static final int DEFAULT_SPAN_COUNT = 2;
}
