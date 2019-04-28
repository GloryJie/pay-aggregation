package com.gloryjie.pay.user.service;

import com.gloryjie.pay.base.util.BeanConverter;
import com.gloryjie.pay.user.dao.UserDao;
import com.gloryjie.pay.user.dto.UserInfoDto;
import com.gloryjie.pay.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author jie
 * @since 2019/4/28
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public UserInfoDto getUserInfo(Long userNo) {
        User user = userDao.getByUserNo(userNo);
        return user == null? null: BeanConverter.covert(user, UserInfoDto.class);
    }

    @Override
    public UserInfoDto getUserInfoByPhone(String phone) {
        User user = userDao.getByPhone(phone);
        return user == null? null: BeanConverter.covert(user, UserInfoDto.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getUserInfoByPhone(username);
    }
}
