package com.learning.llm.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * 天气查询工具
 * 
 * 使用 Spring AI 的 Function Calling 机制
 * LLM 会自动识别何时调用此工具
 * 
 * @author Learning
 */
@Slf4j
@Component
@Description("获取指定城市的天气信息，包括天气状况和温度")
public class WeatherTool implements java.util.function.Function<WeatherTool.Request, WeatherTool.Response> {
    
    private final Random random = new Random();
    
    /**
     * 天气查询请求
     */
    public record Request(
        @Description("城市名称，如：北京、上海、深圳") String city
    ) {}
    
    /**
     * 天气查询响应
     */
    public record Response(
        String city,
        String weather,
        int temperature,
        String description
    ) {}
    
    @Override
    public Response apply(Request request) {
        String city = request.city();
        log.info("执行天气查询工具，城市：{}", city);
        
        // 模拟天气数据（实际应用中调用真实API）
        String[] weathers = {"晴天", "多云", "小雨", "阴天", "雷阵雨"};
        int temperature = 15 + random.nextInt(20);
        String weather = weathers[random.nextInt(weathers.length)];
        
        String description = String.format("%s今天%s，气温%d°C", city, weather, temperature);
        log.info("天气查询结果：{}", description);
        
        return new Response(city, weather, temperature, description);
    }
}

