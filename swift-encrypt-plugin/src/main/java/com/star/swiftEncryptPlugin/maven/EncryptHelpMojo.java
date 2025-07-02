package com.star.swiftEncryptPlugin.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * 帮助
 *
 * @author SHOOTING_STAR_C
 */
@Mojo(name = "help")
public class EncryptHelpMojo extends AbstractMojo {
    /**
     * 插件主方法
     */
    public void execute() {
        getLog().info("加密插件帮助信息:");
        getLog().info("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        getLog().info("可用指令:");
        getLog().info("  mvn encrypt:generate       - 自动生成RSA&AES密钥，使用RSA将AES密钥加密，最后写入配置文件");
        getLog().info("    可用参数:configFilePath（配置文件路径）、aesPropertyName（aes密钥属性名）aesKeySizePropertyName（aes密钥长度）、、rsaPublicKeyPropertyName（rsa公钥属性名）、rsaPrivateKeyPropertyName（rsa私钥属性名）、rsaKeySizePropertyName（rsa密钥长度）");
        getLog().info("  mvn encrypt:encrypt-config - 将YML配置文件中DEC()包裹的内容加密");
        getLog().info("    可用参数:configFilePath（配置文件路径）、aesPropertyName（aes密钥属性名）、rsaPublicKeyPropertyName（rsa公钥属性名）、rsaPrivateKeyPropertyName（rsa私钥属性名）");
        getLog().info("  mvn encrypt:decrypt-config - 将YML配置文件中ENC()包裹的内容解密");
        getLog().info("    可用参数:configFilePath（配置文件路径）、aesPropertyName（aes密钥属性名）、rsaPublicKeyPropertyName（rsa公钥属性名）、rsaPrivateKeyPropertyName（rsa私钥属性名）");
        getLog().info("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    }
}
