package com.star.swiftEncryptPlugin.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Maven插件：AES密钥生成器
 *
 * @author SHOOTING_STAR_C
 */
@Mojo(name = "generate", requiresProject = false)
public class AesKeyGeneratorMojo extends AbstractMojo {
    /**
     * 密钥长度（位）
     * 可选值：128, 192, 256
     */
    @Parameter(property = "keySize", defaultValue = "256")
    private int keySize;

    /**
     * 输出格式
     * 可选值：base64, hex, java, yaml
     */
    @Parameter(property = "format", defaultValue = "base64")
    private String format;

    /**
     * 输出文件路径（可选）
     */
    @Parameter(property = "outputFile")
    private String outputFile;

    /**
     * 密钥属性名（用于YAML格式）
     */
    @Parameter(property = "propertyName", defaultValue = "encryption.aes-key")
    private String propertyName;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            // 生成AES密钥
            SecretKey secretKey = generateAesKey();
            byte[] keyBytes = secretKey.getEncoded();

            // 格式化输出
            String output = formatOutput(keyBytes);

            // 输出结果
            getLog().info("Generated AES-" + keySize + " key:");
            getLog().info(output);

            // 写入文件（如果指定）
            if (outputFile != null && !outputFile.isEmpty()) {
                writeToFile(output);
                getLog().info("Key saved to: " + outputFile);
            }

        } catch (NoSuchAlgorithmException e) {
            throw new MojoExecutionException("AES algorithm not supported", e);
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to generate AES key", e);
        }
    }

    private SecretKey generateAesKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(keySize);
        return keyGenerator.generateKey();
    }

    private String formatOutput(byte[] keyBytes) {
        String base64Key = Base64.getEncoder().encodeToString(keyBytes);

        switch (format.toLowerCase()) {
            case "hex":
                return bytesToHex(keyBytes);
            case "java":
                return "SecretKey key = new SecretKeySpec(Base64.getDecoder().decode(\""
                        + base64Key + "\"), \"AES\");";
            case "yaml":
                return propertyName + ": " + base64Key;
            case "base64":
            default:
                return base64Key;
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    private void writeToFile(String content) throws Exception {
        // 简化的文件写入逻辑，实际实现需处理路径创建等
        java.nio.file.Files.write(
                java.nio.file.Paths.get(outputFile),
                content.getBytes()
        );
    }
}
