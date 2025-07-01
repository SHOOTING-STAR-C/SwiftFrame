package com.star.swiftEncryptPlugin.maven;

import com.star.swiftEncrypt.utils.AesGmcUtil;
import com.star.swiftEncrypt.utils.RsaUtil;
import lombok.Setter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import javax.crypto.SecretKey;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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
    /**
     * 密钥长度（位）
     * 可选值：128, 192, 256
     */
    @Parameter(property = "aesKeySize", defaultValue = "256")
    private int aesKeySize;

    /**
     * RSA密钥长度
     */
    @Parameter(property = "rsaKeySize", defaultValue = "2048")
    private int rsaKeySize;

    /**
     * 输出格式
     * 可选值：yml, yaml
     */
    @Parameter(property = "format", defaultValue = "base64")
    private String format;

    /**
     * 输出文件路径（可选）
     */
    @Parameter(property = "configFilePath")
    private String configFilePath;

    /**
     * aes密钥属性名（用于YAML格式）
     */
    @Parameter(property = "aesPropertyName", defaultValue = "crypto.aes.key")
    private String aesPropertyName;
    /**
     * rsa公钥属性名（用于YAML格式）
     */
    @Parameter(property = "rsaPublicKeyPropertyName", defaultValue = "crypto.rsa.public-key")
    private String rsaPublicKeyPropertyName;
    /**
     * rsa私钥属性名（用于YAML格式）
     */
    @Parameter(property = "rsaPrivateKeyPropertyName", defaultValue = "crypto.rsa.private-key")
    private String rsaPrivateKeyPropertyName;

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
                    List<String> keys = Arrays.asList(
                            aesPropertyName,
                            rsaPublicKeyPropertyName,
                            rsaPrivateKeyPropertyName
                    );
                    if (keysExistInYaml(path, keys)) {
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
        KeyPair keyPair = RsaUtil.generateKeyPair(2048);
        PrivateKey aPrivate = keyPair.getPrivate();
        PublicKey aPublic = keyPair.getPublic();

        byte[] aPrivateKeyBytes = aPrivate.getEncoded();
        byte[] aPublicKeyBytes = aPublic.getEncoded();

        String rsaPrivateKeyStr = formatOutput(aPrivateKeyBytes);
        String rsaPublicKeyStr = formatOutput(aPublicKeyBytes);

        // 生成AES密钥
        SecretKey secretKey = AesGmcUtil.generateAesKey(256);
        byte[] keyBytes = secretKey.getEncoded();

        // 格式化输出
        String aesKeyStr = formatOutput(keyBytes);
        String aesKeyEncStr = RsaUtil.encrypt(aesKeyStr, rsaPublicKeyStr);

        // 写入文件（如果指定）
        if (configFilePath != null && !configFilePath.isEmpty()) {
            getLog().info("尝试将密钥写入配置文件");
            writeToFile(aesPropertyName + ": " + aesKeyEncStr);
            writeToFile(rsaPublicKeyPropertyName + ": " + rsaPublicKeyStr);
            writeToFile(rsaPrivateKeyPropertyName + ": " + rsaPrivateKeyStr);
        } else {
            getLog().info(aesPropertyName + ": " + aesKeyEncStr);
            getLog().info(rsaPublicKeyPropertyName + ": " + rsaPublicKeyStr);
            getLog().info(rsaPrivateKeyPropertyName + ": " + rsaPrivateKeyStr);
        }
    }


    /**
     * 将密钥写入配置文件
     *
     * @param content content
     * @throws Exception Exception
     */
    private void writeToFile(String content) throws Exception {
        Path path = Paths.get(configFilePath);
        Path parentDir = path.getParent();
        if (parentDir != null) {
            Files.createDirectories(parentDir);
        }
        // 处理YAML格式的更新逻辑
        if (format.equalsIgnoreCase("yaml") || format.equalsIgnoreCase("yml")) {
            updateYamlFile(path, content);
        } else {
            getLog().info("仅支持写入yml/yaml文件，其余格式文件暂不支持写入，请自行将密钥写入配置文件");
            getLog().info(content);
        }
    }

    /**
     * 更新YAML文件中的特定属性
     *
     * @param filePath   配置文件路径
     * @param newContent 新的属性内容
     * @throws IOException IOException
     */
    private void updateYamlFile(Path filePath, String newContent) throws IOException {
        // 解析属性名和值
        String[] parts = newContent.split(":", 2);
        if (parts.length < 2) {
            throw new IOException("Invalid YAML content format");
        }
        String key = parts[0].trim();
        String value = parts[1].trim();

        Map<String, Object> data = new LinkedHashMap<>();
        boolean fileExists = Files.exists(filePath);

        // 如果文件存在，加载现有数据
        if (fileExists) {
            Yaml yaml = new Yaml();
            try (InputStream in = Files.newInputStream(filePath)) {
                data = yaml.load(in);
                if (data == null) {
                    data = new LinkedHashMap<>();
                }
            }
        }

        // 更新嵌套属性
        updateNestedProperty(data, key, value);

        // 配置YAML输出选项
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        Yaml yaml = new Yaml(options);

        // 写回文件
        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            yaml.dump(data, writer);
        }
        getLog().info("成功更新YML/YAML文件的属性: " + key);
    }

    /**
     * 递归更新嵌套属性
     *
     * @param map     当前层级的Map
     * @param keyPath 点分隔的属性路径
     * @param value   要设置的值
     */
    @SuppressWarnings("unchecked")
    private void updateNestedProperty(Map<String, Object> map, String keyPath, Object value) {
        String[] keys = keyPath.split("\\.");
        String currentKey = keys[0];

        if (keys.length == 1) {
            // 到达最终层级，设置值
            map.put(currentKey, value);
        } else {
            // 处理嵌套层级
            Object nestedObj = map.get(currentKey);
            Map<String, Object> nestedMap;

            if (nestedObj instanceof Map) {
                // 已有嵌套Map
                nestedMap = (Map<String, Object>) nestedObj;
            } else {
                // 创建新的嵌套Map
                nestedMap = new LinkedHashMap<>();
                map.put(currentKey, nestedMap);
            }

            // 递归处理剩余路径
            String remainingPath = String.join(".", Arrays.copyOfRange(keys, 1, keys.length));
            updateNestedProperty(nestedMap, remainingPath, value);
        }
    }

    /**
     * 检查YAML文件中是否存在所有指定属性
     *
     * @param filePath 配置文件路径
     * @param keys     要检查的属性列表
     * @return 所有属性都存在返回true，否则false
     * @throws IOException 读取文件异常
     */
    private boolean keysExistInYaml(Path filePath, List<String> keys) throws IOException {
        Yaml yaml = new Yaml();
        try (InputStream in = Files.newInputStream(filePath)) {
            Map<String, Object> data = yaml.load(in);
            if (data == null) return false;

            for (String key : keys) {
                if (!isPropertyPresent(data, key)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * 递归检查嵌套属性是否存在
     *
     * @param data    YAML数据
     * @param keyPath 点分隔的属性路径
     * @return 属性存在返回true
     */
    @SuppressWarnings("unchecked")
    private boolean isPropertyPresent(Map<String, Object> data, String keyPath) {
        String[] parts = keyPath.split("\\.");
        Map<String, Object> current = data;

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            Object value = current.get(part);

            if (value == null) {
                return false;
            }

            if (i < parts.length - 1) {
                if (value instanceof Map) {
                    current = (Map<String, Object>) value;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 转BASE64
     *
     * @return BASE64
     */
    private String formatOutput(byte[] keyBytes) {
        return Base64.getEncoder().encodeToString(keyBytes);
    }
}
