package com.star.swiftEncryptPlugin.maven;

import com.star.swiftEncrypt.utils.AesGmcUtil;
import com.star.swiftEncrypt.utils.RsaUtil;
import com.star.swiftEncryptPlugin.domain.KeySize;
import com.star.swiftEncryptPlugin.utils.EncryptPluginUtil;
import lombok.Setter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import javax.crypto.SecretKey;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.util.*;

/**
 * Maven插件：密钥生成器
 *
 * @author SHOOTING_STAR_C
 */
@Mojo(name = "generate", requiresProject = false)
@Setter
public class KeyGeneratorMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    private KeySize keySize = new KeySize();

    /**
     * 输出格式
     * 可选值：yml, yaml
     */
    @Parameter(property = "format", defaultValue = "yml")
    private String format;

    /**
     * 输出文件路径（可选）
     */
    @Parameter(property = "configFilePath", defaultValue = "${project.basedir}/src/main/resources/application-${app.env}.yml")
    private String configFilePath;

    /**
     * aes密钥属性名
     */
    @Parameter(property = "aesPropertyName", defaultValue = "crypto.aes.key")
    private String aesPropertyName;
    /**
     * aes密钥长度
     */
    @Parameter(property = "aesKeySizePropertyName", defaultValue = "crypto.aes.size")
    private String aesKeySizePropertyName;
    /**
     * rsa公钥属性名
     */
    @Parameter(property = "rsaPublicKeyPropertyName", defaultValue = "crypto.rsa.public-key")
    private String rsaPublicKeyPropertyName;
    /**
     * rsa私钥属性名
     */
    @Parameter(property = "rsaPrivateKeyPropertyName", defaultValue = "crypto.rsa.private-key")
    private String rsaPrivateKeyPropertyName;
    /**
     * rsa密钥长度
     */
    @Parameter(property = "rsaKeySizePropertyName", defaultValue = "crypto.rsa.size")
    private String rsaKeySizePropertyName;

    /**
     * 插件主方法
     *
     * @throws MojoExecutionException MojoExecutionException
     */
    @Override
    public void execute() throws MojoExecutionException {
        try {
            // 检查配置文件是否已存在所有密钥
            if (configFilePath != null && !configFilePath.isEmpty() &&
                    (format.equalsIgnoreCase("yaml") || format.equalsIgnoreCase("yml"))) {
                Path path = Paths.get(configFilePath);
                if (Files.exists(path)) {
                    EncryptPluginUtil.getKeysSize(project, aesKeySizePropertyName, rsaKeySizePropertyName, keySize);
                    List<String> keys = Arrays.asList(
                            aesPropertyName,
                            rsaPublicKeyPropertyName,
                            rsaPrivateKeyPropertyName
                    );
                    if (EncryptPluginUtil.keysExistInYaml(path, keys)) {
                        getLog().info("配置文件中已存在所有密钥，跳过生成。");
                    } else {
                        //生成密钥
                        generateKeys();
                    }
                }
            }
        } catch (NoSuchAlgorithmException e) {
            throw new MojoExecutionException("AES/RSA algorithm not supported", e);
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to generate keys", e);
        }
    }

    /**
     * 生成RSA、AES密钥
     *
     * @throws Exception Exception
     */
    private void generateKeys() throws Exception {
        // 生成RSA公钥、私钥
        KeyPair keyPair = RsaUtil.generateKeyPair(keySize.getRsaKeySize());
        PrivateKey aPrivate = keyPair.getPrivate();
        PublicKey aPublic = keyPair.getPublic();

        byte[] aPrivateKeyBytes = aPrivate.getEncoded();
        byte[] aPublicKeyBytes = aPublic.getEncoded();

        String rsaPrivateKeyStr = EncryptPluginUtil.formatOutput(aPrivateKeyBytes);
        String rsaPublicKeyStr = EncryptPluginUtil.formatOutput(aPublicKeyBytes);

        // 生成AES密钥
        SecretKey secretKey = AesGmcUtil.generateAesKey(keySize.getAesKeySize());
        byte[] keyBytes = secretKey.getEncoded();

        // 格式化输出
        String aesKeyStr = EncryptPluginUtil.formatOutput(keyBytes);
        String aesKeyEncStr = RsaUtil.encrypt(aesKeyStr, rsaPublicKeyStr);

        // 写入文件（如果指定）
        if (configFilePath != null && !configFilePath.isEmpty()) {
            getLog().info("尝试将密钥写入配置文件");
            EncryptPluginUtil.writeToFile(aesPropertyName + ": " + aesKeyEncStr, format, configFilePath);
            EncryptPluginUtil.writeToFile(rsaPublicKeyPropertyName + ": " + rsaPublicKeyStr, format, configFilePath);
            EncryptPluginUtil.writeToFile(rsaPrivateKeyPropertyName + ": " + rsaPrivateKeyStr, format, configFilePath);
        } else {
            getLog().info(aesPropertyName + ": " + aesKeyEncStr);
            getLog().info(rsaPublicKeyPropertyName + ": " + rsaPublicKeyStr);
            getLog().info(rsaPrivateKeyPropertyName + ": " + rsaPrivateKeyStr);
        }
    }


}
