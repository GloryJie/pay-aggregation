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
     * 渠道异常1xx
     */
    EXTRA_NOT_CORRECT(HttpStatus.BAD_REQUEST, "101", "渠道额外参数不正确"),
    PAY_PLATFORM_ERROR(HttpStatus.BAD_GATEWAY, "102", "渠道异常"),

    /**
     * 渠道配置异常2xx
     */
    CHANNEL_CONFIG_EXISTS(HttpStatus.BAD_REQUEST,"201","渠道配置已存在"),
    CHANNEL_CONFIG_NOT_EXISTS(HttpStatus.BAD_REQUEST,"202","渠道未配置"),
    CHANNEL_CONFIG_NOT_USING(HttpStatus.BAD_REQUEST,"203","渠道配置未启用"),
    PLATFORM_NOTIFY_SIGN_NOT_THROUGH(HttpStatus.BAD_REQUEST,"204","渠道异步通知签名未通过"),
    PLATFORM_NOTIFY_PARAM_ILLEGAL(HttpStatus.BAD_REQUEST,"205","渠道异步通知参数异常"),
    CERT_FILE_TYPE_NOT_CORRECT(HttpStatus.BAD_REQUEST,"206","证书文件格式不正确"),
    CERT_FILE_DATA_EMPTY(HttpStatus.BAD_REQUEST,"207","证书文件数据为空"),
    READ_CERT_FILE_DATA_FAIL(HttpStatus.INTERNAL_SERVER_ERROR,"208","读取证书内容错误"),
    CERT_NOT_ALL_READY(HttpStatus.BAD_REQUEST, "209", "证书配置不齐全")
    ;

    private int code;

    private String status;

    private String message;

    ChannelError(int code, String status, String message) {
        this.code = code;
        this.status = code + status;
        this.message = message;
    }
}
