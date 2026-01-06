package com.star.swiftAi.core.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 工具集合
 *
 * @author SHOOTING_STAR_C
 */
@Data
public class ToolSet {
    /**
     * 工具列表
     */
    private List<Tool> tools = new ArrayList<>();
}
