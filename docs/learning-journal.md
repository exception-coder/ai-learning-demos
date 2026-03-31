# Spring AI 学习日志

> 本文档记录学习 Spring AI 过程中的所有疑问、解答和关键知识点，最终将整理成完整的学习手册。

## 目录

- [核心概念理解](#核心概念理解)
- [技术疑问与解答](#技术疑问与解答)
- [实践经验](#实践经验)
- [常见误区](#常见误区)

---

## 核心概念理解

### 1. Spring AI 是什么？

Spring AI 是 Spring 生态中用于构建 AI 应用的框架，提供：
- 统一的 AI 模型访问接口
- Function Calling（工具调用）机制
- RAG（检索增强生成）支持
- 向量数据库集成
- 多模态支持

**核心价值：**
- 屏蔽不同 AI 提供商的差异
- 提供 Spring 风格的开发体验
- 简化 AI 应用开发复杂度

### 2. Agent 和 Tool 的关系

**Agent（代理）：**
- 智能助手，负责理解用户意图
- 决策调用哪些工具
- 编排多个工具完成复杂任务

**Tool（工具）：**
- 独立的能力单元
- 完成特定的单一任务
- 可以被 Agent 调用

**类比：**
- Agent = 项目经理（决策者）
- Tool = 专业工程师（执行者）

### 3. Function Calling 机制

**定义：**
Function Calling 是 LLM 调用外部函数的能力，让 LLM 可以：
- 访问实时数据
- 执行计算任务
- 调用外部 API
- 操作数据库

**工作流程：**
```
用户输入
  ↓
LLM 分析并决定调用哪个函数
  ↓
LLM 生成函数调用请求（函数名 + 参数）
  ↓
Spring AI 拦截并执行真实的 Java 方法
  ↓
工具返回结果
  ↓
LLM 基于结果生成自然语言响应
  ↓
返回给用户
```

---

## 技术疑问与解答

### Q1: Spring AI 只是用 LLM 做语义分析吗？

**疑问：**
> "所以你整合了 Spring AI，仅仅是在做语义分析抽关键字的时候调用 LLM 抽取了而已是吧？"

**解答：**

❌ **不是！** 这是一个常见误解。

**误解的理解：**
```
用户输入 → LLM提取关键词 → 我们的代码选择工具 → 我们的代码调用
```

**实际的工作方式：**
```
用户输入 
  ↓
LLM 理解意图并决定调用哪个工具（LLM 决策）
  ↓
LLM 生成工具调用请求（包含函数名和参数）（LLM 决策）
  ↓
Spring AI 拦截并执行真实的 Java 方法（框架执行）
  ↓
工具返回结果给 LLM
  ↓
LLM 基于工具结果生成自然语言响应（LLM 决策）
```

**关键区别：**

| 维度 | 手动实现 | Spring AI + LLM |
|------|----------|-----------------|
| 意图识别 | 关键词匹配（我们写代码） | LLM 深度理解 |
| 工具选择 | if/switch（我们写代码） | LLM 智能决策 |
| 参数提取 | 正则表达式（我们写代码） | LLM 语义理解 |
| 多工具组合 | 不支持 | LLM 自动编排 |
| 决策者 | 我们的代码 | LLM |

**实际示例：**

用户："北京和上海今天天气怎么样？哪个更暖和？"

**手动实现：**
```java
// 需要我们写代码处理
if (input.contains("和")) {
    String[] cities = extractCities(input);  // 正则提取
    for (String city : cities) {
        results.add(weatherSkill.execute(city));
    }
    // 还要写代码比较温度
    compareTemperature(results);
}
```

**Spring AI：**
```java
// LLM 自动处理：
// 1. 识别需要查询两个城市
// 2. 自动调用 weatherTool("北京")
// 3. 自动调用 weatherTool("上海")
// 4. 自动比较温度
// 5. 生成自然语言回答

chatClient.prompt()
    .user(userInput)
    .call()
    .content();
```

**完整的 API 交互过程：**

**第 1 轮：用户请求 → LLM**
```json
// 发送给 LLM
{
  "messages": [{"role": "user", "content": "北京今天天气怎么样？"}],
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

// LLM 响应（不是文本，是函数调用请求）
{
  "function_call": {
    "name": "weatherTool",
    "arguments": "{\"city\":\"北京\"}"
  }
}
```

**第 2 轮：Spring AI 执行工具**
```java
// Spring AI 自动执行
WeatherTool.Response response = weatherTool.apply(new Request("北京"));
// 返回：Response(city="北京", weather="晴天", temperature=25)
```

**第 3 轮：工具结果 → LLM → 最终响应**
```json
// 将工具结果发送给 LLM
{
  "messages": [
    {"role": "user", "content": "北京今天天气怎么样？"},
    {"role": "assistant", "function_call": {...}},
    {"role": "function", "name": "weatherTool", 
     "content": "{\"city\":\"北京\",\"weather\":\"晴天\",\"temperature\":25}"}
  ]
}

// LLM 生成最终响应
{
  "content": "北京今天是晴天，气温25°C，天气不错哦！"
}
```

**核心要点：**
1. **LLM 是决策者**：决定何时、调用哪个工具、传什么参数
2. **Spring AI 是执行者**：负责协议实现、类型转换、工具执行
3. **我们是提供者**：只需提供工具实现，其他全部自动化

这是一个完整的 **LLM-as-a-Brain** 架构，而不是简单的 NLP 工具。

---

## 实践经验

### 1. 从手动规则到 Spring AI 的重构

**重构前（手动实现）：**
- 代码量：200+ 行
- 需要手动编写意图识别规则
- 需要手动编写参数提取逻辑
- 每个新场景都要修改代码
- 无法处理复杂的自然语言

**重构后（Spring AI）：**
- 代码量：30 行
- LLM 自动理解意图
- LLM 自动提取参数
- 添加新工具无需修改 Agent
- 支持任意复杂的自然语言

**代码对比：**

```java
// 旧实现：SimpleAgent.java (188 行)
public String process(String userInput) {
    // 手动意图识别
    String intent = analyzeIntent(userInput);
    
    // 手动技能选择
    Skill skill = selectSkill(intent);
    
    // 手动参数提取
    Map<String, Object> params = extractParameters(userInput, intent);
    
    // 执行技能
    String result = skill.execute(params);
    
    // 格式化响应
    return formatResponse(userInput, skill.getName(), result);
}

// 新实现：SpringAiAgent.java (30 行)
public String process(String userInput) {
    return chatClient.prompt()
        .user(userInput)
        .call()
        .content();
}
```

### 2. Tool 的设计原则

**使用 Function 接口：**
```java
@Component
@Description("工具功能描述")
public class MyTool implements Function<Request, Response> {
    
    public record Request(
        @Description("参数描述") String param
    ) {}
    
    public record Response(String result) {}
    
    @Override
    public Response apply(Request request) {
        // 实现逻辑
        return new Response("结果");
    }
}
```

**设计要点：**
1. **单一职责**：每个 Tool 只做一件事
2. **清晰描述**：@Description 帮助 LLM 理解何时使用
3. **类型安全**：使用 Record 定义请求和响应
4. **参数说明**：每个参数都要有 @Description

### 3. 配置 AI 模型

**开发环境推荐：Ollama（本地免费）**
```yaml
spring:
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        options:
          model: qwen2.5:7b
```

**生产环境推荐：OpenAI**
```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4o-mini
```

---

## 常见误区

### 误区 1：Spring AI 只是封装了 HTTP 调用

❌ **错误理解：** Spring AI 只是帮我们调用 OpenAI API

✅ **正确理解：** Spring AI 提供了完整的 AI 应用开发框架：
- Function Calling 协议实现
- 类型安全的参数转换
- 多轮对话管理
- 工具执行的拦截和编排
- 错误处理和重试机制
- 流式响应支持
- RAG 和向量数据库集成

### 误区 2：需要手动判断何时调用工具

❌ **错误理解：** 我们需要写代码判断用户是否需要查天气

✅ **正确理解：** LLM 自动判断：
- 用户："北京天气" → LLM 自动调用 weatherTool
- 用户："你好" → LLM 直接回答，不调用工具
- 用户："北京天气怎么样，顺便帮我算一下 100+200" → LLM 自动调用两个工具

### 误区 3：Tool 和 Skill 是一样的

❌ **错误理解：** 只是换了个名字

✅ **正确理解：** 本质不同：

**旧的 Skill：**
- 自定义接口
- 手动注册
- 手动调用
- 参数是 Map

**新的 Tool：**
- 标准 Function 接口
- Spring 自动发现
- LLM 自动调用
- 类型安全的 Record

### 误区 4：只能用 OpenAI

❌ **错误理解：** Spring AI 只支持 OpenAI

✅ **正确理解：** 支持多种模型：
- OpenAI (GPT-4, GPT-3.5)
- Ollama (本地模型)
- Azure OpenAI
- 通义千问
- 智谱 AI
- Claude
- Gemini

---

## 学习路径建议

### 阶段 1：理解核心概念（当前阶段）
- [x] 理解 Agent 和 Tool 的关系
- [x] 理解 Function Calling 机制
- [x] 理解 Spring AI 的价值
- [ ] 实际运行项目并测试

### 阶段 2：实践基础功能
- [ ] 配置 Ollama 本地模型
- [ ] 测试基础的工具调用
- [ ] 观察 LLM 的决策过程
- [ ] 添加自定义工具

### 阶段 3：进阶功能
- [ ] 实现流式响应
- [ ] 添加对话历史管理
- [ ] 集成真实的外部 API
- [ ] 实现多工具组合场景

### 阶段 4：RAG 和向量数据库
- [ ] 理解 RAG 原理
- [ ] 集成向量数据库
- [ ] 实现文档检索
- [ ] 构建知识库问答系统

### 阶段 5：生产化
- [ ] 错误处理和重试
- [ ] 性能优化和缓存
- [ ] 监控和日志
- [ ] 安全性考虑

---

## 待整理的问题

> 记录学习过程中遇到的新问题，后续补充解答

- [ ] 如何处理 LLM 选择错误的工具？
- [ ] 如何限制工具的调用次数？
- [ ] 如何实现工具调用的权限控制？
- [ ] 如何优化 Function Calling 的性能？
- [ ] 如何处理工具执行超时？

---

## 参考资料

- [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/)
- [Function Calling 指南](https://docs.spring.io/spring-ai/reference/api/functions.html)
- [ChatClient API](https://docs.spring.io/spring-ai/reference/api/chatclient.html)
- [项目最佳实践文档](../llm-demos/SPRING_AI_BEST_PRACTICES.md)
- [Function Calling 深度解析](../llm-demos/FUNCTION_CALLING_EXPLAINED.md)

---

### Q: Cursor Skill 为什么是 Markdown 提示词而不是代码？

**疑问：**
> 我创建的 Skill（SKILL.md、gitignore-templates.md）全是 Markdown 提示词，为什么不是代码？对外提供能力的时候，调用方拿到一段 Markdown 就能完成任务了？

**解答：**

这里存在两个层面的 Skill，容易混淆：

| | Cursor Agent Skill | 应用级 Skill（Java 代码） |
|---|---|---|
| **载体** | Markdown 提示词（SKILL.md） | Java 代码（WeatherSkill.java） |
| **执行者** | Cursor AI（IDE 里的 Agent） | Spring Boot 应用（JVM） |
| **运行方式** | AI 读取指令 → 调用终端/文件编辑器执行 | JVM 运行编译后的 class |
| **本质** | 给 AI 的操作手册 | 给机器执行的业务逻辑 |

**为什么 Cursor Skill 只需要 Markdown？**

因为 Cursor 的 Agent 本身就是 AI（LLM），它的"编程语言"就是自然语言。SKILL.md 相当于给一个聪明的助理发了份操作手册，它自己能理解并动手执行。

**对外提供能力时，要看调用者是谁：**

```
调用者是 AI（如 Cursor Agent）
  → 返回 Markdown 指令就够了
  → AI 能理解指令，自己调用终端、编辑文件去完成

调用者是程序（如 MCP Client、HTTP Client）
  → 必须是可执行代码，返回执行结果
  → 程序看不懂 Markdown，它需要你帮它干完活并返回结果
```

**三种模式对比：**

1. **Cursor Skill**：AI 读 Markdown → AI 自己动手执行 → 适合 AI Agent 调用
2. **MCP Tool**：程序调用 API → 你的代码执行逻辑 → 返回结构化结果 → 适合任意程序调用
3. **AI + MCP**：AI 通过 MCP 协议调用你的代码 → 代码执行并返回结果 → AI 理解结果继续工作

**关键要点：**
- Skill（Markdown）= 给 AI 的操作手册，因为 AI 能理解自然语言
- Tool（代码）= 给程序的可调用接口，因为程序只认 API 和结构化数据
- Cursor 的特殊之处：它是 AI + 自带工具（终端、文件编辑器），所以手册就够了
- 如果要对外暴露能力给其他系统，必须用代码实现（如 MCP Server）

**常见误区：**
- 误以为所有 Skill 都是提示词 → 只有给 AI Agent 的 Skill 是提示词
- 误以为返回 Markdown 给任何调用方都能工作 → 只对 AI 调用者有效
- 混淆 Cursor IDE 层面的 Skill 和应用代码层面的 Skill → 两者概念类似但载体完全不同

---

### Q: Cursor Skill（Markdown）如何被执行？

**疑问：**
> 看到 `.cursor/skills/git-init-remote/SKILL.md` 是 Markdown 格式，这种文件如何让 Cursor 执行？

**解答：**

Cursor 中的 Markdown Skill 有**三种触发方式**：

#### 方式 1：@ 符号调用（最常用）

在 Cursor 的聊天框中，输入 `@` 会弹出 Skill 列表，选择你要的 Skill：

```
@git-init-remote
```

然后 Cursor AI 会：
1. 读取 `SKILL.md` 的内容
2. 理解执行流程
3. 按照步骤执行（调用终端命令、读写文件等）
4. 如果缺少必需参数，会主动询问你

#### 方式 2：自然语言触发

直接用自然语言描述需求，如果匹配到 Skill 的 `description`，Cursor 会自动调用：

```
帮我把这个项目推送到 GitHub
```

Cursor AI 会识别到这个需求匹配 `git-init-remote` 的描述，自动执行该 Skill。

#### 方式 3：直接引用文件

在聊天中直接 @ 引用 Skill 文件：

```
@ai-learning-demos/.cursor/skills/git-init-remote/SKILL.md 
帮我初始化 Git 并推送到远程仓库
```

#### 执行原理

```
用户触发 Skill
  ↓
Cursor AI 读取 SKILL.md（作为 Prompt）
  ↓
AI 理解执行步骤（Step 1, Step 2...）
  ↓
AI 调用 Cursor 内置工具执行：
  - Shell 工具（执行 git 命令）
  - 文件读写工具（生成 .gitignore）
  - 询问工具（获取用户输入）
  ↓
按步骤完成任务
```

#### 关键要点

1. **Skill 是给 AI 的操作手册**
   - AI 读懂 Markdown 中的步骤
   - AI 自己调用工具（终端、文件编辑器）执行

2. **不需要编译或安装**
   - 放在 `.cursor/skills/` 目录即可
   - Cursor 会自动扫描并加载

3. **AI 会严格按照步骤执行**
   - Step 1 → Step 2 → Step 3...
   - 遇到错误会按"错误处理"章节处理

4. **参数获取是自动的**
   - Skill 中定义了"必需参数"
   - AI 会主动询问缺失的参数

#### 实际示例

以 `git-init-remote` 为例，执行流程：

```markdown
## 必需参数
- project_root: 项目根目录
- remote_url: 远程仓库地址
- git_username: Git 账号
- git_password: Git 密码或 Token

## 执行流程
Step 1: 检测项目类型（AI 读取 pom.xml/package.json 等）
Step 2: 生成 .gitignore（AI 调用文件写入工具）
Step 3: 初始化 Git（AI 执行 `git init`）
Step 4: 配置远程仓库（AI 执行 `git remote add origin`）
Step 5: 首次提交并推送（AI 执行 `git add/commit/push`）
Step 6: 验证（AI 执行 `git remote -v`）
```

AI 会按照这些步骤，自动调用相应的工具完成任务。

#### 常见问题

**Q: 为什么不是可执行代码？**
因为 Cursor AI 本身就是 LLM，它能理解自然语言指令并调用工具执行。Markdown 对 AI 来说就是"编程语言"。

**Q: 如何调试 Skill？**
- 在 Skill 中添加详细的步骤说明
- 观察 AI 的执行过程
- 如果 AI 理解错误，优化 Markdown 的描述

**Q: Skill 和 MCP Tool 的区别？**
- Skill（Markdown）= 给 Cursor AI 的操作手册，AI 自己执行
- MCP Tool（代码）= 给任意程序调用的 API，返回执行结果

**常见误区：**
- ❌ 以为 Skill 需要编译或安装
- ✅ Skill 是纯文本，放在指定目录即可被 Cursor 加载
- ❌ 以为需要手动调用每个步骤
- ✅ AI 会自动按照 Skill 中的步骤顺序执行

---

---

### Q: Skill 是 AI 的指令，Tool 是固化的执行代码？

**疑问：**
> Skill 是 AI Agent 用的指令，tools/call 是固化的逻辑执行代码？

**解答：**

对，这个理解是准确的。

| | Skill（指令） | Tool（代码） |
|---|---|---|
| **是什么** | "怎么做"的说明书 | "帮你做完"的函数 |
| **谁来执行** | AI 读懂后自己动手 | 代码跑完返回结果 |
| **灵活性** | 高 — AI 可以根据情况变通 | 低 — 固定逻辑，传参进去出结果 |
| **适合场景** | 步骤多、需要判断的复杂流程 | 明确、重复、不需要判断的操作 |

**它们的配合关系：**

```
AI Agent 读取 Skill 指令（理解要做什么）
  → 指令说"调用 git_init 这个 Tool"
    → Tool 执行 git init，返回成功/失败
  → AI 根据返回结果，决定下一步
```

**类比：**
- Skill = 给实习生的操作手册（实习生自己理解、判断、执行）
- Tool = 自动化流水线的按钮（按下去，固定逻辑跑完，吐出结果）

**关键要点：**
- AI 负责"想"（理解意图、规划步骤、处理异常）
- Tool 负责"干"（执行具体操作、返回确定结果）
- Skill 是连接两者的桥梁 — 告诉 AI 什么时候该调用哪个 Tool
- 项目中 SimpleAgent（Agent）+ WeatherSkill.execute()（Tool）就是这个模式

---

### Q: AI 应用开发涉及这么多技术栈，它们分别用在什么环节？

**疑问：**
> 看到一份课程大纲罗列了大模型原理、提示词工程、Spring AI、LangChain4j、FunctionCall、MCP、RAG、Agent、Workflow、长期记忆、上下文工程、Skill、AgentScope、模型微调等技术，想知道每个技术的定位和使用环节。

**解答：**

AI 应用开发的技术栈可以分为五层：

```
应用层（产品）    → 智能客服、通用Agent、旅行助手
编排层（思考）    → ReactAgent、ReflectionAgent、PlanExecuteAgent、Workflow
能力层（做事）    → FunctionCall、MCP、Skill、RAG
模型层（大脑）    → 大模型原理、提示词工程、模型微调
框架层（工具链）  → Spring AI、LangChain4j、Java、Python
```

**核心关系链：**
- FunctionCall 是底层机制（模型如何调用函数）
- MCP 是连接协议（标准化暴露函数清单）
- Skill 是组织方式（打包相关函数为能力单元）
- Agent 是编排者（规划用哪些 Skill 完成任务）
- Workflow 是流程引擎（预定义 Agent 执行顺序）

**关键要点：**
- 不需要一次性学完所有技术，按层逐步深入
- 第一阶段重点：提示词工程 + Spring AI + FunctionCall
- 第二阶段重点：MCP + RAG + Agent
- 模型微调是进阶内容，大部分场景用提示词工程 + RAG 就够了

详细整理见：[07-AI 应用开发技术栈全景图](guides/07-ai-tech-stack-overview.md)

---

### Q: PyTorch、SFT、RL 分别是什么？和我做的应用开发有什么关系？

**疑问：**
> 同行提到在搞 PyTorch 微调、LLM 的 SFT、RL，这些技术栈在全景图中处于什么位置？

**解答：**

这三个技术都属于模型层更底层的"造模型"领域：

| 技术 | 全称 | 做什么 |
|------|------|--------|
| **PyTorch** | — | 深度学习框架，模型训练的基础设施 |
| **SFT** | Supervised Fine-Tuning | 用标注的问答数据微调模型，让模型学会特定领域知识 |
| **RL/RLHF** | Reinforcement Learning from Human Feedback | SFT 之后让人类打分优化模型输出质量 |

**大模型训练三阶段：**
```
预训练（海量数据，需几千张 GPU）
  → SFT（标注数据微调，关键是数据处理：过滤/切割/结构化/权重调整）
    → RLHF（人类反馈对齐，让回答更符合人类偏好）
```

**和应用开发的关系：**
- "造模型"= 算法工程师的活（PyTorch + SFT + RL）
- "用模型"= 应用开发者的活（Spring AI + RAG + Agent）
- 两条路线互补：造模型的人产出模型，用模型的人做成产品
- 对于工作助手和岗位分析项目，用现有大模型 + RAG + 提示词工程就够了
- 只有当通用模型在你的业务场景表现太差时，才需要考虑 SFT 微调

**关键要点：**
- PyTorch 之于模型训练 = Spring 之于后端开发，是基础设施
- SFT 的核心不是训练本身，而是数据处理（同行提到的过滤、切割、权重调整就是这部分）
- RLHF 是 ChatGPT 变得"好用"的关键，但需要大量人工标注成本
- 应用开发者不一定要会这些，但理解原理有助于做出更好的技术决策

## 更新日志

- 2026-03-05: 创建学习日志，记录 Function Calling 机制的理解
- 2026-03-05: 记录 Cursor Skill 为什么是 Markdown 而不是代码，以及 AI Skill vs MCP Tool 的区别
- 2026-03-05: 记录 Skill（AI 指令）与 Tool（固化执行代码）的区别和配合关系
- 2026-03-05: 整理 AI 应用开发技术栈全景图，记录各技术定位和使用环节
- 2026-03-05: 记录 PyTorch/SFT/RL 的定位，以及"造模型"vs"用模型"两条路线的区别
- 2026-03-05: 补充说明 SFT/RL/PyTorch 属于深入知识点，只有微调大模型时才需要掌握，应用开发优先使用通用模型+RAG+提示词工程
- 2026-03-05: 纠正工作助手技术选型：简单问答用 RAG 就够了，不需要一上来就上 ReactAgent，避免过度设计
- 2026-03-05: 解析 ReactAgent 三步（Thought/Action/Observation）的具体实现：Thought 是 LLM 自己想的、Action 是框架自动调用 @Tool 方法、Observation 是 Tool 返回结果框架回传。开发者只需定义 Tool 函数体
- 2026-03-05: 解析 .call() 内部机制：框架把 @Tool 清单塞进 Prompt 发给 LLM，LLM 靠 description 决定调不调、调哪个。.call() 一行代码内部可能和 LLM 来回多轮
- 2026-03-06: 解析向量检索的真实能力和局限：向量库能做语义相似度匹配，但实际会返回多条候选结果（带相似度分数），需要结合关键词过滤、重排序等优化。示例中"昨天会议 系统"精准匹配到 ERP 会议纪要是理想化的，真实场景会有噪音数据
- 2026-03-06: 解析 Cursor Skill（Markdown）的执行方式：通过 @ 符号调用、自然语言触发或直接引用文件。AI 读取 SKILL.md 作为操作手册，理解步骤后调用 Cursor 内置工具（Shell、文件读写、询问）执行。不需要编译，放在 `.cursor/skills/` 即可

---

### Q: 向量检索真的能精准匹配到相关文档吗？

**疑问：**
> 看到 ReAct 示例中，仅用 "昨天会议 系统" 就能从向量库精准返回 ["会议纪要：讨论了新上线的 ERP 系统，需要全员注册"]，向量库真有这么厉害吗？

**解答：**

这个例子确实**理想化**了。让我解释向量检索的真实能力和局限：

#### 向量检索能做到的：

✅ **语义相似度匹配**
- 搜 "昨天会议" 能匹配到 "会议纪要"、"yesterday meeting"、"上次开会" 等语义相近的内容
- 不需要精确关键词，"系统密码" 能找到 "ERP 登录凭证"

✅ **跨语言理解**
- 中文查询可能匹配到英文文档（取决于 embedding 模型）

✅ **模糊匹配**
- 容忍拼写错误、同义词替换

#### 真实场景的表现：

```python
# 用户输入
"昨天会议 系统"

# 向量库实际返回（Top 3，带相似度分数）
[
  {
    "content": "会议纪要：讨论了新上线的 ERP 系统，需要全员注册",
    "score": 0.82  # ← 相似度较高
  },
  {
    "content": "系统维护通知：本周三晚上 10 点进行系统升级",
    "score": 0.76  # ← 也包含"系统"，但不太相关
  },
  {
    "content": "上周五会议：Q1 销售目标讨论",
    "score": 0.71  # ← 包含"会议"，但不是昨天的
  }
]

# LLM 需要从这些结果中判断哪个最相关
```

#### 实际工程中的优化：

```java
@Tool(description = "搜索会议记录和系统文档")
public List<Document> searchLogs(
    @ToolParam(description = "搜索关键词") String keyword,
    @ToolParam(description = "文档类型：meeting/system/all") String type
) {
    // 1. 向量检索（语义搜索）
    List<Document> vectorResults = vectorStore.similaritySearch(
        SearchRequest.query(keyword)
            .withTopK(10)  // 先取 10 条候选
            .withSimilarityThreshold(0.7)  // 过滤低相似度结果
    );
    
    // 2. 混合检索：结合关键词过滤
    if (!"all".equals(type)) {
        vectorResults = vectorResults.stream()
            .filter(doc -> doc.getMetadata().get("type").equals(type))
            .collect(Collectors.toList());
    }
    
    // 3. 重排序（可选）：用更强的模型重新打分
    return rerankResults(vectorResults, keyword).subList(0, 3);
}
```

#### 为什么示例看起来"太准"？

1. **简化了噪音**：真实场景会返回很多不相关结果
2. **省略了阈值调优**：需要反复测试 `topK`、`similarityThreshold` 参数
3. **没展示失败情况**：如果向量库里根本没有相关文档，会返回空或低质量结果

#### 真实的 ReAct 循环可能是这样：

```text
Thought: 用户问系统密码，先搜会议记录
Action: searchLogs("昨天会议 系统", "meeting")
Observation: [
  "会议纪要：ERP 系统上线...",
  "上周五会议：讨论 CRM 系统...",  ← 噪音
  "系统维护通知..."  ← 不相关
]

Thought: 找到了 ERP 系统，现在查密码
Action: searchLogs("ERP 密码", "system")
Observation: [
  "ERP 系统默认密码：admin123",
  "ERP 用户手册..."
]

Thought: 找到了，可以回答用户
Answer: 昨天会议讨论的 ERP 系统，默认密码是 admin123
```

#### 关键要点：

1. **向量检索很强大，但不是魔法**
   - 它返回的是"可能相关"的候选列表，不是"一定正确"的答案
   
2. **实际项目需要配套措施：**
   - 高质量的 embedding 模型（如 OpenAI text-embedding-3、BGE 等）
   - 合理的数据分块和元数据设计
   - 混合检索策略（向量 + 关键词 + 过滤条件）
   - 充分的测试和参数调优

3. **示例的目的是演示流程**
   - 为了清晰展示 ReAct 的 Thought → Action → Observation 循环
   - 实际效果取决于数据质量和检索策略

4. **向量检索的核心价值**
   - 不是"精准匹配"，而是"语义理解"
   - 用户不需要知道精确的关键词，用自然语言描述就能找到相关内容
   - 配合 LLM 的理解能力，可以从多个候选结果中筛选出最相关的

**常见误区：**
- ❌ 以为向量检索能像数据库查询一样精准
- ✅ 向量检索是"模糊的语义匹配"，需要配合其他技术使用

---


---

### Q: /plugin install superpowers 安装后 Claude Code 是如何生效的？

**疑问：**
> 执行 `/plugin install superpowers@claude-plugins-official` 之后，Claude Code 是如何加载和生效的？整个流程是什么？

**解答：**

分三个阶段：

**第一阶段：安装**
- 从插件源拉取 superpowers 仓库
- 解析 `plugin.json` manifest
- 将所有 `SKILL.md` 同步到本地 `.claude/skills/` 目录

**第二阶段：加载（每次启动）**
- Claude Code 启动时自动扫描 skill 目录
- 读取每个 `SKILL.md` 的 frontmatter，核心是 `description` 字段
- `description` 描述该 skill 的适用场景，是触发匹配的依据

**第三阶段：触发（每次对话）**
- 自动触发：Agent 将用户意图与所有 skill 的 description 做语义匹配，自动注入匹配的 skill 内容
- 手动触发：直接输入 `/skill名称`，如 `/brainstorming`

**关键要点：**
- 要点：渐进式加载，skill 内的 `references/` 和 `scripts/` 只在被激活时才读入上下文

**常见误区：**

- ❌ 以为安装后需要手动配置每个 skill 才生效
- ✅ 安装后自动生效，Agent 通过语义匹配自动触发，无需手动干预
- ❌ 以为所有 skill 内容会一次性加载到上下文，消耗大量 token
- ✅ 渐进式加载，只有被激活的 skill 才注入上下文
