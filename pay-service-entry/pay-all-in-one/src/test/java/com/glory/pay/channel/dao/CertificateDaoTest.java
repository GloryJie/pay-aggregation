package com.glory.pay.channel.dao;

import com.gloryjie.pay.PayAllApplication;
import com.gloryjie.pay.channel.dao.CertificateDao;
import com.gloryjie.pay.channel.enums.CertificateType;
import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.channel.model.Certificate;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author jie
 * @since 2019/4/23
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PayAllApplication.class)
//@MybatisTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CertificateDaoTest {

    @Autowired
    private CertificateDao certificateDao;

    @Test
    public void aInsertTest(){
        Certificate certificate = new Certificate();
        certificate.setAppId(100110000);
        certificate.setChannel(ChannelType.UNIONPAY_PAGE);
        certificate.setType(CertificateType.UNIONPAY_SIGN_CERT);
        certificate.setCertData("dataklajflkdj".getBytes(StandardCharsets.UTF_8));

        Assert.assertEquals(certificateDao.insert(certificate), 1);

    }


    @Test
    public void bLoadTest(){
        List<Certificate>  list = certificateDao.loadChannelCert(100110000, ChannelType.UNIONPAY_PAGE);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void cDeleteTest(){
        int result = certificateDao.deleteChannelAllCert(100110000, ChannelType.UNIONPAY_PAGE);
        Assert.assertTrue(result > 0);

    }



}
