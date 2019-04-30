package com.gloryjie.pay.user.error;

import com.gloryjie.pay.base.constant.HttpStatus;
import com.gloryjie.pay.base.enums.base.BaseErrorEnum;
import lombok.Getter;

/**
 * @author jie
 * @since 2019/4/29
 */
@Getter
public enum  UserError implements BaseErrorEnum {

    /**
     * 用户常见错误
     */
    PHONE_ALREADY_EXISTS_ERROR(HttpStatus.BAD_REQUEST,"501","手机号已经存在");

    private int code;

    private String status;

    private String message;

    UserError(int code, String status, String message) {
        this.code = code;
        this.status = code + status;
        this.message = message;
    }

}
