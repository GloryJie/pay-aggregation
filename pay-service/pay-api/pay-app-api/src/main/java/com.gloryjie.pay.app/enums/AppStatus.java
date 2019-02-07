/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.app.enums
 *   Date Created: 2019/2/7
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/2/7      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.app.enums;

import lombok.Getter;

/**
 * @author Jie
 * @since 0.1
 */
@Getter
public enum AppStatus {

    /**
     * 支付宝交易状态
     */
    START(0, "启用"),
    STOP(1, "停用");

    int code;

    String desc;

    AppStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
