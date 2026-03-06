package com.learning.llm.mcp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * MCP 工具定义
 * 
 * Tools 提供可执行的功能
 * 
 * @author Learning
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class McpTool {
    
    /**
     * 工具名称
     */
    private String name;
    
    /**
     * 工具描述
     */
    private String description;
    
    /**
     * 输入参数定义（JSON Schema）
     */
    private Map<String, Object> inputSchema;
}

