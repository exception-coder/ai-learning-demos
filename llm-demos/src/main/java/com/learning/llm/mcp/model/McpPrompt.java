package com.learning.llm.mcp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * MCP 提示词定义
 * 
 * Prompts 提供可复用的提示词模板
 * 
 * @author Learning
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class McpPrompt {
    
    /**
     * 提示词名称
     */
    private String name;
    
    /**
     * 提示词描述
     */
    private String description;
    
    /**
     * 参数定义
     */
    private Map<String, Object> arguments;
}

