package com.example.phat.vnexpressnews.util;

import android.content.Context;

/**
 * This class provides methods that helps to convert from an unit to other one.
 * Such as: convert from dp to px, vice versa, ...
 */
public class UnitConverter {

    /**
     * Convert an dps unit to a pixel unit
     *
     * @param context Used to get screen's density scale.
     * @param dps Number of dps wants to convert to pixel unit
     */
    public static int dpsToPixel(Context context, int dps) {
        // Get screen's density scale
        final float scale = context.getResources().getDisplayMetrics().density;

        // Convert the dps to pixel, based on density scale
        return (int) (dps * scale + 0.5f);
    }

}
