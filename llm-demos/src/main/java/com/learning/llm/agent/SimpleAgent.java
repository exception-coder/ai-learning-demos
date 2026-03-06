package com.learning.llm.agent;

import com.learning.llm.skill.Skill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 简单AI代理实现
 * 
 * 这是一个基于规则的简单Agent实现
 * 通过关键词匹配来选择合适的技能
 * 
 * 实际生产环境中，Agent会使用LLM来理解意图和规划任务
 * 
 * @author Learning
 */
@Slf4j
@Component
public class SimpleAgent implements Agent {
    
    private final SkillRegistry skillRegistry;
    
    public SimpleAgent(SkillRegistry skillRegistry) {
        this.skillRegistry = skillRegistry;
        log.info("SimpleAgent初始化完成");
    }
    
    @Override
    public String getName() {
        return "SimpleAgent";
    }
    
    @Override
    public String getDescription() {
        return "一个简单的AI代理，能够理解用户意图并调用相应的技能完成任务";
    }
    
    @Override
    public String process(String userInput) {
        log.info("Agent收到用户请求：{}", userInput);
        
        // 步骤1：分析用户意图（简单的关键词匹配）
        String intent = analyzeIntent(userInput);
        log.info("识别到用户意图：{}", intent);
        
        // 步骤2：选择合适的技能
        Skill skill = selectSkill(intent);
        if (skill == null) {
            return "抱歉，我还不能处理这个请求。\n\n" + skillRegistry.getSkillsDescription();
        }
        
        log.info("选择技能：{}", skill.getName());
        
        // 步骤3：提取参数
        Map<String, Object> parameters = extractParameters(userInput, intent);
        log.info("提取参数：{}", parameters);
        
        // 步骤4：执行技能
        String result = skill.execute(parameters);
        
        // 步骤5：返回结果
        String response = formatResponse(userInput, skill.getName(), result);
        log.info("Agent处理完成");
        
        return response;
    }
    
    /**
     * 分析用户意图
     */
    private String analyzeIntent(String userInput) {
        String input = userInput.toLowerCase();
        
        if (input.contains("天气") || input.contains("气温") || input.contains("下雨")) {
            return "weather";
        } else if (input.contains("计算") || input.contains("+") || input.contains("-") 
                || input.contains("*") || input.contains("/")) {
            return "calculate";
        } else if (input.contains("搜索") || input.contains("查找") || input.contains("什么是")) {
            return "search";
        }
        
        return "unknown";
    }
    
    /**
     * 选择技能
     */
    private Skill selectSkill(String intent) {
        return switch (intent) {
            case "weather" -> skillRegistry.getSkill("get_weather");
            case "calculate" -> skillRegistry.getSkill("calculator");
            case "search" -> skillRegistry.getSkill("web_search");
            default -> null;
        };
    }
    
    /**
     * 提取参数
     */
    private Map<String, Object> extractParameters(String userInput, String intent) {
        Map<String, Object> params = new HashMap<>();
        
        switch (intent) {
            case "weather" -> {
                // 提取城市名称
                String city = extractCity(userInput);
                params.put("city", city);
            }
            case "calculate" -> {
                // 提取数学表达式
                String expression = extractExpression(userInput);
                params.put("expression", expression);
            }
            case "search" -> {
                // 提取搜索关键词
                String query = extractSearchQuery(userInput);
                params.put("query", query);
            }
        }
        
        return params;
    }
    
    /**
     * 提取城市名称
     */
    private String extractCity(String userInput) {
        // 简单的城市提取逻辑
        String[] cities = {"北京", "上海", "深圳", "广州", "杭州", "成都", "武汉"};
        for (String city : cities) {
            if (userInput.contains(city)) {
                return city;
            }
        }
        return "北京"; // 默认城市
    }
    
    /**
     * 提取数学表达式
     */
    private String extractExpression(String userInput) {
        // 移除"计算"等关键词，保留数学表达式
        String expression = userInput
                .replace("计算", "")
                .replace("等于多少", "")
                .replace("是多少", "")
                .replace("？", "")
                .replace("?", "")
                .trim();
        
        return expression;
    }
    
    /**
     * 提取搜索关键词
     */
    private String extractSearchQuery(String userInput) {
        // 移除"搜索"、"查找"等关键词
        String query = userInput
                .replace("搜索", "")
                .replace("查找", "")
                .replace("什么是", "")
                .trim();
        
        return query;
    }
    
    /**
     * 格式化响应
     */
    private String formatResponse(String userInput, String skillName, String result) {
        return String.format("""
                🤖 Agent处理报告
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                📝 用户请求：%s
                🔧 使用技能：%s
                ✅ 执行结果：
                %s
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                """, userInput, skillName, result);
    }
}

