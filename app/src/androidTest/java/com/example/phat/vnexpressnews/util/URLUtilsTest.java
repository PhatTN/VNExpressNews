package com.example.phat.vnexpressnews.util;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link URLUtils}. Because this class depend on some
 * lib in Android SDK. So, it should be tested by instrumented unit test
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class URLUtilsTest {

    private static final String TEST_WEB_URL_CORRECT_FORMAT = "https://example.com/demo";
    private static final String TEST_WEB_URL_CORRECT_FORMAT_WITH_ARGUMENT = "http://www.example.com.vn/demo?arg1=hello&arg2=%20world";
    private static final String TEST_WEB_URL_INCORRECT_FORMAT_NO_DOMAIN_NAME = "HTtp://example/demo";
    private static final String TEST_WEB_URL_INCORRECT_FORMAT = "https://.example.com/demo";
    private static final String TEST_WEB_URL_INCORRECT_SCHEME = "file://example.com";
    private static final String TEST_WEB_URL_NULL = null;
    private static final String TEST_WEB_URL_EMPTY = "";
    private static final Map<String, String> MOCK_REQUEST_PARAMETER_MAP;

    static {
        MOCK_REQUEST_PARAMETER_MAP = new LinkedHashMap<String, String>() {{ // Use LinkedHashMap to remain the order
            put("params1", "Param value 1"); // try put space in value
            put("params2", "243"); // try put value as number
            put("params 3", "ParamValue3"); // try put space in key
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

    @Test(expected = URISyntaxException.class)
    public void checkBuildURL_InCorrectURLFormat_ThrowsURISE() throws URISyntaxException {
        URLUtils.buildURL(TEST_WEB_URL_INCORRECT_FORMAT, MOCK_REQUEST_PARAMETER_MAP);
    }

    @Test
    public void checkBuildURL_CorrectURLFormat_ReturnsTrue() throws URISyntaxException{
        final String expectedURL = "https://example.com/demo?params1=Param+value+1&params2=243&params+3=ParamValue3";
        assertThat(URLUtils.buildURL(TEST_WEB_URL_CORRECT_FORMAT, MOCK_REQUEST_PARAMETER_MAP),
                is(equalTo(expectedURL)));
    }

}
