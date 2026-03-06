package com.learning.llm.mcp.controller;

import com.learning.llm.mcp.protocol.McpRequest;
import com.learning.llm.mcp.protocol.McpResponse;
import com.learning.llm.mcp.server.FileSystemMcpServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * MCP Server HTTP 接口
 * 
 * 提供 HTTP 方式访问 MCP Server
 * 
 * @author Learning
 */
@Slf4j
@RestController
@RequestMapping("/mcp")
public class McpController {
    
    private final FileSystemMcpServer mcpServer;
    
    public McpController(FileSystemMcpServer mcpServer) {
        this.mcpServer = mcpServer;
    }
    
    /**
     * 处理所有 MCP 请求
     */
    @PostMapping
    public McpResponse handleRequest(@RequestBody McpRequest request) {
        log.info("收到 MCP HTTP 请求：{}", request.getMethod());
        return mcpServer.handleRequest(request);
    }
    
    /**
     * 健康检查
     */
    @GetMapping("/health")
    public String health() {
        return "MCP Server is running";
    }
}

