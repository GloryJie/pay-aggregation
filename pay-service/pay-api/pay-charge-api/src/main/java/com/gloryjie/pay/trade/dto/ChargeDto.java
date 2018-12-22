/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.charge.dto
 *   Date Created: 2018/12/9
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/12/9      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.trade.dto;

import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.trade.enums.ChargeStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author Jie
 * @since 0.1
 */
@Data
public class ChargeDto {

    /**
     * 支付单号
     */
    private String chargeNo;

    /**
     * 商户订单号，必须在商户系统内唯一
     */
    private String orderNo;

    /**
     * 平台应用Id
     */
    private Integer appId;

    /**
     * 服务方应用id
     */
    private Integer serviceAppId;

    /**
     * 支付单金额
     */
    private BigDecimal amount;

    private ChargeStatus status;

    /**
     * 购买商品的标题
     */
    private String subject;

    /**
     * 购买商品的描述信息
     */
    private String body;

    /**
     * 支付渠道
     */
    private ChannelType channel;

    /**
     * 发起支付的客户端IP
     */
    private String clientIp;

    /**
     * 订单备注，限制300个字符内
     */
    private String description;

    /**
     * 用户保留信息
     */
    private String userHold;

    /**
     * 支付单单失效时间，单位分钟
     */
    private Long timeExpire;

    /**
     * 是否是生产模式
     */
    private Boolean liveMode;

    /**
     * 三位ISO货币代码，只支持人民币cny，默认cny
     */
    private String currency;

    /**
     * 渠道额外参数
     */
    private Map<String,String> extra;
    /**
     * 支付凭证
     */
    private String credential;

}
