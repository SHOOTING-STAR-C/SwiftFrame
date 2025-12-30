package com.star.swiftAi.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 多模态消息内容实体
 * 用于支持文本、图片等多种类型的内容
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Content {

    /**
     * 内容类型：text, image_url
     */
    @JsonProperty(value = "type", required = true)
    private String type;

    /**
     * 文本内容
     */
    @JsonProperty("text")
    private String text;

    /**
     * 图片内容URL
     */
    @JsonProperty("image_url")
    private ImageUrl imageUrl;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ImageUrl {

        @JsonProperty(value = "url", required = true)
        private String url;

        @JsonProperty("detail")
        private String detail;
    }
}
