package com.phattn.vnexpressnews;

import com.phattn.vnexpressnews.provider.ArticleContractTest;
import com.phattn.vnexpressnews.provider.ArticleDatabaseTest;
import com.phattn.vnexpressnews.provider.ArticleProviderTest;
import com.phattn.vnexpressnews.provider.ArticleProviderUriMatcherTest;
import com.phattn.vnexpressnews.util.APIEndpointBuilderTest;
import com.phattn.vnexpressnews.util.URLUtilsTest;

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