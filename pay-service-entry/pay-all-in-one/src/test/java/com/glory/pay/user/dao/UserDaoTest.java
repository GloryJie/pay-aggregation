package com.glory.pay.user.dao;

import com.gloryjie.pay.PayAllApplication;
import com.gloryjie.pay.user.dao.UserDao;
import com.gloryjie.pay.user.enums.UserSex;
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

import java.util.List;


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
    public static void init() {
        userNo = System.currentTimeMillis();
    }


    @Test
    public void aInsertTest() {
        User user = new User();
        user.setUserNo(1000000L);
        user.setNickName("super_admin");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setPhone("15811112222");
        user.setEmail("123@123.com");
        user.setStatus(UserStatus.NORMALITY);
        user.setType(UserType.SUPER_ADMIN);
        user.setSex(UserSex.MALE);
        user.setAvatar("https://pic2.zhimg.com/80/v2-1ceecf8a8ce3b882d326e8c3d5382a90_hd.jpg");
        Assert.assertEquals(1, userDao.insert(user));
    }

    @Test
    public void bSelectTest() {
        User user = userDao.getByUserNo(userNo);
        Assert.assertNotNull(user);
        Assert.assertTrue(passwordEncoder.matches("123456", user.getPassword()));
    }

    @Test
    public void cUpdateTest() {
        User user = new User();
        user.setUserNo(userNo);
        user.setNickName("admin");

        Assert.assertEquals(1, userDao.update(user));

    }

    @Test
    public void dDelete() {
        Assert.assertEquals(1, userDao.delete(userNo));
    }

    @Test
    public void eListUserTest() {
        List<User> userList = userDao.listByType(UserType.SUPER_ADMIN, null);
        Assert.assertNotNull(userList);
        Assert.assertFalse(userList.isEmpty());
    }

}
