package com.gloryjie.pay.channel.enums;

import com.gloryjie.pay.base.enums.base.BaseEnum;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * @author jie
 * @since 2019/4/23
 */
@Getter
public enum CertificateType implements BaseEnum {

    /**
     * 支付平台证书类型
     */
    UNIONPAY_SIGN_CERT(0, PlatformType.UNIONPAY, "签名证书", "application/x-pkcs12"),
    UNIONPAY_ROOT_CERT(1, PlatformType.UNIONPAY, "银联根证书", "application/x-x509-ca-cert"),
    UNIONPAY_MIDDLE_CERT(2, PlatformType.UNIONPAY, "银联中级证书", "application/x-x509-ca-cert");

    private int code;

    private PlatformType platformType;

    private String desc;

    private String fileSuffix;

    CertificateType(int code, PlatformType platformType, String desc, String fileSuffix) {
        this.code = code;
        this.platformType = platformType;
        this.desc = desc;
        this.fileSuffix = fileSuffix;
    }

    public static List<CertificateType> getUnionpayAllCertType() {
        return Arrays.asList(UNIONPAY_SIGN_CERT, UNIONPAY_MIDDLE_CERT, UNIONPAY_ROOT_CERT);
    }
}
