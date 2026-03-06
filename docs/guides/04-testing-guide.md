# 快速测试指南

## 在IDE中运行项目

### 1. 导入项目到IntelliJ IDEA

1. 打开IntelliJ IDEA
2. File → Open → 选择 `D:/Users/zhang/IdeaProjects/ai-learning-demos`
3. 等待Maven依赖下载完成

### 2. 运行项目

找到并运行主类：
```
llm-demos/src/main/java/com/learning/llm/LLMDemosApplication.java
```

右键点击 → Run 'LLMDemosApplication'

### 3. 测试API

项目启动后，在浏览器中访问以下URL：

#### 测试1：查看Agent信息
```
http://localhost:8080/api/agent/info
```

#### 测试2：查看所有技能
```
http://localhost:8080/api/agent/skills
```

#### 测试3：测试天气查询
```
http://localhost:8080/api/agent/test?message=北京今天天气怎么样？
```

#### 测试4：测试计算器
```
http://localhost:8080/api/agent/test?message=计算 100*5+20
```

#### 测试5：测试搜索
```
http://localhost:8080/api/agent/test?message=搜索 人工智能
```

#### 测试6：测试其他城市天气
```
http://localhost:8080/api/agent/test?message=上海今天天气怎么样？
```

#### 测试7：复杂计算
```
http://localhost:8080/api/agent/test?message=计算 Math.sqrt(144) + 50
```

### 4. 使用Postman测试POST接口

**URL**: `http://localhost:8080/api/agent/chat`

**Method**: POST

**Headers**: 
```
Content-Type: application/json
```

**Body**:
```json
{
  "message": "深圳今天天气怎么样？"
}
```

## 预期结果示例

### 天气查询结果
```
🤖 Agent处理报告
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📝 用户请求：北京今天天气怎么样？
🔧 使用技能：get_weather
✅ 执行结果：
北京今天晴天，气温25°C
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

### 计算器结果
```
🤖 Agent处理报告
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📝 用户请求：计算 100*5+20
🔧 使用技能：calculator
✅ 执行结果：
计算结果：100*5+20 = 520
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

## 观察日志

在IDE的控制台中，你可以看到详细的执行日志：

```
2026-03-05 14:00:00.000  INFO --- Agent收到用户请求：北京今天天气怎么样？
2026-03-05 14:00:00.001  INFO --- 识别到用户意图：weather
2026-03-05 14:00:00.002  INFO --- 选择技能：get_weather
2026-03-05 14:00:00.003  INFO --- 提取参数：{city=北京}
2026-03-05 14:00:00.004  INFO --- 执行天气查询技能，城市：北京
2026-03-05 14:00:00.005  INFO --- 天气查询结果：北京今天晴天，气温25°C
2026-03-05 14:00:00.006  INFO --- Agent处理完成
```

## 理解代码执行流程

### 流程1：天气查询

1. **用户请求** → `AgentController.test("北京今天天气怎么样？")`
2. **服务层** → `AgentService.processRequest()`
3. **Agent处理** → `SimpleAgent.process()`
4. **意图分析** → 检测到"天气"关键词 → intent = "weather"
5. **选择技能** → 选择 `WeatherSkill`
6. **提取参数** → 提取城市名 "北京"
7. **执行技能** → `WeatherSkill.execute({city: "北京"})`
8. **返回结果** → 格式化输出

### 流程2：计算

1. **用户请求** → `"计算 100*5+20"`
2. **意图分析** → 检测到"计算"或数学符号 → intent = "calculate"
3. **选择技能** → 选择 `CalculatorSkill`
4. **提取参数** → 提取表达式 "100*5+20"
5. **执行技能** → 使用JavaScript引擎计算
6. **返回结果** → "520"

## 扩展练习

### 练习1：添加新的Skill

创建一个时间查询技能：

```java
@Component
public class TimeSkill implements Skill {
    @Override
    public String getName() {
        return "get_time";
    }
    
    @Override
    public String getDescription() {
        return "获取当前时间";
    }
    
    @Override
    public String getParametersSchema() {
        return "{}";
    }
    
    @Override
    public String execute(Map<String, Object> parameters) {
        return "当前时间：" + LocalDateTime.now();
    }
}
```

### 练习2：修改Agent的意图识别

在 `SimpleAgent.analyzeIntent()` 方法中添加：

```java
if (input.contains("时间") || input.contains("几点")) {
    return "time";
}
```

### 练习3：测试新功能

```
http://localhost:8080/api/agent/test?message=现在几点了？
```

## 常见问题

### Q: 端口8080被占用？
A: 修改 `application.yml` 中的端口号：
```yaml
server:
  port: 8081
```

### Q: 依赖下载失败？
A: 检查Maven配置，确保网络连接正常，或配置国内镜像。

### Q: 找不到主类？
A: 确保项目已正确导入，Maven依赖已下载完成。

## 下一步学习

1. ✅ 理解Skill的概念和实现
2. ✅ 理解Agent的工作流程
3. 📝 尝试添加新的Skill
4. 📝 学习如何集成真实的LLM
5. 📝 实现更复杂的多步推理

