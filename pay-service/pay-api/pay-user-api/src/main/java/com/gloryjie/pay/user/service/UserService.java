package com.gloryjie.pay.user.service;

import com.gloryjie.pay.user.dto.UserInfoDto;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author jie
 * @since 2019/4/28
 */
public interface UserService extends UserDetailsService {

    /**
     * 根据用户号获取用户信息
     * @param userNo
     * @return
     */
    UserInfoDto getUserInfo(Long userNo);

    /**
     * 根据手机号获取用户信息
     * @param phone
     * @return
     */
    UserInfoDto getUserInfoByPhone(String phone);

}
