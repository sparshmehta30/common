/*
 * @author Sparsh Mehta
 * @since 2021
 * @version 1.0
 */
package com.common.exception;

import org.springframework.http.HttpStatus;

import com.common.dto.ResponseDto;


/**
 * The Class FileUploadValidateException.
 */
public class FileUploadValidateException extends FileUploadException {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 8259031362877695438L;

  /** The Constant DEFAULT_MESSAGE. */
  private static final String DEFAULT_MESSAGE = "File could not be uploaded due to invalid fields";

  /** The Constant DEFAULT_HTTP_STATUS. */
  private static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.BAD_REQUEST;

  /** The response dto. */
  private static ResponseDto responseDto;

  /**
   * Instantiates a new file upload validate exception.
   */
  public FileUploadValidateException() {
    this(DEFAULT_HTTP_STATUS, DEFAULT_MESSAGE, responseDto);

  }

  /**
   * Instantiates a new file upload validate exception.
   *
   * @param httpStatus the http status
   * @param message the message
   * @param responseDto the response dto
   */
  public FileUploadValidateException(HttpStatus httpStatus, String message,
      ResponseDto responseDto) {
    super();
    FileUploadValidateException.responseDto = responseDto;

  }

  /**
   * Instantiates a new file upload validate exception.
   *
   * @param message the message
   */
  public FileUploadValidateException(String message) {
    this(DEFAULT_HTTP_STATUS, message, responseDto);

  }

  public ResponseDto getResponseDto() {

    return responseDto;
  }

}
