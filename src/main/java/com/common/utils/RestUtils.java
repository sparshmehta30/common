/*
 * @author Sparsh Mehta
 * @since 2021
 * @version 1.0
 */
package com.common.utils;

import java.util.Objects;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


/**
 * The Class RestUtils.
 */
public class RestUtils {

  /**
   * Success response.
   *
   * @param <T> the generic type
   * @param data the data
   * @param statusCode the status code
   * @return the response entity
   */
  public static <T> ResponseEntity<RestResponse<T>> successResponse(T data, String message,
      HttpStatus statusCode) {
    LogManager.info(RestUtils.class, statusCode.name() + " - " + statusCode.value(),
        MessageType.ApiRequestResponse);
    Logger.getLogger(RestUtils.class).setValue(
        Logger.getLogger(RestUtils.class).getValue(Constant.REQUEST_ID),
        String.valueOf(statusCode.value()));
    return new ResponseEntity<>(new RestResponse<T>(data, message), statusCode);
  }

  /**
   * Success response.
   *
   * @param <T> the generic type
   * @param data the data
   * @return the response entity
   */
  public static <T> ResponseEntity<RestResponse<T>> successResponse(T data) {
    return successResponse(data, null, HttpStatus.OK);
  }

  public static <T> ResponseEntity<RestResponse<T>> successResponse(T data, String message) {
    return successResponse(data, message, HttpStatus.OK);
  }

  public static <T> ResponseEntity<T> successResponseNew(T data) {
    LogManager.info(RestUtils.class, HttpStatus.OK.name() + " - " + HttpStatus.OK.value(),
        MessageType.ApiRequestResponse);
    Logger.getLogger(RestUtils.class).setValue(
        Logger.getLogger(RestUtils.class).getValue(Constant.REQUEST_ID),
        String.valueOf(HttpStatus.OK.value()));
    return new ResponseEntity<>(data, HttpStatus.OK);
  }

  /**
   * Error response.
   *
   * @param <T> the generic type
   * @param errorMessage the error message
   * @param statusCode the status code
   * @return the response entity
   */
  public static <T> ResponseEntity<RestResponse<?>> errorResponse(String errorMessage,
      HttpStatus statusCode) {
    LogManager.info(RestUtils.class, statusCode.name() + " - " + statusCode.value(),
        MessageType.ApiRequestResponse);
    LogManager.error(RestUtils.class, errorMessage, MessageType.Error);
    Logger.getLogger(RestUtils.class).setValue(
        Logger.getLogger(RestUtils.class).getValue(Constant.REQUEST_ID),
        String.valueOf(statusCode.value()));
    return new ResponseEntity<>(
        new RestResponse<T>(errorMessage, null, Boolean.FALSE, statusCode.value()), statusCode);
  }

  /**
   * Error response.
   *
   * @param <T> the generic type
   * @param errorMessage the error message
   * @param errorDescription the error description
   * @param statusCode the status code
   * @return the response entity
   */
  public static <T> ResponseEntity<RestResponse<?>> errorResponse(String errorMessage,
      T errorDescription, HttpStatus statusCode) {
    LogManager.info(RestUtils.class, statusCode.name() + " - " + statusCode.value(),
        MessageType.ApiRequestResponse);
    LogManager.error(RestUtils.class, errorMessage, MessageType.Error);
    if (Objects.nonNull(errorDescription))
      LogManager.error(RestUtils.class, errorDescription.toString(), MessageType.Error);
    Logger.getLogger(RestUtils.class).setValue(
        Logger.getLogger(RestUtils.class).getValue(Constant.REQUEST_ID),
        String.valueOf(statusCode.value()));
    return new ResponseEntity<>(
        new RestResponse<T>(errorMessage, errorDescription, Boolean.FALSE, statusCode.value()),
        statusCode);
  }
}
