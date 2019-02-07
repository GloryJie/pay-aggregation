/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.base.util.validator
 *   Date Created: 2019/2/7
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/2/7      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.base.util.validator;

import com.gloryjie.pay.base.enums.error.CommonErrorEnum;
import com.gloryjie.pay.base.exception.error.SystemException;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * 参数校验器
 *
 * @author Jie
 * @since 0.1
 */
@Slf4j
public class ParamValidator {

    private static Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        log.info("PAY-BASE[] init param validation success");
    }

    public static void validate(Object param) {
        Set<ConstraintViolation<Object>> validResult = validator.validate(param);
        if (validResult != null && validResult.size() > 0) {
            ConstraintViolation constraintViolation = validResult.iterator().next();
            throw SystemException.create(CommonErrorEnum.ILLEGAL_ARGUMENT_ERROR, constraintViolation.getPropertyPath() + constraintViolation.getMessage());
        }
    }

}
