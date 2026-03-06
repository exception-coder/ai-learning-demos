# 快速开始指南

## 环境要求

- JDK 17+
- Maven 3.6+
- IDE（推荐IntelliJ IDEA）

## 安装步骤

### 1. 克隆或打开项目

```bash
cd D:/Users/zhang/IdeaProjects/ai-learning-demos
```

### 2. 构建项目

```bash
mvn clean install
```

### 3. 运行示例

#### 运行LLM示例

```bash
cd llm-demos
mvn spring-boot:run
```

访问：http://localhost:8080/api/llm/hello

#### 运行其他模块

```bash
# 向量数据库示例
cd vector-demos
mvn spring-boot:run

# Agent示例
cd agent-demos
mvn spring-boot:run

# RAG示例
cd rag-demos
mvn spring-boot:run
```

## 配置说明

### LLM配置

编辑 `llm-demos/src/main/resources/application.yml`：

```yaml
llm:
  openai:
    api-key: 你的OpenAI API Key
  deepseek:
    api-key: 你的DeepSeek API Key
```

### 向量数据库配置

需要先启动相应的向量数据库服务：

```bash
# 启动Qdrant（使用Docker）
docker run -p 6333:6333 qdrant/qdrant
```

## 开始学习

1. 查看各模块的README了解功能
2. 阅读示例代码和注释
3. 运行和调试代码
4. 记录学习笔记到 `docs/learning-notes.md`
5. 更新开发日志到 `docs/dev-log.md`

## 常见问题

### Q: Maven构建失败？
A: 检查JDK版本是否为17+，确保网络连接正常。

### Q: API调用失败？
A: 检查API Key是否正确配置，确认网络可以访问API服务。

### Q: 如何添加新的DEMO？
A: 在对应模块下创建新的包和类，参考现有代码结构。

## 下一步

- 完善各模块的具体实现
- 添加更多实用的示例
- 记录学习心得和最佳实践

