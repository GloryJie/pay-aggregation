/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.channel.config
 *   Date Created: 2019/2/7
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/2/7      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.channel.config;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Jie
 * @since
 */
@Data
public class AlipayChannelConfig {

    /**
     * 支付宝商户号
     */
    @NotNull
    private String merchantId;

    /**
     * 商户私钥
     */
    @NotNull
    private String merchantPrivateKey;

    /**
     * 支付宝公钥
     */
    @NotNull
    private String merchantPublicKey;

    /**
     * 签名类型
     */
    @NotNull
    private String type;
}
