package com.learning.llm.mcp.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MCP 响应基类
 * 
 * 遵循 JSON-RPC 2.0 规范
 * 
 * @author Learning
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class McpResponse {
    
    /**
     * JSON-RPC 版本，固定为 "2.0"
     */
    private String jsonrpc = "2.0";
    
    /**
     * 请求 ID
     */
    private String id;
    
    /**
     * 成功结果
     */
    private Object result;
    
    /**
     * 错误信息
     */
    private McpError error;
    
    /**
     * 创建成功响应
     */
    public static McpResponse success(String id, Object result) {
        return new McpResponse("2.0", id, result, null);
    }
    
    /**
     * 创建错误响应
     */
    public static McpResponse error(String id, int code, String message) {
        return new McpResponse("2.0", id, null, new McpError(code, message));
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class McpError {
        private int code;
        private String message;
    }
}

