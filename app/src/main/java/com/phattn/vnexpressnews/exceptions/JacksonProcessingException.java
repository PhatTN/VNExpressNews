package com.phattn.vnexpressnews.exceptions;

import com.fasterxml.jackson.core.JsonLocation;

/**
 * Intermediate class for all problems encountered when
 * using Jackson Lib to process (parsing, generating) JSON content.
 */
public class JacksonProcessingException extends Exception {

    private static final long serialVersionUID = 3499617967135313424L;

    protected JsonLocation mLocation;

    /**
     * Constructs a new {@code JacksonProcessingException} with its stack trace filled in.
     */
    protected JacksonProcessingException(){
    }

    /**
     * Constructs a new {@code JacksonProcessingException} with its stack trace and detail
     * message filled in.
     * @param detailMessage the detail message of this exception
     */
    public JacksonProcessingException(String detailMessage) {
        this(detailMessage, null, null);
    }

    /**
     * Constructs a new {@code JacksonProcessingException} with detail message and cause
     * filled in.
     * @param message the detail message for the exception
     * @param cause the detail cause for the exception
     */
    public JacksonProcessingException(String message, Throwable cause) {
        this(message, null, cause);
    }

    /**
     * Constructs a new {@code JacksonProcessingException} with its cause filled in.
     * @param cause the detail cause for the exception
     */
    public JacksonProcessingException(Throwable cause) {
        this(null, null, cause);
    }

    /**
     * Constructs a new {@code JacksonProcessingException} with it's detail message,
     * cause and json location filled in.
     */
    public JacksonProcessingException(String message, JsonLocation loc, Throwable cause) {
        super(message);
        if (cause != null) {
            initCause(cause);
        }

        mLocation = loc;
    }

    /**
     * Constructs a new {@code JacksonProcessingException} with it's detail message and
     * json location filled in.
     */
    public JacksonProcessingException(String message, JsonLocation loc) {
        this(message, loc, null);
    }

    /**
     * Get json location.
     */
    public JsonLocation getLocation() {
        return mLocation;
    }

    /**
     * Method that allows accessing the original "message" variable without
     * addition json location information
     */
    public String getOriginalMessage() {
        return super.getMessage();
    }

    /**
     * Default method overridden so that we can add location information
     */
    @Override
    public String getMessage() {
        String msg = super.getMessage();
        if (msg == null) {
            msg = "N/A";
        }
        JsonLocation loc = getLocation();
        if (loc != null) {
            StringBuilder sb = new StringBuilder(100);
            sb.append(msg);
            sb.append('\n');
            sb.append(" at ");
            sb.append(loc.toString());

            msg = sb.toString();
        }

        return msg;
    }

    @Override
    public String toString() {
        return getClass().getName() + ": " + getMessage();
    }
}
