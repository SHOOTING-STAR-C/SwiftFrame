package com.star.swiftAi.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图片URL内容块
 *
 * @author SHOOTING_STAR_C
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageURLPart implements ContentPart {
    /**
     * 图片URL
     */
    private ImageURL imageUrl;
    
    @Override
    public String getType() {
        return "image_url";
    }
    
    /**
     * 图片URL
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageURL {
        /**
         * URL地址
         */
        private String url;
    }
}
