package com.gloryjie.pay.channel.config;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 银联配置参数
 * @author jie
 * @since 2019/4/21
 */
@Data
public class UnionpayChannelConfig {

    /**
     * 银联商户号
     */
    @NotNull
    private String merchantId;

    /**
     * 私钥证书的存放地址
     */
    private String privateCertPath;

    /**
     * 私钥证书对应的密码
     */
    private String privateCertPwd;

    /**
     * 根证书
     */
    private String rootCert;


}
