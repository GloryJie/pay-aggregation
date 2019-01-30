package com.gloryjie.pay.channel.model;

import com.gloryjie.pay.channel.enums.ChannelType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChannelConfig {

    private Integer id;

    /**
     * 支付应用ID
     */
    private Integer appId;

    /**
     * 交易渠道
     */
    private ChannelType channel;

    /**
     * 渠道参数配置 json格式
     */
    private String channelConfig;

    /**
     * 启用时间
     */
    private LocalDateTime startDate;

    /**
     * 停用时间
     */
    private LocalDateTime stopDate;

    /**
     * 渠道状态
     * ‘0’：未启用 ‘1’：启用
     */
    private String status;

    /**
     * 逻辑删除(针对App删除后设置) ‘0’未删除 1：已删除
     */
    private Boolean logicalDel;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}