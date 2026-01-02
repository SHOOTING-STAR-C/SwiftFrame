package com.star.swiftEncryptPlugin.maven;

import com.star.swiftEncrypt.utils.AesGmcUtil;
import com.star.swiftEncryptPlugin.utils.EncryptPluginUtil;
import lombok.Setter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL文件解密指令
 *
 * @author SHOOTING_STAR_C
 */
@Mojo(name = "decrypt-sql", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
@Setter
public class DecryptSqlMojo extends AbstractMojo {

    private static final Pattern ENC_PATTERN = Pattern.compile("ENC\\(([^)]+)\\)");

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * SQL文件目录路径（可选）
     */
    @Parameter(property = "sqlDirPath", defaultValue = "${project.basedir}/src/main/resources/sql")
    private String sqlDirPath;

    /**
     * 是否递归处理子目录
     */
    @Parameter(property = "recursive", defaultValue = "true")
    private boolean recursive;

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
     * 配置文件路径（用于获取密钥）
     */
    @Parameter(property = "configFilePath", defaultValue = "${project.basedir}/src/main/resources/application-${app.env}.yml")
    private String configFilePath;

    /**
     * 插件主方法
     *
     * @throws MojoExecutionException MojoExecutionException
     */
    public void execute() throws MojoExecutionException {
        getLog().info("Starting SQL file decryption...");

        try {
            // 1. 获取AES密钥
            String aesKey = getAesKey();

            // 2. 查找所有SQL文件
            List<File> sqlFiles = findSqlFiles();
            if (sqlFiles.isEmpty()) {
                getLog().warn("No SQL files found in: " + sqlDirPath);
                return;
            }

            getLog().info("Found " + sqlFiles.size() + " SQL file(s) to process");

            // 3. 解密处理每个SQL文件
            int successCount = 0;
            for (File sqlFile : sqlFiles) {
                try {
                    decryptSqlFile(sqlFile, aesKey);
                    successCount++;
                    getLog().info("Successfully decrypted: " + sqlFile.getPath());
                } catch (Exception e) {
                    getLog().error("Failed to decrypt: " + sqlFile.getPath(), e);
                }
            }

            getLog().info("SQL decryption completed. Success: " + successCount + "/" + sqlFiles.size());
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to process SQL files", e);
        }
    }

    /**
     * 获取AES密钥
     */
    private String getAesKey() throws Exception {
        File configFile = resolveConfigFile();
        getLog().info("Loading encryption keys from: " + configFile.getPath());

        org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
        java.util.Map<String, Object> config = yaml.load(new FileInputStream(configFile));

        return EncryptPluginUtil.getAesKey(config, aesPropertyName, rsaPrivateKeyPropertyName);
    }

    /**
     * 查找所有SQL文件
     */
    private List<File> findSqlFiles() {
        List<File> sqlFiles = new ArrayList<>();
        File sqlDir = new File(sqlDirPath);

        if (!sqlDir.exists() || !sqlDir.isDirectory()) {
            getLog().warn("SQL directory not found: " + sqlDirPath);
            return sqlFiles;
        }

        collectSqlFiles(sqlDir, sqlFiles);
        return sqlFiles;
    }

    /**
     * 递归收集SQL文件
     */
    private void collectSqlFiles(File dir, List<File> sqlFiles) {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory() && recursive) {
                collectSqlFiles(file, sqlFiles);
            } else if (file.isFile() && file.getName().toLowerCase().endsWith(".sql")) {
                sqlFiles.add(file);
            }
        }
    }

    /**
     * 解密SQL文件
     */
    private void decryptSqlFile(File sqlFile, String aesKey) throws Exception {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(sqlFile), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String decryptedLine = decryptLine(line, aesKey);
                lines.add(decryptedLine);
            }
        }

        // 写回文件
        try (FileWriter writer = new FileWriter(sqlFile, StandardCharsets.UTF_8)) {
            for (String line : lines) {
                writer.write(line);
                writer.write(System.lineSeparator());
            }
        }
    }

    /**
     * 解密单行SQL
     */
    private String decryptLine(String line, String aesKey) throws Exception {
        Matcher matcher = ENC_PATTERN.matcher(line);
        if (matcher.find()) {
            String encryptedText = matcher.group(1);
            String decrypted = "DEC(" + AesGmcUtil.decrypt(encryptedText, aesKey) + ")";
            return matcher.replaceAll(decrypted);
        }
        return line;
    }

    /**
     * 解析配置文件路径
     */
    private File resolveConfigFile() throws MojoExecutionException {
        // 处理绝对路径
        if (configFilePath.startsWith("/") || configFilePath.contains(":")) {
            File configFile = new File(configFilePath);
            if (!configFile.exists()) {
                throw new MojoExecutionException("Config file not found at absolute path: " + configFilePath);
            }
            return configFile;
        }

        // 处理相对路径（基于项目根目录）
        File configFile = new File(project.getBasedir(), configFilePath);
        if (!configFile.exists()) {
            throw new MojoExecutionException("Config file not found at: " + configFile.getAbsolutePath());
        }
        return configFile;
    }
}
