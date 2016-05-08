package com.phattn.vnexpressnews.io;

import android.test.suitebuilder.annotation.MediumTest;

import com.phattn.vnexpressnews.exceptions.InternalServerErrorException;
import com.phattn.vnexpressnews.exceptions.JacksonProcessingException;
import com.phattn.vnexpressnews.model.Category;
import com.phattn.vnexpressnews.util.CollectionUtils;
import com.phattn.vnexpressnews.util.FileSystemUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

@MediumTest
@RunWith(MockitoJUnitRunner.class)
public class CategoryHandlerTest {

    private String jsonString;
    private JSONHandler<List<Category>> jsonHandler;

    // All of this properties are got from json file which is used for this test
    private static final List<Category> MOCK_CATEGORY_LIST = new ArrayList<>(3);

    static {
        MOCK_CATEGORY_LIST.add(new Category(
                1001005,   // Category ID
                "Thời sự", // Category Name
                "thoi-su", // Category Code
                1000000,   // Parent ID
                1000000,   // Full Parent
                1,         // Show Folder
                1          // Display Order
        ));

        MOCK_CATEGORY_LIST.add(new Category(
                1001002,   // Category ID
                "Thế giới", // Category Name
                "the-gioi", // Category Code
                1000000,   // Parent ID
                1000000,   // Full Parent
                1,         // Show Folder
                2          // Display Order
        ));

        MOCK_CATEGORY_LIST.add(new Category(
                1003450,   // Category ID
                "Góc nhìn", // Category Name
                "goc-nhin", // Category Code
                1000000,   // Parent ID
                1000000,   // Full Parent
                1,         // Show Folder
                3          // Display Order
        ));
    }

    @Before
    public void createJsonHandler() {
        jsonHandler = new CategoryHandler();
    }

    @Test
    public void parseJson_CorrectFormat_ParseIntoListCorrectly()
            throws JacksonProcessingException, InternalServerErrorException{
        initWithCorrectFormatJsonResources();
        List<Category> categories = jsonHandler.process(jsonString);

        assertThat("Expected: The list should be not null. But it wasn't", categories, is(notNullValue()));
        assertThat("Expected: The list should be empty. But it wasn't", categories.isEmpty(), is(false));
        assertThat("Expected: The category list should be same as MOCK_CATEGORY_LIST. But it wasn't",
                CollectionUtils.equalLists(categories, MOCK_CATEGORY_LIST), is(true));
    }

    @Test(expected = JacksonProcessingException.class)
    public void parseJson_InCorrectFormat_ThrowsJPE()
            throws JacksonProcessingException, InternalServerErrorException{
        initWithInCorrectFormatJsonResources();
        jsonHandler.process(jsonString);
    }

    @Test(expected = JacksonProcessingException.class)
    public void parseJson_NullJsonResource_ThrowsJPE()
            throws JacksonProcessingException, InternalServerErrorException{
        initWithANullJsonResources();
        jsonHandler.process(jsonString);
    }

    @Test(expected = InternalServerErrorException.class)
    public void parseJson_CorrectFormatWithErrorCodeIsNotEqualZero_ThrowsISEE()
            throws JacksonProcessingException, InternalServerErrorException{
        initWithCorrectFormatJsonResourcesWithErrorCodeIsNotEqualZero();
        jsonHandler.process(jsonString);
    }

    private void initWithCorrectFormatJsonResources() {
        final String filename = "category_correctFormat.json";
        jsonString = FileSystemUtils.loadJsonFile(filename);
    }

    private void initWithInCorrectFormatJsonResources() {
        final String filename = "category_incorrectFormat.json"; // this json file include an object is not Category
        jsonString = FileSystemUtils.loadJsonFile(filename);
    }

    private void initWithCorrectFormatJsonResourcesWithErrorCodeIsNotEqualZero() {
        final String filename = "category_correctFormat_withErrorCodeIsNotEqualZero.json";
        jsonString = FileSystemUtils.loadJsonFile(filename);
    }

    private void initWithANullJsonResources() {
        jsonString = null;
    }
}
