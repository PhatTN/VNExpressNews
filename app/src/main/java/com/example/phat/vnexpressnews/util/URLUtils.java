package com.example.phat.vnexpressnews.util;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Patterns;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static com.example.phat.vnexpressnews.util.LogUtils.makeLogTag;

/**
 * The class proves some helper method to check valid URL,
 * create valid URL, ...
 */
public class URLUtils {
    private static final String TAG = makeLogTag(URLUtils.class);

    /**
     * This is used to check the given URL is valid or not
     * @param url The URL that will be checked
     * @return true if valid, otherwise is false
     */
    public static boolean isValidURL(String url) {

        // The first, it must be not null or empty
        if (TextUtils.isEmpty(url)) {
            return false;
        }

        return Patterns.WEB_URL.matcher(url).matches();
    }

    /**
     * This method helps to build a valid URL
     * @param url A base url which we build on it
     * @param requestParameters A request parameter map
     * @return A valid parameter in {@link String} format
     */
    public static String buildURL(String url, Map<String, String> requestParameters)
            throws URISyntaxException{

        // Check url argument is valid or not
        if (!isValidURL(url)) {
            if (url == null) {
                throw new NullPointerException("Passed URL Argument must not be null");
            }
            throw new URISyntaxException(url, "Passed URL Argument is invalid");
        }

        Uri.Builder uriBuilder = Uri.parse(url).buildUpon();

        if (requestParameters != null) {
            // Loop through requestParameter Map and add parameter to urlBuilder
            for (String key : requestParameters.keySet()) {
                String value = requestParameters.get(key);
                uriBuilder.appendQueryParameter(key, value);
            }
        }

        String builtUrl = uriBuilder.build().toString();

        return builtUrl;
    }

    /**
     * SubURL term means incomplete URL. It does not include scheme, host. It only includes
     * path and query string. Some API Endpoint in this app are weird. It composes multi sub url
     * in query string as:
     * http://api3.vnexpress.net/api/group?api[]=article?type=get_topstory&limit=15&api[]=article?type=get_rule_2
     * In above API Endpoint has 2 sub url: article?type=get_topstory&limit=15 and article?type=get_rule_2
     * Their format like a query string, but in fact, both of them are "value" role for "api[]" request name.
     * So we created this method to build sub url like that for some API Endpoint.
     *
     */
    public static String buildSubURL(@NonNull String path, Map<String, String> requestParameters)
            throws URISyntaxException {

        if (TextUtils.isEmpty(path)) {
            throw new IllegalStateException("The first parameter must not be null or empty.");
        }

        if (requestParameters == null) {
            throw new IllegalStateException("The request parameters must not be null.");
        }

        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.path(path);

        // Loop through requestParameter Map and add parameter to urlBuilder
        for (String key : requestParameters.keySet()) {
            String value = requestParameters.get(key);
            uriBuilder.appendQueryParameter(key, value);
        }

        String builtUrl = uriBuilder.build().toString();

        return builtUrl;
    }

    /**
     * Build a url with its path. This method will remove existed path of URL,
     * then set its path to {@code path} param
     *
     * @param url  The URL which you want to build upon
     * @param path The path which is appended to the URL
     * @throws URISyntaxException if either the URL or the path is invalid
     */
    public static String buildURL(String url, String path) throws URISyntaxException {

        if (!isValidURL(url)) {
            if (url == null) {
                throw new NullPointerException("Passed URL Argument must not be null");
            }
            throw new URISyntaxException(url, "Passed URL Argument is invalid");
        }

        Uri.Builder uriBuilder = Uri.parse(url).buildUpon();

        if (!TextUtils.isEmpty(path)) {
            uriBuilder.path(path);
        }

        String builtUrl = uriBuilder.build().toString();
        return builtUrl;
    }

    /**
     * Basically, this method has same function as {@code buildURL<String, Map<String, String>} method.
     * But, in some cases, an URL includes complex query string. The query string has one "name" contains
     * multi values. Example: https://example.com/api?param=TheFirst&param=TheSecond&param=TheThird&app_id=111.
     * In the above example URL, the name "param" repeats three times and includes different value.
     * So for building that URL is this methods purpose.
     *
     * @param url  The URL which you want to build upon
     * @param requestParameters  A request parameter map
     */
    public static String buildComplexURL(String url, Map<String, List<String>> requestParameters)
            throws URISyntaxException {
        // Check url argument is valid or not
        if (!isValidURL(url)) {
            if (url == null) {
                throw new NullPointerException("Passed URL Argument must not be null");
            }
            throw new URISyntaxException(url, "Passed URL Argument is invalid");
        }

        Uri.Builder uriBuilder = Uri.parse(url).buildUpon();

        if (requestParameters != null) {
            // Loop through requestParameter Map and add parameter to urlBuilder
            for (String key : requestParameters.keySet()) {
                List<String> values = requestParameters.get(key);
                if (values == null || values.isEmpty()) {
                    continue;
                }

                if (values.size() == 1) {
                    uriBuilder.appendQueryParameter(key, values.get(0));
                    continue;
                }

                for (String v : values) {
                    uriBuilder.appendQueryParameter(key, v);
                }
            }
        }

        String builtUrl = uriBuilder.build().toString();

        return builtUrl;
    }
}
