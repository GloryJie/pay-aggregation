package com.gloryjie.pay.user.dao;


import com.gloryjie.pay.user.model.User;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface UserDao {

    int delete(Long userNo);

    int insert(User record);

    int update(User record);

    User getByUserNo(Long userNo);

    User getByPhone(String phone);




}