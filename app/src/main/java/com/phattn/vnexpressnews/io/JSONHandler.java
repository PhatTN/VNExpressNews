package com.phattn.vnexpressnews.io;

import com.phattn.vnexpressnews.exceptions.InternalServerErrorException;
import com.phattn.vnexpressnews.exceptions.JacksonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.phattn.vnexpressnews.util.LogUtils.makeLogTag;

public abstract class JSONHandler<T> {
    private static final String TAG = makeLogTag(JSONHandler.class);

    protected static final boolean SHOULD_BE_NOT_CACHED = false;
    protected static final boolean SHOULD_BE_CACHED = true;

    protected static final String DATA = "data";
    protected static final String ERROR_CODE = "error";
    protected static final int ERROR_CODE_NO_ERRORS = 0x00000000;

    // Error code is returned from web server, 0 indices no errors.
    protected int mErrorCode = 0;
    // This flag indices the data should be cached into DB
    protected boolean mIsCached = false;
    protected ObjectMapper objectMapper;

    public JSONHandler(boolean isCached) {
        mIsCached = isCached;
        mErrorCode = ERROR_CODE_NO_ERRORS;
        objectMapper = new ObjectMapper();
    }

    public abstract T process(String jsonData) throws JacksonProcessingException, InternalServerErrorException;

    protected int getErrorCodeFromJsonNode(JsonNode rootNode) {
        mErrorCode = rootNode.path(ERROR_CODE).asInt(); // Unwrap error_code node and get it as integer
        return mErrorCode;
    }

    protected boolean hasInternalServerError(JsonNode rootNode) {
        mErrorCode = getErrorCodeFromJsonNode(rootNode);
        return mErrorCode != ERROR_CODE_NO_ERRORS;
    }
}
