/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.trade
 *   Date Created: 2018/12/9
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/12/9      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.trade.controller;

import com.gloryjie.pay.channel.dto.param.ChargeCreateParam;
import com.gloryjie.pay.trade.api.ChargeControllerApi;
import com.gloryjie.pay.trade.dto.ChargeDto;
import com.gloryjie.pay.trade.service.ChargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author Jie
 * @since
 */
@RequestMapping("/api/v1")
@RestController
public class ChargeController implements ChargeControllerApi {

    @Autowired
    private ChargeService chargeService;

    @PostMapping("/charge")
    public ChargeDto createCharge(@RequestBody @Valid ChargeCreateParam createParam){
        return chargeService.pay(createParam);
    }

}
