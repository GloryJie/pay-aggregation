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

import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.trade.biz.ChargeBiz;
import com.gloryjie.pay.trade.dao.ChargeDao;
import com.gloryjie.pay.trade.task.strategy.RandomIntervalStrategy;
import com.gloryjie.pay.trade.task.strategy.TimeIntervalStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 执行轮询的线程池
 *
 * @author Jie
 * @since
 */
@Slf4j
@Component
public class ChargeQueryExecutors {

    @Autowired
    private ChargeDao chargeDao;

    @Autowired
    private ChargeBiz chargeBiz;

    private static final ScheduledThreadPoolExecutor EXECUTOR_SERVICE;

    static {
        /**
         * 轮询属于高IO操作，有数据库连接以及网络连接
         * 使用ScheduledThreadPoolExecutor来实现不断轮询有个问题，内部DelayWorkQueue为无界队列，有可能造成 OOM
         * 另外一种思路就是利用RMQ的延迟消息队列来实现
         * 或者继承ScheduledThreadPoolExecutor，重写schedule，自己控制大小
         * 或者不在创建支付单后触发查询任务，而是在第一次调用查询接口后在状态不成功的前提下触发查询任务, 这样可以在异步未达到之后再创建任务，减少不必要的轮询
         * TODO 2019/03/08 需要优化
         */
        int corePoolSize = Runtime.getRuntime().availableProcessors() * 2;
        ThreadFactory threadFactory = new ChargeTaskThreadFactory();
        // 使用丢弃策略，因为是无界队列，只有在线程池shutdown的时候才会执行handler
        RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardPolicy();
        EXECUTOR_SERVICE = new ScheduledThreadPoolExecutor(corePoolSize, threadFactory, handler);
    }

    public void executeQueryTask(String chargeNo, ChannelType channelType, long timeExpire) {
        TimeIntervalStrategy strategy;
        int maxTimes;
        switch (channelType) {
            case ALIPAY_PAGE:
            case ALIPAY_WAP:
                maxTimes = 5;
                strategy = new RandomIntervalStrategy(0, Math.toIntExact(timeExpire * 60 / maxTimes), TimeUnit.SECONDS);
                break;
            default:
                log.info("unsupported ChannelType={} query task, chargeNo={}", channelType, chargeNo);
                return;
        }

        ChargeQueryTask task = new ChargeQueryTask(chargeNo, maxTimes, chargeDao, chargeBiz, strategy, EXECUTOR_SERVICE);
        EXECUTOR_SERVICE.schedule(task, strategy.nextInterval(), strategy.getTimeUnit());
    }


    static class ChargeTaskThreadFactory implements ThreadFactory {

        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix = "ChargeQueryExecutors-thread-";


        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, namePrefix + threadNumber.getAndIncrement());

        }
    }

}
