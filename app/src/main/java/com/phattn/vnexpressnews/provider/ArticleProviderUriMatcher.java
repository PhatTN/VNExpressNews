package com.phattn.vnexpressnews.provider;

import android.content.UriMatcher;
import android.net.Uri;
import android.util.SparseArray;

/**
 * Provides methods to match a {@link android.net.Uri} to a {@link ArticleUriEnum}.
 */
public class ArticleProviderUriMatcher {

    private UriMatcher mUriMatcher;

    private SparseArray<ArticleUriEnum> mEnumsMap = new SparseArray<>();

    public ArticleProviderUriMatcher() {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        buildUriMatcher();
    }

    private void buildUriMatcher() {
        final String authority = ArticleContract.CONTENT_AUTHORITY;

        ArticleUriEnum[] uris = ArticleUriEnum.values();
        for (int i = 0; i < uris.length; i++) {
            mUriMatcher.addURI(authority, uris[i].path, uris[i].code);
        }

        buildEnumsMap();
    }

    private void buildEnumsMap() {
        ArticleUriEnum[] uris = ArticleUriEnum.values();
        for (int i = 0; i < uris.length; i++) {
            mEnumsMap.put(uris[i].code, uris[i]);
        }
    }

    /**
     * Matches a {@code uri} to a {@link ArticleUriEnum}.
     *
     * @return the {@link ArticleUriEnum}, or throws new UnsupportedOperationException if no match.
     */
    public ArticleUriEnum matchUri(Uri uri) {
        final int code = mUriMatcher.match(uri);
        try {
            return matchCode(code);
        } catch (UnsupportedOperationException e) {
            throw new UnsupportedOperationException("Unknown uri " + uri);
        }
    }

    /**
     * Matches a {@code code} to a {@link ArticleUriEnum}.
     *
     * @return the {@link ArticleUriEnum}, or throws new UnsupportedOperationException if no match
     */
    public ArticleUriEnum matchCode(int code) {
        ArticleUriEnum articleUriEnum = mEnumsMap.get(code);
        if (articleUriEnum != null) {
            return articleUriEnum;
        } else throw new UnsupportedOperationException("Unknown uri with code " + code);
    }
}
