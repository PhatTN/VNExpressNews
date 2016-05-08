package com.phattn.vnexpressnews.io;

import android.test.suitebuilder.annotation.MediumTest;

import com.phattn.vnexpressnews.exceptions.InternalServerErrorException;
import com.phattn.vnexpressnews.exceptions.JacksonProcessingException;
import com.phattn.vnexpressnews.io.BriefArticlesHandler.HandlerType;
import com.phattn.vnexpressnews.model.BriefArticle;
import com.phattn.vnexpressnews.util.FileSystemUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;


@MediumTest
@RunWith(MockitoJUnitRunner.class)
public class BriefArticlesHandlerTest {

    private String mJsonString;
    private JSONHandler<Set<BriefArticle>> mJsonHandler;

    @Before
    public void reset() {
        mJsonString = null;
    }

    @Test
    public void parseJson_TopNewsType_CorrectFormat_ReturnsFalse()
            throws JacksonProcessingException, InternalServerErrorException{
        mJsonHandler = new BriefArticlesHandler(HandlerType.TOP_NEWS);
        initWithCorrectFormatJsonResource();

        Set<BriefArticle> set = mJsonHandler.process(mJsonString);

        assertThat(set.isEmpty(), is(false));
    }

    @Test(expected = JacksonProcessingException.class)
    public void parseJson_TopNewsType_InCorrectFormat_ThrowsJPE()
            throws JacksonProcessingException, InternalServerErrorException{
        mJsonHandler = new BriefArticlesHandler(HandlerType.TOP_NEWS);
        initWithInCorrectFormatJsonResource();
        mJsonHandler.process(mJsonString);
    }

    @Test(expected = InternalServerErrorException.class)
    public void parseJson_TopNewsType_CorrectFormatWithErrorCodeIsNotEqualZero_ThrowsISEE()
            throws JacksonProcessingException, InternalServerErrorException{
        mJsonHandler = new BriefArticlesHandler(HandlerType.TOP_NEWS);
        initWithCorrectFormatJsonResourceWithErrorCodeIsNotEqualZero();
        mJsonHandler.process(mJsonString);
    }

    @Test(expected = JacksonProcessingException.class)
    public void parseJson_TopNewsType_NullJsonResource_ThrowsJPE()
            throws JacksonProcessingException, InternalServerErrorException{
        mJsonHandler = new BriefArticlesHandler(HandlerType.TOP_NEWS);
        initWithNullJsonResources();
        mJsonHandler.process(mJsonString);
    }

    @Test
    public void parseJson_newsWithCategoryType_CorrectFormat_ReturnsFalse()
            throws JacksonProcessingException, InternalServerErrorException{

        // Load mock data
        final String filename = "briefArticle_newsWithCategory_correctFormat.json";
        loadJsonFile(filename);

        mJsonHandler = new BriefArticlesHandler(HandlerType.NEWS_WITH_CATEGORY);
        Set<BriefArticle> set = mJsonHandler.process(mJsonString);

        assertThat(set.isEmpty(), is(false));
    }

    @Test
    public void parseJson_newsWithCategoryType_InCorrectFormat_ThrowsJPE()
            throws JacksonProcessingException, InternalServerErrorException{
        // Load mock data
        final String filename = "briefArticle_newsWithCategory_incorrectFormat.json";
        loadJsonFile(filename);

        mJsonHandler = new BriefArticlesHandler(HandlerType.NEWS_WITH_CATEGORY);
        Set<BriefArticle> set = mJsonHandler.process(mJsonString);
        assertThat(set.isEmpty(), is(true));
    }

    @Test(expected = InternalServerErrorException.class)
    public void parseJson_newsWithCategoryType_CorrectFormatWithErrorCodeIsNotEqualZero_ThrowsISEE()
            throws JacksonProcessingException, InternalServerErrorException{
        // Load mock data
        final String filename = "briefArticle_newsWithCategory_correctFormat_withErrorCodeIsNotEqualZero.json";
        loadJsonFile(filename);

        mJsonHandler = new BriefArticlesHandler(HandlerType.NEWS_WITH_CATEGORY);
        mJsonHandler.process(mJsonString);
    }

    @Test(expected = JacksonProcessingException.class)
    public void parseJson_newsWithCategoryType_NullJsonResource_ThrowsJPE()
            throws JacksonProcessingException, InternalServerErrorException{
        mJsonHandler = new BriefArticlesHandler(HandlerType.NEWS_WITH_CATEGORY);
        initWithNullJsonResources();
        mJsonHandler.process(mJsonString);
    }

    private void initWithCorrectFormatJsonResource() {
        final String filename = "briefArticle_topNews_correctFormat.json";
        mJsonString = FileSystemUtils.loadJsonFile(filename);
        assertThat("Can not load JSON file " + filename, mJsonString, is(notNullValue()));
    }

    private void initWithInCorrectFormatJsonResource() {
        final String filename = "briefArticle_topNews_IncorrectFormat.json";
        mJsonString = FileSystemUtils.loadJsonFile(filename);
        assertThat("Can not load JSON file " + filename, mJsonString, is(notNullValue()));
    }

    private void initWithCorrectFormatJsonResourceWithErrorCodeIsNotEqualZero() {
        final String filename = "briefArticle_topNews_correctFormat_withErrorCodeIsNotEqualZero.json";
        mJsonString = FileSystemUtils.loadJsonFile(filename);
        assertThat("Can not load JSON file " + filename, mJsonString, is(notNullValue()));
    }

    private void initWithNullJsonResources() {
        mJsonString = null;
    }

    private void loadJsonFile(String filename) {
        mJsonString = FileSystemUtils.loadJsonFile(filename);
        assertThat("Can not load JSON file " + filename, mJsonString, is(notNullValue()));
    }
}
