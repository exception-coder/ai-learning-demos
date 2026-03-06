package com.learning.llm.mcp.controller;

import com.learning.llm.mcp.service.McpIntegrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * MCP 演示控制器
 * 
 * 提供 MCP 功能的演示接口
 * 
 * @author Learning
 */
@Slf4j
@RestController
@RequestMapping("/api/mcp")
public class McpDemoController {
    
    private final McpIntegrationService mcpService;
    
    public McpDemoController(McpIntegrationService mcpService) {
        this.mcpService = mcpService;
    }
    
    /**
     * 初始化 MCP
     */
    @GetMapping("/init")
    public String initialize() {
        return mcpService.initializeMcp();
    }
    
    /**
     * 列出所有资源
     */
    @GetMapping("/resources")
    public String listResources() {
        return mcpService.listAvailableResources();
    }
    
    /**
     * 读取文件
     */
    @GetMapping("/read")
    public String readFile(@RequestParam String filename) {
        return mcpService.readFile(filename);
    }
    
    /**
     * 写入文件
     */
    @PostMapping("/write")
    public String writeFile(@RequestParam String filename, @RequestBody String content) {
        return mcpService.writeFile(filename, content);
    }
    
    /**
     * 列出所有文件
     */
    @GetMapping("/files")
    public String listFiles() {
        return mcpService.listFiles();
    }
    
    /**
     * 列出所有工具
     */
    @GetMapping("/tools")
    public String listTools() {
        return mcpService.listAvailableTools();
    }
    
    /**
     * 列出所有提示词
     */
    @GetMapping("/prompts")
    public String listPrompts() {
        return mcpService.listAvailablePrompts();
    }
    
    /**
     * 获取提示词
     */
    @GetMapping("/prompt")
    public String getPrompt(@RequestParam String name, @RequestParam String filename) {
        return mcpService.getPrompt(name, filename);
    }
}

