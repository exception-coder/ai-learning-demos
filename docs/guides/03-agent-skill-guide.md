# AI Agent 和 Skill 示例说明

## 核心概念

### 什么是 AI Skill？

**AI Skill（技能）** 是一个独立的能力单元，就像人的一项技能一样。每个Skill都有：
- **明确的功能**：做一件具体的事情（如查天气、计算、搜索）
- **参数定义**：需要什么输入
- **执行逻辑**：如何完成任务
- **返回结果**：输出什么

**类比**：就像你会"做饭"、"开车"、"编程"这些技能，每个技能都是独立的能力。

### 什么是 AI Agent？

**AI Agent（代理）** 是一个智能助手，它能够：
1. **理解意图**：听懂用户想做什么
2. **规划任务**：决定用哪个技能
3. **调用技能**：执行具体操作
4. **返回结果**：给用户反馈

**类比**：Agent就像一个聪明的助理，你告诉他"我想知道北京天气"，他会：
- 理解你要查天气
- 选择"天气查询"技能
- 提取"北京"这个参数
- 调用技能获取结果
- 把结果告诉你

## 项目结构

```
llm-demos/
├── skill/                          # 技能层
│   ├── Skill.java                  # 技能接口
│   ├── WeatherSkill.java           # 天气查询技能
│   ├── CalculatorSkill.java        # 计算器技能
│   └── SearchSkill.java            # 搜索技能
├── agent/                          # 代理层
│   ├── Agent.java                  # 代理接口
│   ├── SimpleAgent.java            # 简单代理实现
│   └── SkillRegistry.java          # 技能注册中心
├── service/                        # 服务层
│   └── AgentService.java           # 代理服务
└── controller/                     # 控制器层
    └── AgentController.java        # API接口
```

## 工作流程

```
用户请求 → Agent → 分析意图 → 选择Skill → 执行Skill → 返回结果
```

### 详细流程示例

**用户输入**：`"北京今天天气怎么样？"`

1. **Agent接收请求**
   ```
   SimpleAgent.process("北京今天天气怎么样？")
   ```

2. **分析意图**
   ```
   检测到关键词"天气" → 意图：weather
   ```

3. **选择技能**
   ```
   根据意图选择 → WeatherSkill
   ```

4. **提取参数**
   ```
   从输入中提取 → city: "北京"
   ```

5. **执行技能**
   ```
   WeatherSkill.execute({city: "北京"})
   → 返回："北京今天晴天，气温25°C"
   ```

6. **返回结果**
   ```
   格式化输出给用户
   ```

## API使用示例

### 1. 查看Agent信息

```bash
GET http://localhost:8080/api/agent/info
```

**响应**：
```
Agent名称：SimpleAgent
Agent描述：一个简单的AI代理，能够理解用户意图并调用相应的技能完成任务

可用技能列表：
- get_weather: 获取指定城市的天气信息
- calculator: 执行数学计算
- web_search: 在互联网上搜索信息
```

### 2. 查看所有技能

```bash
GET http://localhost:8080/api/agent/skills
```

### 3. 测试天气查询

```bash
GET http://localhost:8080/api/agent/test?message=上海今天天气怎么样？
```

**响应**：
```
🤖 Agent处理报告
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📝 用户请求：上海今天天气怎么样？
🔧 使用技能：get_weather
✅ 执行结果：
上海今天晴天，气温28°C
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

### 4. 测试计算器

```bash
GET http://localhost:8080/api/agent/test?message=计算 100*5+20
```

**响应**：
```
🤖 Agent处理报告
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📝 用户请求：计算 100*5+20
🔧 使用技能：calculator
✅ 执行结果：
计算结果：100*5+20 = 520
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

### 5. 测试搜索

```bash
GET http://localhost:8080/api/agent/test?message=搜索 AI Agent
```

### 6. POST方式调用

```bash
POST http://localhost:8080/api/agent/chat
Content-Type: application/json

{
  "message": "深圳今天天气怎么样？"
}
```

## 如何扩展

### 添加新的Skill

1. **创建Skill类**，实现`Skill`接口：

```java
@Component
public class TranslateSkill implements Skill {
    
    @Override
    public String getName() {
        return "translate";
    }
    
    @Override
    public String getDescription() {
        return "翻译文本";
    }
    
    @Override
    public String getParametersSchema() {
        return """
            {
                "text": "要翻译的文本",
                "target_lang": "目标语言"
            }
            """;
    }
    
    @Override
    public String execute(Map<String, Object> parameters) {
        // 实现翻译逻辑
        return "翻译结果";
    }
}
```

2. **Spring会自动注册**到`SkillRegistry`

3. **在Agent中添加意图识别**逻辑

### 升级为LLM驱动的Agent

当前的`SimpleAgent`使用简单的关键词匹配，实际生产环境中应该使用LLM：

```java
// 使用LLM理解意图
String intent = llmService.analyzeIntent(userInput, skillRegistry.getSkillsDescription());

// 使用LLM提取参数
Map<String, Object> params = llmService.extractParameters(userInput, skill.getParametersSchema());
```

## 实际应用场景

### 1. 客服机器人
- 技能：查询订单、退款处理、问题解答
- Agent：理解客户问题，调用相应技能

### 2. 智能助手
- 技能：日程管理、邮件发送、文件搜索
- Agent：理解用户指令，自动完成任务

### 3. 数据分析助手
- 技能：SQL查询、数据可视化、报表生成
- Agent：理解分析需求，组合多个技能完成复杂分析

## 关键要点

1. **Skill是能力单元**：每个Skill做一件事，做好一件事
2. **Agent是协调者**：理解意图，选择和组合Skill
3. **解耦设计**：Skill和Agent分离，易于扩展
4. **标准接口**：统一的接口定义，便于管理

## 运行项目

```bash
cd D:/Users/zhang/IdeaProjects/ai-learning-demos/llm-demos
mvn spring-boot:run
```

访问：http://localhost:8080/api/agent/test

## 下一步学习

1. 理解当前的简单实现
2. 尝试添加新的Skill
3. 学习如何集成真实的LLM
4. 实现更复杂的多步推理Agent

