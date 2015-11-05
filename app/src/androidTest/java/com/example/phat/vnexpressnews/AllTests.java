package com.example.phat.vnexpressnews;

import com.example.phat.vnexpressnews.provider.ArticleContractTest;
import com.example.phat.vnexpressnews.provider.ArticleDatabaseTest;
import com.example.phat.vnexpressnews.provider.ArticleProviderTest;
import com.example.phat.vnexpressnews.provider.ArticleProviderUriMatcherTest;
import com.example.phat.vnexpressnews.util.APIEndpointBuilderTest;
import com.example.phat.vnexpressnews.util.URLUtilsTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ArticleContractTest.class,
        ArticleDatabaseTest.class,
        ArticleProviderTest.class,
        ArticleProviderUriMatcherTest.class,
        APIEndpointBuilderTest.class,
        URLUtilsTest.class
})
public class AllTests{}