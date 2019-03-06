/* ------------------------------------------------------------------
 *   Product:      music-dj
 *   Module Name:  com.gloryjie.common.exception
 *   Package Name: com.gloryjie.common.exception
 *   Date Created: 2018-09-10
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018-09-10      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.base.exception;

import com.gloryjie.pay.base.enums.base.BaseErrorEnum;
import lombok.Getter;

/**
 * @author Jie
 * @since 0.1
 */
@Getter
public class BaseException extends RuntimeException implements ErrorInterface {

  private BaseErrorEnum errorEnum;

  private String status;

  private String message;

  public BaseException() {
    super();
  }

  public BaseException(String status, String message) {
    this.status = status;
    this.message = message;
  }

  public BaseException(String status, String message, Throwable t) {
    super(t);
    this.status = status;
    this.message = message;
  }

  public BaseException(BaseErrorEnum error) {
    this(error.getStatus(), error.getMessage());
    this.errorEnum = error;
  }

  public BaseException(BaseErrorEnum error, Throwable t) {
    this(error.getStatus(),error.getMessage(), t);
    this.errorEnum = error;
  }

  public BaseException(BaseErrorEnum error, String detailMsg) {
    this(error.getStatus(), error.getMessage() + ":" + detailMsg);
    this.errorEnum = error;
  }

  public BaseException(BaseErrorEnum error, String detailMsg, Throwable t) {
    this(error.getStatus(), detailMsg, t);
    this.errorEnum = error;
  }
}
