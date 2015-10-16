package com.example.phat.vnexpressnews.io;

import com.example.phat.vnexpressnews.exceptions.InternalServerErrorException;
import com.example.phat.vnexpressnews.exceptions.JacksonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class JSONHandler<T> {

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
        objectMapper = new ObjectMapper();
    }

    public abstract T process(String jsonData) throws JacksonProcessingException, InternalServerErrorException;


}
