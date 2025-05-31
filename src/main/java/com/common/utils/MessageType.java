/*
 * @author Sparsh Mehta
 * @since 2021
 * @version 1.0
 */

package com.common.utils;


public enum MessageType {

  /** The Api request. */
  ApiRequest,
  /** The Api request response. */
  ApiRequestResponse,
  /** The Info. */
  Info,
  /** The Debug. */
  Debug,
  /** The Error. */
  Error;

  public String getLogName(Logger logger) {
    return String.format("%s - %s - %s", logger.getValue(Constant.REQUEST_KEY), this.name(),
        logger.getValue(Constant.REQUEST_ID));
  }

}
