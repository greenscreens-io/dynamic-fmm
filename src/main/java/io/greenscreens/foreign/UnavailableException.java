/*
* Copyright (C) 2015, 2023 Green Screens Ltd.
 */
package io.greenscreens.foreign;

/**
 * Exception thrown when foreign method mapping contains unsupported Java types
 */
public class UnavailableException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnavailableException() {
        super();
    }

    public UnavailableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public UnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnavailableException(String message) {
        super(message);
    }

    public UnavailableException(Throwable cause) {
        super(cause);
    }

    public static UnavailableException create() {
        return new UnavailableException();
    }
}
