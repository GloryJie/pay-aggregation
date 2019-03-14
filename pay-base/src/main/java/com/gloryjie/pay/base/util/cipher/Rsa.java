/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.base.util.cipher
 *   Date Created: 2019/3/6
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/3/6      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.base.util.cipher;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * RSA 工具类
 *
 * @author Jie
 * @since
 */
@Slf4j
public class Rsa {

    private static final String SIGN_ALGORITHM = "SHA256withRSA";

    private static final String KEY_ALGORITHM = "RSA";

    private static final int KEY_SIZE = 2048;

    public static final String PUBLIC_KEY = "publicKey";

    public static final String PRIVATE_KEY = "privateKey";


    /**
     * 签名
     *
     * @param data
     * @param privateKey
     * @return
     * @throws SignatureException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     */
    public static String sign(@NonNull byte[] data, @NonNull String privateKey) throws SignatureException,
            NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        // 解码私钥
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);

        // 获取key实例
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey pk = keyFactory.generatePrivate(keySpec);
        // 签名
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initSign(pk);
        signature.update(data);
        return Base64.getEncoder().encodeToString(signature.sign());
    }

    /**
     * 验证签名
     *
     * @param data
     * @param publicKey
     * @param sign
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    public static boolean verifySign(@NonNull byte[] data, @NonNull String publicKey, String sign) throws
            NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        // 解码私钥
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        // 获取key实例
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey pk = keyFactory.generatePublic(keySpec);
        // 验证签名
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initVerify(pk);
        signature.update(data);

        return signature.verify(Base64.getDecoder().decode(sign));
    }

    /**
     * 生成公私钥对
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static Map<String, String> generateRsaKeyPair() {
        KeyPairGenerator generator = null;
        try {
            generator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            // 当前异常不会发生
            e.printStackTrace();
        }
        generator.initialize(KEY_SIZE);
        KeyPair keyPair = generator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        Map<String, String> param = new HashMap<>(4);
        param.put(PUBLIC_KEY, Base64.getEncoder().encodeToString(publicKey.getEncoded()));
        param.put(PRIVATE_KEY, Base64.getEncoder().encodeToString(privateKey.getEncoded()));

        return param;
    }
}
