package com.phattn.vnexpressnews.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.phattn.vnexpressnews.R;

/**
 * This class provides some methods to handles navigation drawer and it's menu item
 */
public class NavDrawerUtils {

    // Primary color is used to displays instead of menu item icon, and toolbar color,
    // must correspond to the bellow array
    private static final int[] PRIMARY_COLOR = new int[] {
            R.color.color_red,
            R.color.color_indigo,
            R.color.color_purple,
            R.color.color_pink,
            R.color.color_green,
            R.color.color_blue,
            R.color.color_deep_orange,
            R.color.color_deep_purple,
            R.color.color_teal,
            R.color.color_lime,
            R.color.color_amber,
            R.color.color_brown,
            R.color.color_grey,
            R.color.color_light_green,
            R.color.color_cyan,
            R.color.color_orange,
            R.color.color_yellow,
            R.color.color_light_blue,
            R.color.color_blue_grey
    };

    private static final int PRIMARY_COLOR_ARRAY_SIZE = PRIMARY_COLOR.length;

    // Primary dark color is used to instead of status bar color
    // This array must correspond to the above array
    private static final int[] PRIMARY_DARK_COLOR = new int[] {
            R.color.color_red_dark,
            R.color.color_indigo_dark,
            R.color.color_purple_dark,
            R.color.color_pink_dark,
            R.color.color_green_dark,
            R.color.color_blue_dark,
            R.color.color_deep_orange_dark,
            R.color.color_deep_purple_dark,
            R.color.color_teal_dark,
            R.color.color_lime_dark,
            R.color.color_amber_dark,
            R.color.color_brown_dark,
            R.color.color_grey_dark,
            R.color.color_light_green_dark,
            R.color.color_cyan_dark,
            R.color.color_orange_dark,
            R.color.color_yellow_dark,
            R.color.color_light_blue_dark,
            R.color.color_blue_grey_dark
    };

    private static final int PRIMARY_DARK_COLOR_ARRAY_SIZE = PRIMARY_DARK_COLOR.length;
    public static final int INVALID_COLOR_ID = -1;

    /**
     * The helper method returns color resource id based on passed index
     */
    public static int getPrimaryColor(int index) {
        if (index < 0) {
            return INVALID_COLOR_ID;
        }

        if (index > PRIMARY_COLOR_ARRAY_SIZE) {
            index -= PRIMARY_COLOR_ARRAY_SIZE;
        }

        return PRIMARY_COLOR[index];
    }

    /**
     * The helper method returns an integer which indicates an ARGB color
     */
    public static int getPrimaryColor(Context context, int index) {
        int resourceId = getPrimaryColor(index);
        return ContextCompat.getColor(context, resourceId);
    }

    public static int getPrimaryDarkColor(int index) {
        if (index < 0) {
            return INVALID_COLOR_ID;
        }

        if (index > PRIMARY_DARK_COLOR_ARRAY_SIZE) {
            index -= PRIMARY_DARK_COLOR_ARRAY_SIZE;
        }

        return PRIMARY_DARK_COLOR[index];
    }

}
