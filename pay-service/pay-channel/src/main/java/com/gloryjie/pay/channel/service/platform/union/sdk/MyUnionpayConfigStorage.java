package com.gloryjie.pay.channel.service.platform.union.sdk;

import com.egzosn.pay.common.util.sign.CertDescriptor;
import com.egzosn.pay.union.api.UnionPayConfigStorage;

/**
 * 重写配置, 使用自有CertDescriptor, 以支持直接传入数据, 而非一定是文件路径
 * @author jie
 * @since 2019/4/24
 */
public class MyUnionpayConfigStorage extends UnionPayConfigStorage {

    private MyCertDescriptor myCertDescriptor;

    @Override
    public CertDescriptor getCertDescriptor() {
        if (null == myCertDescriptor) {
            myCertDescriptor = new MyCertDescriptor();
        }
        return myCertDescriptor;
    }
}
