/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.base.handler
 *   Date Created: 2019/3/6
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/3/6      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.base.handler;

import com.gloryjie.pay.base.constant.HttpStatus;
import com.gloryjie.pay.base.enums.error.CommonErrorEnum;
import com.gloryjie.pay.base.exception.BaseException;
import com.gloryjie.pay.base.exception.error.ExternalException;
import com.gloryjie.pay.base.response.Response;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;

/**
 * 全局异常处理
 *
 * @author Jie
 * @since
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = BaseException.class)
    @ResponseBody
    public Response handleCustomError(HttpServletResponse response, BaseException e) {
        Response responseResult = Response.failure(e);
        // 修改成对应的错误响应码
        response.setStatus(e.getErrorEnum().getCode());
        return responseResult;
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public Response handleMethodArgumentError(HttpServletResponse response, MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String fieldErrMsg = "";
        if (fieldError != null) {
            fieldErrMsg = fieldError.getDefaultMessage();
        }
        e.getBindingResult().getFieldError().getDefaultMessage();
        Response responseResult = Response.failure(ExternalException.create(CommonErrorEnum.ILLEGAL_ARGUMENT_ERROR, fieldErrMsg));
        // 修改成对应的错误响应码
        response.setStatus(HttpStatus.BAD_REQUEST);
        return responseResult;
    }


}
