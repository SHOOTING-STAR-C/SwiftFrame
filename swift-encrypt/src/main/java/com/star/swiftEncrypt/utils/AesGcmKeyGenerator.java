package com.star.swiftEncrypt.utils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Scanner;

/**
 * AES-GCM 密钥生成工具
 * 支持密钥长度: 128, 192, 256 位
 *
 * @author SHOOTING_STAR_C
 */
public class AesGcmKeyGenerator {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== AES-GCM 密钥生成工具 ===");

        // 获取密钥长度
        int keySize = getKeySize(scanner);

        // 获取输出格式
        System.out.print("\n选择输出格式 (1: Base64, 2: Hex, 3: Java代码): ");
        int formatChoice = scanner.nextInt();

        // 生成密钥
        SecretKey secretKey = AesGmcUtil.generateAesKey(keySize);

        // 输出结果
        printKey(secretKey, formatChoice);

        scanner.close();
    }

    private static int getKeySize(Scanner scanner) {
        while (true) {
            System.out.print("请输入密钥长度 (128, 192 或 256): ");
            int keySize = scanner.nextInt();

            if (keySize == 128 || keySize == 192 || keySize == 256) {
                return keySize;
            }

            System.out.println("错误: 密钥长度必须是 128, 192 或 256 位!");
        }
    }

    private static SecretKey generateAesGcmKey(int keySize) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(keySize, SecureRandom.getInstanceStrong());
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("AES 算法不支持: " + e.getMessage(), e);
        }
    }

    private static void printKey(SecretKey secretKey, int formatChoice) {
        byte[] keyBytes = secretKey.getEncoded();

        System.out.println("\n=== 生成的 AES-GCM 密钥 ===");

        switch (formatChoice) {
            case 1: // Base64
                String base64Key = Base64.getEncoder().encodeToString(keyBytes);
                System.out.println("Base64 格式: " + base64Key);
                break;

            case 2: // Hex
                System.out.println("Hex 格式: " + bytesToHex(keyBytes));
                break;

            case 3: // Java代码
                System.out.println("Java 代码片段:");
                System.out.println(generateJavaCodeSnippet(keyBytes));
                break;

            default:
                System.out.println("无效的选择，使用默认 Base64 格式:");
                System.out.println(Base64.getEncoder().encodeToString(keyBytes));
        }

        System.out.println("\n密钥长度: " + (keyBytes.length * 8) + "位");
        System.out.println("算法: " + secretKey.getAlgorithm());
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    private static String generateJavaCodeSnippet(byte[] keyBytes) {
        StringBuilder sb = new StringBuilder();
        sb.append("// 在 Spring Boot 配置文件中使用:\n");
        sb.append("encryption:\n");
        sb.append("  aes-gcm-key: ").append(Base64.getEncoder().encodeToString(keyBytes)).append("\n\n");

        sb.append("// 或者在 Java 代码中使用:\n");
        sb.append("byte[] keyBytes = Base64.getDecoder().decode(\"")
                .append(Base64.getEncoder().encodeToString(keyBytes))
                .append("\");\n");
        sb.append("SecretKey key = new SecretKeySpec(keyBytes, \"AES\");");

        return sb.toString();
    }
}
