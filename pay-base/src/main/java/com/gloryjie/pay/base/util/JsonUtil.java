/* ------------------------------------------------------------------
 *   Product:      music-dj
 *   Module Name:  common
 *   Package Name: com.gloryjie.common.util
 *   Date Created: 2018-09-12
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018-09-12      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.base.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gloryjie.pay.base.enums.error.CommonErrorEnum;
import com.gloryjie.pay.base.exception.error.SystemErrorException;
import com.sun.istack.internal.NotNull;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Json序列化和反序列化工具, 使用Jackson
 *
 * @author Jie
 * @since 0.1
 */
@Slf4j
public class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        // 序列化所有字段, 详细的序列化控制使用注解
        OBJECT_MAPPER.setSerializationInclusion(Include.ALWAYS);
        // 不将时间转换成时间戳
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        // 忽略bean为null的情况
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        // 忽略json数据中存在但Java类中不存在的属性
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 解决Java8时间序列化问题
        OBJECT_MAPPER.registerModules(new JavaTimeModule());
    }


    /**
     * 将对象转换为Json字符串
     *
     * @param object 带转换对象
     * @param <T>    对象类型
     * @return json字符串, 若object为null, 则返回 "null" 字符串
     */
    public static <T> String toJson(@NonNull T object) {
        try {
            return object instanceof String ? (String) object : OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("serialize object={} to json fail", object, e);
            throw SystemErrorException.create(CommonErrorEnum.INTERNAL_SYSTEM_ERROR, e);
        }
    }


    /**
     * 简单的Json反序列化
     *
     * @param src   json字符串
     * @param clazz 对象类型
     * @return T的实例对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T parse(@NonNull String src, @NonNull Class<T> clazz) {
        try {
            return clazz.equals(String.class) ? (T) src : OBJECT_MAPPER.readValue(src, clazz);
        } catch (Exception e) {
            log.error("deserialization src={} to object={} fail", src,
                    clazz.getSimpleName(), e);
            throw SystemErrorException.create(CommonErrorEnum.INTERNAL_SYSTEM_ERROR, e);
        }
    }

    /**
     * 复杂Json反序列化
     *
     * @param src           json字符串
     * @param typeReference 对象类型
     * @return 指定的复杂对象T
     */
    @SuppressWarnings("unchecked")
    public static <T> T parse(@NonNull String src, @NonNull TypeReference<T> typeReference) {
        try {
            return typeReference.getType().equals(String.class) ? (T) src : OBJECT_MAPPER.readValue(src, typeReference);
        } catch (Exception e) {
            log.error("deserialization src={} to object={} fail", src, typeReference.getType().getTypeName(), e);
            throw SystemErrorException.create(CommonErrorEnum.INTERNAL_SYSTEM_ERROR, e);
        }
    }

    /**
     * 更加复杂的转换
     *
     * @param src          json字符串
     * @param collections  集合类型
     * @param elementClass 元素类型
     * @return 指定的返回类型
     */
    public static <T> T parse(@NonNull String src, @NonNull Class<?> collections, Class<?>... elementClass) {
        try {
            JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(collections, elementClass);
            return OBJECT_MAPPER.readValue(src, javaType);
        } catch (IOException e) {
            log.error("deserialization src={} to complex collection={} element={} fail", src, collections.getSimpleName(), elementClass, e);
            throw SystemErrorException.create(CommonErrorEnum.INTERNAL_SYSTEM_ERROR, e);
        }
    }
}
