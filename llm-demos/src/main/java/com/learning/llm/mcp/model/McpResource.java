package com.learning.llm.mcp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MCP 资源定义
 * 
 * Resources 提供只读的数据访问
 * 
 * @author Learning
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class McpResource {
    
    /**
     * 资源 URI（唯一标识）
     */
    private String uri;
    
    /**
     * 资源名称
     */
    private String name;
    
    /**
     * 资源描述
     */
    private String description;
    
    /**
     * MIME 类型
     */
    private String mimeType;
}

