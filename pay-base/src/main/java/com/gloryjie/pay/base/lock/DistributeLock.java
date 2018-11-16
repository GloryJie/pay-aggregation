/* ------------------------------------------------------------------
 *   Product:      music-dj
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.common.lock
 *   Date Created: 2018-09-16
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018-09-16      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.base.lock;

/**
 * 分布式锁接口定义
 *
 * @author Jie
 * @since 0.1
 */
public interface DistributeLock {

    /**
     * 非阻塞获取锁
     *
     * @return 是否成功
     */
    boolean lock(String key);

    /**
     * 阻塞式获取锁
     *
     * @param lockKey  锁的key
     * @param keyExpireTime
     * @param waitTime 允许阻塞的最长时间, 单位毫秒
     * @param waitInterval 尝试的间隔时间
     * @return 是否成功
     */
    boolean tryLock(String lockKey, int keyExpireTime, int waitTime, int waitInterval);


    /**
     * 释放锁
     *
     * @param lockKey
     */
    void unLock(String lockKey);

}
