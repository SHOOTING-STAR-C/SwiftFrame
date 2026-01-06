package com.star.swiftAi.core.model;

import lombok.Data;

import java.util.Map;

/**
 * 工具定义
 *
 * @author SHOOTING_STAR_C
 */
@Data
public class Tool {
    /**
     * 工具类型
     */
    private String type;
    
    /**
     * 函数定义
     */
    private Function function;
    
    /**
     * 函数定义
     */
    @Data
    public static class Function {
        /**
         * 函数名称
         */
        private String name;
        
        /**
         * 函数描述
         */
        private String description;
        
        /**
         * 函数参数（JSON Schema）
         */
        private Map<String, Object> parameters;
    }
}
