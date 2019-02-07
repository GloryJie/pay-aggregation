/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.channel.dto
 *   Date Created: 2019/2/7
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/2/7      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.channel.dto;

import com.gloryjie.pay.channel.enums.ChannelConfigStatus;
import com.gloryjie.pay.channel.enums.ChannelType;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author Jie
 * @since 0.1
 */
@Data
public class ChannelConfigDto {

    /**
     * 支付应用ID
     */
    private Integer appId;

    /**
     * 交易渠道
     */
    @NotNull
    private ChannelType channel;

    /**
     * 渠道参数配置 json格式
     */
    @NotNull
    private Map<String,String> channelConfig;


    /**
     * 渠道状态
     * ‘0’：未启用 ‘1’：启用
     */
    @NotNull
    private ChannelConfigStatus status;

    /**
     * 启用时间
     */
    private LocalDateTime startDate;

    /**
     * 停用时间
     */
    private LocalDateTime stopDate;

}
