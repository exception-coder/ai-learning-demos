package com.learning.llm.mcp.client;

import com.learning.llm.mcp.protocol.McpRequest;
import com.learning.llm.mcp.protocol.McpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

/**
 * MCP Client
 * 
 * 用于连接和调用 MCP Server
 * 
 * @author Learning
 */
@Slf4j
@Component
public class McpClient {
    
    private final RestTemplate restTemplate;
    private final String serverUrl;
    
    public McpClient() {
        this.restTemplate = new RestTemplate();
        this.serverUrl = "http://localhost:8080/mcp";
    }
    
    /**
     * 初始化连接
     */
    public McpResponse initialize() {
        McpRequest request = new McpRequest();
        request.setId(UUID.randomUUID().toString());
        request.setMethod("initialize");
        request.setParams(Map.of(
            "protocolVersion", "1.0",
            "clientInfo", Map.of(
                "name", "spring-ai-mcp-client",
                "version", "1.0.0"
            )
        ));
        
        return sendRequest(request);
    }
    
    /**
     * 列出所有资源
     */
    public McpResponse listResources() {
        McpRequest request = new McpRequest();
        request.setId(UUID.randomUUID().toString());
        request.setMethod("resources/list");
        request.setParams(Map.of());
        
        return sendRequest(request);
    }
    
    /**
     * 读取资源
     */
    public McpResponse readResource(String uri) {
        McpRequest request = new McpRequest();
        request.setId(UUID.randomUUID().toString());
        request.setMethod("resources/read");
        request.setParams(Map.of("uri", uri));
        
        return sendRequest(request);
    }
    
    /**
     * 列出所有工具
     */
    public McpResponse listTools() {
        McpRequest request = new McpRequest();
        request.setId(UUID.randomUUID().toString());
        request.setMethod("tools/list");
        request.setParams(Map.of());
        
        return sendRequest(request);
    }
    
    /**
     * 调用工具
     */
    public McpResponse callTool(String toolName, Map<String, Object> arguments) {
        McpRequest request = new McpRequest();
        request.setId(UUID.randomUUID().toString());
        request.setMethod("tools/call");
        request.setParams(Map.of(
            "name", toolName,
            "arguments", arguments
        ));
        
        return sendRequest(request);
    }
    
    /**
     * 列出所有提示词
     */
    public McpResponse listPrompts() {
        McpRequest request = new McpRequest();
        request.setId(UUID.randomUUID().toString());
        request.setMethod("prompts/list");
        request.setParams(Map.of());
        
        return sendRequest(request);
    }
    
    /**
     * 获取提示词
     */
    public McpResponse getPrompt(String promptName, Map<String, Object> arguments) {
        McpRequest request = new McpRequest();
        request.setId(UUID.randomUUID().toString());
        request.setMethod("prompts/get");
        request.setParams(Map.of(
            "name", promptName,
            "arguments", arguments
        ));
        
        return sendRequest(request);
    }
    
    /**
     * 发送请求
     */
    private McpResponse sendRequest(McpRequest request) {
        try {
            log.info("发送 MCP 请求：method={}", request.getMethod());
            McpResponse response = restTemplate.postForObject(serverUrl, request, McpResponse.class);
            log.info("收到 MCP 响应：success={}", response.getError() == null);
            return response;
        } catch (Exception e) {
            log.error("MCP 请求失败", e);
            return McpResponse.error(request.getId(), -32603, "请求失败：" + e.getMessage());
        }
    }
}

