/* ------------------------------------------------------------------
 *   Product:      music-dj
 *   Module Name:  music-common
 *   Package Name: com.gloryjie.common.constant
 *   Date Created: 2018-09-10
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018-09-10      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.base.constant;

/**
 * @author Jie
 * @since 0.1
 */
public class DefaultConstant {

  /**
   * 字符集
   */
  public static final String CHARSET = "UTF-8";

  /**
   * 时间格式
   */
  public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

  /**
   * 秒
   */
  public static final int SECOND = 60;

  public static final int MILLISECOND = 1000;

  /**
   * 时区
   */
  public static final String ZONE_OFFSET = "+8";


  /**
   * 起始页码
   */
  public static final int PAGE_NO = 1;

  /**
   * 每页大小
   */
  public static final int PAGE_SIZE = 10;

  public static final String NULL_STRING = "null";

  public static final String CURRENCY = "cny";

  /**
   * IPV4正则表达式
   */
  public static final String IPV4_PATTERM = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
      + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
      + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
      + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";

  public static final String NOTIFY_SUCCESS_RESPONSE = "success";

  public interface REQUEST_METHOD{
    String GET = "GET";

    String POST = "POST";

    String PUT = "PUT";

    String DELETE = "DELETE";
  }

}
