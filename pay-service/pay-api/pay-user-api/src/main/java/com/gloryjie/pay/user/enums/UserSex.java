package com.gloryjie.pay.user.enums;

import lombok.Getter;

/**
 * @author jie
 * @since 2019/4/26
 */
@Getter
public enum  UserSex {


    /**
     * 用户状态
     */
    FEMALE(0,"女性"),
    MALE(1,"男性");

    private int code;

    private String desc;

    UserSex(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
