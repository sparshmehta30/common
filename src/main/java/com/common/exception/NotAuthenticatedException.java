/*
 * @author Sparsh Mehta
 * @since 2021
 * @version 1.0
 */
package com.common.exception;

import org.springframework.http.HttpStatus;

/**
 * The Class NotAuthenticatedException.
 */
public class NotAuthenticatedException extends BaseException{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 758250789024800095L;

	/** The Constant DEFAULT_MESSAGE. */
	private static final String DEFAULT_MESSAGE = "Not authenticated !";

	/** The Constant DEFAULT_HTTP_STATUS. */
	private static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.FORBIDDEN;

	/**
	 * Instantiates a new not authenticated exception.
	 */
	public NotAuthenticatedException() {
		this(DEFAULT_HTTP_STATUS, DEFAULT_MESSAGE);
	}

	/**
	 * Instantiates a new not authenticated exception.
	 *
	 * @param httpStatus the http status
	 * @param message the message
	 */
	public NotAuthenticatedException(HttpStatus httpStatus, String message) {
		super(httpStatus, DEFAULT_HTTP_STATUS, message, DEFAULT_MESSAGE);

	}

	/**
	 * Instantiates a new not authenticated exception.
	 *
	 * @param message the message
	 */
	public NotAuthenticatedException(String message) {
		this(DEFAULT_HTTP_STATUS, message);
	}

}
