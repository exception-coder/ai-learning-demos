# MCP (Model Context Protocol) 实现指南

## 项目结构

```
llm-demos/src/main/java/com/learning/llm/mcp/
├── protocol/                   # MCP 协议定义
│   ├── McpRequest.java        # MCP 请求
│   └── McpResponse.java       # MCP 响应
├── model/                      # MCP 数据模型
│   ├── McpResource.java       # 资源定义
│   ├── McpTool.java           # 工具定义
│   └── McpPrompt.java         # 提示词定义
├── server/                     # MCP Server 实现
│   └── FileSystemMcpServer.java  # 文件系统 MCP Server
├── client/                     # MCP Client 实现
│   └── McpClient.java         # MCP 客户端
├── service/                    # 业务服务
│   └── McpIntegrationService.java  # MCP 集成服务
└── controller/                 # HTTP 接口
    ├── McpController.java     # MCP Server HTTP 接口
    └── McpDemoController.java # MCP 演示接口
```

## 快速开始

### 1. 启动应用

```bash
cd llm-demos
mvn spring-boot:run
```

### 2. 测试 MCP Server

#### 初始化连接
```bash
curl http://localhost:8080/api/mcp/init
```

**响应示例：**
```
MCP 连接初始化成功：
{protocolVersion=1.0, serverInfo={name=filesystem-mcp-server, version=1.0.0}, capabilities={resources=true, tools=true, prompts=true}}
```

#### 列出所有资源
```bash
curl http://localhost:8080/api/mcp/resources
```

#### 列出所有工具
```bash
curl http://localhost:8080/api/mcp/tools
```

**响应示例：**
```
可用工具：
{tools=[
  {name=read_file, description=读取文件内容, ...},
  {name=write_file, description=写入文件内容, ...},
  {name=list_files, description=列出所有文件, ...}
]}
```

#### 写入文件
```bash
curl -X POST "http://localhost:8080/api/mcp/write?filename=test.txt" \
  -H "Content-Type: text/plain" \
  -d "Hello, MCP!"
```

#### 读取文件
```bash
curl "http://localhost:8080/api/mcp/read?filename=test.txt"
```

#### 列出所有文件
```bash
curl http://localhost:8080/api/mcp/files
```

#### 列出所有提示词
```bash
curl http://localhost:8080/api/mcp/prompts
```

#### 获取提示词
```bash
curl "http://localhost:8080/api/mcp/prompt?name=summarize_file&filename=test.txt"
```

## MCP 核心概念

### 1. Resources（资源）

**定义：** 提供只读的数据访问

**示例：**
```java
McpResource resource = new McpResource(
    "file://test.txt",      // URI
    "test.txt",             // 名称
    "测试文件",              // 描述
    "text/plain"            // MIME 类型
);
```

**使用场景：**
- 文件内容
- 数据库记录
- API 响应数据

### 2. Tools（工具）

**定义：** 提供可执行的功能

**示例：**
```java
McpTool tool = new McpTool(
    "read_file",            // 工具名称
    "读取文件内容",          // 描述
    Map.of(                 // 参数定义（JSON Schema）
        "type", "object",
        "properties", Map.of(
            "filename", Map.of(
                "type", "string",
                "description", "文件名"
            )
        )
    )
);
```

**使用场景：**
- 文件读写
- 数据库查询
- API 调用

### 3. Prompts（提示词）

**定义：** 提供可复用的提示词模板

**示例：**
```java
McpPrompt prompt = new McpPrompt(
    "summarize_file",       // 提示词名称
    "总结文件内容",          // 描述
    Map.of(                 // 参数定义
        "filename", Map.of(
            "description", "要总结的文件名",
            "required", true
        )
    )
);
```

**使用场景：**
- 代码审查模板
- 文档生成模板
- 数据分析模板

## MCP 协议通信

### JSON-RPC 2.0 格式

**请求格式：**
```json
{
  "jsonrpc": "2.0",
  "id": "uuid",
  "method": "resources/list",
  "params": {}
}
```

**成功响应：**
```json
{
  "jsonrpc": "2.0",
  "id": "uuid",
  "result": {
    "resources": [...]
  }
}
```

**错误响应：**
```json
{
  "jsonrpc": "2.0",
  "id": "uuid",
  "error": {
    "code": -32603,
    "message": "Internal error"
  }
}
```

## 支持的方法

### 初始化
- `initialize` - 初始化连接

### 资源相关
- `resources/list` - 列出所有资源
- `resources/read` - 读取资源内容

### 工具相关
- `tools/list` - 列出所有工具
- `tools/call` - 调用工具

### 提示词相关
- `prompts/list` - 列出所有提示词
- `prompts/get` - 获取提示词

## 实现细节

### FileSystemMcpServer

**功能：**
- 提供文件系统访问能力
- 支持文件读写操作
- 提供文件操作提示词

**工作目录：**
- 默认：`~/mcp-workspace`
- 自动创建

**支持的工具：**
1. `read_file` - 读取文件
2. `write_file` - 写入文件
3. `list_files` - 列出文件

**支持的提示词：**
1. `summarize_file` - 总结文件内容
2. `analyze_code` - 分析代码文件

### McpClient

**功能：**
- 连接 MCP Server
- 发送 MCP 请求
- 处理 MCP 响应

**方法：**
- `initialize()` - 初始化连接
- `listResources()` - 列出资源
- `readResource(uri)` - 读取资源
- `listTools()` - 列出工具
- `callTool(name, args)` - 调用工具
- `listPrompts()` - 列出提示词
- `getPrompt(name, args)` - 获取提示词

## 与 Spring AI 集成

### 方式 1：在 Tool 中使用 MCP

```java
@Component
@Description("读取文件内容")
public class FileReadTool implements Function<Request, Response> {
    
    private final McpClient mcpClient;
    
    public record Request(@Description("文件名") String filename) {}
    public record Response(String content) {}
    
    @Override
    public Response apply(Request request) {
        // 通过 MCP 读取文件
        McpResponse mcpResponse = mcpClient.callTool("read_file", 
            Map.of("filename", request.filename()));
        
        // 提取内容
        String content = extractContent(mcpResponse);
        return new Response(content);
    }
}
```

### 方式 2：在 Agent 中使用 MCP Resources

```java
@Service
public class McpAwareAgent {
    
    private final ChatClient chatClient;
    private final McpClient mcpClient;
    
    public String process(String userInput) {
        // 1. 从 MCP 获取相关资源
        McpResponse resources = mcpClient.listResources();
        String context = buildContext(resources);
        
        // 2. 将资源作为上下文提供给 LLM
        return chatClient.prompt()
            .system("可用资源：\n" + context)
            .user(userInput)
            .call()
            .content();
    }
}
```

## 扩展 MCP Server

### 添加新的工具

```java
// 在 FileSystemMcpServer 中添加
private McpResponse handleToolsCall(McpRequest request) {
    String toolName = (String) params.get("name");
    
    return switch (toolName) {
        case "read_file" -> readFile(...);
        case "write_file" -> writeFile(...);
        case "delete_file" -> deleteFile(...);  // 新增工具
        default -> throw new IllegalArgumentException("未知工具");
    };
}
```

### 添加新的资源类型

```java
// 添加数据库资源
private McpResponse handleResourcesList(McpRequest request) {
    List<McpResource> resources = new ArrayList<>();
    
    // 文件资源
    resources.addAll(listFileResources());
    
    // 数据库资源（新增）
    resources.addAll(listDatabaseResources());
    
    return McpResponse.success(request.getId(), 
        Map.of("resources", resources));
}
```

## 最佳实践

### 1. 错误处理

```java
try {
    McpResponse response = mcpClient.callTool(toolName, args);
    if (response.getError() != null) {
        log.error("MCP 调用失败：{}", response.getError().getMessage());
        // 处理错误
    }
} catch (Exception e) {
    log.error("MCP 请求异常", e);
    // 处理异常
}
```

### 2. 日志记录

```java
log.info("发送 MCP 请求：method={}, params={}", method, params);
McpResponse response = sendRequest(request);
log.info("收到 MCP 响应：success={}", response.getError() == null);
```

### 3. 资源清理

```java
// 使用完 MCP 资源后及时清理
try {
    // 使用 MCP
} finally {
    // 清理资源
}
```

## 常见问题

### Q1: MCP 和 Function Calling 有什么区别？

**Function Calling:**
- OpenAI 专有协议
- 仅支持工具调用
- 每个应用独立实现

**MCP:**
- 开放标准协议
- 支持 Resources、Tools、Prompts
- 可跨应用共享 MCP Server

### Q2: 什么时候使用 MCP？

**适合使用 MCP：**
- 需要访问外部数据源（文件、数据库）
- 需要复用工具和提示词
- 多个 AI 应用共享相同的集成

**适合使用 Function Calling：**
- 简单的工具调用
- 应用特定的功能
- 不需要跨应用共享

### Q3: 如何保证 MCP 的安全性？

1. **权限控制**：限制 MCP Server 的访问权限
2. **输入验证**：验证所有输入参数
3. **审计日志**：记录所有 MCP 操作
4. **资源隔离**：限制 MCP Server 的资源访问范围

## 参考资料

- [MCP 官方文档](https://modelcontextprotocol.io/)
- [MCP GitHub](https://github.com/modelcontextprotocol)
- [MCP Specification](https://spec.modelcontextprotocol.io/)

---

**更新时间**: 2026-03-05

