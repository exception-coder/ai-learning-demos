# AI Learning Demos

> AI技术学习和实践DEMO集合项目

## 📚 项目简介

这是一个用于学习和实践各类AI技术的DEMO集合项目。每个模块都包含独立的示例代码和详细的学习笔记，帮助理解和掌握AI相关技术。

## 🏗️ 项目结构

```
ai-learning-demos/
├── llm-demos/          # 大语言模型DEMO
│   ├── openai-demo/    # OpenAI API使用示例
│   ├── deepseek-demo/  # DeepSeek API使用示例
│   └── ollama-demo/    # Ollama本地模型示例
├── vector-demos/       # 向量数据库DEMO
│   ├── qdrant-demo/    # Qdrant使用示例
│   └── milvus-demo/    # Milvus使用示例
├── agent-demos/        # AI Agent DEMO
│   └── simple-agent/   # 简单Agent实现
├── rag-demos/          # RAG检索增强生成DEMO
│   └── basic-rag/      # 基础RAG实现
├── knowledge-map/      # 知识图谱可视化（前端）
│   └── src/            # React + TypeScript + ReactFlow
└── docs/               # 学习笔记和文档
    └── dev-log.md      # 开发日志
```

## 🚀 技术栈

- **语言**: Java 17
- **框架**: Spring Boot 3.2.0
- **构建工具**: Maven
- **工具库**: Lombok, Hutool, FastJSON2

## 📖 模块说明

### 1. llm-demos - 大语言模型示例
学习和实践各类大语言模型API的使用，包括：
- OpenAI GPT系列
- DeepSeek
- Ollama本地部署模型
- 流式响应处理
- 对话上下文管理

### 2. vector-demos - 向量数据库示例
学习向量数据库的使用和语义搜索，包括：
- 向量存储和检索
- 相似度搜索
- 向量索引优化

### 3. agent-demos - AI Agent示例
学习AI Agent的设计和实现，包括：
- 工具调用
- 任务规划
- 多步推理

### 4. rag-demos - RAG示例
学习检索增强生成技术，包括：
- 文档切分和向量化
- 语义检索
- 上下文增强生成

## 🎯 使用方式

1. **克隆项目**
```bash
cd D:/Users/zhang/IdeaProjects/ai-learning-demos
```

2. **构建项目**
```bash
mvn clean install
```

3. **运行具体DEMO**
```bash
cd llm-demos
mvn spring-boot:run
```

## 📝 学习日志

所有的学习记录和开发日志都记录在 `docs/dev-log.md` 文件中。

## 🔧 开发规范

- 每个DEMO都应该有清晰的README说明
- 代码注释要详细，便于理解
- 优先使用构造函数注入
- 遵循DDD分层架构
- 保持代码简洁和可测试性

## 📅 更新记录

查看 [开发日志](docs/dev-log.md) 了解详细的更新记录。

## 📧 联系方式

如有问题或建议，欢迎交流学习。

