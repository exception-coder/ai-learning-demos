package com.learning.llm.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI 配置
 * 
 * 配置 ChatClient 和工具函数
 * 
 * @author Learning
 */
@Configuration
public class SpringAiConfig {
    
    /**
     * 配置 ChatClient
     * 
     * 注册所有工具函数，让 LLM 可以自动调用
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
            .defaultFunctions("weatherTool", "calculatorTool", "searchTool")
            .build();
    }
}

