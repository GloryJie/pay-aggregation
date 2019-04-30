package com.gloryjie.pay.user.service;

import com.github.pagehelper.PageInfo;
import com.gloryjie.pay.user.dto.UserInfoDto;
import com.gloryjie.pay.user.enums.UserType;
import com.sun.istack.Nullable;
import org.springframework.security.core.userdetails.UserDetailsService;


/**
 * @author jie
 * @since 2019/4/28
 */
public interface UserService extends UserDetailsService {

    /**
     * 根据用户号获取用户信息
     *
     * @param userNo
     * @return
     */
    UserInfoDto getUserInfo(Long userNo);

    /**
     * 根据手机号获取用户信息
     *
     * @param phone
     * @return
     */
    UserInfoDto getUserInfoByPhone(String phone);


    /**
     * 列出所在应用的用户
     *
     * @param type  用户类型
     * @param appId 应用, 类型为平台,则此appId为空
     * @return
     */
    PageInfo<UserInfoDto> listAllUserByType(UserType type, @Nullable Integer appId, int startPage, int pageSize);

    /**
     * 添加用户
     * @param userInfoDto
     * @return
     */
    UserInfoDto addUser(UserInfoDto userInfoDto);


}
