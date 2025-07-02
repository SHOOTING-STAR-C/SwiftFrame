package com.star.swiftEncryptPlugin.utils;

import com.star.swiftEncrypt.utils.RsaUtil;
import com.star.swiftEncryptPlugin.domain.KeySize;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.project.MavenProject;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 插件公告方法
 *
 * @author SHOOTING_STAR_C
 */
public class EncryptPluginUtil {
    private static final Log log = new SystemStreamLog();

    /**
     * 读取Aes密钥密文并解密
     *
     * @throws Exception Exception
     */
    public static String getAesKey(Map<String, Object> config, String aesPropertyName, String rsaPrivateKeyPropertyName) throws Exception {
        // 1. 从YML中获取加密的AES密钥
        String encryptedAesKey = EncryptPluginUtil.getValueByPath(config, aesPropertyName);
        if (encryptedAesKey == null || encryptedAesKey.isEmpty()) {
            throw new MojoExecutionException("AES key not found in YML configuration");
        }

        // 2. 从YML中获取RSA私钥
        String rsaPrivateKey = EncryptPluginUtil.getValueByPath(config, rsaPrivateKeyPropertyName);
        if (rsaPrivateKey == null || rsaPrivateKey.isEmpty()) {
            throw new MojoExecutionException("RSA private key not found in YML configuration");
        }

        // 3. 移除可能的标记前缀（如"ENC("）
        if (encryptedAesKey.startsWith("ENC(") && encryptedAesKey.endsWith(")")) {
            encryptedAesKey = encryptedAesKey.substring(4, encryptedAesKey.length() - 1);
        }
        // 4. 使用RSA私钥解密AES密钥
        return RsaUtil.decrypt(encryptedAesKey, rsaPrivateKey);

    }

    /**
     * 将密钥写入配置文件
     *
     * @param content content
     * @throws Exception Exception
     */
    public static void writeToFile(String content, String format, String configFilePath) throws Exception {
        Path path = Paths.get(configFilePath);
        Path parentDir = path.getParent();
        if (parentDir != null) {
            Files.createDirectories(parentDir);
        }
        // 处理YAML格式的更新逻辑
        if (format.equalsIgnoreCase("yaml") || format.equalsIgnoreCase("yml")) {
            updateYamlFile(path, content);
        } else {
            log.info("仅支持写入yml/yaml文件，其余格式文件暂不支持写入，请自行将密钥写入配置文件");
            log.info(content);
        }
    }

    /**
     * 更新YAML文件中的特定属性
     *
     * @param filePath   配置文件路径
     * @param newContent 新的属性内容
     * @throws IOException IOException
     */
    private static void updateYamlFile(Path filePath, String newContent) throws IOException {
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
        log.info("成功更新YML/YAML文件的属性: " + key);
    }

    /**
     * 递归更新嵌套属性
     *
     * @param map     当前层级的Map
     * @param keyPath 点分隔的属性路径
     * @param value   要设置的值
     */
    @SuppressWarnings("unchecked")
    private static void updateNestedProperty(Map<String, Object> map, String keyPath, Object value) {
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
    public static boolean keysExistInYaml(Path filePath, List<String> keys) throws IOException {
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
     * 获取配置的密钥长度
     *
     * @param project                project
     * @param aesPropertySizeName    aesPropertySizeName
     * @param rsaKeyPropertySizeName rsaKeyPropertySizeName
     * @param keySize                keySize
     */
    public static void getKeysSize(MavenProject project, String aesPropertySizeName, String rsaKeyPropertySizeName, KeySize keySize) {
        // 安全获取AES密钥长度
        String aesSizeStr = project.getProperties().getProperty(aesPropertySizeName);
        if (aesSizeStr != null && !aesSizeStr.trim().isEmpty()) {
            try {
                keySize.setAesKeySize(Integer.parseInt(aesSizeStr));
            } catch (NumberFormatException e) {
                log.warn("Invalid AES key size format: " + aesSizeStr + ". Using default 256");
                keySize.setAesKeySize(256);
            }
        } else {
            log.info("Using default AES key size: 256");
            keySize.setAesKeySize(256);
        }

        // 安全获取RSA密钥长度
        String rsaSizeStr = project.getProperties().getProperty(rsaKeyPropertySizeName);
        if (rsaSizeStr != null && !rsaSizeStr.trim().isEmpty()) {
            try {
                keySize.setRsaKeySize(Integer.parseInt(rsaSizeStr));
            } catch (NumberFormatException e) {
                log.warn("Invalid RSA key size format: " + rsaSizeStr + ". Using default 2048");
                keySize.setRsaKeySize(2048);
            }
        } else {
            log.info("Using default RSA key size: 2048");
            keySize.setRsaKeySize(2048);
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
    private static boolean isPropertyPresent(Map<String, Object> data, String keyPath) {
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
     * 从嵌套Map中按路径获取值
     *
     * @param config 配置数据Map
     * @param path   点分隔的路径（如："server.port"）
     * @return 配置值，未找到时返回null
     */
    @SuppressWarnings("unchecked")
    public static String getValueByPath(Map<String, Object> config, String path) {
        String[] keys = path.split("\\.");
        Object current = config;

        for (String key : keys) {
            if (current instanceof Map) {
                current = ((Map<String, Object>) current).get(key);
            } else {
                return null;
            }

            if (current == null) {
                return null;
            }
        }

        return (current != null) ? current.toString() : null;
    }

    /**
     * 转BASE64
     *
     * @return BASE64
     */
    public static String formatOutput(byte[] keyBytes) {
        return Base64.getEncoder().encodeToString(keyBytes);
    }
}
