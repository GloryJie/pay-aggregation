/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.app.dto
 *   Date Created: 2019/2/7
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/2/7      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.app.dto;

import com.gloryjie.pay.app.enums.AppStatus;
import com.gloryjie.pay.app.enums.AppType;
import com.gloryjie.pay.base.annotation.IgnoreCovertProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Jie
 * @since 0.1
 */
@Data
public class AppDto {

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
    @NotNull
    private String description;

    /**
     * 应用状态 0 停用 1 启用
     */
    private AppStatus status;


    /**
     * 交易公钥
     */
    private String tradePublicKey;

    /**
     * 通知公钥
     */
    private String notifyPublicKey;

    /**
     * 通知私钥
     */
    @IgnoreCovertProperty
    private String notifyPrivateKey;

    /**
     * 应用关联的用户对象id, 即负责人
     */
    private Long responsibleUserNo;

    /**
     * 创建人
     */
    private Long createUserNo;

    private Integer level;

    private Integer parentAppId;

}
