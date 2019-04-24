package com.gloryjie.pay.channel.dao;


import com.gloryjie.pay.channel.enums.CertificateType;
import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.channel.model.Certificate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface CertificateDao {

    int insert(Certificate certificate);

    /**
     * 获取渠道的所有证书
     *
     * @param appid
     * @param channel
     * @return
     */
    List<Certificate> loadChannelCert(@Param("appId") Integer appid, @Param("channel") ChannelType channel);

    /**
     * 删除渠道对应的而所有证书
     *
     * @param appid
     * @param channel
     * @return
     */
    int deleteChannelAllCert(@Param("appId") Integer appid, @Param("channel") ChannelType channel);

    /**
     * 删除指定的证书
     * @param appid
     * @param channel
     * @param type
     * @return
     */
    int deleteSpecifyCert(@Param("appId") Integer appid, @Param("channel") ChannelType channel, @Param("certType") CertificateType type);

}