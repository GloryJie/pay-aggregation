/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.channel.error
 *   Date Created: 2018/12/21
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/12/21      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.channel.error;

import com.gloryjie.pay.base.constant.HttpStatus;
import com.gloryjie.pay.base.enums.base.BaseErrorEnum;
import lombok.Getter;

/**
 * @author Jie
 * @since
 */
@Getter
public enum ChannelError implements BaseErrorEnum {

    /**
     * 系统异常 5xx
     */
    EXTRA_NOT_CORRECT(HttpStatus.BAD_REQUEST, "101", "渠道额外参数不正确"),
    PAY_PLATFORM_ERROR(HttpStatus.BAD_GATEWAY, "102", "渠道异常");

    private int code;

    private String status;

    private String message;

    ChannelError(int code, String status, String message) {
        this.code = code;
        this.status = code + status;
        this.message = message;
    }
}
