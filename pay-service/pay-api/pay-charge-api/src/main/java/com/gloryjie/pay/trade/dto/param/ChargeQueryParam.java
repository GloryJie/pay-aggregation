/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.trade.dto.param
 *   Date Created: 2019/2/6
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/2/6      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.trade.dto.param;

import com.gloryjie.pay.base.constant.DefaultConstant;
import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.trade.enums.ChargeStatus;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author Jie
 * @since
 */
@Data
public class ChargeQueryParam {

    @NotNull
    private Integer appId;

    private String chargeNo;

    private Integer startPage = 1;

    private Integer pageSize = 10;

    private String orderNo;

    private ChannelType channel;

    @DateTimeFormat(pattern = DefaultConstant.DATE_TIME_FORMAT)
    private LocalDateTime startDate;

    @DateTimeFormat(pattern = DefaultConstant.DATE_TIME_FORMAT)
    private LocalDateTime endDate;

    private ChargeStatus status;

    private Integer maxAppId;

    private Integer subAppId;
}
