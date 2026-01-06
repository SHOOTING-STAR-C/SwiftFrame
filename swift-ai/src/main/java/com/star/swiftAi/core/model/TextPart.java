package com.star.swiftAi.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文本内容块
 *
 * @author SHOOTING_STAR_C
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextPart implements ContentPart {
    /**
     * 文本内容
     */
    private String text;
    
    @Override
    public String getType() {
        return "text";
    }
}
