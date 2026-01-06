package com.star.swiftAi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI模型类型枚举
 *
 * @author SHOOTING_STAR_C
 */
@Getter
@AllArgsConstructor
public enum ModelType {

    /**
     * 大语言模型
     */
    LLM("llm", "大语言模型"),

    /**
     * 嵌入模型
     */
    EMBEDDING("embedding", "嵌入模型"),

    /**
     * 重排序模型
     */
    RERANK("rerank", "重排序模型"),

    /**
     * 图像生成模型
     */
    IMAGE_GENERATION("image_generation", "图像生成模型"),

    /**
     * 语音识别模型
     */
    ASR("asr", "语音识别模型"),

    /**
     * 语音合成模型
     */
    TTS("tts", "语音合成模型"),

    /**
     * 其他类型
     */
    OTHER("other", "其他类型");

    /**
     * 类型代码
     */
    private final String code;

    /**
     * 类型名称
     */
    private final String name;

    /**
     * 根据代码获取枚举
     *
     * @param code 类型代码
     * @return 模型类型枚举
     */
    public static ModelType fromCode(String code) {
        for (ModelType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return OTHER;
    }
}
