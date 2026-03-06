package com.learning.llm.skill;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;

/**
 * 天气查询技能
 * 
 * 演示如何实现一个查询类的Skill
 * 实际应用中可以调用真实的天气API
 * 
 * @author Learning
 */
@Slf4j
@Component
public class WeatherSkill implements Skill {
    
    private final Random random = new Random();
    
    @Override
    public String getName() {
        return "get_weather";
    }
    
    @Override
    public String getDescription() {
        return "获取指定城市的天气信息。当用户询问天气、气温、是否下雨等问题时使用此技能。";
    }
    
    @Override
    public String getParametersSchema() {
        return """
            {
                "type": "object",
                "properties": {
                    "city": {
                        "type": "string",
                        "description": "城市名称，如：北京、上海、深圳"
                    }
                },
                "required": ["city"]
            }
            """;
    }
    
    @Override
    public String execute(Map<String, Object> parameters) {
        String city = (String) parameters.get("city");
        log.info("执行天气查询技能，城市：{}", city);
        
        // 模拟天气数据（实际应用中调用真实API）
        String[] weathers = {"晴天", "多云", "小雨", "阴天"};
        int temperature = 15 + random.nextInt(20);
        String weather = weathers[random.nextInt(weathers.length)];
        
        String result = String.format("%s今天%s，气温%d°C", city, weather, temperature);
        log.info("天气查询结果：{}", result);
        
        return result;
    }
}

