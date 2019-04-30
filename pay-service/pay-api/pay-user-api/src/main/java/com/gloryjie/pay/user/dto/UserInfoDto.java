package com.gloryjie.pay.user.dto;

import com.gloryjie.pay.user.enums.UserSex;
import com.gloryjie.pay.user.enums.UserStatus;
import com.gloryjie.pay.user.enums.UserType;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Collections;

/**
 * @author jie
 * @since 2019/4/28
 */
@Data
public class UserInfoDto implements UserDetails {
    /**
     * 全局唯一的用户号
     */
    private Long userNo;

    /**
     * 昵称
     */
    @NotBlank
    @Size(min = 2, max = 24)
    private String nickName;

    /**
     * 密码
     */
    @NotNull
    @Size(min = 6, max = 32)
    private String password;

    /**
     * 密码加盐
     */
    private String salt;

    /**
     * 手机号码,全局唯一
     */
    @NotBlank
    @Size(min = 11, max = 11)
    private String phone;

    /**
     * 邮箱,全局唯一
     */
    private String email;

    /**
     * 性别,女性,男性
     */
    @NotNull
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
    @NotNull
    private Integer originalAppId;

    /**
     * 创建当前用户的用户
     */
    private Long createdUserNo;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO: 2019/4/28 测试用,需要修改
        return Collections.singletonList(new SimpleGrantedAuthority("super_admin"));
    }

    @Override
    public String getUsername() {
        return getPhone();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserStatus.FREEZE != getStatus();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
