# Spring AI Function Calling 深度解析

## 常见误解

❌ **误解**：Spring AI 只是用 LLM 做语义分析，提取关键词后还是我们的代码在选择和调用工具。

✅ **真相**：LLM 全程参与决策，Spring AI 实现了完整的 Function Calling 协议。

## 完整的交互流程

### 示例：用户问 "北京今天天气怎么样？"

#### 第 1 轮：用户请求 → LLM 决策

**发送给 LLM 的内容：**
```json
{
  "messages": [
    {
      "role": "user",
      "content": "北京今天天气怎么样？"
    }
  ],
  "functions": [
    {
      "name": "weatherTool",
      "description": "获取指定城市的天气信息，包括天气状况和温度",
      "parameters": {
        "type": "object",
        "properties": {
          "city": {
            "type": "string",
            "description": "城市名称，如：北京、上海、深圳"
          }
        },
        "required": ["city"]
      }
    },
    {
      "name": "calculatorTool",
      "description": "执行数学计算，支持加减乘除、括号、幂运算等",
      "parameters": { ... }
    },
    {
      "name": "searchTool",
      "description": "在互联网上搜索信息，获取最新资讯和知识",
      "parameters": { ... }
    }
  ]
}
```

**LLM 的响应（不是文本，而是函数调用请求）：**
```json
{
  "role": "assistant",
  "content": null,
  "function_call": {
    "name": "weatherTool",
    "arguments": "{\"city\":\"北京\"}"
  }
}
```

**关键点：**
- LLM 自己决定调用 `weatherTool`（不是我们的代码决定）
- LLM 自己提取参数 `{"city":"北京"}`（不是正则表达式）
- LLM 知道不应该调用 calculatorTool 或 searchTool

#### 第 2 轮：Spring AI 执行工具 → 返回结果给 LLM

**Spring AI 自动执行：**
```java
WeatherTool.Request request = new Request("北京");
WeatherTool.Response response = weatherTool.apply(request);
// response = Response(city="北京", weather="晴天", temperature=25, ...)
```

**将工具结果返回给 LLM：**
```json
{
  "messages": [
    {
      "role": "user",
      "content": "北京今天天气怎么样？"
    },
    {
      "role": "assistant",
      "content": null,
      "function_call": {
        "name": "weatherTool",
        "arguments": "{\"city\":\"北京\"}"
      }
    },
    {
      "role": "function",
      "name": "weatherTool",
      "content": "{\"city\":\"北京\",\"weather\":\"晴天\",\"temperature\":25,\"description\":\"北京今天晴天，气温25°C\"}"
    }
  ]
}
```

#### 第 3 轮：LLM 生成最终响应

**LLM 基于工具结果生成自然语言：**
```json
{
  "role": "assistant",
  "content": "北京今天是晴天，气温25°C，天气不错哦！"
}
```

**关键点：**
- LLM 不是简单返回工具结果
- LLM 会用自然语言重新组织信息
- LLM 可以添加额外的建议或评论

## 复杂场景：多工具组合

### 示例：用户问 "北京和上海今天天气怎么样？哪个更暖和？"

#### 第 1 轮：LLM 决定调用第一个工具

```json
{
  "function_call": {
    "name": "weatherTool",
    "arguments": "{\"city\":\"北京\"}"
  }
}
```

#### 第 2 轮：LLM 决定调用第二个工具

```json
{
  "function_call": {
    "name": "weatherTool",
    "arguments": "{\"city\":\"上海\"}"
  }
}
```

#### 第 3 轮：LLM 综合分析并回答

```json
{
  "content": "北京今天25°C，上海今天28°C。上海更暖和一些，温度高出3度。"
}
```

**关键点：**
- LLM 自己决定需要调用两次工具
- LLM 自己进行温度比较
- 我们的代码完全不需要处理这些逻辑

## 与旧实现的本质区别

### 旧实现（手动规则）

```java
public String process(String userInput) {
    // 1. 我们的代码分析意图
    String intent = analyzeIntent(userInput);  // 关键词匹配
    
    // 2. 我们的代码选择工具
    Skill skill = selectSkill(intent);  // switch/if-else
    
    // 3. 我们的代码提取参数
    Map<String, Object> params = extractParameters(userInput, intent);  // 正则
    
    // 4. 我们的代码调用工具
    String result = skill.execute(params);
    
    // 5. 我们的代码格式化输出
    return formatResponse(userInput, skill.getName(), result);
}
```

**问题：**
- ❌ 只能处理预定义的关键词
- ❌ 无法理解复杂的自然语言
- ❌ 无法处理多工具组合
- ❌ 每个新场景都要写规则

### 新实现（Spring AI）

```java
public String process(String userInput) {
    // LLM 自动处理一切
    return chatClient.prompt()
        .user(userInput)
        .call()
        .content();
}
```

**Spring AI 在背后做的事：**
1. 将所有工具的定义发送给 LLM
2. LLM 决定是否需要调用工具
3. LLM 决定调用哪个工具
4. LLM 生成工具调用参数
5. Spring AI 拦截并执行 Java 方法
6. 将结果返回给 LLM
7. LLM 生成最终的自然语言响应

**优势：**
- ✅ 理解任意自然语言表达
- ✅ 自动处理多工具组合
- ✅ 自动处理复杂推理
- ✅ 添加新工具无需修改 Agent

## 真实的 API 调用示例

### 使用 OpenAI API 的实际请求

**第 1 次请求（用户输入）：**
```bash
POST https://api.openai.com/v1/chat/completions
{
  "model": "gpt-4o-mini",
  "messages": [
    {"role": "user", "content": "北京今天天气怎么样？"}
  ],
  "functions": [
    {
      "name": "weatherTool",
      "description": "获取指定城市的天气信息",
      "parameters": {
        "type": "object",
        "properties": {
          "city": {"type": "string", "description": "城市名称"}
        }
      }
    }
  ]
}
```

**第 1 次响应（LLM 决定调用工具）：**
```json
{
  "choices": [{
    "message": {
      "role": "assistant",
      "function_call": {
        "name": "weatherTool",
        "arguments": "{\"city\":\"北京\"}"
      }
    }
  }]
}
```

**第 2 次请求（包含工具执行结果）：**
```bash
POST https://api.openai.com/v1/chat/completions
{
  "model": "gpt-4o-mini",
  "messages": [
    {"role": "user", "content": "北京今天天气怎么样？"},
    {
      "role": "assistant",
      "function_call": {
        "name": "weatherTool",
        "arguments": "{\"city\":\"北京\"}"
      }
    },
    {
      "role": "function",
      "name": "weatherTool",
      "content": "{\"city\":\"北京\",\"weather\":\"晴天\",\"temperature\":25}"
    }
  ]
}
```

**第 2 次响应（LLM 生成最终答案）：**
```json
{
  "choices": [{
    "message": {
      "role": "assistant",
      "content": "北京今天是晴天，气温25°C。"
    }
  }]
}
```

## Spring AI 的价值

### 1. 协议实现

Spring AI 实现了完整的 Function Calling 协议：
- 工具定义的序列化（Java → JSON Schema）
- 工具调用的拦截和执行
- 结果的序列化和返回
- 多轮对话的管理

### 2. 类型安全

```java
// Spring AI 自动处理类型转换
public record Request(
    String city,           // LLM 返回的 JSON 自动转换为 String
    boolean includeForecast  // 自动转换为 boolean
) {}
```

### 3. 错误处理

```java
// Spring AI 自动处理工具执行异常
try {
    Response response = weatherTool.apply(request);
} catch (Exception e) {
    // 将错误信息返回给 LLM，让 LLM 生成友好的错误提示
}
```

### 4. 并发控制

```java
// Spring AI 自动处理多工具并发调用
// 如果 LLM 决定同时调用多个工具，Spring AI 会并发执行
```

## 对比总结

| 维度 | 旧实现 | Spring AI |
|------|--------|-----------|
| 意图理解 | 关键词匹配 | LLM 深度理解 |
| 工具选择 | 硬编码规则 | LLM 智能决策 |
| 参数提取 | 正则表达式 | LLM 语义理解 |
| 多工具组合 | 不支持 | LLM 自动编排 |
| 复杂推理 | 不支持 | LLM 自动推理 |
| 自然语言生成 | 模板拼接 | LLM 生成 |
| 扩展性 | 需修改 Agent | 只需添加 Tool |
| 代码复杂度 | 200+ 行 | 30 行 |

## 结论

Spring AI 不是简单地用 LLM 做语义分析，而是：

1. **LLM 是决策者**：决定何时、调用哪个工具、传什么参数
2. **Spring AI 是执行者**：负责协议实现、类型转换、工具执行
3. **我们是提供者**：只需提供工具实现，其他全部自动化

这是一个完整的 **LLM-as-a-Brain** 架构，而不是简单的 NLP 工具。

