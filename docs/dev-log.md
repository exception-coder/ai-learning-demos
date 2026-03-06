# AI Learning Demos - 开发日志

## 2026-03-05

### 任务1：创建AI学习DEMO项目

**创建的文件：**
- `/pom.xml` - 父POM，统一依赖管理
- `/README.md` - 项目说明文档
- `/llm-demos/pom.xml` - 大语言模型DEMO模块
- `/vector-demos/pom.xml` - 向量数据库DEMO模块
- `/agent-demos/pom.xml` - AI Agent DEMO模块
- `/rag-demos/pom.xml` - RAG检索增强生成DEMO模块
- `/docs/dev-log.md` - 本开发日志

**项目结构：**
采用Maven多模块结构，便于管理不同类型的AI技术DEMO。每个模块都是独立的Spring Boot应用，可以单独运行和学习。

**技术选型：**
- Java 17 + Spring Boot 3.2.0
- Lombok（简化代码）
- Hutool（工具类库）
- FastJSON2（JSON处理）

**设计决策：**
1. 使用多模块结构，按AI技术领域分类（LLM、向量数据库、Agent、RAG）
2. 每个模块独立可运行，便于单独学习和演示
3. 统一依赖管理，保持版本一致性
4. 预留扩展空间，后续可以添加更多DEMO模块

**创建原因：**
用户需要一个专门的项目来学习和实践各类AI技术，记录学习过程和代码示例。这个项目将作为AI技术的学习日志和代码仓库。

---

### 任务2：实现AI Agent和Skill示例

**创建的文件：**
- `/llm-demos/src/main/java/com/learning/llm/skill/Skill.java` - 技能接口定义
- `/llm-demos/src/main/java/com/learning/llm/skill/WeatherSkill.java` - 天气查询技能
- `/llm-demos/src/main/java/com/learning/llm/skill/CalculatorSkill.java` - 计算器技能
- `/llm-demos/src/main/java/com/learning/llm/skill/SearchSkill.java` - 搜索技能
- `/llm-demos/src/main/java/com/learning/llm/agent/Agent.java` - 代理接口定义
- `/llm-demos/src/main/java/com/learning/llm/agent/SimpleAgent.java` - 简单代理实现
- `/llm-demos/src/main/java/com/learning/llm/agent/SkillRegistry.java` - 技能注册中心
- `/llm-demos/src/main/java/com/learning/llm/service/AgentService.java` - 代理服务
- `/llm-demos/src/main/java/com/learning/llm/controller/AgentController.java` - API控制器
- `/llm-demos/AGENT_SKILL_GUIDE.md` - Agent和Skill详细使用指南

**核心概念：**

1. **AI Skill（技能）**：
   - 独立的能力单元，每个Skill完成一个特定任务
   - 包含名称、描述、参数定义和执行逻辑
   - 类似于人的技能，如"查天气"、"做计算"、"搜索信息"

2. **AI Agent（代理）**：
   - 智能助手，能理解用户意图并调用合适的Skill
   - 工作流程：接收请求 → 分析意图 → 选择技能 → 执行任务 → 返回结果
   - 类似于一个聪明的助理，协调各种技能完成复杂任务

**架构设计：**
- 采用DDD分层架构
- Skill层：定义各种独立能力
- Agent层：智能协调和任务规划
- Service层：业务逻辑封装
- Controller层：API接口暴露

**实现的三个Skill：**
1. **WeatherSkill**：查询城市天气（模拟数据）
2. **CalculatorSkill**：执行数学计算（使用JavaScript引擎）
3. **SearchSkill**：网络搜索（模拟搜索结果）

**SimpleAgent实现：**
- 基于关键词匹配的意图识别
- 自动参数提取
- 技能选择和调用
- 结果格式化输出

**设计决策：**
1. 使用接口定义规范，便于扩展新的Skill和Agent
2. 通过Spring自动注入实现技能注册，无需手动配置
3. 当前使用简单规则匹配，为后续升级为LLM驱动预留接口
4. 每个Skill独立实现，符合单一职责原则

**API接口：**
- `GET /api/agent/info` - 查看Agent和技能信息
- `GET /api/agent/skills` - 查看所有技能列表
- `GET /api/agent/test?message=xxx` - 快速测试（GET方式）
- `POST /api/agent/chat` - 聊天接口（POST方式）

**使用示例：**
```bash
# 查询天气
GET /api/agent/test?message=北京今天天气怎么样？

# 计算
GET /api/agent/test?message=计算 100*5+20

# 搜索
GET /api/agent/test?message=搜索 AI Agent
```

**创建原因：**
用户需要理解AI Agent和Skill的概念及其工作原理。通过实际可运行的代码示例，帮助用户：
1. 理解Skill是什么（独立的能力单元）
2. 理解Agent是什么（智能协调者）
3. 理解它们如何协作完成任务
4. 学习如何扩展新的Skill
5. 为后续集成真实LLM打下基础

**下一步计划：**
1. 集成真实的LLM（OpenAI/DeepSeek）来驱动Agent
2. 实现更复杂的多步推理Agent
3. 添加更多实用的Skill（如文件操作、数据库查询等）
4. 实现Skill之间的组合调用
5. 添加对话历史管理

---

## 2026-03-05

### 任务3：创建项目开发规范文件（Spring AI 集成规范）

**创建的文件：**
- `/.cursorrules` - 项目开发规范和编码标准

**规范内容：**

1. **Spring AI 强制使用规范**
   - 所有 AI 功能必须使用 Spring AI 框架，禁止直接调用原生 SDK
   - 使用 ChatClient 而非 ChatModel
   - 统一依赖管理（Spring AI BOM）

2. **DDD 分层架构规范**
   - Controller 层：仅处理协议转换
   - Application 层：编排用例
   - Domain 层：业务规则和模型
   - Infrastructure 层：外部系统集成（AI、数据库等）

3. **Spring AI 核心功能规范**
   - ChatClient 使用规范（构建器模式）
   - Prompt 工程规范（PromptTemplate、外部文件）
   - Function Calling 规范（@Tool 注解）
   - RAG 检索增强生成规范
   - 流式响应规范
   - 多模态支持规范

4. **配置管理规范**
   - 统一配置文件结构
   - 动态配置刷新机制
   - 多模型提供商配置（OpenAI、Ollama、通义千问等）

5. **依赖注入规范**
   - 强制使用构造函数注入
   - 禁止字段注入（@Autowired）

6. **测试规范**
   - 单元测试标准
   - Mock 测试标准

7. **错误处理、性能优化、日志规范**
   - 统一异常处理
   - 缓存策略
   - 异步处理
   - 结构化日志

8. **代码审查清单**
   - 提交前必须检查的 9 项标准

**设计决策：**
1. 强制使用 Spring AI 框架，避免项目中混用多种 AI SDK，保持代码一致性
2. 使用 ChatClient 而非 ChatModel，因为 ChatClient 提供更流畅的 API 和更好的功能支持
3. 规范 Function Calling 使用 @Tool 注解，与 Spring 生态无缝集成
4. 明确 DDD 分层职责，确保代码可维护性和可测试性
5. 构造函数注入保证依赖明确且不可变

**创建原因：**
1. 当前项目未集成 Spring AI，需要建立统一的开发规范
2. 避免开发人员直接使用原生 SDK 导致代码风格不一致
3. 为后续重构现有代码（如 SimpleAgent）提供标准
4. 确保新功能开发遵循最佳实践
5. 提高代码质量和可维护性

**影响范围：**
- 所有新开发的 AI 功能必须遵循此规范
- 现有代码（如 SimpleAgent）需要逐步重构为 Spring AI 实现
- 需要在父 pom.xml 中添加 Spring AI BOM 依赖
- 需要在各模块中添加对应的 Spring AI starter 依赖

**后续行动：**
1. 更新父 pom.xml，添加 Spring AI 依赖管理
2. 在 llm-demos 模块中添加 Spring AI OpenAI starter
3. 重构 SimpleAgent 使用 Spring AI 的 ChatClient 和 @Tool
4. 添加配置文件支持多种 AI 模型提供商
5. 编写 Spring AI 使用示例和最佳实践文档

---

## 2026-03-05

### 任务4：重构 Agent 和 Skill 为 Spring AI 最佳实践

**修改的文件：**
- `/pom.xml` - 添加 Spring AI BOM 依赖管理
- `/llm-demos/pom.xml` - 添加 Spring AI OpenAI 和 Ollama starter
- `/llm-demos/src/main/resources/application.yml` - 添加 Spring AI 配置

**创建的文件：**
- `/llm-demos/src/main/java/com/learning/llm/tool/WeatherTool.java` - 天气查询工具（Spring AI Function）
- `/llm-demos/src/main/java/com/learning/llm/tool/CalculatorTool.java` - 计算器工具（Spring AI Function）
- `/llm-demos/src/main/java/com/learning/llm/tool/SearchTool.java` - 搜索工具（Spring AI Function）
- `/llm-demos/src/main/java/com/learning/llm/agent/SpringAiAgent.java` - Spring AI Agent 实现
- `/llm-demos/src/main/java/com/learning/llm/config/SpringAiConfig.java` - Spring AI 配置类
- `/llm-demos/SPRING_AI_BEST_PRACTICES.md` - Spring AI 最佳实践指南

**重大架构变更：**

**从手动规则 → Spring AI 自动化**

旧实现问题：
1. 手动关键词匹配识别意图（不准确）
2. 硬编码技能选择逻辑（难扩展）
3. 正则表达式提取参数（容易出错）
4. 自定义 Skill 接口（不符合 Spring AI 规范）
5. 代码复杂度高（200+ 行）

新实现优势：
1. LLM 自动理解用户意图（准确）
2. LLM 自动选择工具（智能）
3. LLM 自动提取参数（可靠）
4. 使用 Spring AI Function（标准化）
5. 代码简洁（30 行）

**核心技术变更：**

1. **Tool 实现方式**
   - 旧：实现自定义 `Skill` 接口
   - 新：实现 `Function<Request, Response>` 接口
   - 优势：Spring AI 自动识别和调用，支持类型安全

2. **Agent 实现方式**
   - 旧：手动编写意图识别、技能选择、参数提取逻辑
   - 新：使用 `ChatClient` 一行代码完成所有工作
   - 优势：LLM 自动处理，支持复杂对话和多工具组合

3. **配置方式**
   - 旧：手动注册 Skill 到 SkillRegistry
   - 新：在 SpringAiConfig 中注册 Function
   - 优势：声明式配置，清晰明了

**Function Calling 工作原理：**

```
用户输入："北京今天天气怎么样？"
    ↓
LLM 分析：需要调用 weatherTool
    ↓
LLM 提取参数：{ "city": "北京" }
    ↓
Spring AI 自动调用：weatherTool.apply(new Request("北京"))
    ↓
工具返回：Response(city="北京", weather="晴天", temperature=25)
    ↓
LLM 生成响应："北京今天晴天，气温25°C"
```

**支持的 AI 模型：**
- OpenAI (GPT-4, GPT-4o-mini)
- Ollama (本地模型，如 Qwen 2.5)
- 可扩展支持：Azure OpenAI、通义千问、智谱 AI

**配置说明：**
- 生产环境推荐使用 OpenAI（需要 API Key）
- 开发测试推荐使用 Ollama（本地免费）
- 通过环境变量配置 API Key，避免硬编码

**设计决策：**
1. 完全遵循 Spring AI 最佳实践，不保留旧的手动实现
2. 使用 Record 定义请求和响应，保证类型安全
3. 每个 Tool 单一职责，便于维护和测试
4. 使用 @Description 注解帮助 LLM 理解工具用途
5. 构造函数注入，符合 Spring 最佳实践

**代码对比：**

旧 Agent（188 行）：
- 手动意图识别（30 行）
- 手动技能选择（20 行）
- 手动参数提取（60 行）
- 格式化响应（20 行）

新 Agent（30 行）：
- LLM 自动处理一切
- 代码减少 84%

**创建原因：**
1. 旧实现不符合 Spring AI 最佳实践
2. 手动规则难以维护和扩展
3. 无法处理复杂的自然语言
4. 不支持多工具组合调用
5. 需要为每个新场景编写规则

**影响范围：**
- 旧的 Skill 接口和实现可以保留作为参考
- SimpleAgent 可以重命名为 RuleBasedAgent 作为对比
- 新代码使用 tool 包而非 skill 包
- AgentService 已更新为使用新的 SpringAiAgent

**测试方式：**
```bash
# 1. 配置 AI 模型（二选一）
export OPENAI_API_KEY=sk-xxx  # 使用 OpenAI
# 或安装 Ollama 并拉取模型

# 2. 启动应用
mvn spring-boot:run

# 3. 测试
curl "http://localhost:8080/api/agent/test?message=北京今天天气怎么样？"
```

**后续计划：**
1. 集成真实的天气 API（如和风天气）
2. 添加更多实用工具（文件操作、数据库查询、HTTP 请求）
3. 实现流式响应（Server-Sent Events）
4. 添加对话历史管理（支持上下文）
5. 实现 RAG（向量数据库 + 文档检索）
6. 添加工具执行的可观测性（监控、追踪）

---

## 2026-03-05

### 任务5：建立学习日志体系

**创建的文件：**
- `/docs/learning-journal.md` - 学习日志文档

**修改的文件：**
- `/.cursorrules` - 添加学习日志规范

**核心内容：**

本项目是学习型项目，需要系统记录学习过程中的所有疑问和解答。

**学习日志记录内容：**
1. 用户的原始疑问
2. 问题分析和背景
3. 详细的解答和原理说明
4. 代码示例和对比
5. 关键要点提炼
6. 常见误区总结

**已记录的核心疑问：**

**Q1: Spring AI 只是用 LLM 做语义分析吗？**

这是一个非常重要的误解。很多初学者认为 Spring AI 只是：
```
用户输入 → LLM提取关键词 → 我们的代码选择工具 → 我们的代码调用
```

实际上，Spring AI 的 Function Calling 机制是：
```
用户输入 
  ↓
LLM 理解意图并决定调用哪个工具（LLM 决策）
  ↓
LLM 生成工具调用请求（包含函数名和参数）
  ↓
Spring AI 拦截并执行真实的 Java 方法
  ↓
工具返回结果给 LLM
  ↓
LLM 基于工具结果生成自然语言响应
```

**关键区别：**
- 手动实现：我们的代码是决策者
- Spring AI：LLM 是决策者，我们只提供工具

**文档结构：**
- 核心概念理解
- 技术疑问与解答
- 实践经验
- 常见误区
- 学习路径建议
- 待整理的问题

**设计决策：**
1. 记录所有用户提出的疑问，保持原始性
2. 提供详细的解答和代码示例
3. 对比新旧实现，突出差异
4. 总结常见误区，避免重复踩坑
5. 规划学习路径，循序渐进

**最终目标：**
将学习日志整理成完整的 **从 0 到 1 的 Spring AI 学习手册**，包括：
- 核心概念讲解
- 实践案例分析
- 常见问题解答
- 最佳实践总结
- 进阶学习路径

**更新规范：**
- 每次用户提出疑问时，必须记录到学习日志
- 每次学习新知识点时，必须补充到相应章节
- 定期整理和归类问题，保持文档结构清晰
- 补充相关的参考资料和延伸阅读

**创建原因：**
1. 本项目是学习型项目，需要系统记录学习过程
2. 用户提出的疑问往往代表初学者的共同困惑
3. 详细的解答可以帮助其他学习者快速理解
4. 最终整理成学习手册，提供更大的价值
5. 记录学习路径，帮助规划后续学习方向

---

## 2026-03-05

### 任务6：整理技术文档到统一目录

**创建的文件：**
- `/docs/guides/README.md` - 技术指南索引文档

**移动的文件：**
- `llm-demos/SPRING_AI_BEST_PRACTICES.md` → `/docs/guides/01-spring-ai-best-practices.md`
- `llm-demos/FUNCTION_CALLING_EXPLAINED.md` → `/docs/guides/02-function-calling-explained.md`
- `llm-demos/AGENT_SKILL_GUIDE.md` → `/docs/guides/03-agent-skill-guide.md`
- `llm-demos/TESTING_GUIDE.md` → `/docs/guides/04-testing-guide.md`

**修改的文件：**
- `/.cursorrules` - 添加技术指南规范（9.4 节）

**问题背景：**
用户发现技术文档散乱在 llm-demos 子模块目录中，不便于管理和查找。这些文档应该统一放在母项目的 docs 目录中。

**解决方案：**

1. **创建统一的指南目录**
   - 在 `/docs/` 下创建 `guides/` 子目录
   - 专门存放所有技术指南文档

2. **文档重新组织**
   - 将散落在 llm-demos 中的 4 个技术文档移动到 guides 目录
   - 使用数字前缀（01-、02-、03-、04-）标识阅读顺序
   - 统一使用小写字母和连字符命名

3. **创建索引文档**
   - 编写 `guides/README.md` 作为导航入口
   - 包含所有文档的内容概要
   - 提供不同学习路径建议（初学者、有经验者、对比学习）
   - 列出其他学习资源和官方文档链接

4. **建立文档规范**
   - 禁止在子模块目录创建散乱的技术文档
   - 所有技术指南必须放在 `/docs/guides/`
   - 明确文档命名、结构、维护规范
   - 要求更新索引文件

**文档目录结构：**
```
/docs/
├── dev-log.md              # 开发日志
├── learning-journal.md     # 学习日志
├── learning-notes.md       # 学习笔记
├── quick-start.md          # 快速开始
└── guides/                 # 技术指南目录（新增）
    ├── README.md           # 指南索引
    ├── 01-spring-ai-best-practices.md
    ├── 02-function-calling-explained.md
    ├── 03-agent-skill-guide.md
    └── 04-testing-guide.md
```

**索引文档内容：**
- 📚 文档列表（包含内容概要和适合人群）
- 🎯 学习路径建议（3 种不同路径）
- 📖 其他学习资源
- 🔄 文档更新记录
- 💡 使用建议
- 📝 文档维护规范
- 🤝 贡献指南

**设计决策：**
1. 使用数字前缀便于排序和标识学习顺序
2. 统一放在母项目 docs 目录，便于全局管理
3. 创建索引文档作为导航入口，提升可读性
4. 提供多种学习路径，适应不同背景的学习者
5. 明确文档规范，避免未来再次散乱

**规范要点：**
- ✅ 所有技术指南必须放在 `/docs/guides/`
- ✅ 使用数字前缀和规范命名
- ✅ 新增文档必须更新索引
- ✅ 文档要包含标题、目录、示例、FAQ
- ❌ 禁止在子模块目录创建技术文档

**创建原因：**
1. 散乱的文档不便于查找和管理
2. 缺少统一的导航入口
3. 没有明确的学习路径指引
4. 需要建立文档管理规范，避免未来混乱
5. 提升项目的专业性和可维护性

**影响范围：**
- 所有技术文档统一管理
- 后续新增文档必须遵循规范
- 提升文档的可发现性和可读性
- 便于整理成完整的学习手册

---

## 2026-03-05 创建 git-init-remote Cursor Skill

**任务描述：**
创建一个 Cursor Agent Skill，用于将本地没有 Git 仓库的项目初始化并关联到远程仓库，同时自动生成合适的 `.gitignore` 文件。

**创建/修改的文件：**
- `~/.cursor/skills/git-init-remote/SKILL.md` — Skill 主文件，定义执行流程和参数要求
- `~/.cursor/skills/git-init-remote/gitignore-templates.md` — 各类项目的 .gitignore 模板

**关键设计决策：**
1. 采用参数化设计：必须传入项目路径、远程 URL、账号、密码四个参数
2. 自动检测项目类型（Java/Maven、Node、Python、Go 等），选择对应的 .gitignore 模板
3. 安全优先：push 完成后立即将带密码的 URL 替换为无密码版本，绝不在配置中保留凭证
4. 如果 .gitignore 已存在，追加缺失规则而非覆盖

**变更原因：**
需要一个可复用的标准化流程，避免每次手动关联仓库时遗漏 .gitignore 配置或暴露凭证信息。

---

## 2026-03-05 修正 Skill 文件位置 + 新增文件归属规则

**任务描述：**
将 git-init-remote Skill 文件从全局目录（`~/.cursor/skills/`）移到当前项目内（`ai-learning-demos/.cursor/skills/`），并在 `.cursorrules` 中登记"文件归属规范"。

**创建/修改的文件：**
- `ai-learning-demos/.cursor/skills/git-init-remote/SKILL.md` — 从全局移到项目内
- `ai-learning-demos/.cursor/skills/git-init-remote/gitignore-templates.md` — 从全局移到项目内
- `ai-learning-demos/.cursorrules` — 新增第 10 节"文件归属规范"，审查清单增加检查项
- 删除 `~/.cursor/skills/git-init-remote/` 下的旧文件

**关键设计决策：**
1. 项目级 Skill 放 `.cursor/skills/`，不放全局目录，确保跟随项目版本管理
2. 在 `.cursorrules` 中明确规定：用户在哪个项目发起需求，新增文件就写到哪个项目
3. 在代码审查清单中新增"新增文件在当前项目内"检查项

**变更原因：**
AI 助手错误地将 Skill 文件放到了全局 `~/.cursor/skills/` 目录，而非当前项目内。需要建立明确的规则避免再次发生。

---

## 2026-03-05 整理 AI 应用开发技术栈全景图

**任务描述：**
根据课程大纲截图，系统整理 AI 应用开发涉及的所有技术栈，分析每个技术的定位、作用和使用环节。

**创建/修改的文件：**
- `docs/guides/07-ai-tech-stack-overview.md` — 新增技术栈全景图文档
- `docs/guides/README.md` — 更新索引，添加 07 文档条目
- `docs/learning-journal.md` — 追加技术栈全景图的问答记录

**关键设计决策：**
1. 采用五层分层结构组织（模型层→框架层→能力层→编排层→应用层）
2. 重点厘清 FunctionCall / MCP / Skill / Agent 四者的关系链
3. 对比四种 Agent 模式（React/Reflection/PlanExecute/HumanInTheLoop）
4. 提供分阶段学习路径建议

**变更原因：**
学习 AI 应用开发需要先建立全局认知，理解每个技术在整个体系中的位置，避免只见树木不见森林。

---

## 2026-03-05 创建 knowledge-map 前端可视化子项目

**任务描述：**
新增前端子项目 `knowledge-map/`，用交互式可视化图表展示 AI 应用开发的知识点和技术栈关系。

**创建的文件：**
- `knowledge-map/` — 完整前端子项目
- `knowledge-map/package.json` — 项目配置
- `knowledge-map/vite.config.ts` — Vite + React + TailwindCSS 配置
- `knowledge-map/src/main.tsx` — 应用入口
- `knowledge-map/src/App.tsx` — 路由配置
- `knowledge-map/src/components/Layout.tsx` — 侧边栏导航布局
- `knowledge-map/src/components/DetailPanel.tsx` — 节点详情面板（动画滑出）
- `knowledge-map/src/pages/Home.tsx` — 首页（知识图谱卡片入口）
- `knowledge-map/src/pages/TechStackMap.tsx` — 技术栈全景图（ReactFlow 交互式图表）
- `knowledge-map/src/data/tech-stack-nodes.ts` — 技术栈节点和连线数据

**技术选型：**
- React 19 + TypeScript + Vite 7（快速构建）
- @xyflow/react（ReactFlow v12）— 交互式节点/连线图
- TailwindCSS v4 — 样式框架
- framer-motion — 动画效果
- react-router-dom — 客户端路由

**关键设计决策：**
1. 独立前端项目，不纳入 Maven 构建体系
2. 数据与视图分离：知识点数据集中在 `data/` 目录，方便后续扩展
3. 五层分层架构可视化：模型层→框架层→能力层→编排层→应用层
4. 节点可点击查看详情，支持拖拽和缩放
5. 预留路由结构，后续可持续添加新的知识图谱页面

**变更原因：**
纯文档形式不够直观，前端可视化能更好地展示技术栈之间的层级关系和调用链路，便于学习和理解。

---

## 2026-03-05 重构技术栈全景图：围绕两个真实项目

**任务描述：**
将技术栈全景图从通用示例替换为围绕「工作助手」和「岗位需求分析」两个真实项目的技术选型和调用链路。

**修改的文件：**
- `knowledge-map/src/data/tech-stack-nodes.ts` — 重写所有节点和连线数据
- `knowledge-map/src/components/DetailPanel.tsx` — 增加项目归属标签显示
- `knowledge-map/src/pages/TechStackMap.tsx` — 更新图例，增加连线颜色说明
- `knowledge-map/src/pages/Home.tsx` — 更新首页卡片描述

**关键设计决策：**
1. 应用层替换为两个真实项目：工作助手（日志/记忆/回忆）、岗位需求分析（薪资/能力/匹配）
2. 每层技术节点的描述说明具体在这两个项目中做什么
3. 连线用颜色区分项目归属：绿色=工作助手、黄色=岗位分析、紫色=共用
4. 新增 Embedding 模型和向量数据库节点（两个项目都依赖 RAG）

**变更原因：**
通用示例缺乏代入感，围绕自己真实要做的项目来展示技术栈，能更清晰理解每个技术为什么需要、用在哪里。
