/*
 * @author Sparsh Mehta
 * @since 2021
 * @version 1.0
 */
package com.common.exception;

import org.springframework.http.HttpStatus;

import org.springframework.util.StringUtils;

/**
 * The Class BaseException.
 *
 * @author Ankush Goyal
 * @version v1.0
 * @see TutionCenter
 * @see com.tutioncenter.exception
 * @since 22 Jan, 2017
 */
public class BaseException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 7589898601904574752L;

  /** The Constant DEFAULT_HTTP_STATUS. */
  public static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

  /** The Constant DEFAULT_MESSAGE. */
  public static final String DEFAULT_MESSAGE = "Something Bad Happened !";

  public Object data = null;

  /** The http status. */
  private HttpStatus httpStatus;

  /** The message. */
  private String message;

  /**
   * Gets the http status.
   *
   * @return the http status
   */
  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  /**
   * Sets the http status.
   *
   * @param httpStatus the new http status
   */
  public void setHttpStatus(HttpStatus httpStatus) {
    this.httpStatus = httpStatus;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Throwable#getMessage()
   */
  public String getMessage() {
    return message;
  }

  /**
   * Sets the message.
   *
   * @param message the new message
   */
  public void setMessage(String message) {
    this.message = message;
  }

  public Object getData() {
    return data;
  }

  public void setData(Object data) {
    this.data = data;
  }

  /**
   * Instantiates a new car exception.
   *
   * @param httpStatus the http status
   * @param message the message
   */
  public BaseException(HttpStatus httpStatus, String message) {
    super();
    this.httpStatus = httpStatus == null ? DEFAULT_HTTP_STATUS : httpStatus;
    this.message = StringUtils.isEmpty(message) ? DEFAULT_MESSAGE : message;
  }

  public BaseException(HttpStatus httpStatus, String message, Object data) {
    super();
    this.httpStatus = httpStatus == null ? DEFAULT_HTTP_STATUS : httpStatus;
    this.message = StringUtils.isEmpty(message) ? DEFAULT_MESSAGE : message;
    this.data = data;
  }

  /**
   * Instantiates a new car exception.
   *
   * @param httpStatus the http status
   * @param defaultStatus the default status
   * @param message the message
   * @param defaultMessage the default message
   */
  public BaseException(HttpStatus httpStatus, HttpStatus defaultStatus, String message,
      String defaultMessage) {
    super();
    this.httpStatus = httpStatus == null ? defaultStatus : httpStatus;
    this.message = StringUtils.isEmpty(message) ? defaultMessage : message;
  }

  /**
   * Instantiates a new car exception.
   *
   * @param message the message
   */
  public BaseException(String message) {
    this(DEFAULT_HTTP_STATUS, message);
  }

  /**
   * Instantiates a new car exception.
   */
  public BaseException() {
    this(DEFAULT_HTTP_STATUS, DEFAULT_MESSAGE);
  }
}
