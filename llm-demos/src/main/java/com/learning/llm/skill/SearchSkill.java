package com.learning.llm.skill;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 搜索技能
 * 
 * 演示如何实现一个搜索类的Skill
 * 实际应用中可以调用真实的搜索引擎API
 * 
 * @author Learning
 */
@Slf4j
@Component
public class SearchSkill implements Skill {
    
    @Override
    public String getName() {
        return "web_search";
    }
    
    @Override
    public String getDescription() {
        return "在互联网上搜索信息。当用户询问最新资讯、需要查找网络信息时使用此技能。";
    }
    
    @Override
    public String getParametersSchema() {
        return """
            {
                "type": "object",
                "properties": {
                    "query": {
                        "type": "string",
                        "description": "搜索关键词"
                    }
                },
                "required": ["query"]
            }
            """;
    }
    
    @Override
    public String execute(Map<String, Object> parameters) {
        String query = (String) parameters.get("query");
        log.info("执行搜索技能，关键词：{}", query);
        
        // 模拟搜索结果（实际应用中调用真实搜索API）
        String result = String.format("搜索'%s'的结果：\n" +
                "1. 相关文章A - 详细介绍了%s的概念和应用\n" +
                "2. 相关文章B - %s的最新发展趋势\n" +
                "3. 相关文章C - %s的实践案例分享", 
                query, query, query, query);
        
        log.info("搜索完成");
        return result;
    }
}

