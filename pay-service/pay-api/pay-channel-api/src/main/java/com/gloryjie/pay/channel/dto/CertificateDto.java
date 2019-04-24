package com.gloryjie.pay.channel.dto;

import com.gloryjie.pay.channel.enums.CertificateType;
import com.gloryjie.pay.channel.enums.ChannelType;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author jie
 * @since 2019/4/24
 */
@Data
public class CertificateDto {
    /**
     * 应用标识
     */
    @NotNull
    private Integer appId;

    /**
     * 证书类型
     */
    @NotNull
    private CertificateType type;

    /**
     * 渠道
     */
    @NotNull
    private ChannelType channel;

    /**
     * 证书数据
     */
    private byte[] certData;
}
