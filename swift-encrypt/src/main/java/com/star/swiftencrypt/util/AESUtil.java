package com.star.swiftencrypt.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES 对称加密工具（GCM）
 *
 * @author SHOOTING_STAR_C
 */
public class AESUtil {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128; // 128位认证标签
    private static final int GCM_IV_LENGTH = 12;   // 12字节初始化向量（推荐长度）

    /**
     * 加密
     *
     * @param data 明文
     * @param key  密钥
     * @return 密文
     * @throws Exception
     */
    public static String encrypt(String data, String key) throws Exception {
        // 生成随机IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);

        // 创建密钥规范
        SecretKey secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);

        // 初始化Cipher
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
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
     * @throws Exception
     */
    public static String decrypt(String encryptedData, String key) throws Exception {
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
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

        // 解密数据
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
