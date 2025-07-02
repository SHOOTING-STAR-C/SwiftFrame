package com.star.swiftEncrypt.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.ByteArrayOutputStream;
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

        // 使用分段加密方法
        byte[] encryptedBytes = encryptInBlocks(bytes, cipher);

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * 私钥解密（修复版）
     *
     * @param encryptedData 密文（Base64编码）
     * @param privateKey    私钥
     * @return 明文
     * @throws Exception Exception
     */
    public static String decrypt(String encryptedData, String privateKey) throws Exception {
        byte[] data = Base64.getDecoder().decode(encryptedData);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(privateKey));
        // 分段解密
        return new String(decryptInBlocks(data, cipher), StandardCharsets.UTF_8);
    }

    /**
     * 分段加密实现
     *
     * @param data   原始数据字节数组
     * @param cipher 已初始化的Cipher对象
     * @return 加密后的字节数组
     */
    private static byte[] encryptInBlocks(byte[] data, Cipher cipher)
            throws IllegalBlockSizeException, BadPaddingException {
        int inputLen = data.length;
        return getBytes(data, cipher, inputLen, RsaUtil.MAX_ENCRYPT_BLOCK);
    }

    /**
     * 分段解密实现
     *
     * @param encryptedData 加密数据字节数组
     * @param cipher        已初始化的Cipher对象
     * @return 解密后的字节数组
     */
    private static byte[] decryptInBlocks(byte[] encryptedData, Cipher cipher)
            throws IllegalBlockSizeException, BadPaddingException {
        int inputLen = encryptedData.length;
        int blockSize = cipher.getBlockSize(); // 获取实际块大小
        int maxBlockSize = (blockSize > 0) ? blockSize : MAX_DECRYPT_BLOCK;

        return getBytes(encryptedData, cipher, inputLen, maxBlockSize);
    }

    /**
     * 提取Bytes
     * @param encryptedData encryptedData
     * @param cipher cipher
     * @param inputLen inputLen
     * @param maxBlockSize maxBlockSize
     * @return byte[]
     * @throws IllegalBlockSizeException IllegalBlockSizeException
     * @throws BadPaddingException BadPaddingException
     */
    private static byte[] getBytes(byte[] encryptedData, Cipher cipher, int inputLen, int maxBlockSize) throws IllegalBlockSizeException, BadPaddingException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int offset = 0;

        while (inputLen - offset > 0) {
            int len = Math.min(inputLen - offset, maxBlockSize);
            byte[] decryptedBlock = cipher.doFinal(encryptedData, offset, len);
            outputStream.write(decryptedBlock, 0, decryptedBlock.length);
            offset += len;
        }

        return outputStream.toByteArray();
    }
}
