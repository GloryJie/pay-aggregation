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
public class ExternalErrorException extends BaseException {

    public ExternalErrorException(BaseErrorEnum baseErrorEnum) {
        super(baseErrorEnum);
    }

    public ExternalErrorException(BaseErrorEnum baseErrorEnum, String detailMsg) {
        super(baseErrorEnum, detailMsg);
    }

    public static BusinessErrorException create(BaseErrorEnum errorEnum) {
        return new BusinessErrorException(errorEnum);
    }

    public static BusinessErrorException create(BaseErrorEnum errorEnum, String detailMsg) {
        return new BusinessErrorException(errorEnum, detailMsg);
    }

    public static BusinessErrorException create(BaseErrorEnum errorEnum, String detailMsg, Object... args) {
        String message = String.format(detailMsg, args);
        return new BusinessErrorException(errorEnum, message);
    }
}
