package com.glory.pay.app.service;

import com.gloryjie.pay.PayAllApplication;
import com.gloryjie.pay.app.dto.AppDto;
import com.gloryjie.pay.app.dto.AppUpdateParam;
import com.gloryjie.pay.app.enums.AppStatus;
import com.gloryjie.pay.app.service.AppService;
import com.gloryjie.pay.base.exception.BaseException;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * @author jie
 * @since 2019/4/26
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PayAllApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AppServiceTest {

    @Autowired
    private AppService appService;

    static Integer masterAppId = 0;

    static String masterName = "测试";


    /**
     * 添加正常逻辑
     */
    @Test
    public void aCreateMasterAppTest() {
        masterName = "测试" + Math.round(Math.random() * 1000);
        AppDto appDto = appService.createMasterApp(masterName, "测试专用", 123456L, 123456L);
        Assert.assertNotNull(appDto);
        masterAppId = appDto.getAppId();
    }

    /**
     * 添加同名异常
     */
    @Test(expected = BaseException.class)
    public void bCreateMasterAppTest() {
        AppDto appDto = appService.createMasterApp(masterName, "测试专用", 123456L, 123456L);
    }

    /**
     * 更新测试
     */
    @Test
    public void cUpdateAppInfoTest() {
        AppUpdateParam param = new AppUpdateParam();
        param.setAppId(masterAppId);
        param.setDescription("更新测试2");
        param.setStatus(AppStatus.STOP);
        param.setTradePublicKey("public key");

        Assert.assertTrue(appService.updateAppInfo(param));
    }

    /**
     * 正常添加子应用
     */
    @Test
    public void dCreateSubAppTest() {

        AppDto appDto = appService.createSubApp("子应用", "子应用desc", masterAppId, 123456L, 123456L);

        Assert.assertNotNull(appDto);
    }

    /**
     * 同名子应用测试
     */
    @Test(expected = BaseException.class)
    public void eCreateSubAppSameNameErrorTest() {
        AppDto appDto = appService.createSubApp("子应用", "子应用desc", masterAppId, 123456L, 123456L);
    }

    /**
     * 测试超越层次
     */
    @Test(expected = BaseException.class)
    public void fCreateSubAppBeyondLevelTest() {
        int lastAppId = masterAppId;
        for (int i = 2; i < 7; i++) {
            AppDto appDto = appService.createSubApp("子应用" + i, "子应用desc", lastAppId, 123456L, 123456L);
            lastAppId = appDto.getAppId();
        }
    }


}
