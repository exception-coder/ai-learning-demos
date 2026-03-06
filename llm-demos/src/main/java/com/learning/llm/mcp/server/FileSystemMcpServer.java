package com.learning.llm.mcp.server;

import com.learning.llm.mcp.model.McpResource;
import com.learning.llm.mcp.model.McpTool;
import com.learning.llm.mcp.model.McpPrompt;
import com.learning.llm.mcp.protocol.McpRequest;
import com.learning.llm.mcp.protocol.McpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件系统 MCP Server
 * 
 * 提供文件系统访问能力：
 * - Resources: 文件内容
 * - Tools: 文件读写操作
 * - Prompts: 文件操作模板
 * 
 * @author Learning
 */
@Slf4j
@Component
public class FileSystemMcpServer {
    
    private final String baseDir;
    
    public FileSystemMcpServer() {
        // 默认工作目录
        this.baseDir = System.getProperty("user.home") + "/mcp-workspace";
        createWorkspace();
    }
    
    /**
     * 创建工作目录
     */
    private void createWorkspace() {
        try {
            Path path = Paths.get(baseDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("创建 MCP 工作目录：{}", baseDir);
            }
        } catch (IOException e) {
            log.error("创建工作目录失败", e);
        }
    }
    
    /**
     * 处理 MCP 请求
     */
    public McpResponse handleRequest(McpRequest request) {
        log.info("收到 MCP 请求：method={}", request.getMethod());
        
        try {
            return switch (request.getMethod()) {
                case "initialize" -> handleInitialize(request);
                case "resources/list" -> handleResourcesList(request);
                case "resources/read" -> handleResourcesRead(request);
                case "tools/list" -> handleToolsList(request);
                case "tools/call" -> handleToolsCall(request);
                case "prompts/list" -> handlePromptsList(request);
                case "prompts/get" -> handlePromptsGet(request);
                default -> McpResponse.error(request.getId(), -32601, "Method not found");
            };
        } catch (Exception e) {
            log.error("处理 MCP 请求失败", e);
            return McpResponse.error(request.getId(), -32603, "Internal error: " + e.getMessage());
        }
    }
    
    /**
     * 初始化连接
     */
    private McpResponse handleInitialize(McpRequest request) {
        Map<String, Object> result = new HashMap<>();
        result.put("protocolVersion", "1.0");
        result.put("serverInfo", Map.of(
            "name", "filesystem-mcp-server",
            "version", "1.0.0"
        ));
        result.put("capabilities", Map.of(
            "resources", true,
            "tools", true,
            "prompts", true
        ));
        
        return McpResponse.success(request.getId(), result);
    }
    
    /**
     * 列出所有资源
     */
    private McpResponse handleResourcesList(McpRequest request) {
        try {
            List<McpResource> resources = Files.walk(Paths.get(baseDir), 1)
                .filter(Files::isRegularFile)
                .map(path -> {
                    String fileName = path.getFileName().toString();
                    return new McpResource(
                        "file://" + fileName,
                        fileName,
                        "文件：" + fileName,
                        "text/plain"
                    );
                })
                .collect(Collectors.toList());
            
            return McpResponse.success(request.getId(), Map.of("resources", resources));
        } catch (IOException e) {
            return McpResponse.error(request.getId(), -32603, "读取文件列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 读取资源内容
     */
    private McpResponse handleResourcesRead(McpRequest request) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> params = (Map<String, Object>) request.getParams();
            String uri = (String) params.get("uri");
            
            // 解析 URI: file://filename
            String fileName = uri.replace("file://", "");
            Path filePath = Paths.get(baseDir, fileName);
            
            if (!Files.exists(filePath)) {
                return McpResponse.error(request.getId(), -32602, "文件不存在：" + fileName);
            }
            
            String content = Files.readString(filePath);
            
            Map<String, Object> result = Map.of(
                "contents", List.of(Map.of(
                    "uri", uri,
                    "mimeType", "text/plain",
                    "text", content
                ))
            );
            
            return McpResponse.success(request.getId(), result);
        } catch (Exception e) {
            return McpResponse.error(request.getId(), -32603, "读取文件失败：" + e.getMessage());
        }
    }
    
    /**
     * 列出所有工具
     */
    private McpResponse handleToolsList(McpRequest request) {
        List<McpTool> tools = List.of(
            new McpTool(
                "read_file",
                "读取文件内容",
                Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "filename", Map.of(
                            "type", "string",
                            "description", "文件名"
                        )
                    ),
                    "required", List.of("filename")
                )
            ),
            new McpTool(
                "write_file",
                "写入文件内容",
                Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "filename", Map.of(
                            "type", "string",
                            "description", "文件名"
                        ),
                        "content", Map.of(
                            "type", "string",
                            "description", "文件内容"
                        )
                    ),
                    "required", List.of("filename", "content")
                )
            ),
            new McpTool(
                "list_files",
                "列出所有文件",
                Map.of("type", "object", "properties", Map.of())
            )
        );
        
        return McpResponse.success(request.getId(), Map.of("tools", tools));
    }
    
    /**
     * 调用工具
     */
    private McpResponse handleToolsCall(McpRequest request) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> params = (Map<String, Object>) request.getParams();
            String toolName = (String) params.get("name");
            @SuppressWarnings("unchecked")
            Map<String, Object> arguments = (Map<String, Object>) params.get("arguments");
            
            String result = switch (toolName) {
                case "read_file" -> readFile((String) arguments.get("filename"));
                case "write_file" -> writeFile(
                    (String) arguments.get("filename"),
                    (String) arguments.get("content")
                );
                case "list_files" -> listFiles();
                default -> throw new IllegalArgumentException("未知工具：" + toolName);
            };
            
            return McpResponse.success(request.getId(), Map.of(
                "content", List.of(Map.of(
                    "type", "text",
                    "text", result
                ))
            ));
        } catch (Exception e) {
            return McpResponse.error(request.getId(), -32603, "工具执行失败：" + e.getMessage());
        }
    }
    
    /**
     * 列出所有提示词
     */
    private McpResponse handlePromptsList(McpRequest request) {
        List<McpPrompt> prompts = List.of(
            new McpPrompt(
                "summarize_file",
                "总结文件内容",
                Map.of(
                    "filename", Map.of(
                        "description", "要总结的文件名",
                        "required", true
                    )
                )
            ),
            new McpPrompt(
                "analyze_code",
                "分析代码文件",
                Map.of(
                    "filename", Map.of(
                        "description", "要分析的代码文件名",
                        "required", true
                    )
                )
            )
        );
        
        return McpResponse.success(request.getId(), Map.of("prompts", prompts));
    }
    
    /**
     * 获取提示词
     */
    private McpResponse handlePromptsGet(McpRequest request) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> params = (Map<String, Object>) request.getParams();
            String promptName = (String) params.get("name");
            @SuppressWarnings("unchecked")
            Map<String, Object> arguments = (Map<String, Object>) params.get("arguments");
            
            String prompt = switch (promptName) {
                case "summarize_file" -> String.format(
                    "请总结以下文件的内容：%s\n\n要求：\n1. 提取关键信息\n2. 简洁明了\n3. 突出重点",
                    arguments.get("filename")
                );
                case "analyze_code" -> String.format(
                    "请分析以下代码文件：%s\n\n分析维度：\n1. 代码质量\n2. 潜在问题\n3. 改进建议",
                    arguments.get("filename")
                );
                default -> throw new IllegalArgumentException("未知提示词：" + promptName);
            };
            
            return McpResponse.success(request.getId(), Map.of(
                "messages", List.of(Map.of(
                    "role", "user",
                    "content", Map.of(
                        "type", "text",
                        "text", prompt
                    )
                ))
            ));
        } catch (Exception e) {
            return McpResponse.error(request.getId(), -32603, "获取提示词失败：" + e.getMessage());
        }
    }
    
    // ========== 工具实现 ==========
    
    private String readFile(String filename) throws IOException {
        Path filePath = Paths.get(baseDir, filename);
        if (!Files.exists(filePath)) {
            throw new IOException("文件不存在：" + filename);
        }
        return Files.readString(filePath);
    }
    
    private String writeFile(String filename, String content) throws IOException {
        Path filePath = Paths.get(baseDir, filename);
        Files.writeString(filePath, content);
        return "文件写入成功：" + filename;
    }
    
    private String listFiles() throws IOException {
        List<String> files = Files.walk(Paths.get(baseDir), 1)
            .filter(Files::isRegularFile)
            .map(path -> path.getFileName().toString())
            .collect(Collectors.toList());
        
        return "文件列表：\n" + String.join("\n", files);
    }
}

