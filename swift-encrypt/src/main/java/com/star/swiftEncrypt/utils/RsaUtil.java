package com.star.swiftEncrypt.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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
public class RsaUtil {
    private static final String ALGORITHM = "RSA";
    private static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    private static final int MAX_ENCRYPT_BLOCK = 245;   // 2048位密钥最大加密块
    private static final int MAX_DECRYPT_BLOCK = 256;    // 2048位密钥最大解密块

    /**
     * 生成RSA密钥对
     *
     * @return 密钥对
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     */
    public static KeyPair generateKeyPair(int rsaKeySize) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGen.initialize(rsaKeySize, new SecureRandom());
        return keyPairGen.generateKeyPair();
    }

    /**
     * 获取公钥
     *
     * @param base64PublicKey base64编码
     * @return 公钥
     * @throws Exception Exception
     */
    public static PublicKey getPublicKey(String base64PublicKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 获取私钥
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
     * 公钥加密
     *
     * @param data      明文
     * @param publicKey 公钥
     * @return 密文
     * @throws Exception Exception
     */
    public static String encrypt(String data, String publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);

        // 分段加密
        PKCS1Padding(bytes, cipher, MAX_ENCRYPT_BLOCK);

        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 私钥解密
     *
     * @param encryptedData 密文
     * @param privateKey    私钥
     * @return 明文
     * @throws Exception Exception
     */
    public static String decrypt(String encryptedData, String privateKey) throws Exception {
        byte[] data = Base64.getDecoder().decode(encryptedData);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(privateKey));

        // 分段解密
        PKCS1Padding(data, cipher, MAX_DECRYPT_BLOCK);
        return Base64.getEncoder().encodeToString(data);
    }

    private static void PKCS1Padding(byte[] data, Cipher cipher, int maxDecryptBlock) throws IllegalBlockSizeException, BadPaddingException {
        int inputLen = data.length;
        int offset = 0;
        byte[] cache;
        byte[] decryptedData = new byte[0];

        while (inputLen - offset > 0) {
            if (inputLen - offset > maxDecryptBlock) {
                cache = cipher.doFinal(data, offset, maxDecryptBlock);
            } else {
                cache = cipher.doFinal(data, offset, inputLen - offset);
            }
            offset += maxDecryptBlock;

            // 合并结果
            byte[] temp = new byte[decryptedData.length + cache.length];
            System.arraycopy(decryptedData, 0, temp, 0, decryptedData.length);
            System.arraycopy(cache, 0, temp, decryptedData.length, cache.length);
            decryptedData = temp;
        }
    }
}
