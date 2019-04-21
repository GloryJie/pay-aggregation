/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.base.util
 *   Date Created: 2019/1/16
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/1/16      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.base.util;

import lombok.NonNull;

import java.math.BigDecimal;

/**
 * 金额工具类,Long和string之间的转换
 *
 * @author Jie
 * @since 0.1
 */
public class AmountUtil {

    public static final String ZERO_POINT = "0.";

    public static final String ZERO_POINT_ZERO = "0.0";


    public static final String POINT = ".";


    public static String amountToStr(@NonNull Long amount) {
        String temp = amount.toString();
        if (amount < 10) {
            return ZERO_POINT_ZERO + temp;
        } else if (amount < 100) {
            return ZERO_POINT + temp;
        } else {
            return temp.substring(0, temp.length() - 2) + POINT + temp.substring(temp.length() - 2);
        }
    }

    public static Long strToAmount(String amount) {
        BigDecimal decimal = new BigDecimal(amount);
        return decimal.multiply(new BigDecimal(100)).longValue();
    }

    public static BigDecimal longToBigDecimal(Long amount){
        BigDecimal decimal = new BigDecimal(amount);
        return decimal.divide(new BigDecimal(100L), 2, BigDecimal.ROUND_HALF_DOWN);
    }
}
