package com.learning.llm.agent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * Spring AI Agent 实现
 * 
 * 使用 Spring AI 的 ChatClient 和 Function Calling
 * LLM 会自动：
 * 1. 理解用户意图
 * 2. 选择合适的工具
 * 3. 提取参数
 * 4. 调用工具
 * 5. 生成自然语言响应
 * 
 * @author Learning
 */
@Slf4j
@Component
public class SpringAiAgent implements Agent {
    
    private final ChatClient chatClient;
    
    public SpringAiAgent(ChatClient chatClient) {
        this.chatClient = chatClient;
        log.info("SpringAiAgent 初始化完成");
    }
    
    @Override
    public String getName() {
        return "SpringAiAgent";
    }
    
    @Override
    public String getDescription() {
        return "基于 Spring AI 的智能代理，使用 LLM 理解意图并自动调用工具完成任务";
    }
    
    @Override
    public String process(String userInput) {
        log.info("Agent 收到用户请求：{}", userInput);
        
        try {
            long startTime = System.currentTimeMillis();
            
            // Spring AI 会自动处理：
            // 1. 意图识别
            // 2. 工具选择
            // 3. 参数提取
            // 4. 工具调用
            // 5. 结果整合
            String response = chatClient.prompt()
                .user(userInput)
                .call()
                .content();
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("Agent 处理完成，耗时：{}ms", duration);
            
            return response;
            
        } catch (Exception e) {
            log.error("Agent 处理失败", e);
            return "抱歉，处理您的请求时出现了错误：" + e.getMessage();
        }
    }
}

