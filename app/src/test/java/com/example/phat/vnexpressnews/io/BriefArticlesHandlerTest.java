package com.example.phat.vnexpressnews.io;

import android.test.suitebuilder.annotation.MediumTest;

import com.example.phat.vnexpressnews.exceptions.InternalServerErrorException;
import com.example.phat.vnexpressnews.exceptions.JacksonProcessingException;
import com.example.phat.vnexpressnews.model.BriefArticle;
import com.example.phat.vnexpressnews.util.FileSystemUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@MediumTest
@RunWith(MockitoJUnitRunner.class)
public class BriefArticlesHandlerTest {

    private String jsonString;
    private JSONHandler<Set<BriefArticle>> jsonHandler;

    @Before
    public void createJsonHandler() {
        jsonHandler = new BriefArticlesHandler();
    }

    @Test
    public void parseJson_CorrectFormat_ReturnsFalse() throws JacksonProcessingException, InternalServerErrorException{

        initWithCorrectFormatJsonResource();

        Set<BriefArticle> set = jsonHandler.process(jsonString);

        assertThat(set.isEmpty(), is(false));
    }

    @Test(expected = JacksonProcessingException.class)
    public void parseJson_InCorrectFormat_ThrowsJPE() throws JacksonProcessingException, InternalServerErrorException{
        initWithInCorrectFormatJsonResource();
        jsonHandler.process(jsonString);
    }

    @Test(expected = InternalServerErrorException.class)
    public void parseJson_CorrectFormatWithErrorCodeIsNotEqualZero_ThrowsISEE()
            throws JacksonProcessingException, InternalServerErrorException{
        initWithCorrectFormatJsonResourceWithErrorCodeIsNotEqualZero();
        jsonHandler.process(jsonString);
    }

    @Test(expected = JacksonProcessingException.class)
    public void parseJson_ANullJsonResource_ThrowsJPE()
            throws JacksonProcessingException, InternalServerErrorException{
        initWithANullJsonResources();
        jsonHandler.process(jsonString);
    }

    private void initWithCorrectFormatJsonResource() {
        final String filename = "briefArticle_correctFormat.json";
        jsonString = FileSystemUtils.loadJsonFile(filename);
    }

    private void initWithInCorrectFormatJsonResource() {
        final String filename = "briefArticle_IncorrectFormat.json";
        jsonString = FileSystemUtils.loadJsonFile(filename);
    }

    private void initWithCorrectFormatJsonResourceWithErrorCodeIsNotEqualZero() {
        final String filename = "briefArticle_correctFormat_withErrorCodeIsNotEqualZero.json";
        jsonString = FileSystemUtils.loadJsonFile(filename);
    }

    private void initWithANullJsonResources() {
        jsonString = null;
    }
}
