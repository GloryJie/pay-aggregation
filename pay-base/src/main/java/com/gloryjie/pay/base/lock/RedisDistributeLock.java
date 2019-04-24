/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.base.lock
 *   Date Created: 2018/11/11
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/11/11      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.base.lock;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * 基于Redis实现的分布式锁
 *
 * @author Jie
 * @since 0.1
 */
@Slf4j
//@Component
public class RedisDistributeLock implements DistributeLock {

    /**
     * 获取锁成功后,默认的过期时间, 30秒
     */
    private static final int DEFAULT_LOCK_EXPIRES_MILL_SECONDS = 1000 * 30;

    /**
     * 获取锁的默认超时时间, 10秒
     */
    private static final int DEFAULT_LOCK_WAIT_DEFAULT_TIME_OUT = 1000 * 10;

    /**
     * 默认的循环等待时间, 150毫秒
     */
    private static final int DEFAULT_WAIT_INTERVAL_TIME = 150;

    @Autowired
    private RedisTemplate redisTemplate;

    private ValueOperations<String, String> valueOperations;

    @PostConstruct
    public void init() {
        valueOperations = redisTemplate.opsForValue();
    }


    @Override
    public boolean lock(@NonNull String lockKey) {
        return tryLock(lockKey, DEFAULT_LOCK_EXPIRES_MILL_SECONDS, -1, 0);
    }

    @Override
    public boolean tryLock(@NonNull String key, int keyExpireTime, int waitTime, int waitInterval) {
        do {
            long lockExpireTime = Instant.now().toEpochMilli() + keyExpireTime;
            // 使用Redis原子命令 setnx
            boolean executeResult = valueOperations.setIfAbsent(key, String.valueOf(lockExpireTime));
            // 获取锁成功
            if (executeResult) {
                // 设置过期时间
                redisTemplate.expire(key, lockExpireTime, TimeUnit.MILLISECONDS);
                return true;
            } else {
                // 没有获取到锁, 则继续
                String oldTimeValue = valueOperations.get(key);
                // 锁过期时间小于当前时间,锁已经超时,重新取锁
                if (StringUtils.isNotBlank(oldTimeValue) && Long.valueOf(oldTimeValue) < Instant.now().toEpochMilli()) {
                    String currentTimeValue = valueOperations.getAndSet(key, String.valueOf(lockExpireTime));
                    if (oldTimeValue.equals(currentTimeValue)) {
                        // 获取锁成功, 设置过期时间
                        redisTemplate.expire(key, keyExpireTime, TimeUnit.MILLISECONDS);
                        return true;
                    }
                }
            }
            // 如果waitTime<0, 说明是非阻塞返回
            if (waitTime < 0) {
                return false;
            }
            waitTime -= waitInterval;
            try {
                // 稍后再尝试获取锁
                Thread.sleep(waitInterval);
                log.debug("retry get lock key={} waitInterval={} waitTime={}", key, waitInterval, waitTime);
            } catch (Exception e) {
                log.error("try get lock error lockKey={} waitTime={}", e);
            }
        } while (waitTime > 0);
        return false;
    }


    @Override
    public void unLock(String lockKey) {
        try {
            redisTemplate.delete(lockKey);
        } catch (Exception e) {
            log.error("unlock key={} error", e);
        }
    }
}
