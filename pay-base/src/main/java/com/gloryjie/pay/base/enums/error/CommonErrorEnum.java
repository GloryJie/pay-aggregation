/* ------------------------------------------------------------------
 *   Product:      music-dj
 *   Module Name:  com.gloryjie.common.enums.error
 *   Package Name: com.gloryjie.common.enums.error
 *   Date Created: 2018-09-11
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018-09-11      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.base.enums.error;

import com.gloryjie.pay.base.constant.HttpStatus;
import com.gloryjie.pay.base.enums.base.BaseErrorEnum;
import lombok.Getter;

/**
 * @author Jie
 * @since 0.1
 */
@Getter
public enum CommonErrorEnum implements BaseErrorEnum {

    /**
     * 系统异常 5xx
     */
    INTERNAL_SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500", "系统内部异常"),
    SYSTEM_BUSY_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "501", "系统繁忙"),
    ILLEGAL_ARGUMENT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"502","参数异常"),
    SIGNATURE_NOT_PASS_ERROR(HttpStatus.BAD_REQUEST,"503","签名未通过")
    ;

    private int code;

    private String status;

    private String message;

    CommonErrorEnum(int code, String status, String message){
        this.code = code;
        this.status = code + status;
        this.message = message;
    }

}
