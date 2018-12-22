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

import java.math.BigDecimal;

/**
 * @author Jie
 * @since
 */
public class RefundDto {

    private Integer appId;

    private String chargeNo;

    private BigDecimal amount;

}
