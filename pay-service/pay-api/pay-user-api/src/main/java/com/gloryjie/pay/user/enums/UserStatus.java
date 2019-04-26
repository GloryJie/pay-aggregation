package com.gloryjie.pay.user.enums;

import lombok.Getter;

/**
 * @author jie
 * @since 2019/4/26
 */
@Getter
public enum  UserStatus {

    /**
     * 用户状态
     */
    NORMALITY(0,"正常"),
    FREEZE(1,"冻结");

    private int code;

    private String desc;

    UserStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
