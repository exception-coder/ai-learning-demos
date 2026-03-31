# Superpowers — 核心技能（Skills）

---

## 开发流程技能（按顺序触发）

| 技能 | 作用 |
|------|------|
| `brainstorming` | 需求澄清，通过对话提炼规格，保存设计文档 |
| `using-git-worktrees` | 在新分支创建隔离工作区，防止污染主分支 |
| `writing-plans` | 将工作拆解为 2-5 分钟细粒度任务，含文件路径、代码、验证步骤 |
| `subagent-driven-development` | 为每个任务派发独立子 Agent，执行两阶段 review |
| `test-driven-development` | 强制 RED-GREEN-REFACTOR 循环，禁止先写实现代码 |
| `requesting-code-review` | 按严重程度汇报问题，关键问题阻断进度 |
| `finishing-a-development-branch` | 验证测试后提供 merge / PR / 保留 / 丢弃选项 |

---

## 调试技能

| 技能 | 作用 |
|------|------|
| `systematic-debugging` | 四阶段根因分析流程 |
| `verification-before-completion` | 确认问题真正修复，而非表面修复 |

---

## 元技能

| 技能 | 作用 |
|------|------|
| `writing-skills` | 创建自定义新技能的指导 |
| `using-superpowers` | 系统入门介绍 |

---

## 适用场景

- 希望 AI Agent 无监督运行长周期任务（数小时）而不跑偏
- 需要在 Agent 流程中强制引入 TDD、设计评审、代码 review 等工程纪律
- 多 Agent 并行开发，需要 git worktree 隔离工作区和计划追踪
- 想为团队定制和扩展 Agent 工作流（支持编写自定义 Skill 并 PR 贡献）
