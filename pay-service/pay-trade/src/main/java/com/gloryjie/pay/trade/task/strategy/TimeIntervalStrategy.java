/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.trade.task.strategy
 *   Date Created: 2019/3/8
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/3/8      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.trade.task.strategy;

import java.util.concurrent.TimeUnit;

/**
 * 事件间隔策略
 * @author Jie
 * @since
 */
public interface TimeIntervalStrategy {

    /**
     * 下次的事件间隔
     * @return
     */
    int nextInterval();

    /**
     * 当前次数
     * @return
     */
    int times();

    /**
     * 时间单位
     * @return
     */
    TimeUnit getTimeUnit();

}
