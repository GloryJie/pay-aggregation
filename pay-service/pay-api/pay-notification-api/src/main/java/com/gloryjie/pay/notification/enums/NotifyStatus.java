/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.notification.enums
 *   Date Created: 2019/2/1
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/2/1      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.notification.enums;

import com.gloryjie.pay.base.enums.base.BaseEnum;
import lombok.Getter;

/**
 * @author Jie
 * @since
 */
@Getter
public enum NotifyStatus implements BaseEnum {

    PROCESSING(1,"处理中"),
    SUCCESS(20,"成功"),
    FAIL(30,"失败");


    int code;
    String desc;

    NotifyStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
