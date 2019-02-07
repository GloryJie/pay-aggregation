/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.app.error
 *   Date Created: 2019/2/7
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/2/7      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.app.error;

import com.gloryjie.pay.base.constant.HttpStatus;
import com.gloryjie.pay.base.enums.base.BaseErrorEnum;
import lombok.Getter;

/**
 * @author Jie
 * @since
 */
@Getter
public enum  AppError implements BaseErrorEnum {

    /**
     * 应用异常1xx
     */
    SAME_APP_NAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST,"101","同名应用已存在")
    ;

    private int code;

    private String status;

    private String message;

    AppError(int code, String status, String message) {
        this.code = code;
        this.status = code + status;
        this.message = message;
    }
}
