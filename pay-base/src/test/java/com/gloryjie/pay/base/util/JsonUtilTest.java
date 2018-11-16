package com.gloryjie.pay.base.util;

import com.gloryjie.pay.base.enums.error.CommonErrorEnum;
import com.gloryjie.pay.base.exception.error.SystemErrorException;
import com.gloryjie.pay.base.response.Response;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Jie
 * @since
 */
public class JsonUtilTest {

    @Test
    public void toJson() {
        String target = "{\"success\":false,\"message\":\"系统内部异常\",\"status\":\"500500\"}";
        String json = JsonUtil.toJson(Response.failure(SystemErrorException.create(CommonErrorEnum.INTERNAL_SYSTEM_ERROR)));
        assertEquals(json,target);
    }

    @Test
    public void parse() {
        Response target =Response.failure(SystemErrorException.create(CommonErrorEnum.INTERNAL_SYSTEM_ERROR));
        String source = "{\"success\":false,\"message\":\"系统内部异常\",\"status\":\"500500\"}";
        Response response = JsonUtil.parse(source,Response.class);
        assertEquals(response,target);
    }
}