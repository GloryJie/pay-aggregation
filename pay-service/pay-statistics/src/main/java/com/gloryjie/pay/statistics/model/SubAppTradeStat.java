package com.gloryjie.pay.statistics.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 统计平台内子应用交易
 * @author jie
 * @since 2019/4/30
 */
@Data
public class SubAppTradeStat {

    /**
     * ID编码
     */
    private Integer id;

    /**
     * 应用ID
     */
    private Integer appId;

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
