package com.glory.pay.channel.dao;

import com.gloryjie.pay.PayAllApplication;
import com.gloryjie.pay.user.dao.UserDao;
import com.gloryjie.pay.user.enums.UserStatus;
import com.gloryjie.pay.user.enums.UserType;
import com.gloryjie.pay.user.model.User;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * @author jie
 * @since 2019/4/26
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PayAllApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserDaoTest {

    @Autowired
    UserDao userDao;

    @Autowired
    PasswordEncoder passwordEncoder;


    static long userNo;

    @BeforeClass
    public static void init(){
        userNo = System.currentTimeMillis();
    }


    @Test
    public void aInsertTest() {
        User user = new User();
        user.setUserNo(userNo);
        user.setNickName("super_root");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setPhone("123456");
        user.setEmail("123@123.com");
        user.setStatus(UserStatus.NORMALITY);
        user.setType(UserType.PLATFORM_INNER_USER);

        Assert.assertEquals(1, userDao.insert(user));
    }

    @Test
    public void bSelectTest() {
        User user = userDao.getByUserNo(userNo);
        Assert.assertNotNull(user);
        Assert.assertTrue(passwordEncoder.matches("123456", user.getPassword()));
    }

    @Test
    public void cUpdateTest(){
        User user = new User();
        user.setUserNo(userNo);
        user.setNickName("admin");

        Assert.assertEquals(1, userDao.update(user));

    }

    @Test
    public void dDelete(){
        Assert.assertEquals(1, userDao.delete(userNo));
    }

}
