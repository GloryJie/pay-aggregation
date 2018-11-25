/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.channel.config
 *   Date Created: 2018/11/24
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/11/24      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.channel.config;

import lombok.Data;

/**
 * 支付宝渠道配置
 * @author Jie
 * @since
 */
@Data
public class AlipayChannelConfig {

    /**
     * 支付宝商户号
     */
    private String merchantId;

    /**
     * 商户私钥
     */
    private String merchantPrivateKey;

    /**
     * 支付宝公钥
     */
    private String alipayPublicKey;


}
