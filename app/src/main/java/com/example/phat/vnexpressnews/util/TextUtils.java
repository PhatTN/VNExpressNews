package com.example.phat.vnexpressnews.util;

/**
 * This class provides some helper methods to interact, modify {@link String}
 */
public class TextUtils {

    /**
     * Check if string is null or empty.
     * Android have {@link android.text.TextUtils#isEmpty(CharSequence)} method,
     * it same function as this. But I write this for testing purpose. We can create unit test and run it on
     * JVM dependently without need Android SDK. It will much faster for testing.
     */
    public static boolean isEmpty(String str) {
        if (str == null || str.length() == 0) {
            return true;
        }

        return false;
    }

}
