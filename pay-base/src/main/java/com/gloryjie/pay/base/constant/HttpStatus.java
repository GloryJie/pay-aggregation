/* ------------------------------------------------------------------
 *   Product:      music
 *   Module Name:  COMMON
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
public class HttpStatus {

    /** 2xx Success*/
    public static final int SUCCESS = 200;
    public static final int CREATED = 201;
    public static final int ACCEPTED = 202;

    /** 3xx Redirection*/
    public static final int MULTIPLE_CHOICES = 300;

    /** 4xx Client errors*/
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int NOT_FOUND = 404;

    /** 5xx Server errors*/
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int NOT_IMPLEMENTED = 501;
    public static final int BAD_GATEWAY = 502;


}
