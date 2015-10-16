package com.example.phat.vnexpressnews.util;

import android.text.TextUtils;
import android.util.Patterns;

/**
 * The class proves some helper method to check valid URL,
 * create valid URL, ...
 */
public class URLUtils {

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

}
