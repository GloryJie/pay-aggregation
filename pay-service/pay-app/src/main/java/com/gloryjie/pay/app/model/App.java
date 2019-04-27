package com.gloryjie.pay.app.model;

import com.gloryjie.pay.app.enums.AppStatus;
import com.gloryjie.pay.app.enums.AppType;
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
    private AppType type;

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
    private AppStatus status;

    /**
     * 应用关联的用户对象id, 即负责人
     */
    private Long responsibleUserNo;

    /**
     * 创建人
     */
    private Long createUserNo;

    /**
     * 是否使用平台商户渠道配置，默认使用平台商户配置，子商户字段
     */
    private Boolean usePlatformConfig;

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

    private Integer parentAppId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}