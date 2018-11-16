/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.base.annotation
 *   Date Created: 2018/11/11
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/11/11      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.base.annotation;

/**
 * 分布式锁注解
 * @author Jie
 * @since 0.1
 */
public @interface DistributeLockAnnotation {

    /**
     * key或者表达式 object.name
     * @return
     */
    String key();

    /**
     * key的前缀
     * @return
     */
    String prefix() default "";

    /**
     * key是否从参数中获取, 若true, 则key为简单的表达式
     * @return
     */
    boolean userParamKey() default false;

    /**
     * key存储超时时间, 默认10秒
     */
    int keyExpireTime() default 10000;

    /**
     * 等待时间, 默认3秒
     */
    int waitTime() default 3000;

    /**
     * 尝试获取锁的间隔时间, 默认500毫秒
     */
    int retryInterval() default 500;
}
