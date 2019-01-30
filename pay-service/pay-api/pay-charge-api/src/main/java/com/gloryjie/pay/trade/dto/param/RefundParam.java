/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.trade.dto.param
 *   Date Created: 2019/1/23
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/1/23      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.trade.dto.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 发起退款所需要的参数
 * @author Jie
 * @since 0.1
 */
@Data
public class RefundParam {

    @NotNull
    private Integer appId;

    @NotNull
    private String orderNo;

    private String chargeNo;

    private String refundNo;

    private Long amount;

    private String description;

}
