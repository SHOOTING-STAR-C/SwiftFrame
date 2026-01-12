package com.star.swiftSecurity.properties;

import jakarta.servlet.ServletContext;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * 安全相关配置
 *
 * @author SHOOTING_STAR_C
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "security.auth")
public class SecurityProperties {

    private String whiteList;

    private ServletContext servletContext;

    /**
     * 白名单配置项（支持HTTP方法）
     */
    public static class WhitelistItem {
        private final String method;
        private final String path;

        public WhitelistItem(String method, String path) {
            this.method = method;
            this.path = path;
        }

        public String getMethod() {
            return method;
        }

        public String getPath() {
            return path;
        }

        public boolean isAnyMethod() {
            return "ANY".equalsIgnoreCase(method) || method == null || method.isEmpty();
        }
    }

    /**
     * 转换为数组（向后兼容）
     *
     * @return String[]
     */
    public String[] getWhitelistArray() {
        if (whiteList == null || whiteList.isEmpty()) {
            return new String[0];
        }
        
        return Arrays.stream(whiteList.split(","))
                .map(String::trim)
                .toArray(String[]::new);
    }

    /**
     * 转换为白名单项数组
     *
     * @return WhitelistItem[]
     */
    public WhitelistItem[] getWhitelistItems() {
        if (whiteList == null || whiteList.isEmpty()) {
            return new WhitelistItem[0];
        }
        
        return Arrays.stream(whiteList.split(","))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .map(this::parseWhitelistItem)
                .toArray(WhitelistItem[]::new);
    }

    /**
     * 解析白名单项
     * 格式支持：
     * - /auth/login (所有HTTP方法)
     * - GET:/ai/models/** (仅GET方法)
     * - POST,PUT:/api/test (多个HTTP方法)
     *
     * @param item 白名单项字符串
     * @return WhitelistItem数组
     */
    private WhitelistItem parseWhitelistItem(String item) {
        if (item.contains(":")) {
            String[] parts = item.split(":", 2);
            String methods = parts[0].trim();
            String path = parts[1].trim();
            
            // 支持多个HTTP方法，用逗号分隔
            if (methods.contains(",")) {
                // 返回第一个方法项，实际使用时需要处理多个方法
                String firstMethod = methods.split(",")[0].trim();
                return new WhitelistItem(firstMethod, path);
            }
            
            return new WhitelistItem(methods, path);
        }
        
        // 没有指定HTTP方法，表示所有方法都允许
        return new WhitelistItem("ANY", item);
    }

    /**
     * 获取完全放行的路径（所有HTTP方法）
     *
     * @return String[]
     */
    public String[] getPermitAllPaths() {
        return Arrays.stream(getWhitelistItems())
                .filter(WhitelistItem::isAnyMethod)
                .map(WhitelistItem::getPath)
                .toArray(String[]::new);
    }

    /**
     * 获取按HTTP方法分组的路径映射
     *
     * @return Map<String, String[]>
     */
    public Map<String, String[]> getMethodPaths() {
        Map<String, String[]> result = new HashMap<>();
        
        Arrays.stream(getWhitelistItems())
                .filter(item -> !item.isAnyMethod())
                .forEach(item -> {
                    String method = item.getMethod().toLowerCase();
                    String path = item.getPath();
                    
                    result.putIfAbsent(method, new String[0]);
                    String[] existingPaths = result.get(method);
                    String[] newPaths = new String[existingPaths.length + 1];
                    System.arraycopy(existingPaths, 0, newPaths, 0, existingPaths.length);
                    newPaths[existingPaths.length] = path;
                    result.put(method, newPaths);
                });
        
        return result;
    }
}
