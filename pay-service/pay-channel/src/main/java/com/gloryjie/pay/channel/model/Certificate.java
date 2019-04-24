package com.gloryjie.pay.channel.model;

import com.gloryjie.pay.channel.enums.CertificateType;
import com.gloryjie.pay.channel.enums.ChannelType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 存储证书
 *
 * @author jie
 * @since 2019/4/23
 */
@Data
public class Certificate {

    private Integer id;

    /**
     * 应用标识
     */
    private Integer appId;

    /**
     * 证书类型
     */
    private CertificateType type;

    /**
     * 渠道
     */
    private ChannelType channel;

    /**
     * 证书数据
     */
    private byte[] certData;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
