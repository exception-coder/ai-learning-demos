---
name: git-smart-push
description: Analyze local code changes, automatically generate meaningful commit messages based on the changes, and push to remote repository. Use when the user wants to commit and push changes with auto-generated commit messages, or needs help writing good commit messages.
---

# Git Smart Push

智能分析本地代码变更，自动生成有意义的 commit 信息并推送到远程仓库。

## 必需参数

在执行前，必须从用户获取以下参数（缺少任何一个都要主动询问）：

| 参数 | 说明 | 示例 | 默认值 |
|------|------|------|--------|
| `project_root` | 本地项目根目录绝对路径 | `D:/Users/zhang/IdeaProjects/my-project` | 当前工作目录 |
| `branch` | 要推送的分支名称 | `main` / `develop` / `feature/xxx` | 当前分支 |
| `commit_type` | commit 类型（可选） | `feat` / `fix` / `docs` / `refactor` / `auto` | `auto`（自动判断） |

## 执行流程

按以下步骤严格顺序执行：

### Step 1: 检查 Git 状态

```bash
cd <project_root>
git status --porcelain
```

**判断：**
- 如果没有任何变更（输出为空），提示用户"工作目录干净，无需提交"，终止执行
- 如果有未跟踪或已修改的文件，继续执行

### Step 2: 分析变更内容

执行以下命令获取详细变更：

```bash
# 获取变更文件列表
git status --short

# 获取变更详情（已暂存的）
git diff --cached

# 获取变更详情（未暂存的）
git diff
```

**分析维度：**
1. **变更文件数量和类型**
   - 新增文件（`A`）、修改文件（`M`）、删除文件（`D`）、重命名（`R`）
   - 文件类型：代码文件、配置文件、文档、测试文件等

2. **变更内容特征**
   - 新增功能：大量新增代码、新增类/函数
   - Bug 修复：小范围修改、修复逻辑错误
   - 重构：代码结构调整、重命名、移动文件
   - 文档更新：仅修改 README、注释、文档文件
   - 配置变更：修改配置文件、依赖版本
   - 样式调整：CSS/样式文件修改
   - 测试相关：测试文件的增删改

3. **变更规模**
   - 小型变更（< 50 行）
   - 中型变更（50-200 行）
   - 大型变更（> 200 行）

### Step 3: 生成 Commit 信息

根据 Step 2 的分析结果，生成符合规范的 commit 信息。

**Commit 信息格式：**

```
<type>(<scope>): <subject>

<body>
```

**Type 类型（根据变更内容自动判断）：**

| Type | 说明 | 使用场景 |
|------|------|---------|
| `feat` | 新功能 | 新增功能、新增模块、新增接口 |
| `fix` | Bug 修复 | 修复错误、修复异常、修复逻辑问题 |
| `docs` | 文档 | 仅修改文档、README、注释 |
| `style` | 格式 | 代码格式化、缺少分号、空格调整（不影响代码运行） |
| `refactor` | 重构 | 代码重构、优化结构（不改变功能） |
| `perf` | 性能优化 | 提升性能的代码修改 |
| `test` | 测试 | 添加测试、修改测试 |
| `build` | 构建 | 修改构建工具、依赖、配置 |
| `ci` | CI/CD | 修改 CI 配置文件和脚本 |
| `chore` | 其他 | 其他不影响源代码的修改 |

**Scope 范围（可选，根据变更文件自动提取）：**
- 模块名、组件名、文件名等
- 例如：`user`、`auth`、`api`、`ui`、`config`

**Subject 主题（必需，简洁描述变更）：**
- 使用祈使句，不超过 50 字符
- 不以句号结尾
- 中文项目用中文，英文项目用英文
- 例如：
  - `添加用户登录功能`
  - `修复用户注册时的验证错误`
  - `重构数据库连接逻辑`

**Body 正文（可选，详细描述变更）：**
- 当变更较复杂或涉及多个文件时添加
- 说明变更的原因、影响范围
- 列出主要变更点（使用 `-` 列表）

**生成示例：**

```
# 示例 1：新增功能
feat(user): 添加用户登录功能

- 实现用户名密码登录
- 添加 JWT token 认证
- 新增登录接口 /api/login

# 示例 2：Bug 修复
fix(auth): 修复用户注册时邮箱验证失败的问题

修复了正则表达式错误导致的邮箱格式验证失败

# 示例 3：文档更新
docs: 更新 README 安装说明

# 示例 4：重构
refactor(database): 优化数据库连接池配置

- 调整连接池大小
- 添加连接超时处理
- 优化连接复用逻辑

# 示例 5：多类型变更
chore: 更新项目配置和依赖

- 升级 Spring Boot 到 3.2.0
- 更新 .gitignore 规则
- 调整 Maven 插件配置
```

### Step 4: 暂存所有变更

```bash
git add .
```

如果用户只想提交部分文件，询问用户是否需要选择性暂存。

### Step 5: 执行提交

使用生成的 commit 信息执行提交：

```bash
git commit -m "<生成的 commit 信息>"
```

**注意：**
- 如果 commit 信息包含多行（有 body），使用 `-m` 多次或使用临时文件
- 提交前向用户展示生成的 commit 信息，询问是否确认

### Step 6: 推送到远程

```bash
git push origin <branch>
```

**错误处理：**
- 如果远程分支不存在，使用 `git push -u origin <branch>` 创建并推送
- 如果推送被拒绝（远程有新提交），提示用户先执行 `git pull --rebase`
- 如果认证失败，提示检查 Git 凭证

### Step 7: 验证并反馈

```bash
git log --oneline -1
git status
```

向用户确认：
- Commit 信息
- 推送的分支
- 最新的 commit hash
- 工作目录状态

## 智能分析规则

### 自动判断 Commit Type

根据以下规则自动判断：

1. **仅修改文档文件** → `docs`
   - `*.md`、`*.txt`、`docs/` 目录下的文件

2. **仅修改测试文件** → `test`
   - `*test.js`、`*_test.py`、`test/` 目录下的文件

3. **仅修改配置/依赖** → `build` 或 `chore`
   - `package.json`、`pom.xml`、`requirements.txt`、`.gitignore`

4. **新增文件 > 50%** → `feat`
   - 大量新增文件，表示新功能

5. **修改文件包含关键词** → 根据关键词判断
   - `fix`、`bug`、`修复`、`错误` → `fix`
   - `add`、`新增`、`feature` → `feat`
   - `refactor`、`重构`、`优化` → `refactor`
   - `perf`、`性能` → `perf`

6. **默认** → `chore`
   - 无法明确判断时使用

### 生成 Scope

从变更文件路径中提取：
- `src/user/UserService.java` → `user`
- `components/Button.tsx` → `button`
- `api/auth.js` → `auth`

如果变更涉及多个模块，选择最主要的或使用 `*` 表示多个模块。

### 生成 Subject

根据变更内容生成简洁描述：
1. 识别主要变更动作（新增、修改、删除、重构）
2. 提取核心功能或模块名称
3. 组合成简洁的描述

**示例：**
- 新增 `LoginService.java` → `添加用户登录服务`
- 修改 `UserController.java` 中的验证逻辑 → `修复用户验证逻辑错误`
- 删除 `OldApi.java` → `移除废弃的旧版 API`

## 用户交互

### 确认 Commit 信息

在执行 commit 前，向用户展示生成的信息：

```
📝 生成的 Commit 信息：

feat(user): 添加用户登录功能

- 实现用户名密码登录
- 添加 JWT token 认证
- 新增登录接口 /api/login

是否确认提交？(Y/n)
```

用户可以：
- 确认（Y）：直接提交
- 拒绝（n）：手动输入 commit 信息
- 修改：提供修改建议，重新生成

### 推送确认

提交成功后，询问是否立即推送：

```
✅ 提交成功！

是否推送到远程分支 'main'？(Y/n)
```

## 安全检查

在执行前进行以下检查：

1. **敏感文件检查**
   - 检查是否包含 `.env`、`*.key`、`credentials.json` 等敏感文件
   - 如果发现，警告用户并询问是否继续

2. **大文件检查**
   - 检查是否有 > 10MB 的文件
   - 如果发现，提示用户考虑使用 Git LFS

3. **分支检查**
   - 如果当前在 `main` 或 `master` 分支，提示用户是否确认直接推送到主分支

## 错误处理

| 错误场景 | 处理方式 |
|---------|---------|
| 不是 Git 仓库 | 提示用户先初始化 Git 或使用 `git-init-remote` skill |
| 没有远程仓库 | 提示用户先添加远程仓库 |
| 工作目录干净 | 提示无需提交，终止执行 |
| Commit 失败 | 显示错误信息，询问是否重试 |
| Push 被拒绝 | 提示先 pull，询问是否执行 `git pull --rebase` |
| 认证失败 | 提示检查 Git 凭证或使用 SSH |
| 网络超时 | 提示检查网络连接 |

## 高级选项

用户可以通过参数自定义行为：

| 参数 | 说明 | 默认值 |
|------|------|--------|
| `--no-push` | 只提交不推送 | false |
| `--amend` | 修改上一次提交 | false |
| `--force` | 强制推送（慎用） | false |
| `--dry-run` | 仅显示将要执行的操作，不实际执行 | false |

## 示例场景

### 场景 1：简单功能开发

```
用户：帮我提交并推送代码

变更：
- 新增 UserService.java
- 修改 UserController.java

生成：
feat(user): 添加用户服务层

- 新增 UserService 处理用户业务逻辑
- 更新 UserController 调用服务层
```

### 场景 2：Bug 修复

```
用户：修复了一个登录 bug，帮我提交

变更：
- 修改 AuthService.java（10 行）

生成：
fix(auth): 修复登录验证失败的问题

修复了空指针异常导致的登录失败
```

### 场景 3：文档更新

```
用户：更新了 README，提交一下

变更：
- 修改 README.md

生成：
docs: 更新 README 安装说明
```

### 场景 4：大型重构

```
用户：重构了数据库层，提交推送

变更：
- 重命名 10+ 个文件
- 修改 20+ 个文件
- 删除 5 个废弃文件

生成：
refactor(database): 重构数据库访问层

- 统一数据库连接管理
- 优化 DAO 层结构
- 移除废弃的数据库工具类
- 更新所有调用方代码
```
