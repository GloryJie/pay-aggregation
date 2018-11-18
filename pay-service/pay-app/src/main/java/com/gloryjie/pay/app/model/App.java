package com.gloryjie.pay.app.model;

import lombok.Data;

import java.util.Date;

@Data
public class App {
    /**
     * 应用id
     */
    private Integer appId;

    /**
     * 应用类型 1 平台应用 2 子商户应用
     */
    private Integer type;

    /**
     * 应用名
     */
    private String name;

    /**
     * 应用描述
     */
    private String description;

    /**
     * 应用状态 0 停用 1 启用
     */
    private Integer status;

    /**
     * 应用关联的用户对象id，创建应用时同时会创建用户
     */
    private String userId;

    /**
     * 异步通知地址
     */
    private String notifyUrl;

    /**
     * 平台应用
     */
    private Integer platformApp;

    /**
     * 是否使用平台商户渠道配置，默认使用平台商户配置，子商户字段
     */
    private Integer usePlatformConfig;

    private String extra;

    private String accessSecret;

    private Integer parentApp;

    /**
     * 交易公钥
     */
    private String tradePublicKey;

    /**
     * 通知私钥
     */
    private String notifyPrivateKey;

    /**
     * 通知公钥
     */
    private String notifyPublicKey;

    private Integer level;

    /**
     * 用于实现乐观锁
     */
    private Integer version;

    /**
     * 是否逻辑删除
     */
    private Boolean logicalDel;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}