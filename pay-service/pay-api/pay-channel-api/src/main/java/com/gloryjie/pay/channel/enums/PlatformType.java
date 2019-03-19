/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.channel.enums
 *   Date Created: 2019/3/19
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/3/19      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.channel.enums;

import lombok.Getter;

/**
 * 支付平台
 * @author Jie
 * @since
 */
@Getter
public enum PlatformType {

    ALIPAY(1,"支付宝"),
    WXPAY(2,"微信支付"),
    UNIONPAY(3,"银联");

    int code;

    private String desc;

    PlatformType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
