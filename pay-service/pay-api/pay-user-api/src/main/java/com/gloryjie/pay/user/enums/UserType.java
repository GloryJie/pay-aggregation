package com.gloryjie.pay.user.enums;

import lombok.Getter;

/**
 * @author jie
 * @since 2019/4/26
 */
@Getter
public enum  UserType {

    /**
     * 用户类型
     */
    PLATFORM_USER(0,"平台用户"),
    SUB_MERCHANT_USER(1,"子商户负责人"),
    PLATFORM_INNER_USER(2,"平台内用户"),
    SUPER_ADMIN(3,"超级管理员");

    private int code;

    private String desc;

    UserType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
