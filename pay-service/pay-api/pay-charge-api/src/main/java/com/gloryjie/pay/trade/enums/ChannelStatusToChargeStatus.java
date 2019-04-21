/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.trade.enums
 *   Date Created: 2018/12/22
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/12/22      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.trade.enums;

import com.gloryjie.pay.base.enums.error.CommonErrorEnum;
import com.gloryjie.pay.base.exception.error.SystemException;
import com.gloryjie.pay.channel.enums.AlipayStatus;
import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.channel.enums.UnionpayStatus;

/**
 * 渠道状态转换为支付单对应状态
 *
 * @author Jie
 * @since
 */
public class ChannelStatusToChargeStatus {

    public static ChargeStatus switchStatus(ChannelType channel, String status) {
        switch (channel.getPlatformType()) {
            case ALIPAY:
                return alipayStatusToChargeStatus(status);
            case UNIONPAY:
                return unionpayStatusToChargeStatus(status);
            default:
                throw SystemException.create(CommonErrorEnum.INTERNAL_SYSTEM_ERROR);
        }
    }


    private static ChargeStatus alipayStatusToChargeStatus(String status) {
        AlipayStatus alipayStatus = AlipayStatus.valueOf(status);
        ChargeStatus chargeStatus;
        switch (alipayStatus) {
            case WAIT_BUYER_PAY:
                chargeStatus = ChargeStatus.WAIT_PAY;
                break;
            case TRADE_FINISHED:
            case TRADE_SUCCESS:
                chargeStatus = ChargeStatus.SUCCESS;
                break;
            case TRADE_CLOSED:
                chargeStatus = ChargeStatus.CLOSED;
                break;
            case TRADE_FAIL:
                chargeStatus = ChargeStatus.FAILURE;
                break;
            default:
                throw SystemException.create(CommonErrorEnum.INTERNAL_SYSTEM_ERROR, "alipay status not exists");
        }
        return chargeStatus;
    }

    private static ChargeStatus unionpayStatusToChargeStatus(String status) {
        UnionpayStatus unionpayStatus = UnionpayStatus.valueOf(status);
        ChargeStatus chargeStatus;
        switch (unionpayStatus) {
            case TRADE_ACCEPT_SUCCESS:
            case TIMEOUT_OR_UNKNOWN_STATUS:
            case TRADE_NOT_EXISTS:
            case TRADE_REPEAT:
                chargeStatus = ChargeStatus.WAIT_PAY;
                break;
            case TRADE_SUCCESS:
                chargeStatus = ChargeStatus.SUCCESS;
                break;
            case TRADE_FAIL:
                chargeStatus = ChargeStatus.FAILURE;
                break;
            default:
                throw SystemException.create(CommonErrorEnum.INTERNAL_SYSTEM_ERROR, "unionpay status not exists");
        }
        return chargeStatus;
    }

}
