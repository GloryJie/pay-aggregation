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

import com.gloryjie.pay.base.annotation.DistributeLockAnnotation;
import com.gloryjie.pay.base.enums.error.CommonErrorEnum;
import com.gloryjie.pay.base.exception.error.SystemErrorException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * 分布式锁切面
 *
 * @author Jie
 * @since
 */
@Slf4j
@Component
@Aspect
public class DistributeLockAdvise {

    @Autowired
    private DistributeLock distributeLock;

    @Around("@annotation(com.gloryjie.pay.base.annotation.DistributeLockAnnotation)")
    public Object processDistributeLock(ProceedingJoinPoint joinPoint) {
        // 获取注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributeLockAnnotation lockConfig = method.getDeclaredAnnotation(DistributeLockAnnotation.class);

        Object[] args = joinPoint.getArgs();
        String key = getKey(method, args, lockConfig);
        if (StringUtils.isBlank(key) || "null".equals(key)) {
            log.error("get param key null");
            throw SystemErrorException.create(CommonErrorEnum.INTERNAL_SYSTEM_ERROR);
        }

        boolean lockResult = distributeLock.tryLock(key, lockConfig.keyExpireTime(), lockConfig.waitTime(), lockConfig.retryInterval());
        try {
            // 获取锁成功, 则执行目标方法
            if (lockResult) {
                log.debug("get lock success");
                return joinPoint.proceed(args);
            } else {
                // 否则返回系统繁忙
                throw SystemErrorException.create(CommonErrorEnum.SYSTEM_BUSY_ERROR);
            }
        } catch (Throwable throwable) {
            log.error("execute target method={} error", method.getName(), throwable);
            throw SystemErrorException.create(CommonErrorEnum.INTERNAL_SYSTEM_ERROR, throwable.getMessage());
        } finally {
            // 释放锁
            distributeLock.unLock(key);
        }
    }


    // TODO: 2018/11/12 当前仅支持一层的参数获取, 并且只能是当前类声明的字段, 若有需要可以继续优化

    /**
     * 根据表达式获取key值
     *
     * @param method
     * @param args
     * @param lockConfig
     * @return
     */
    private String getKey(Method method, Object[] args, DistributeLockAnnotation lockConfig) {
        String targetKey;
        try {
            ParamToken paramToken = new ParamToken(lockConfig.key());
            // 是否使用参数化key
            if (lockConfig.userParamKey()) {
                int index = -1;
                // 根据名称获取对应的参数
                Parameter[] parameters = method.getParameters();
                for (int i = 0; i < parameters.length; i++) {
                    if (paramToken.getName().equals(parameters[i].getName())) {
                        index = i;
                        break;
                    }
                }
                Object param = args[index];
                // 如果参数token没有下一个, 则当前参数即为key
                if (!paramToken.hasNext() && index > -1) {
                    targetKey = String.valueOf(param);
                } else {
                    // 获取对象内的属性值
                    Class<?> clazz = param.getClass();
                    String targetFieldName = paramToken.next().getName();
                    Field targetField = clazz.getDeclaredField(targetFieldName);
                    targetField.setAccessible(true);
                    targetKey = String.valueOf(targetField.get(param));
                }
            } else {
                // 若没有指定参数key, 则key为值
                targetKey = lockConfig.key();
            }
        } catch (Exception e) {
            log.error("get param key fail ", e);
            throw SystemErrorException.create(CommonErrorEnum.INTERNAL_SYSTEM_ERROR, e);
        }
        return targetKey;
    }


    /**
     * 递归获取字段
     *
     * @param clazz
     * @param fieldMap
     */
    private void addField(Class<?> clazz, Map<String, Field> fieldMap) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            fieldMap.put(field.getName(), field);
        }
        if (clazz.getSuperclass() != null) {
            addField(clazz.getSuperclass(), fieldMap);
        }

    }
}
