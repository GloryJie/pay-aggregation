package com.gloryjie.pay.log.http;

import com.gloryjie.pay.gateway.GatewayApplication;
import com.gloryjie.pay.log.http.enums.HttpLogType;
import com.gloryjie.pay.log.http.model.HttpLogRecord;
import com.gloryjie.pay.log.http.model.PageInfo;
import com.gloryjie.pay.log.http.model.param.LogQueryParam;
import com.gloryjie.pay.log.http.service.HttpLogService;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author jie
 * @since 2019/4/7
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GatewayApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HttpLogServiceTest {

    @Autowired
    HttpLogService logService;

    @Test
    public void aInsertTest() {
        HttpLogRecord record = new HttpLogRecord();
        record.setAppId(123456);
        record.setPlatform("支付吧");
        record.setReqBody("{}");
        record.setType(HttpLogType.PLATFORM_NOTIFY_REQUEST.name().toLowerCase());
        record.setReqTimestamp(LocalDateTime.now().minusDays(2L).toEpochSecond(ZoneOffset.of("+8")));

        logService.record(record);
    }


    @Test
    public void bQueryTest(){
        LogQueryParam queryParam = new LogQueryParam();
        queryParam.setAppId(123456);
        queryParam.setType(HttpLogType.PLATFORM_NOTIFY_REQUEST.name().toLowerCase());
        queryParam.setStartPage(1);
        queryParam.setPageSize(10);

        PageInfo<HttpLogRecord> pageInfo = logService.find(queryParam);

        Assert.assertNotNull(pageInfo.getList());
        Assert.assertTrue(pageInfo.getList().size() > 0);
    }

    @Test
    public void cDeleteTest(){
        logService.deleteBeforeTime(HttpLogType.PLATFORM_NOTIFY_REQUEST.name().toLowerCase(), LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8")));
    }
}
