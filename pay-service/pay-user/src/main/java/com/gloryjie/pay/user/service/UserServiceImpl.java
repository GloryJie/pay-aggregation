package com.gloryjie.pay.user.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gloryjie.pay.base.exception.error.BusinessException;
import com.gloryjie.pay.base.util.BeanConverter;
import com.gloryjie.pay.user.dao.UserDao;
import com.gloryjie.pay.user.dto.UserInfoDto;
import com.gloryjie.pay.user.enums.UserStatus;
import com.gloryjie.pay.user.enums.UserType;
import com.gloryjie.pay.user.error.UserError;
import com.gloryjie.pay.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


/**
 * @author jie
 * @since 2019/4/28
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserInfoDto getUserInfo(Long userNo) {
        User user = userDao.getByUserNo(userNo);
        return user == null ? null : BeanConverter.covert(user, UserInfoDto.class);
    }

    @Override
    public UserInfoDto getUserInfoByPhone(String phone) {
        User user = userDao.getByPhone(phone);
        return user == null ? null : BeanConverter.covert(user, UserInfoDto.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getUserInfoByPhone(username);
    }

    @Override
    public PageInfo<UserInfoDto> listAllUserByType(UserType type, Integer appId, int startPage, int pageSize) {
        PageHelper.startPage(startPage, pageSize);
        PageInfo pageInfo = PageInfo.of(userDao.listByType(type, appId));
        pageInfo.setList(BeanConverter.batchCovert(pageInfo.getList(), UserInfoDto.class));
        return pageInfo;
    }

    @Override
    public UserInfoDto addUser(UserInfoDto userInfoDto) {
        User user = userDao.getByPhone(userInfoDto.getPhone());
        if (user != null){
            throw BusinessException.create(UserError.PHONE_ALREADY_EXISTS_ERROR);
        }
        userInfoDto.setUserNo(System.currentTimeMillis());
        userInfoDto.setStatus(UserStatus.NORMALITY);
        userInfoDto.setPassword(passwordEncoder.encode(userInfoDto.getPassword()));

        userDao.insert(BeanConverter.covert(userInfoDto, User.class));
        return userInfoDto;
    }
}
