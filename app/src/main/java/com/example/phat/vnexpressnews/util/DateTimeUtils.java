package com.example.phat.vnexpressnews.util;

import com.example.phat.vnexpressnews.Config;

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

}
