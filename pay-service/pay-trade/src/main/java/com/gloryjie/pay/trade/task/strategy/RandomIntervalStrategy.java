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

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 随机的时间间隔
 * @author Jie
 * @since
 */
public class RandomIntervalStrategy implements TimeIntervalStrategy {
    private ThreadLocalRandom random;

    private int times;

    private TimeUnit unit;

    private int min;

    private int max;

    public RandomIntervalStrategy(int min, int max, TimeUnit unit) {
        this.random = ThreadLocalRandom.current();
        this.times = 0;
        this.unit = unit;
        this.min = min;
        this.max = max;
    }

    @Override
    public int nextInterval() {
        times += 1;
        return random.nextInt(min,max);
    }

    @Override
    public int times() {
        return times;
    }

    @Override
    public TimeUnit getTimeUnit() {
        return unit;
    }
}
