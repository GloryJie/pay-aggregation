package com.gloryjie.pay.statistics.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 统计平台交易
 * @author jie
 * @since 2019/4/30
 */
@Data
public class PlatformTradeStat {

    /**
     * ID编码
     */
    private Integer id;

    /**
     * 应用ID
     */
    private Integer appId;

    /**
     * 待支付笔数
     */
    private Long processCount;

    /**
     * 待支付金额
     */
    private Long processAmount;

    /**
     * 支付成功笔数
     */
    private Long successCount;

    /**
     * 支付成功金额
     */
    private Long successAmount;

    /**
     * 关闭笔数
     */
    private Long closeCount;

    /**
     * 关闭金额
     */
    private Long closeAmount;

    /**
     * 支付失败笔数
     */
    private Long failCount;

    /**
     * 支付失败金额
     */
    private Long failAmount;

    /**
     * 统计日
     */
    private LocalDate statisticsDay;

    /**
     * 统计时间
     */
    private LocalDateTime statDateTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
