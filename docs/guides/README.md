# Spring AI 学习指南索引

> 本目录包含所有学习指南和技术文档

## 文档列表

### 01. Spring AI 最佳实践

**文件**: `01-spring-ai-best-practices.md`

**内容概要**:

- Spring AI 核心特性介绍
- 架构对比（手动规则 vs Spring AI）
- Tool 和 Agent 的实现方式
- 配置和使用方法
- 扩展新工具的步骤
- 最佳实践和常见问题

**适合人群**: 已了解基础概念，准备使用 Spring AI 开发的开发者

---

### 02. Function Calling 深度解析

**文件**: `02-function-calling-explained.md`

**内容概要**:

- Function Calling 的完整工作流程
- 3 轮对话的详细过程
- 多工具组合场景
- 与手动实现的本质区别
- 真实的 API 调用示例
- Spring AI 的核心价值

**适合人群**: 想深入理解 Function Calling 机制的开发者

---

### 03. Agent 和 Skill 指南（旧实现）

**文件**: `03-agent-skill-guide.md`

**内容概要**:

- Agent 和 Skill 的基础概念
- 手动实现的工作流程
- API 使用示例
- 如何扩展新的 Skill
- 实际应用场景

**适合人群**: 想了解传统实现方式的开发者（对比学习）

---

### 04. 快速测试指南

**文件**: `04-testing-guide.md`

**内容概要**:

- 在 IDE 中运行项目
- API 测试方法
- 预期结果示例
- 代码执行流程
- 扩展练习
- 常见问题解答

**适合人群**: 刚开始上手项目的开发者

---

### 07. AI 应用开发技术栈全景图

**文件**: `07-ai-tech-stack-overview.md`

**内容概要**:

- AI 应用开发全景分层图（模型层→框架层→能力层→编排层→应用层）
- 18+ 个技术栈的定位、作用和使用环节
- FunctionCall vs MCP vs Skill 的关系
- 四种 Agent 模式对比（React/Reflection/PlanExecute/HumanInTheLoop）
- 实战项目技术选型分析
- 学习路径建议

**适合人群**: AI 应用开发初学者，想要全局了解技术栈全貌

---

### 08. Superpowers — AI 编码助手工作流框架（目录）

**目录**: `superpowers/`

**内容概要**:

- 概述与安装（`01-overview.md`）
- 核心技能全览 brainstorming / TDD / code review 等（`02-skills.md`）

**适合人群**: 希望让 AI Agent 遵循工程纪律、可靠执行长周期开发任务的开发者

---

## 学习路径建议

### 初学者路径

1. 先阅读 `04-testing-guide.md` - 快速上手运行项目
2. 再阅读 `03-agent-skill-guide.md` - 理解基础概念
3. 然后阅读 `01-spring-ai-best-practices.md` - 学习最佳实践
4. 最后阅读 `02-function-calling-explained.md` - 深入理解原理

### 有经验开发者路径

1. 直接阅读 `01-spring-ai-best-practices.md` - 了解 Spring AI 用法
2. 阅读 `02-function-calling-explained.md` - 理解核心机制
3. 参考 `04-testing-guide.md` - 快速测试验证

### 对比学习路径

1. 先阅读 `03-agent-skill-guide.md` - 了解传统实现
2. 再阅读 `01-spring-ai-best-practices.md` - 对比 Spring AI 实现
3. 最后阅读 `02-function-calling-explained.md` - 理解本质区别

---

## 其他学习资源

### 项目文档

- `/docs/dev-log.md` - 开发日志，记录所有开发过程
- `/docs/learning-journal.md` - 学习日志，记录所有疑问和解答
- `/docs/learning-notes.md` - 学习笔记
- `/docs/quick-start.md` - 快速开始指南

### 官方文档

- [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/)
- [Function Calling 指南](https://docs.spring.io/spring-ai/reference/api/functions.html)
- [ChatClient API](https://docs.spring.io/spring-ai/reference/api/chatclient.html)

---

## 文档更新记录

- **2026-03-05**: 创建指南索引，整理所有技术文档到 guides 目录
- **2026-03-05**: 添加文档编号和分类
- **2026-03-05**: 补充学习路径建议
- **2026-03-05**: 添加 07-AI 应用开发技术栈全景图
- **2026-03-28**: 新增 08-superpowers 组件目录；规范所有 markdown 格式

---

## 文档维护规范

### 文档命名规范

- 使用数字前缀表示阅读顺序：`01-`, `02-`, `03-`...
- 使用小写字母和连字符：`spring-ai-best-practices.md`
- 文件名要清晰表达文档内容
- 若知识点属于独立组件/框架，建立子目录而非单文件

### 文档结构规范

- 必须包含清晰的标题和目录
- 使用代码示例说明概念
- 提供实际的使用场景
- 包含常见问题解答

### 更新规范

- 新增技术文档必须放在 `/docs/guides/` 目录
- 更新文档后必须同步更新本索引文件
- 重大更新需要在更新记录中说明
