package com.learning.llm.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * 计算器工具
 * 
 * 使用 Spring AI 的 Function Calling 机制
 * LLM 会自动识别何时需要进行数学计算
 * 
 * @author Learning
 */
@Slf4j
@Component
@Description("执行数学计算，支持加减乘除、括号、幂运算等")
public class CalculatorTool implements java.util.function.Function<CalculatorTool.Request, CalculatorTool.Response> {
    
    private final ScriptEngine engine;
    
    public CalculatorTool() {
        this.engine = new ScriptEngineManager().getEngineByName("JavaScript");
    }
    
    /**
     * 计算请求
     */
    public record Request(
        @Description("数学表达式，如：2+3*4、100/5、Math.sqrt(16)") String expression
    ) {}
    
    /**
     * 计算响应
     */
    public record Response(
        String expression,
        String result,
        boolean success,
        String error
    ) {}
    
    @Override
    public Response apply(Request request) {
        String expression = request.expression();
        log.info("执行计算器工具，表达式：{}", expression);
        
        try {
            Object result = engine.eval(expression);
            log.info("计算结果：{} = {}", expression, result);
            return new Response(expression, result.toString(), true, null);
        } catch (Exception e) {
            String error = "计算错误：" + e.getMessage();
            log.error(error, e);
            return new Response(expression, null, false, error);
        }
    }
}

