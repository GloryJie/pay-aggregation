package com.glory.pay.app.dao;

import com.gloryjie.pay.PayAllApplication;
import com.gloryjie.pay.app.dao.AppDao;
import com.gloryjie.pay.app.model.App;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author jie
 * @since 2019/4/27
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PayAllApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AppDaoTest {

    @Autowired
    AppDao appDao;

    /**
     * 测试获取应用数据所有的节点
     */
    @Test
    public void getAppTreeAllNodeTest() {
        List<App> appList = appDao.getAppTree(10110000, 10199999);
        Assert.assertNotNull(appList);
        Assert.assertTrue(!appList.isEmpty());
    }


}
