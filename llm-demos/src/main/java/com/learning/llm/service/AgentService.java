package com.learning.llm.service;

import com.learning.llm.agent.Agent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Agent服务
 * 
 * 提供Agent相关的业务逻辑
 * 
 * @author Learning
 */
@Slf4j
@Service
public class AgentService {
    
    private final Agent agent;
    
    public AgentService(Agent agent) {
        this.agent = agent;
    }
    
    /**
     * 处理用户请求
     */
    public String processRequest(String userInput) {
        log.info("AgentService收到请求：{}", userInput);
        return agent.process(userInput);
    }
    
    /**
     * 获取Agent信息
     */
    public String getAgentInfo() {
        return String.format("""
                🤖 Agent 信息
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                名称：%s
                描述：%s
                
                💡 可用工具：
                - WeatherTool: 查询城市天气信息
                - CalculatorTool: 执行数学计算
                - SearchTool: 搜索互联网信息
                
                🚀 使用方式：
                直接发送自然语言请求，Agent 会自动选择合适的工具完成任务。
                
                示例：
                - "北京今天天气怎么样？"
                - "计算 123 * 456"
                - "搜索 Spring AI"
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                """, 
                agent.getName(), 
                agent.getDescription());
    }
    
    /**
     * 获取所有工具信息
     */
    public String getAllSkillsInfo() {
        return """
                📦 可用工具列表
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                
                1. WeatherTool (天气查询)
                   - 功能：获取指定城市的天气信息
                   - 参数：城市名称
                   - 示例：北京今天天气怎么样？
                
                2. CalculatorTool (计算器)
                   - 功能：执行数学计算
                   - 参数：数学表达式
                   - 示例：计算 100 * 5 + 20
                
                3. SearchTool (搜索)
                   - 功能：在互联网上搜索信息
                   - 参数：搜索关键词
                   - 示例：搜索 AI Agent
                
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                💡 提示：直接用自然语言描述需求，Agent 会自动选择工具！
                """;
    }
}

