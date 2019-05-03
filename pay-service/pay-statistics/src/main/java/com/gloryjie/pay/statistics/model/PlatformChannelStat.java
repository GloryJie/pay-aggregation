package com.gloryjie.pay.statistics.model;

import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.channel.enums.PlatformType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 统计平台渠道
 * @author jie
 * @since 2019/4/30
 */
@Data
public class PlatformChannelStat {

    /**
     * ID编码
     */
    private Integer id;

    /**
     * 根应用ID
     */
    private Integer appId;

    /**
     * 支付渠道
     */
    private ChannelType channelType;

    /**
     * 交易平台
     */
    private PlatformType platformType;

    /**
     * 支付成功笔数
     */
    private Long successCount;

    /**
     * 支付成功金额
     */
    private Long successAmount;

    /**
     * 统计日
     */
    private LocalDate statDay;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
