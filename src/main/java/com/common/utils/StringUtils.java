
package com.common.utils;


public class StringUtils {

  /**
   * <p>
   * Checks if a String is not empty (""), not null and not whitespace only.
   * </p>
   *
   * <pre>
   * StringUtils.isNotBlank(null)      = false
   * StringUtils.isNotBlank("")        = false
   * StringUtils.isNotBlank(" ")       = false
   * StringUtils.isNotBlank("bob")     = true
   * StringUtils.isNotBlank("  bob  ") = true
   * </pre>
   *
   * @param str the String to check, may be null
   * @return <code>true</code> if the String is not empty and not null and not whitespace
   * @since 2.0
   */
  public static boolean isNotBlank(String str) {
    return org.apache.commons.lang3.StringUtils.isNotBlank(str);
  }

  /**
   * <p>
   * Checks if a String is whitespace, empty ("") or null.
   * </p>
   *
   * <pre>
   * StringUtils.isBlank(null)      = true
   * StringUtils.isBlank("")        = true
   * StringUtils.isBlank(" ")       = true
   * StringUtils.isBlank("bob")     = false
   * StringUtils.isBlank("  bob  ") = false
   * </pre>
   *
   * @param str the String to check, may be null
   * @return <code>true</code> if the String is null, empty or whitespace
   * @since 2.0
   */
  public static boolean isBlank(String str) {
    return org.apache.commons.lang3.StringUtils.isBlank(str);
  }

}
