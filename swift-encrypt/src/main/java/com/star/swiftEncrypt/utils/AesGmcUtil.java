package com.star.swiftEncrypt.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES 对称加密工具（GCM）
 *
 * @author SHOOTING_STAR_C
 */
public class AesGmcUtil {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;   // 12字节初始化向量（推荐长度）

    /**
     * 加密
     *
     * @param data 明文
     * @param key  密钥
     * @return 密文
     * @throws Exception Exception
     */
    public static String encrypt(String data, String key, int size) throws Exception {
        // 生成随机IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);

        // 创建密钥规范
        SecretKey secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);

        // 初始化Cipher
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(size, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

        // 加密数据
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

        // 组合 IV + 密文 (IV不加密)
        byte[] combined = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    /**
     * 解密
     *
     * @param encryptedData 密文
     * @param key           密钥
     * @return 明文
     * @throws Exception Exception
     */
    public static String decrypt(String encryptedData, String key, int size) throws Exception {
        // 解码Base64
        byte[] combined = Base64.getDecoder().decode(encryptedData);

        // 分离 IV 和实际密文
        byte[] iv = new byte[GCM_IV_LENGTH];
        byte[] encryptedBytes = new byte[combined.length - GCM_IV_LENGTH];
        System.arraycopy(combined, 0, iv, 0, iv.length);
        System.arraycopy(combined, iv.length, encryptedBytes, 0, encryptedBytes.length);

        // 创建密钥规范
        SecretKey secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);

        // 初始化Cipher
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(size, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

        // 解密数据
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * 生成AES密钥
     *
     * @return SecretKey
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     */
    public static SecretKey generateAesKey(int aesKeySize) throws NoSuchAlgorithmException {
        validateKeySize(aesKeySize);
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(aesKeySize, SecureRandom.getInstanceStrong());
        return keyGenerator.generateKey();
    }

    /**
     * 校验密钥长度是否合规
     *
     * @param keySize keySize
     */
    private static void validateKeySize(int keySize) {
        if (keySize != 128 && keySize != 192 && keySize != 256) {
            throw new IllegalArgumentException("无效的密钥长度: " + keySize +
                    ". 支持的长度: 128, 192, 256");
        }
    }
}
