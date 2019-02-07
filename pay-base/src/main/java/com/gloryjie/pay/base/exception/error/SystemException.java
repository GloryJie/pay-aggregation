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
public class SystemException extends BaseException {


    public SystemException(BaseErrorEnum baseErrorEnum) {
        super(baseErrorEnum);
    }

    public SystemException(BaseErrorEnum baseErrorEnum, String detailMsg) {
        super(baseErrorEnum, detailMsg);
    }

    public SystemException(BaseErrorEnum baseErrorEnum, String detailMsg, Throwable e) {
        super(baseErrorEnum, detailMsg, e);
    }

    public SystemException(BaseErrorEnum baseErrorEnum, Throwable e) {
        super(baseErrorEnum, e);
    }

    public static SystemException create(BaseErrorEnum errorEnum) {
        return new SystemException(errorEnum);
    }

    public static SystemException create(BaseErrorEnum errorEnum, Throwable t) {
        return new SystemException(errorEnum, t);
    }

    public static SystemException create(BaseErrorEnum errorEnum, String detailMsg, Object... args) {
        String message = String.format(detailMsg, args);
        return new SystemException(errorEnum, message);
    }
}
