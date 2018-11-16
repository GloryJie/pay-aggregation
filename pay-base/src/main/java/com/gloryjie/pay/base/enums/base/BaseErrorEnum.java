/* ------------------------------------------------------------------
 *   Product:      music-dj
 *   Module Name:  music-common
 *   Package Name: com.gloryjie.common.enums.base
 *   Date Created: 2018-09-11
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018-09-11      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.base.enums.base;

/**
 * @author Jie
 * @since 0.1
 */
public interface BaseErrorEnum extends BaseEnum{

    /**
     * 获取错误枚举的状态码
     * @return 状态码
     */
    String getStatus();

    /**
     * 获取错误枚举的消息描述
     * @return 消息描述
     */
    String getMessage();
}
