/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.channel.enums
 *   Date Created: 2018/11/24
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/11/24      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.channel.enums;

import com.gloryjie.pay.base.exception.error.BusinessException;
import com.gloryjie.pay.channel.error.ChannelError;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author Jie
 * @since
 */
@Getter
public enum ChannelType {

    /**
     * 支付渠道
     */
    ALIPAY_PAGE(10, "GATEWAY", "FAST_INSTANT_TRADE_PAY", "支付宝网页支付"),
    ALIPAY_WAP(20, "GATEWAY", "QUICK_WAP_WAY", "支付宝手机网站支付"),
    ALIPAY_SCAN_CODE(30, "GATEWAY", "", "支付宝扫码支付"),
    ALIPAY_BAR_CODE(30, "NON_GATEWAY", "FACE_TO_FACE_PAYMENT", "支付宝条码支付");

    /**
     * 10~19为支付宝, 20~29为微信, 30~39为银联
     */
    private int code;
    /**
     * 支付类型,网关以及非网关
     */
    private String type;
    /**
     * 产品销售码
     */
    private String productCode;
    /**
     * 中文描述
     */
    private String desc;

    ChannelType(int code, String type, String productCode, String desc) {
        this.code = code;
        this.type = type;
        this.productCode = productCode;
        this.desc = desc;
    }


    public boolean isGateway(){
        return this.type.equals("GATEWAY");
    }

    /**
     * 检查渠道额外参数
     *
     * @param extra
     */
    public void checkExtraParam(Map<String, String> extra) {
        boolean checkResult = true;
        switch (this) {
            case ALIPAY_PAGE:
            case ALIPAY_WAP:
                if (extra !=null && StringUtils.isBlank(extra.get("returnUrl"))){
                    checkResult = false;
                }
                break;
            default:
                break;
        }
        if (!checkResult){
            throw BusinessException.create(ChannelError.EXTRA_NOT_CORRECT);
        }
    }

}
