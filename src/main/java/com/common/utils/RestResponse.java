/*
 * @author Sparsh Mehta
 * @since 2021
 * @version 1.0
 */
package com.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The Class RestResponse.
 *
 * @param <T> the generic type
 */
@JsonInclude(Include.NON_NULL)
public class RestResponse<T> {

  /** The response message. */
  private String responseMessage;

  /** The response status. */
  private Boolean responseStatus = true;
  
  /** The response status code. */
  private Integer responseStatusCode = 200;

  /** The response object. */
  private T responseObject;

  public String getResponseMessage() {
    return responseMessage;
  }

  public void setResponseMessage(String responseMessage) {
    this.responseMessage = responseMessage;
  }

  public Boolean getResponseStatus() {
    return responseStatus;
  }

  public void setResponseStatus(Boolean responseStatus) {
    this.responseStatus = responseStatus;
  }

  public T getResponseObject() {
    return responseObject;
  }

  public void setResponseObject(T responseObject) {
    this.responseObject = responseObject;
  }

  public Integer getResponseStatusCode() {
    return responseStatusCode;
  }

  public void setResponseStatusCode(Integer responseStatusCode) {
    this.responseStatusCode = responseStatusCode;
  }

  /**
   * Instantiates a new rest response.
   *
   * @param responseMessage the response message
   * @param responseObject the response object
   * @param responseStatus the response status
   * @param responseStatusCode the response status code
   */
  public RestResponse(String responseMessage, T responseObject, Boolean responseStatus, Integer responseStatusCode) {
    this.responseMessage = responseMessage;
    this.responseObject = responseObject;
    this.responseStatus = responseStatus;
    this.responseStatusCode = responseStatusCode;
  }

  /**
   * Instantiates a new rest response.
   *
   * @param data the data
   */
  public RestResponse(T data) {
    this.responseObject = data;
  }
  
  public RestResponse(T data, String responseMessage) {
    this.responseObject = data;
    this.responseMessage = responseMessage;
  }

  /**
   * Instantiates a new rest response.
   */
  public RestResponse() {}

}
