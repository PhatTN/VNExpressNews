package com.example.phat.vnexpressnews.util;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link URLUtils}. Because {@link URLUtils} class depends on some
 * lib in Android SDK. So, it should be tested by instrumented unit test
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class URLUtilsTest {

    private static final String TEST_WEB_URL_CORRECT_FORMAT = "https://example.com/demo";
    private static final String TEST_WEB_URL_CORRECT_FORMAT_WITH_ARGUMENT = "http://www.example.com.vn/demo?arg1=hello&arg2=%20world";
    private static final String TEST_WEB_URL_CORRECT_FORMAT_WITH_SQUARE_BRACKETS = "https://www.example/demo?api%5B%5D?arg1=value1";
    private static final String TEST_WEB_URL_INCORRECT_FORMAT_NO_DOMAIN_NAME = "HTtp://example/demo";
    private static final String TEST_WEB_URL_INCORRECT_FORMAT = "https://.example.com/demo";
    private static final String TEST_WEB_URL_INCORRECT_SCHEME = "file://example.com";
    private static final String TEST_WEB_URL_NULL = null;
    private static final String TEST_WEB_URL_EMPTY = "";
    private static final Map<String, String> MOCK_REQUEST_PARAMETER_MAP;
    private static final Map<String, List<String>> MOCK_REQUEST_PARAMETERS_FOR_COMPLEX_URL;

    static {
        MOCK_REQUEST_PARAMETER_MAP = new LinkedHashMap<String, String>(3) {{ // Use LinkedHashMap to remain the order
            put("params1", "Param value 1"); // try put space in value
            put("params2", "243"); // try put value as number
            put("params 3", "ParamValue3"); // try put space in key
        }};

        MOCK_REQUEST_PARAMETERS_FOR_COMPLEX_URL = new LinkedHashMap<String, List<String>>(4) {{
            put("name1", Arrays.asList("Param value 1", "234", "Param1")); // Tests with three values, and tests encoding value
            put("name2", Collections.singletonList("Param value 2")); // Tests with only one value
            put("name 3", null);                                            // Tests with value is null
            put("name 4", new ArrayList<String>());                         // Tests with value is empty
        }};
    }

    @Test
    public void checkValidURL_CorrectURLFormat_ReturnsTrue() {
        assertTrue(URLUtils.isValidURL(TEST_WEB_URL_CORRECT_FORMAT));
    }

    @Test
    public void checkValidURL_CorrectURLFormatWithArgument_ReturnsTrue() {
        assertTrue(URLUtils.isValidURL(TEST_WEB_URL_CORRECT_FORMAT_WITH_ARGUMENT));
    }

    @Test
    public void checkValidURL_CorrectURLFormatWithSquareBrackets_ReturnsTrue() {
        assertTrue(URLUtils.isValidURL(TEST_WEB_URL_CORRECT_FORMAT_WITH_SQUARE_BRACKETS));
    }

    @Test
    public void checkValidURL_InCorrectURLFormatNoDomainName_ReturnsFalse() {
        assertFalse(URLUtils.isValidURL(TEST_WEB_URL_INCORRECT_FORMAT_NO_DOMAIN_NAME));
    }

    @Test
    public void checkValidURL_InCorrectURLFormat_ReturnsFalse() {
        assertFalse(URLUtils.isValidURL(TEST_WEB_URL_INCORRECT_FORMAT));
    }

    @Test
    public void checkValidURL_InCorrectScheme_ReturnsFalse() {
        assertFalse(URLUtils.isValidURL(TEST_WEB_URL_INCORRECT_SCHEME));
    }

    @Test
    public void checkValidURL_NullURL_ReturnsFalse() {
        assertFalse(URLUtils.isValidURL(TEST_WEB_URL_NULL));
    }

    @Test
    public void checkValidURL_EmptyURL_ReturnsFalse() {
        assertFalse(URLUtils.isValidURL(TEST_WEB_URL_EMPTY));
    }

    /**
     *  Tests buildURL(String,Map<String,String>) method
     */
    @Test(expected = URISyntaxException.class)
    public void checkBuildURL_InCorrectURLFormat_ThrowsUSE() throws URISyntaxException {
        URLUtils.buildURL(TEST_WEB_URL_INCORRECT_FORMAT, MOCK_REQUEST_PARAMETER_MAP);
    }

    /**
     *  Tests buildURL(String,Map<String,String>) method
     */
    @Test(expected = NullPointerException.class)
    public void checkBuildURL_NullURL_ThrowsNPE() throws URISyntaxException {
        URLUtils.buildURL(null, MOCK_REQUEST_PARAMETER_MAP);
    }

    /**
     *  Tests buildURL(String,Map<String,String>) method
     */
    @Test
    public void checkBuildURL_CorrectURLFormat_ReturnsTrue() throws URISyntaxException{
        final String expectedURL = "https://example.com/demo?params1=Param%20value%201&params2=243&params%203=ParamValue3";
        final String builtURL = URLUtils.buildURL(TEST_WEB_URL_CORRECT_FORMAT, MOCK_REQUEST_PARAMETER_MAP);
        assertTrue(URLUtils.isValidURL(builtURL));
        assertThat(builtURL, is(equalTo(expectedURL)));
    }

    /**
     *  Tests buildURL(String,Map<String,String>) method
     */
    @Test
    public void checkBuildURL_NullRequestParameters_ReturnsTrue() throws URISyntaxException{
        final String expectedURL = "https://example.com/demo";
        final Map<String, String> nullRequestParameters = null;
        final String builtURL = URLUtils.buildURL(TEST_WEB_URL_CORRECT_FORMAT, nullRequestParameters);
        assertThat(builtURL, is(equalTo(expectedURL)));
    }

    @Test(expected = IllegalStateException.class)
    public void checkBuildSubURL_NullPath_ThrowsISE() throws URISyntaxException {
        URLUtils.buildSubURL(null, MOCK_REQUEST_PARAMETER_MAP);
    }

    @Test(expected = IllegalStateException.class)
    public void checkBuildSubURL_NullRequestParameters_ThrowsISE() throws URISyntaxException {
        final Map<String, String> nullRequestParameters = null;
        URLUtils.buildSubURL("api/demo", nullRequestParameters);
    }

    @Test
    public void checkBuildSubURL_EmptyRequestParameters_ReturnsTrue() throws URISyntaxException {
        final String expectedResult = "api/demo";
        final Map<String, String> emptyRequestParameters = new HashMap<>();
        final String builtResult = URLUtils.buildSubURL("api/demo", emptyRequestParameters);
        assertThat(builtResult, is(equalTo(expectedResult)));
    }

    @Test
    public void checkBuildSubURL_CorrectFormat_ReturnsTrue() throws URISyntaxException {
        final String expectedResult = "api/demo?params1=Param%20value%201&params2=243&params%203=ParamValue3";
        final String builtResult = URLUtils.buildSubURL("api/demo", MOCK_REQUEST_PARAMETER_MAP);
        assertThat(builtResult, is(equalTo(expectedResult)));
    }

    /**
     *  Tests buildURL(String,String) method
     */
    @Test
    public void checkBuildURL_CorrectFormatURL_ReturnsTrue() throws URISyntaxException {
        final String path = "api/demo";
        final String correctFormatURL = "https://example.com";
        final String expectedResult = "https://example.com/api/demo";
        final String builtResult = URLUtils.buildURL(correctFormatURL, path);
        assertThat(builtResult, is(equalTo(expectedResult)));
    }

    /**
     *  Tests buildURL(String,String) method
     */
    @Test
    public void checkBuildURL_CorrectFormatURLCase2_ReturnsTrue() throws URISyntaxException {
        final String path = "api/demo";
        final String correctFormatURL = "https://example.com/path";
        final String expectedResult = "https://example.com/api/demo";
        final String builtResult = URLUtils.buildURL(correctFormatURL, path);
        assertThat(builtResult, is(equalTo(expectedResult)));
    }

    /**
     *  Tests buildURL(String,String) method
     */
    @Test(expected = URISyntaxException.class)
    public void checkBuildURL_IncorrectFormatURL_ThrowsUSE() throws URISyntaxException {
        final String path = "api/demo";
        final String incorrectFormatURL = "https://example";
        URLUtils.buildURL(incorrectFormatURL, path);
    }

    /**
     *  Tests buildURL(String,String) method
     */
    @Test(expected = NullPointerException.class)
    public void checkBuildURL_NullURLString_ThrowsNPE() throws URISyntaxException {
        final String path = "api/demo";
        final String nullURL = null;
        URLUtils.buildURL(nullURL, path);
    }

    /**
     *  Tests buildURL(String,String) method
     */
    @Test(expected = URISyntaxException.class)
    public void checkBuildURL_EmptyURL_ThrowsUSE() throws URISyntaxException {
        final String path = "api/demo";
        final String emptyURL = "";
        URLUtils.buildURL(emptyURL, path);
    }

    /**
     *  Tests buildURL(String,String) method
     */
    @Test
    public void checkBuildURL_NullPath_ReturnsTrue() throws URISyntaxException {
        final String path = null;
        final String correctFormatURL = "https://example.com";
        final String expectedResult = "https://example.com";
        final String builtResult = URLUtils.buildURL(correctFormatURL, path);
        assertThat(builtResult, is(equalTo(expectedResult)));
    }

    /**
     *  Tests buildURL(String,String) method
     */
    @Test
    public void checkBuildURL_EmptyPath_ReturnsTrue() throws URISyntaxException {
        final String path = "";
        final String correctFormatURL = "https://example.com";
        final String expectedResult = "https://example.com";
        final String builtResult = URLUtils.buildURL(correctFormatURL, path);
        assertThat(builtResult, is(equalTo(expectedResult)));
    }

    @Test(expected = NullPointerException.class)
    public void checkBuildComplexURL_NullURL_ThrowsNPE() throws URISyntaxException {
        URLUtils.buildComplexURL(null, MOCK_REQUEST_PARAMETERS_FOR_COMPLEX_URL);
    }

    @Test(expected = URISyntaxException.class)
    public void checkBuildComplexURL_EmptyURL_ThrowsUSE() throws URISyntaxException {
        URLUtils.buildComplexURL("", MOCK_REQUEST_PARAMETERS_FOR_COMPLEX_URL);
    }

    @Test(expected = URISyntaxException.class)
    public void checkBuildComplexURL_IncorrectFormatURL_ThrowsUSE() throws URISyntaxException {
        URLUtils.buildComplexURL(TEST_WEB_URL_INCORRECT_FORMAT_NO_DOMAIN_NAME, MOCK_REQUEST_PARAMETERS_FOR_COMPLEX_URL);
    }

    @Test
    public void checkBuildComplexURL_CorrectFormatURL_EqualsExpectedResult() throws URISyntaxException {
        final String expectedResult = "https://example.com/demo?name1=Param%20value%201&name1=234&name1=Param1&name2=Param%20value%202";
        final String actualResult = URLUtils.buildComplexURL(TEST_WEB_URL_CORRECT_FORMAT,
                MOCK_REQUEST_PARAMETERS_FOR_COMPLEX_URL);
        assertThat(actualResult, is(equalTo(expectedResult)));
    }
}
