# Spring AI Agent 最佳实践指南

## 概述

本项目已完全重构为 **Spring AI 最佳实践**实现，使用 Spring AI 框架的核心特性：
- **ChatClient**: 流畅的 AI 交互 API
- **Function Calling**: LLM 自动选择和调用工具
- **自动意图识别**: 无需手动编写规则

## 架构对比

### ❌ 旧实现（手动规则）
```
用户输入 → 关键词匹配 → 手动选择Skill → 手动提取参数 → 执行 → 返回
```

### ✅ 新实现（Spring AI）
```
用户输入 → LLM理解意图 → LLM选择工具 → LLM提取参数 → 自动调用 → LLM生成响应
```

## 核心组件

### 1. Tool（工具）- 替代旧的 Skill

**旧方式（自定义接口）：**
```java
@Component
public class WeatherSkill implements Skill {
    String execute(Map<String, Object> parameters) { ... }
}
```

**新方式（Spring AI Function）：**
```java
@Component
@Description("获取指定城市的天气信息")
public class WeatherTool implements Function<Request, Response> {
    public record Request(@Description("城市名称") String city) {}
    public record Response(String city, String weather, int temperature) {}
    
    @Override
    public Response apply(Request request) { ... }
}
```

**优势：**
- ✅ LLM 自动识别何时调用
- ✅ 自动参数提取和类型转换
- ✅ 类型安全（使用 Record）
- ✅ 无需手动注册

### 2. Agent（代理）

**旧方式（手动规则）：**
```java
@Component
public class SimpleAgent {
    // 手动关键词匹配
    private String analyzeIntent(String input) {
        if (input.contains("天气")) return "weather";
        ...
    }
    
    // 手动选择技能
    private Skill selectSkill(String intent) { ... }
    
    // 手动提取参数
    private Map<String, Object> extractParameters(...) { ... }
}
```

**新方式（Spring AI）：**
```java
@Component
public class SpringAiAgent {
    private final ChatClient chatClient;
    
    public String process(String userInput) {
        // LLM 自动处理一切
        return chatClient.prompt()
            .user(userInput)
            .call()
            .content();
    }
}
```

**优势：**
- ✅ 无需编写意图识别规则
- ✅ 无需编写参数提取逻辑
- ✅ 支持复杂的自然语言理解
- ✅ 代码量减少 90%

### 3. 配置（SpringAiConfig）

```java
@Configuration
public class SpringAiConfig {
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
            .defaultFunctions("weatherTool", "calculatorTool", "searchTool")
            .build();
    }
}
```

**说明：**
- 注册所有工具函数
- LLM 会根据用户输入自动选择调用哪个工具
- 支持多工具组合调用

## 使用方式

### 1. 配置 AI 模型

**方式一：使用 OpenAI（推荐用于生产）**
```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4o-mini
```

设置环境变量：
```bash
export OPENAI_API_KEY=sk-xxx
```

**方式二：使用 Ollama（推荐用于开发）**
```yaml
spring:
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        options:
          model: qwen2.5:7b
```

安装 Ollama：
```bash
# 下载并安装 Ollama
curl -fsSL https://ollama.com/install.sh | sh

# 拉取模型
ollama pull qwen2.5:7b
```

### 2. 启动应用

```bash
cd llm-demos
mvn spring-boot:run
```

### 3. 测试 API

**查看 Agent 信息：**
```bash
curl http://localhost:8080/api/agent/info
```

**查看工具列表：**
```bash
curl http://localhost:8080/api/agent/skills
```

**测试天气查询：**
```bash
curl "http://localhost:8080/api/agent/test?message=北京今天天气怎么样？"
```

**测试计算：**
```bash
curl "http://localhost:8080/api/agent/test?message=帮我计算123乘以456"
```

**测试搜索：**
```bash
curl "http://localhost:8080/api/agent/test?message=搜索Spring AI的最新信息"
```

## 工作原理

### Function Calling 流程

```
1. 用户：北京今天天气怎么样？
   ↓
2. LLM 分析：需要调用 weatherTool
   ↓
3. LLM 提取参数：{ "city": "北京" }
   ↓
4. Spring AI 自动调用：weatherTool.apply(new Request("北京"))
   ↓
5. 工具返回：Response(city="北京", weather="晴天", temperature=25)
   ↓
6. LLM 生成自然语言：北京今天晴天，气温25°C
   ↓
7. 返回给用户
```

### 多工具组合示例

**用户：** "北京和上海今天天气怎么样？哪个城市更暖和？"

**LLM 会自动：**
1. 调用 `weatherTool("北京")`
2. 调用 `weatherTool("上海")`
3. 比较温度
4. 生成综合回答

## 扩展新工具

### 步骤1：创建 Tool 类

```java
@Component
@Description("工具功能描述")
public class MyTool implements Function<MyTool.Request, MyTool.Response> {
    
    public record Request(
        @Description("参数描述") String param
    ) {}
    
    public record Response(String result) {}
    
    @Override
    public Response apply(Request request) {
        // 实现工具逻辑
        return new Response("结果");
    }
}
```

### 步骤2：注册到 ChatClient

```java
@Bean
public ChatClient chatClient(ChatClient.Builder builder) {
    return builder
        .defaultFunctions("weatherTool", "calculatorTool", "searchTool", "myTool")
        .build();
}
```

### 步骤3：直接使用

无需修改 Agent 代码，LLM 会自动识别何时调用新工具！

## 最佳实践

### 1. Tool 设计原则

✅ **单一职责**：每个 Tool 只做一件事
```java
// 好的设计
WeatherTool - 只查天气
CalculatorTool - 只做计算

// 不好的设计
UniversalTool - 什么都做
```

✅ **清晰的描述**：帮助 LLM 理解何时使用
```java
@Description("获取指定城市的实时天气信息，包括温度、天气状况和风力")
```

✅ **类型安全**：使用 Record 定义请求和响应
```java
public record Request(
    @Description("城市名称，如：北京、上海") String city,
    @Description("是否包含未来7天预报") boolean includeForecast
) {}
```

### 2. 错误处理

```java
@Override
public Response apply(Request request) {
    try {
        // 业务逻辑
        return new Response(true, result, null);
    } catch (Exception e) {
        log.error("工具执行失败", e);
        return new Response(false, null, e.getMessage());
    }
}
```

### 3. 日志记录

```java
@Override
public Response apply(Request request) {
    log.info("执行工具：{}", request);
    long start = System.currentTimeMillis();
    
    Response response = doWork(request);
    
    log.info("工具执行完成，耗时：{}ms", System.currentTimeMillis() - start);
    return response;
}
```

### 4. 性能优化

```java
@Component
public class WeatherTool implements Function<Request, Response> {
    
    @Cacheable(value = "weather", key = "#request.city")
    @Override
    public Response apply(Request request) {
        // 缓存天气查询结果
    }
}
```

## 对比总结

| 特性 | 旧实现（手动规则） | 新实现（Spring AI） |
|------|------------------|-------------------|
| 意图识别 | 手动关键词匹配 | LLM 自动理解 |
| 工具选择 | 硬编码 switch | LLM 自动选择 |
| 参数提取 | 正则表达式 | LLM 自动提取 |
| 多工具组合 | 不支持 | 自动支持 |
| 自然语言理解 | 有限 | 强大 |
| 代码复杂度 | 高（200+ 行） | 低（30 行） |
| 可维护性 | 差 | 优秀 |
| 扩展性 | 需修改 Agent | 只需添加 Tool |

## 常见问题

### Q1: 如何切换 AI 模型？

修改 `application.yml`，注释掉不用的配置：

```yaml
spring:
  ai:
    # 使用 OpenAI
    openai:
      api-key: xxx
    
    # 使用 Ollama（注释掉 OpenAI）
    # ollama:
    #   base-url: http://localhost:11434
```

### Q2: 工具没有被调用？

检查：
1. Tool 是否添加了 `@Component` 注解
2. Tool 是否在 `SpringAiConfig` 中注册
3. `@Description` 是否清晰描述了工具功能
4. 日志中是否有 Function Calling 相关信息

### Q3: 如何调试 Function Calling？

开启 DEBUG 日志：
```yaml
logging:
  level:
    org.springframework.ai: DEBUG
```

### Q4: 支持哪些 AI 模型？

- OpenAI: GPT-4, GPT-4o, GPT-3.5
- Ollama: Llama 3, Qwen 2.5, Mistral 等
- Azure OpenAI
- 通义千问
- 智谱 AI

## 下一步

1. ✅ 集成真实的天气 API
2. ✅ 添加更多实用工具（文件操作、数据库查询）
3. ✅ 实现流式响应
4. ✅ 添加对话历史管理
5. ✅ 实现 RAG（检索增强生成）

## 参考资料

- [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/)
- [Function Calling 指南](https://docs.spring.io/spring-ai/reference/api/functions.html)
- [ChatClient API](https://docs.spring.io/spring-ai/reference/api/chatclient.html)

