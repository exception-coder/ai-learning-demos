package com.learning.llm.mcp.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MCP 请求基类
 * 
 * 遵循 JSON-RPC 2.0 规范
 * 
 * @author Learning
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class McpRequest {
    
    /**
     * JSON-RPC 版本，固定为 "2.0"
     */
    private String jsonrpc = "2.0";
    
    /**
     * 请求 ID
     */
    private String id;
    
    /**
     * 方法名
     */
    private String method;
    
    /**
     * 参数
     */
    private Object params;
}

