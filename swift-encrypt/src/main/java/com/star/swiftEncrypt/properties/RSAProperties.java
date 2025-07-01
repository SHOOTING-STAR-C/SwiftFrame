package com.star.swiftEncrypt.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * RSA配置文件
 *
 * @author SHOOTING_STAR_C
 */
@Getter
@Setter
public class RSAProperties {
    private int size;//公钥
    private String publicKey;//公钥
    private String privateKey;//私钥
}
