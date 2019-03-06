/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.channel.enums
 *   Date Created: 2019/2/7
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/2/7      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.channel.enums;

import lombok.Getter;

/**
 * @author Jie
 * @since 0.1
 */
@Getter
public enum ChannelConfigStatus {


    /**
     * 渠道配置状态
     */
    STOP_USING(0, "未启用"),
    START_USING(1, "启用");

    private int code;

    private String desc;

    ChannelConfigStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public boolean isStart() {
        return this == ChannelConfigStatus.START_USING;
    }

    public boolean isStop() {
        return this == ChannelConfigStatus.STOP_USING;
    }

}
