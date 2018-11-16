/* ------------------------------------------------------------------
 *   Product:      music-dj
 *   Module Name:  music-common
 *   Package Name: com.gloryjie.common.exception
 *   Date Created: 2018-09-10
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018-09-10      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.base.exception;

/**
 * @author Jie
 * @since 0.1
 */
public interface ErrorInterface {

    /**
     * 获取状态码
     * @return 状态码
     */
    String getStatus();

    /**
     * 获取消息描述
     * @return 描述
     */
    String getMessage();

}
