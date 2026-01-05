package com.star.swiftAi.core.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 模型列表响应
 *
 * @author SHOOTING_STAR_C
 */
@Data
public class ModelsResponse {

    /**
     * 对象类型
     */
    private String object;

    /**
     * 模型列表
     */
    private List<Model> data;

    @Data
    public static class Model {
        /**
         * 模型ID
         */
        private String id;

        /**
         * 对象类型
         */
        private String object;

        /**
         * 创建时间
         */
        @JsonProperty("created")
        private Long createdAt;

        /**
         * 模型所属者
         */
        private String ownedBy;
    }
}
