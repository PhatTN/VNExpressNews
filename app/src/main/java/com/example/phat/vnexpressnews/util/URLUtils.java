package com.example.phat.vnexpressnews.util;

import android.text.TextUtils;
import android.util.Patterns;

import org.apache.http.client.utils.URIBuilder;

import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;

import static com.example.phat.vnexpressnews.util.LogUtils.LOGE;
import static com.example.phat.vnexpressnews.util.LogUtils.LOGI;
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
            LOGE(TAG, "Passed URL String is invalid. URL: " +url);
            throw new URISyntaxException(url, "Passed URL Argument is invalid");
        }

        try {
            URIBuilder uriBuilder = new URIBuilder(url);

            // Loop through requestParameter Map and add parameter to urlBuilder
            Iterator<String> keys = requestParameters.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = requestParameters.get(key);

                uriBuilder.addParameter(key, value);
            }

            String builtUrl = uriBuilder.build().toString();

            LOGI(TAG, "Build url successful. Url is: " +builtUrl);
            return builtUrl;

        } catch (URISyntaxException e) {
            LOGE(TAG, "Passed URL is unhealthy. Url: " +url);
            throw new URISyntaxException(e.getInput(), e.getReason(), e.getIndex());
        }
    }

}
