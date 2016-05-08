package com.phattn.vnexpressnews.util;

import com.phattn.vnexpressnews.Config;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This class provides some helper methods to work with DateTime easier
 */
public class DateTimeUtils {

    /**
     * Format time in seconds into string with format {@link Config#DATE_TIME_FORMAT_TEMPLATE}
     */
    public static String format(long seconds) {
        SimpleDateFormat sdf = new SimpleDateFormat(Config.DATE_TIME_FORMAT_TEMPLATE, Locale.US);
        long milliseconds = seconds * 1000;
        return sdf.format(new Date(milliseconds));
    }

    public static String toMinutes(long milliseconds) {
        long seconds = Math.round(milliseconds / 1000);
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }

}
