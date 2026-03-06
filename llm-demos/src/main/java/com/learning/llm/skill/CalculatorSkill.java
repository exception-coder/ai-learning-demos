package com.learning.llm.skill;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Map;

/**
 * 计算器技能
 * 
 * 演示如何实现一个计算类的Skill
 * 可以执行数学表达式计算
 * 
 * @author Learning
 */
@Slf4j
@Component
public class CalculatorSkill implements Skill {
    
    private final ScriptEngine engine;
    
    public CalculatorSkill() {
        this.engine = new ScriptEngineManager().getEngineByName("JavaScript");
    }
    
    @Override
    public String getName() {
        return "calculator";
    }
    
    @Override
    public String getDescription() {
        return "执行数学计算。当用户需要计算数学表达式、加减乘除等运算时使用此技能。";
    }
    
    @Override
    public String getParametersSchema() {
        return """
            {
                "type": "object",
                "properties": {
                    "expression": {
                        "type": "string",
                        "description": "数学表达式，如：2+3*4、100/5、Math.sqrt(16)"
                    }
                },
                "required": ["expression"]
            }
            """;
    }
    
    @Override
    public String execute(Map<String, Object> parameters) {
        String expression = (String) parameters.get("expression");
        log.info("执行计算器技能，表达式：{}", expression);
        
        try {
            Object result = engine.eval(expression);
            String resultStr = String.format("计算结果：%s = %s", expression, result);
            log.info(resultStr);
            return resultStr;
        } catch (Exception e) {
            String error = "计算错误：" + e.getMessage();
            log.error(error, e);
            return error;
        }
    }
}

