/* ------------------------------------------------------------------
 *   Product:      music-dj
 *   Module Name:  com.gloryjie.common.util
 *   Package Name: com.gloryjie.common.util
 *   Date Created: 2018-09-16
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018-09-16      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.base.util;


import com.gloryjie.pay.base.annotation.IgnoreCovertProperty;
import com.gloryjie.pay.base.enums.error.CommonErrorEnum;
import com.gloryjie.pay.base.exception.error.SystemException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanMap;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * model和dto之间的属性拷贝, 使用Spring BeanUtils
 * 若需要提升性能, 可考虑使用Cglib 的beanCopier
 *
 * @author Jie
 * @since 0.1
 */
@Slf4j
public class BeanConverter {

    /**
     * 转换
     *
     * @param src        数据源
     * @param targetType 目标类型
     * @return 目标类型实例
     */
    public static <T> T covert(@NonNull Object src, @NonNull Class<T> targetType) {
        try {
            T target = targetType.newInstance();
            BeanUtils.copyProperties(src, target);
            return target;
        } catch (Exception e) {
            log.error("covert src={} to target={} failed, error message={} ", src, targetType, e.getMessage());
            throw SystemException.create(CommonErrorEnum.INTERNAL_SYSTEM_ERROR);
        }
    }


    /**
     * 转换
     *
     * @param src              数据源
     * @param targetType       目标类型
     * @param ignoreProperties 需要忽略的属性
     * @return 目标类型实例
     */
    public static <T> T covertIgnore(@NonNull Object src, @NonNull Class<T> targetType, String[] ignoreProperties) {
        try {
            T target = targetType.newInstance();
            BeanUtils.copyProperties(src, target, ignoreProperties);
            return target;
        } catch (Exception e) {
            log.error("covertIgnore src={} to target={} failed,ignoreProperties={}", src, targetType.getSimpleName(),
                    ignoreProperties, e.getMessage());
            throw SystemException.create(CommonErrorEnum.INTERNAL_SYSTEM_ERROR);
        }
    }

    /**
     * 转换, 使用注解的方式忽略指定字段
     *
     * @param src        数据源
     * @param targetType 目标类型
     * @return 目标类型实例
     */
    public static <T> T covertIgnore(@NonNull Object src, @NonNull Class<T> targetType) {
        List<String> ignoreProperties = new ArrayList<>();
        Field[] fields = targetType.getDeclaredFields();
        // 判断字段是否有 @IgnoreCovertProperty 注解
        for (Field field : fields) {
            if (field.isAnnotationPresent(IgnoreCovertProperty.class)) {
                ignoreProperties.add(field.getName());
            }
        }
        return covertIgnore(src, targetType, ignoreProperties.toArray(new String[0]));
    }


    /**
     * 批量转换
     *
     * @param srcList    数据源集合
     * @param targetType 目标类型
     * @return 目标实例集合
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> batchCovert(@NonNull List<?> srcList, @NonNull Class<T> targetType) {
        try {
            T targetObject;
            List<T> targetList = srcList.getClass().newInstance();
            for (Object src : srcList) {
                targetObject = covert(src, targetType);
                targetList.add(targetObject);
            }
            return targetList;
        } catch (Exception e) {
            log.error("batchCovert srcList={} to target={} failed", srcList, targetType, e);
            throw SystemException.create(CommonErrorEnum.INTERNAL_SYSTEM_ERROR);
        }
    }

    /**
     * 批量转换
     *
     * @param srcList    数据源集合
     * @param targetType 目标类型
     * @return 目标实例集合
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> batchCovertIgnore(@NonNull List<?> srcList, @NonNull Class<T> targetType) {
        try {
            T targetObject;
            List<T> targetList = srcList.getClass().newInstance();
            for (Object src : srcList) {
                targetObject = covertIgnore(src, targetType);
                targetList.add(targetObject);
            }
            return targetList;
        } catch (Exception e) {
            log.error("batchCovert srcList={} to target={} failed", srcList, targetType, e);
            throw SystemException.create(CommonErrorEnum.INTERNAL_SYSTEM_ERROR);
        }
    }

    /**
     * map转bean
     *
     * @param src    数据源
     * @param tClass 目的bean的class类型
     * @param <T>
     * @return
     */
    public static <T> T mapToBean(@NonNull Map<String, Object> src, @NonNull Class<T> tClass) {
        try {
            T object = tClass.newInstance();
            BeanMap beanMap = BeanMap.create(object);
            beanMap.putAll(src);
            return object;
        } catch (Exception e) {
            log.error("map to bean error map={}, bean={}", src, tClass.getSimpleName(), e);
            throw SystemException.create(CommonErrorEnum.INTERNAL_SYSTEM_ERROR);
        }
    }

    public static <T> Map<String, Object> beanToMap(@NonNull T src) {
        Map<String, Object> map = new HashMap<>(16);
        BeanMap beanMap = BeanMap.create(src);
        for (Object key : beanMap.keySet()) {
            map.put(key + "", beanMap.get(key));
        }
        return map;
    }
}
