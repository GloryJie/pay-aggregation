/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.base.util.idGenerator
 *   Date Created: 2018/11/17
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/11/17      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.base.util.idGenerator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Jie
 * @since
 */
@Slf4j
@Component
public class IdFactory {

    private static long workerId;

    private static long dataCenterId;

    private static IdGenerator ID_GENERATOR;

    // TODO: 2018/11/17 目前机器数较少,所以为配置方式, 后期可使用zk自动分配

    @Value("${IdFactory.workerId:0}")
    public void setWorkerId(long workerId) {
        IdFactory.workerId = workerId;
    }

    @Value("${IdFactory.dataCenterId:0}")
    public void setDataCenterId(long dataCenterId) {
        IdFactory.dataCenterId = dataCenterId;
    }

    @PostConstruct
    public void init() {
        ID_GENERATOR = new DefaultIdGenerator(workerId, dataCenterId);
        log.debug("init IdFactory success, workerId={}, dataCenterId={}", workerId, dataCenterId);
    }

    public static long generateId() {
        return ID_GENERATOR.generatorId();
    }

    public static String generateStringId() {
        return String.valueOf(generateId());
    }
}
