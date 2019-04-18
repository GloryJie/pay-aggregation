/* ------------------------------------------------------------------
 *   Product:      music-dj
 *   Module Name:  music-common
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
 * 外部依赖异常
 * @author Jie
 * @since 0.1
 */
public class ExternalException extends BaseException {

    public ExternalException(BaseErrorEnum baseErrorEnum) {
        super(baseErrorEnum);
    }

    public ExternalException(BaseErrorEnum baseErrorEnum, String detailMsg) {
        super(baseErrorEnum, detailMsg);
    }

    public static ExternalException create(BaseErrorEnum errorEnum) {
        return new ExternalException(errorEnum);
    }

    public static ExternalException create(BaseErrorEnum errorEnum, String detailMsg) {
        return new ExternalException(errorEnum, detailMsg);
    }

    public static ExternalException create(BaseErrorEnum errorEnum, String detailMsg, Object... args) {
        String message = String.format(detailMsg, args);
        return new ExternalException(errorEnum, message);
    }
}
