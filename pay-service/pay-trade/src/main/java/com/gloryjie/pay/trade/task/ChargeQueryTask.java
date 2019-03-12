/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.trade.task
 *   Date Created: 2019/3/8
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/3/8      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.trade.task;

import com.gloryjie.pay.trade.biz.ChargeBiz;
import com.gloryjie.pay.trade.dao.ChargeDao;
import com.gloryjie.pay.trade.enums.ChargeStatus;
import com.gloryjie.pay.trade.model.Charge;
import com.gloryjie.pay.trade.task.strategy.TimeIntervalStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledExecutorService;

/**
 * 支付单查询任务
 *
 * @author Jie
 * @since 0.1
 */
@Slf4j
public class ChargeQueryTask implements Runnable {

    private String chargeNo;

    private int maxTimes;

    private ChargeDao chargeDao;

    private ChargeBiz chargeBiz;

    private TimeIntervalStrategy intervalStrategy;

    private ScheduledExecutorService executor;


    public ChargeQueryTask(String chargeNo, int maxTimes, ChargeDao chargeDao, ChargeBiz chargeBiz,
                           TimeIntervalStrategy intervalStrategy, ScheduledExecutorService executor) {
        this.chargeNo = chargeNo;
        this.maxTimes = maxTimes;
        this.chargeDao = chargeDao;
        this.chargeBiz = chargeBiz;
        this.intervalStrategy = intervalStrategy;
        this.executor = executor;
    }

    @Override
    public void run() {
        Charge charge = chargeDao.load(chargeNo);
        if (charge == null || ChargeStatus.WAIT_PAY != charge.getStatus()) {
            return;
        }
        log.info("execute query chargeNo={} channel={} task, times={}", charge.getChargeNo(), charge.getChannel().name(), intervalStrategy.times());

        charge = chargeBiz.queryChannel(charge);

        // 若当前状态仍为待支付，提交给线程池
        if ( intervalStrategy.times() < maxTimes && ChargeStatus.WAIT_PAY == charge.getStatus()) {
            int nextTime = intervalStrategy.nextInterval();
            log.info("query chargeNo={} channel={} task will execute {} {} later", charge.getChargeNo(), charge.getChannel().name(), nextTime, intervalStrategy.getTimeUnit().name());
            executor.schedule(new ChargeQueryTask(this.chargeNo, maxTimes, chargeDao, chargeBiz, intervalStrategy, executor),
                    nextTime, intervalStrategy.getTimeUnit());
        }

    }
}
