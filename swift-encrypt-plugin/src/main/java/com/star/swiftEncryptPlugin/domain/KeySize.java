package com.star.swiftEncryptPlugin.domain;

import lombok.Data;

/**
 * 密码长度
 *
 * @author SHOOTING_STAR_C
 */
@Data
public class KeySize {
    /**
     * 密钥长度（位）
     * 可选值：128, 192, 256
     */
    private Integer aesKeySize = 256;

    /**
     * RSA密钥长度
     */
    private Integer rsaKeySize = 2048;
}
