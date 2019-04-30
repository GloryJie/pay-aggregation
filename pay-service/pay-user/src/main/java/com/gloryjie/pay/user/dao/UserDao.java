package com.gloryjie.pay.user.dao;


import com.gloryjie.pay.user.enums.UserType;
import com.gloryjie.pay.user.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface UserDao {

    int delete(Long userNo);

    int insert(User record);

    int update(User record);

    User getByUserNo(Long userNo);

    User getByPhone(String phone);

    List<User> listByType(@Param("type")UserType type, @Param("appId")Integer appId);

}