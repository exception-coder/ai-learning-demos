package com.learning.llm.mcp.service;

import com.learning.llm.mcp.client.McpClient;
import com.learning.llm.mcp.protocol.McpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * MCP 集成服务
 * 
 * 演示如何在 Spring AI 中使用 MCP
 * 
 * @author Learning
 */
@Slf4j
@Service
public class McpIntegrationService {
    
    private final McpClient mcpClient;
    
    public McpIntegrationService(McpClient mcpClient) {
        this.mcpClient = mcpClient;
    }
    
    /**
     * 初始化 MCP 连接
     */
    public String initializeMcp() {
        log.info("初始化 MCP 连接");
        McpResponse response = mcpClient.initialize();
        
        if (response.getError() != null) {
            return "初始化失败：" + response.getError().getMessage();
        }
        
        return "MCP 连接初始化成功：\n" + formatResponse(response.getResult());
    }
    
    /**
     * 列出所有可用资源
     */
    public String listAvailableResources() {
        log.info("列出所有资源");
        McpResponse response = mcpClient.listResources();
        
        if (response.getError() != null) {
            return "获取资源列表失败：" + response.getError().getMessage();
        }
        
        return "可用资源：\n" + formatResponse(response.getResult());
    }
    
    /**
     * 读取文件内容
     */
    public String readFile(String filename) {
        log.info("读取文件：{}", filename);
        String uri = "file://" + filename;
        McpResponse response = mcpClient.readResource(uri);
        
        if (response.getError() != null) {
            return "读取文件失败：" + response.getError().getMessage();
        }
        
        return "文件内容：\n" + formatResponse(response.getResult());
    }
    
    /**
     * 写入文件
     */
    public String writeFile(String filename, String content) {
        log.info("写入文件：{}", filename);
        McpResponse response = mcpClient.callTool("write_file", Map.of(
            "filename", filename,
            "content", content
        ));
        
        if (response.getError() != null) {
            return "写入文件失败：" + response.getError().getMessage();
        }
        
        return "写入成功：\n" + formatResponse(response.getResult());
    }
    
    /**
     * 列出所有文件
     */
    public String listFiles() {
        log.info("列出所有文件");
        McpResponse response = mcpClient.callTool("list_files", Map.of());
        
        if (response.getError() != null) {
            return "列出文件失败：" + response.getError().getMessage();
        }
        
        return formatResponse(response.getResult());
    }
    
    /**
     * 列出所有工具
     */
    public String listAvailableTools() {
        log.info("列出所有工具");
        McpResponse response = mcpClient.listTools();
        
        if (response.getError() != null) {
            return "获取工具列表失败：" + response.getError().getMessage();
        }
        
        return "可用工具：\n" + formatResponse(response.getResult());
    }
    
    /**
     * 列出所有提示词
     */
    public String listAvailablePrompts() {
        log.info("列出所有提示词");
        McpResponse response = mcpClient.listPrompts();
        
        if (response.getError() != null) {
            return "获取提示词列表失败：" + response.getError().getMessage();
        }
        
        return "可用提示词：\n" + formatResponse(response.getResult());
    }
    
    /**
     * 获取提示词
     */
    public String getPrompt(String promptName, String filename) {
        log.info("获取提示词：{}", promptName);
        McpResponse response = mcpClient.getPrompt(promptName, Map.of("filename", filename));
        
        if (response.getError() != null) {
            return "获取提示词失败：" + response.getError().getMessage();
        }
        
        return "提示词内容：\n" + formatResponse(response.getResult());
    }
    
    /**
     * 格式化响应
     */
    private String formatResponse(Object result) {
        if (result == null) {
            return "无结果";
        }
        return result.toString();
    }
}

