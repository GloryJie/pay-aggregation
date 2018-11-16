/* ------------------------------------------------------------------
 *   Product:      music-dj
 *   Module Name:  com.gloryjie.common.exception.error
 *   Package Name: com.gloryjie.common.exception.error
 *   Date Created: 2018-09-10
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018-09-10      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.base.exception.error;


import com.gloryjie.pay.base.enums.base.BaseErrorEnum;
import com.gloryjie.pay.base.exception.BaseException;

/**
 * 系统异常
 *
 * @author Jie
 * @since 0.1
 */
public class SystemErrorException extends BaseException {


    public SystemErrorException(BaseErrorEnum baseErrorEnum) {
        super(baseErrorEnum);
    }

    public SystemErrorException(BaseErrorEnum baseErrorEnum, String detailMsg) {
        super(baseErrorEnum, detailMsg);
    }

    public SystemErrorException(BaseErrorEnum baseErrorEnum, String detailMsg, Throwable e) {
        super(baseErrorEnum, detailMsg, e);
    }

    public SystemErrorException(BaseErrorEnum baseErrorEnum, Throwable e) {
        super(baseErrorEnum, e);
    }

    public static SystemErrorException create(BaseErrorEnum errorEnum) {
        return new SystemErrorException(errorEnum);
    }

    public static SystemErrorException create(BaseErrorEnum errorEnum, Throwable t) {
        return new SystemErrorException(errorEnum, t);
    }

    public static SystemErrorException create(BaseErrorEnum errorEnum, String detailMsg, Object... args) {
        String message = errorEnum.getMessage() + ": " + String.format(detailMsg, args);
        return new SystemErrorException(errorEnum, message);
    }
}
