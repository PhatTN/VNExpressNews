package com.phattn.vnexpressnews.util;

import android.text.Spanned;
import android.text.SpannedString;

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

    /**
     * Check passed argument is null or empty
     *
     * @return true if passed argument is or empty. Otherwise is false
     */
    public static boolean isEmpty(CharSequence charSequence) {
        if (charSequence == null || charSequence.length() == 0) {
            return true;
        }

        return false;
    }

    /**
     * Finds index of last-non whitespace character of {@link CharSequence}.
     * This method is almost used for trim trailing while space {@link CharSequence}
     */
    private static int findIndexOfLastNonWhiteSpaceCharacter(CharSequence charSequence) {
        int length = charSequence.length() - 1;

        for (int i = length; i >= 0; i--) {
            if (!Character.isWhitespace(charSequence.charAt(i))) {
                return i;
            }
        }

        return 0;
    }

    /**
     * Trims trailing whitespace
     */
    public static Spanned trimTrailingWhiteSpace(Spanned spanned) {

        if (spanned == null || spanned.length() == 0) {
            return new SpannedString("");
        }

        int lastIndexNonWhiteSpace = findIndexOfLastNonWhiteSpaceCharacter(spanned);

        return (Spanned) spanned.subSequence(0, lastIndexNonWhiteSpace + 1);
    }

}
