package com.star.swiftEncryptPlugin.maven;

import com.star.swiftEncrypt.utils.AesGmcUtil;
import com.star.swiftEncryptPlugin.utils.EncryptPluginUtil;
import lombok.Setter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 加密指令
 *
 * @author SHOOTING_STAR_C
 */
@Mojo(name = "encrypt-config")
@Setter
public class EncryptConfigMojo extends AbstractMojo {

    private static final Pattern DEC_PATTERN = Pattern.compile("DEC\\(([^)]+)\\)");

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * 输出文件路径（可选）
     */
    @Parameter(property = "configFilePath", defaultValue = "${project.basedir}/src/main/resources/application-${app.env}.yml")
    private String configFilePath;
    private File configFile;

    /**
     * aes密钥属性名
     */
    @Parameter(property = "aesPropertyName", defaultValue = "crypto.aes.key")
    private String aesPropertyName;
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
     * 插件主方法
     *
     * @throws MojoExecutionException MojoExecutionException
     */
    public void execute() throws MojoExecutionException {
        resolveConfigFile();
        getLog().info("Processing YML configuration: " + configFilePath);

        try {
            // 1. 加载YML文件
            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.load(new FileInputStream(configFile));

            String aesKey = EncryptPluginUtil.getAesKey(config, aesPropertyName, rsaPrivateKeyPropertyName);
            // 2. 加密处理
            encryptConfig(config, aesKey);

            // 4. 写回文件
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);

            try (FileWriter writer = new FileWriter(configFile)) {
                new Yaml(options).dump(config, writer);
                getLog().info("Successfully encrypted configuration to: " + configFile.getPath());
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to process YML file", e);
        }
    }

    /**
     * 加密
     *
     * @param config config
     * @throws Exception Exception
     */
    private void encryptConfig(Map<String, Object> config, String aesKey) throws Exception {
        for (Map.Entry<String, Object> entry : config.entrySet()) {
            if (entry.getValue() instanceof String) {
                entry.setValue(encryptValue((String) entry.getValue(), aesKey));
            } else if (entry.getValue() instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> nestedMap = (Map<String, Object>) entry.getValue();
                encryptConfig(nestedMap, aesKey);
            }
        }
    }

    /**
     * 调用实际加密方法
     *
     * @param value value
     * @return String
     * @throws Exception Exception
     */
    private String encryptValue(String value, String aesKey) throws Exception {
        Matcher matcher = DEC_PATTERN.matcher(value);
        if (matcher.find()) {
            String plainText = matcher.group(1);
            String encrypted = "ENC(" + AesGmcUtil.encrypt(plainText, aesKey) + ")";
            return matcher.replaceAll(encrypted);
        }
        return value;
    }

    /**
     * 解析配置文件路径
     */
    private void resolveConfigFile() throws MojoExecutionException {
        // 2. 处理绝对路径
        if (configFilePath.startsWith("/") || configFilePath.contains(":")) {
            configFile = new File(configFilePath);
            if (!configFile.exists()) {
                throw new MojoExecutionException("Absolute path not found: " + configFilePath);
            }
            return;
        }

        // 3. 处理相对路径（基于项目根目录）
        configFile = new File(project.getBasedir(), configFilePath);

        // 5. 最终验证
        if (!configFile.exists()) {
            throw new MojoExecutionException("Config file not found at: " + configFile.getAbsolutePath());
        }
    }
}
