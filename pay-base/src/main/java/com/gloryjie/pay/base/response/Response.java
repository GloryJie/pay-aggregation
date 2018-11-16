/* ------------------------------------------------------------------
 *   Product:      music-dj
 *   Module Name:  com.gloryjie.common.response
 *   Package Name: com.gloryjie.common.response
 *   Date Created: 2018-09-10
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018-09-10      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.base.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.gloryjie.pay.base.constant.HttpStatus;
import com.gloryjie.pay.base.exception.ErrorInterface;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一的响应对象
 *
 * @author Jie
 * @since 0.1
 */
@Data
@JsonInclude(Include.NON_NULL)
public class Response<T> implements Serializable {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 消息描述
     */
    private String message;

    /**
     * 状态码, 基于HttpStatus
     */
    private String status;

    /**
     * 传递的数据
     */
    private T data;

    private Response(ErrorInterface error) {
        this.success = false;
        this.status = error.getStatus();
        this.message = error.getMessage();
    }

    private Response(T data) {
        this.success = true;
        this.status = String.valueOf(HttpStatus.SUCCESS);
        this.data = data;
    }

    private Response() {
        this.success = true;
        this.status = String.valueOf(HttpStatus.SUCCESS);
    }

    /**
     * 创建成功响应,无数据
     *
     * @return response实例
     */
    public static Response success() {
        return new Response();
    }

    /**
     * 创建成功响应,附带数据
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return response实例
     */
    public static <T> Response<T> success(T data) {
        return new Response<>(data);
    }

    /**
     * 失败响应
     *
     * @param error 错误
     * @return response实例
     */
    public static Response failure(ErrorInterface error) {
        return new Response(error);
    }


}
