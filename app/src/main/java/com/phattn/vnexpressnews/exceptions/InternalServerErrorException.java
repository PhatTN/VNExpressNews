package com.phattn.vnexpressnews.exceptions;

/**
 * This exception indicates that something was wrong on Server side.
 */
public class InternalServerErrorException extends Exception {

    private static final long serialVersionUID = -8544071183617538012L;

    protected int mErrorCode = 0; // 0 indicates no errors.

    /**
     * Constructs a new {@code InternalServerErrorException} with its stack trace filled in.
     */
    protected InternalServerErrorException() {}


    /**
     * Constructs a new {@code InternalServerErrorException} with its detail message and
     * server error code filled in.
     * @param message The detailed message of this exception
     * @param errorCode The server error code which is returned from server.
     */
    public InternalServerErrorException(String message, int errorCode) {
        this(message, errorCode, null);
    }

    /**
     * Constructs a new {@code InternalServerErrorException} with its detail message,
     * server error code and cause filled in.
     * @param message The detail error message.
     * @param errorCode The server error code which is returned from server.
     * @param cause The cause of this exception.
     */
    public InternalServerErrorException(String message, int errorCode, Throwable cause) {
        super(message);
        if (cause != null) {
            initCause(cause);
        }

        mErrorCode = errorCode;
    }

    /**
     * Gets the error code.
     * @return The error code is returned from server.
     */
    public int getErrorCode() {
        return mErrorCode;
    }

    /**
     *  Method that allows accessing the original "message" variable without
     *  addition server error code.
     */
    public String getOriginalMessage() {
        return super.getMessage();
    }

    /**
     * Default method overridden so that we can add error code.
     */
    @Override
    public String getMessage() {
        String msg = super.getMessage();
        if (msg == null) {
            msg = "N/A";
        }
        int errorCode = getErrorCode();
        StringBuilder sb = new StringBuilder(100);
        sb.append("The server error code: ").append(errorCode);
        sb.append('\n');
        sb.append(msg);

        return sb.toString();
    }
}
