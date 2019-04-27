package com.gloryjie.pay.app.constant;

/**
 * @author jie
 * @since 2019/4/26
 */
public class AppConstant {

    /**
     * 第一个APPID
     * 前三位为master应用标识，101为第一个
     * 第四位为层次，1标识第一层
     * 后四位子应用id, 0000表示没有一个子应用
     */
    public static final int FIRST_APP_ID = 10110000;

    /**
     * 创建master应用步长
     */
    public static final int MASTER_APP_STEP = 100000;

    /**
     * 子应用最大深度
     */
    public static final int MAX_LEVEL = 5;

    public static final int FIRST_SUB_APP_ID_STEP = 20001;

    /**
     * 一层子应用的最大数量
     */
    public static final int MAX_SUB_APP_ID_STEP = 9999;
}
