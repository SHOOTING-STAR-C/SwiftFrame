package com.star.swiftencrypt.service;


import java.security.KeyPair;

/**
 * 加密服务（整合AES和RSA）
 *
 * @author SHOOTING_STAR_C
 */
public interface CryptoService {
    /**
     * 使用AES加密数据
     */
    String encryptWithAES(String data);

    /**
     * 使用AES解密数据
     */
    String decryptWithAES(String encryptedData);

    /**
     * 使用RSA加密数据
     */
    String encryptWithRSA(String data);

    /**
     * 使用RSA解密数据
     */
    String decryptWithRSA(String encryptedData);

    /**
     * 混合加密：使用RSA加密AES密钥 + AES加密数据
     *
     * @return 格式：RSA(AES密钥) + "|" + AES(数据)
     */
    String hybridEncrypt(String data);

    /**
     * 混合解密
     */
    String hybridDecrypt(String encrypted);

    /**
     * 生成RSA密钥对（Base64编码）
     */
    KeyPair generateRSAKeyPair() throws Exception;


    /**
     * 生成并设置新的RSA密钥对
     *
     * @throws Exception
     */
    void generateAndSetNewRsaKeys() throws Exception;

    /**
     * 判断加密模式
     *
     * @return boolean
     */
    boolean isRsaModeEnabled();
}
