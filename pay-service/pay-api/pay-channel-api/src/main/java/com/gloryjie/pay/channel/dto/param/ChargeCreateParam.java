/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.channel.dto.param
 *   Date Created: 2018/12/21
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/12/21      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.channel.dto.param;

import com.gloryjie.pay.channel.enums.ChannelType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @author Jie
 * @since
 */
@Data
public class ChargeCreateParam {
    /**
     * 商户订单号，必须在商户系统内唯一
     */
    @NotBlank
    private String orderNo;
    /**
     * 平台应用Id
     */
    @NotNull
    private Integer appId;

    /**
     * 服务方APPId
     */
    private Integer serviceAppId;

    /**
     * 支付单金额
     */
    @NotNull
    private BigDecimal amount;
    /**
     * 购买商品的标题
     */
    @NotBlank
    private String subject;
    /**
     * 购买商品的描述信息
     */
    @NotBlank
    private String body;
    /**
     * 支付渠道
     */
    @NotNull
    private ChannelType channel;
    /**
     * 发起支付的客户端IP
     */
    @NotNull
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
     * 支付单单失效时间，单位分钟, 默认120m
     */
    private Long timeExpire;
    /**
     * 是否是生产模式
     */
    private Boolean liveMode;
    /**
     * 三位ISO货币代码，只支持人民币cny，默认cny
     */
    @NotBlank
    private String currency;

    /**
     * 渠道额外参数
     */
    private Map<String,String> extra;
}
