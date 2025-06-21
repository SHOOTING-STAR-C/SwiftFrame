package com.star.swiftencrypt.utils;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA 非对称加密工具
 *
 * @author SHOOTING_STAR_C
 */
public class RSAUtil {
    private static final String ALGORITHM = "RSA";

    /**
     * 生成RSA密钥对
     *
     * @return 密钥对
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     */
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    /**
     * 获取公钥
     *
     * @param base64PublicKey base64编码
     * @return 公钥
     * @throws Exception Exception
     */
    private static PublicKey getPublicKey(String base64PublicKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 获取密钥
     *
     * @param base64PrivateKey base64编码
     * @return 密钥
     * @throws Exception Exception
     */
    private static PrivateKey getPrivateKey(String base64PrivateKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 加密
     *
     * @param data 明文
     * @param publicKey 公钥
     * @return 密文
     * @throws Exception Exception
     */
    public static String encrypt(String data, String publicKey) throws Exception {
        PublicKey key = getPublicKey(publicKey);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * 解密
     *
     * @param encryptedData 密文
     * @param privateKey 私钥
     * @return 明文
     * @throws Exception Exception
     */
    public static String decrypt(String encryptedData, String privateKey) throws Exception {
        PrivateKey key = getPrivateKey(privateKey);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decoded = Base64.getDecoder().decode(encryptedData);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
