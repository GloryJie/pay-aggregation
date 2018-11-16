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
import com.gloryjie.pay.base.exception.error.SystemErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * model和dto之间的属性拷贝, 使用Spring BeanUtils 若需要提升性能, 可考虑使用Cglib 的eanCopier
 *
 * @author Jie
 * @since 0.1
 */
@Slf4j
public class BeanConverter {

  /**
   * 转换
   *
   * @param src 数据源
   * @param targetType 目标类型
   * @return 目标类型实例
   */
  public static <T> T covert(Object src, Class<T> targetType) {
    T target = null;
    try {
      target = targetType.newInstance();
      BeanUtils.copyProperties(src, target);
    } catch (Exception e) {
      log.error(
          "COMMON-MODULE[]BeanConverter.covert()[] covert src={} to target={} failed, error message={} ",
          src, target, e.getMessage());
      throw SystemErrorException.create(CommonErrorEnum.INTERNAL_SYSTEM_ERROR);
    }
    return target;
  }


  /**
   * 转换
   *
   * @param src 数据源
   * @param targetType 目标类型
   * @param ignoreProperties 需要忽略的属性
   * @return 目标类型实例
   */
  public static <T> T covertIgnore(Object src, Class<T> targetType, String[] ignoreProperties) {
    T target = null;
    try {
      target = targetType.newInstance();
      BeanUtils.copyProperties(src, target, ignoreProperties);
    } catch (Exception e) {
      log.error(
          "COMMON-MODULE[]BeanConverter.covertIgnore()[] covert src={} to target={} failed",
          src, target, e.getMessage());
      throw SystemErrorException.create(CommonErrorEnum.INTERNAL_SYSTEM_ERROR);
    }
    return target;
  }

  /**
   * 转换, 使用注解的方式忽略指定字段
   *
   * @param src 数据源
   * @param targetType 目标类型
   * @return 目标类型实例
   */
  public static <T> T covertIgnore(Object src, Class<T> targetType) {
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
   * @param srcList 数据源集合
   * @param targetType 目标类型
   * @return 目标实例集合
   */
  @SuppressWarnings("unchecked")
  public static <T> List<T> batchCovert(List<?> srcList, Class<T> targetType) {
    T targetObject;
    List<T> targetList;
    try {
      targetList = srcList.getClass().newInstance();
      for (Object src : srcList) {
        targetObject = covert(src, targetType);
        targetList.add(targetObject);
      }
    } catch (Exception e) {
      log.error(
          "COMMON-MODULE[]BeanConverter.batchCovert()[] covert src={} to target={} failed",
          srcList, targetType, e);
      throw SystemErrorException.create(CommonErrorEnum.INTERNAL_SYSTEM_ERROR);
    }
    return targetList;
  }
}
