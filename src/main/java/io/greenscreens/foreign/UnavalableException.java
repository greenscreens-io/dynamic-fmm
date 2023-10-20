/*
 * Copyright (C) 2015, 2023 Green Screens Ltd.
 */
package io.greenscreens.foreign;

/**
 * Exception thrown when foreign method mapping contains unsupported Java types 
 */
public class UnavalableException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public UnavalableException() {
		super();
	}

	public UnavalableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UnavalableException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnavalableException(String message) {
		super(message);
	}

	public UnavalableException(Throwable cause) {
		super(cause);
	}

	public static UnavalableException create() {
		return new UnavalableException();
	}
}
