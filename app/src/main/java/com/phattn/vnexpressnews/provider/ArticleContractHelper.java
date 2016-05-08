package com.phattn.vnexpressnews.provider;

import android.net.Uri;

import com.phattn.vnexpressnews.util.TextUtils;

/**
 * Provides helper methods for specifying query parameters on {@code Uri}s.
 */
public class ArticleContractHelper {

    public static final String QUERY_PARAMETER_DISTINCT = "distinct";

    public static boolean isQueryDistinct(Uri uri) {
        return !TextUtils.isEmpty(uri.getQueryParameter(QUERY_PARAMETER_DISTINCT));
    }

    public static String formatQueryDistinctParameter(String parameter) {
        return QUERY_PARAMETER_DISTINCT + " " + parameter;
    }
}
