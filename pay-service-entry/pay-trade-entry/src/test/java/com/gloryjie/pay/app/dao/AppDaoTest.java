/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.app.dao
 *   Date Created: 2018/11/18
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/11/18      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.app.dao;

import com.gloryjie.pay.app.model.App;
import com.gloryjie.pay.trade.PayTradeApplication;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

/**
 * @author Jie
 * @since
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PayTradeApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AppDaoTest {

    @Autowired
    private AppDao appDao;

    public static Integer appId;

    @BeforeClass
    public static void init(){
        appId = new Random().nextInt(10000);
    }

    @Test
    public void aInsertTest(){
        App app = new App();
        app.setAppId(appId);
        app.setType(0);
        app.setName("测试");
        app.setDescription("测试");
        app.setStatus(0);
        app.setUserId("123");
        app.setPlatformApp(1);
        app.setNotifyPrivateKey("test");
        app.setNotifyPublicKey("test");
        app.setTradePublicKey("test");
        app.setLevel(0);
        app.setVersion(0);
        app.setLogicalDel(Boolean.FALSE);
        Assert.assertEquals(1, appDao.insert(app));
    }

    @Test
    public void bLoadTest(){
        Assert.assertNotNull(appDao.load(appId));
    }

    @Test
    public void cUpdateTest(){
        App app = new App();
        app.setAppId(appId);
        app.setDescription("update");

        Assert.assertEquals(1, appDao.update(app));
    }

}
