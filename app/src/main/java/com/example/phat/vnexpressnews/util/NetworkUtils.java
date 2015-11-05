package com.example.phat.vnexpressnews.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * This helper class provides some static methods that help us handle network-related task.
 */
public class NetworkUtils {

    /**
     * This method checks the device is online or not
     *
     * @return true if the device connected to a network, otherwise is fail.
     */
    public static boolean hasNetworkConnection(Context context) {
        NetworkInfo activeConnection = ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        return (activeConnection != null) && activeConnection.isConnected();
    }

}
