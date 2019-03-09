/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.notification.error
 *   Date Created: 2019/2/7
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/2/7      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.notification.error;

import com.gloryjie.pay.base.constant.HttpStatus;
import com.gloryjie.pay.base.enums.base.BaseErrorEnum;
import lombok.Getter;

/**
 * @author Jie
 * @since
 */
@Getter
public enum NotificationError implements BaseErrorEnum {
    /**
     * 事件订阅错误
     */
    SIGN_FAIL(HttpStatus.INTERNAL_SERVER_ERROR,"301","通知签名失败")
    ;

    private int code;

    private String status;

    private String message;

    NotificationError(int code, String status, String message){
        this.code = code;
        this.status = code + status;
        this.message = message;
    }
}
