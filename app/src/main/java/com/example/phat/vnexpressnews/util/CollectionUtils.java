package com.example.phat.vnexpressnews.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class provides some helper methods to interact with {@link java.util.Collection}
 */
public class CollectionUtils {

    /**
     * This method help to compare two lists are same or not
     */
    public static <T extends Comparable<? super T>> boolean equalLists(List<T> one, List<T> two) {
        if (one == null && two == null) {
            return true;
        }

        if (one == null || two == null || (one.size() != two.size())) {
            return false;
        }

        // to avoid modifying the order of the lists, we will use a copy
        one = new ArrayList<>(one);
        two = new ArrayList<>(two);

        Collections.sort(one);
        Collections.sort(two);

        return one.equals(two);
    }

}
