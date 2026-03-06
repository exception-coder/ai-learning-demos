package com.learning.llm.agent;

/**
 * AI代理接口
 * 
 * Agent是智能代理，能够：
 * 1. 理解用户意图
 * 2. 规划任务步骤
 * 3. 选择合适的技能
 * 4. 执行并返回结果
 * 
 * @author Learning
 */
public interface Agent {
    
    /**
     * 处理用户请求
     * 
     * @param userInput 用户输入
     * @return 处理结果
     */
    String process(String userInput);
    
    /**
     * 获取Agent名称
     */
    String getName();
    
    /**
     * 获取Agent描述
     */
    String getDescription();
}

