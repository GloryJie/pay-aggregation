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
 * @since
 */
@Getter
public enum AppType {

    /**
     * 支付宝交易状态subordinate
     */
    MASTER(0, "主应用"),
    SUBORDINATE(1, "子应用");

    int code;

    String desc;

    AppType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
