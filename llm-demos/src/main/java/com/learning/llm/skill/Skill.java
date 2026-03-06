package com.learning.llm.skill;

import java.util.Map;

/**
 * AI技能接口
 * 
 * Skill是Agent可以使用的独立能力单元
 * 每个Skill都有明确的功能描述和参数定义
 * 
 * @author Learning
 */
public interface Skill {
    
    /**
     * 获取技能名称
     * 
     * @return 技能名称
     */
    String getName();
    
    /**
     * 获取技能描述
     * Agent会根据这个描述来判断是否使用该技能
     * 
     * @return 技能描述
     */
    String getDescription();
    
    /**
     * 获取技能参数定义
     * 
     * @return 参数定义（JSON Schema格式）
     */
    String getParametersSchema();
    
    /**
     * 执行技能
     * 
     * @param parameters 执行参数
     * @return 执行结果
     */
    String execute(Map<String, Object> parameters);
}

