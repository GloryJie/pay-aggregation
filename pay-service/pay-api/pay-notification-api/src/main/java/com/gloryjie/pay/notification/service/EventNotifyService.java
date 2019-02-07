/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.notification.service
 *   Date Created: 2019/2/1
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/2/1      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.notification.service;

import com.github.pagehelper.PageInfo;
import com.gloryjie.pay.notification.dto.EventNotifyDto;
import com.gloryjie.pay.trade.dto.ChargeDto;
import com.gloryjie.pay.trade.dto.RefundDto;

/**
 * @author Jie
 * @since
 */
public interface EventNotifyService {

    void handleChargeSuccessEvent(ChargeDto chargeDto);

    void handleRefundSuccessEvent(RefundDto refundDto);

    PageInfo<EventNotifyDto> getRecord(Integer appId, Integer startPage, Integer pageSize);
}
