package com.gloryjie.pay.user.model;

import com.gloryjie.pay.user.enums.UserSex;
import com.gloryjie.pay.user.enums.UserStatus;
import com.gloryjie.pay.user.enums.UserType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author jie
 * @since 2019/4/26
 */
@Data
public class User {
    /**
     * 全局唯一的用户号
     */
    private Long userNo;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 密码
     */
    private String password;

    /**
     * 密码加盐
     */
    private String salt;

    /**
     * 手机号码,全局唯一
     */
    private String phone;

    /**
     * 邮箱,全局唯一
     */
    private String email;

    /**
     * 性别,0女性,1男性
     */
    private UserSex sex;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 用户类型
     */
    private UserType type;

    /**
     * 账号状态,1正常,2锁定
     */
    private UserStatus status;

    /**
     * 最初创建该用户的应用
     */
    private Integer originalAppId;

    /**
     * 创建当前用户的用户
     */
    private Long createdUserNo;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
