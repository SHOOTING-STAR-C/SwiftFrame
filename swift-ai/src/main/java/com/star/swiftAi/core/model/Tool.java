package com.star.swiftAi.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 工具定义实体
 * 用于定义AI可调用的工具函数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tool {

    /**
     * 工具类型：function
     */
    @JsonProperty(value = "type", required = true)
    private String type;

    /**
     * 函数定义
     */
    @JsonProperty(value = "function", required = true)
    private Function function;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Function {

        /**
         * 函数名称
         */
        @JsonProperty(value = "name", required = true)
        private String name;

        /**
         * 函数描述
         */
        @JsonProperty("description")
        private String description;

        /**
         * 函数参数定义（JSONSchema格式）
         */
        @JsonProperty("parameters")
        private Map<String, Object> parameters;
    }

    /**
     * 便捷方法：创建function类型的工具
     */
    public static Tool function(String name, String description, Map<String, Object> parameters) {
        return Tool.builder()
                .type("function")
                .function(Function.builder()
                        .name(name)
                        .description(description)
                        .parameters(parameters)
                        .build())
                .build();
    }
}
