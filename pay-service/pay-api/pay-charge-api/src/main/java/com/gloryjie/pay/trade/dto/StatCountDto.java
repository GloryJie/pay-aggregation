package com.gloryjie.pay.trade.dto;

import com.gloryjie.pay.trade.enums.ChargeStatus;
import lombok.Data;

/**
 * @author jie
 * @since 2019/5/2
 */
@Data
public class StatCountDto {

    private ChargeStatus status;

    private Long countNum;

    private Long totalAmount;

}
