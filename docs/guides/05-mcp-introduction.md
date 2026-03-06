# MCP (Model Context Protocol) 学习指南

## 什么是 MCP？

**MCP (Model Context Protocol)** 是 Anthropic 在 2024 年推出的开放协议，用于标准化 AI 应用与外部系统的连接。

### 核心概念

**问题背景：**
- 每个 AI 应用都需要连接数据库、API、文件系统等外部资源
- 每次都要写自定义的集成代码
- 不同的 AI 应用无法共享这些集成

**MCP 的解决方案：**
- 提供标准化的协议
- AI 应用（MCP Client）通过协议连接到 MCP Server
- MCP Server 提供对外部资源的访问能力
- 一次编写，到处使用

### 架构图

```
┌─────────────────┐
│   AI 应用        │
│  (MCP Client)   │
└────────┬────────┘
         │ MCP Protocol
         │
┌────────┴────────┐
│   MCP Server    │
├─────────────────┤
│  - Resources    │  (数据源：文件、数据库记录)
│  - Prompts      │  (提示词模板)
│  - Tools        │  (可执行的功能)
└────────┬────────┘
         │
    ┌────┴────┐
    │ 外部资源 │
    │ 数据库   │
    │ 文件系统 │
    │ API      │
    └─────────┘
```

## MCP 的三大核心能力

### 1. Resources（资源）
提供数据访问能力，如：
- 文件内容
- 数据库记录
- API 响应数据

**特点：**
- 只读访问
- 由 AI 主动请求
- 用于提供上下文信息

### 2. Prompts（提示词）
提供预定义的提示词模板，如：
- 代码审查模板
- 文档生成模板
- 数据分析模板

**特点：**
- 可复用的提示词
- 支持参数化
- 提升 AI 输出质量

### 3. Tools（工具）
提供可执行的功能，如：
- 数据库查询
- 文件操作
- API 调用

**特点：**
- 可执行操作
- 由 AI 决定何时调用
- 类似 Function Calling

## MCP vs Function Calling

| 特性 | Function Calling | MCP |
|------|-----------------|-----|
| 协议标准 | OpenAI 专有 | 开放标准 |
| 适用范围 | 单次对话 | 跨应用共享 |
| 资源访问 | 需自己实现 | 内置 Resources |
| 提示词管理 | 需自己管理 | 内置 Prompts |
| 工具定义 | 每个应用独立 | 可复用 MCP Server |
| 生态系统 | 有限 | 可扩展 |

**关系：**
- MCP 的 Tools 类似于 Function Calling
- 但 MCP 提供了更完整的生态系统
- MCP Server 可以被多个 AI 应用共享

## 实际应用场景

### 场景 1：数据库 MCP Server
```
AI 应用 → MCP Client → 数据库 MCP Server → MySQL/PostgreSQL
```
- 提供数据库表结构（Resources）
- 提供 SQL 查询模板（Prompts）
- 提供查询执行功能（Tools）

### 场景 2：文件系统 MCP Server
```
AI 应用 → MCP Client → 文件系统 MCP Server → 本地文件
```
- 提供文件内容（Resources）
- 提供文件操作模板（Prompts）
- 提供文件读写功能（Tools）

### 场景 3：GitHub MCP Server
```
AI 应用 → MCP Client → GitHub MCP Server → GitHub API
```
- 提供仓库信息（Resources）
- 提供 PR 审查模板（Prompts）
- 提供 Issue 创建功能（Tools）

## MCP 协议通信流程

### 1. 初始化连接
```json
// Client → Server
{
  "jsonrpc": "2.0",
  "method": "initialize",
  "params": {
    "protocolVersion": "1.0",
    "clientInfo": {
      "name": "my-ai-app",
      "version": "1.0.0"
    }
  }
}

// Server → Client
{
  "jsonrpc": "2.0",
  "result": {
    "protocolVersion": "1.0",
    "serverInfo": {
      "name": "database-mcp-server",
      "version": "1.0.0"
    },
    "capabilities": {
      "resources": true,
      "prompts": true,
      "tools": true
    }
  }
}
```

### 2. 列出可用资源
```json
// Client → Server
{
  "jsonrpc": "2.0",
  "method": "resources/list"
}

// Server → Client
{
  "jsonrpc": "2.0",
  "result": {
    "resources": [
      {
        "uri": "db://users/table",
        "name": "Users Table",
        "description": "用户表结构和数据"
      }
    ]
  }
}
```

### 3. 读取资源
```json
// Client → Server
{
  "jsonrpc": "2.0",
  "method": "resources/read",
  "params": {
    "uri": "db://users/table"
  }
}

// Server → Client
{
  "jsonrpc": "2.0",
  "result": {
    "contents": [
      {
        "uri": "db://users/table",
        "mimeType": "application/json",
        "text": "{\"columns\": [\"id\", \"name\", \"email\"]}"
      }
    ]
  }
}
```

### 4. 调用工具
```json
// Client → Server
{
  "jsonrpc": "2.0",
  "method": "tools/call",
  "params": {
    "name": "query_database",
    "arguments": {
      "sql": "SELECT * FROM users LIMIT 10"
    }
  }
}

// Server → Client
{
  "jsonrpc": "2.0",
  "result": {
    "content": [
      {
        "type": "text",
        "text": "[{\"id\": 1, \"name\": \"Alice\"}]"
      }
    ]
  }
}
```

## MCP 的优势

### 1. 标准化
- 统一的协议规范
- 不同 AI 应用可以使用相同的 MCP Server
- 降低集成成本

### 2. 可复用
- 一次编写 MCP Server，多个应用使用
- 社区可以共享 MCP Server
- 避免重复造轮子

### 3. 安全性
- 统一的权限控制
- 审计和日志
- 隔离外部资源访问

### 4. 可扩展
- 支持自定义 Resources、Prompts、Tools
- 插件化架构
- 易于添加新功能

## MCP 生态系统

### 官方 MCP Servers
- **Filesystem**: 访问本地文件系统
- **GitHub**: 集成 GitHub API
- **PostgreSQL**: 连接 PostgreSQL 数据库
- **Slack**: 集成 Slack API

### 社区 MCP Servers
- **Google Drive**: 访问 Google Drive
- **Notion**: 集成 Notion API
- **Jira**: 集成 Jira API

## Spring AI 与 MCP

Spring AI 目前主要支持 Function Calling，但可以通过以下方式集成 MCP：

### 方式 1：MCP Client 集成
```java
// 使用 MCP Client 连接到 MCP Server
McpClient mcpClient = new McpClient("http://localhost:3000");

// 在 Spring AI Tool 中调用 MCP
@Component
public class DatabaseTool implements Function<Request, Response> {
    private final McpClient mcpClient;
    
    @Override
    public Response apply(Request request) {
        // 通过 MCP 调用数据库查询
        return mcpClient.callTool("query_database", request);
    }
}
```

### 方式 2：实现 MCP Server
```java
// 使用 Spring Boot 实现 MCP Server
@RestController
@RequestMapping("/mcp")
public class McpServerController {
    
    @PostMapping("/resources/list")
    public McpResponse listResources() {
        // 返回可用资源列表
    }
    
    @PostMapping("/tools/call")
    public McpResponse callTool(@RequestBody McpRequest request) {
        // 执行工具调用
    }
}
```

## 学习资源

- [MCP 官方文档](https://modelcontextprotocol.io/)
- [MCP GitHub](https://github.com/modelcontextprotocol)
- [MCP Servers 列表](https://github.com/modelcontextprotocol/servers)

## 下一步

接下来我们将实现：
1. 一个简单的 MCP Server（提供文件系统访问）
2. 一个 MCP Client（在 Spring AI 中使用）
3. 完整的集成示例

---

**更新时间**: 2026-03-05

