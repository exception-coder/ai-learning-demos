package com.learning.llm.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 搜索工具
 * 
 * 使用 Spring AI 的 Function Calling 机制
 * LLM 会自动识别何时需要搜索信息
 * 
 * @author Learning
 */
@Slf4j
@Component
@Description("在互联网上搜索信息，获取最新资讯和知识")
public class SearchTool implements java.util.function.Function<SearchTool.Request, SearchTool.Response> {
    
    /**
     * 搜索请求
     */
    public record Request(
        @Description("搜索关键词") String query
    ) {}
    
    /**
     * 搜索结果项
     */
    public record SearchResult(
        String title,
        String snippet,
        String url
    ) {}
    
    /**
     * 搜索响应
     */
    public record Response(
        String query,
        List<SearchResult> results,
        int totalResults
    ) {}
    
    @Override
    public Response apply(Request request) {
        String query = request.query();
        log.info("执行搜索工具，关键词：{}", query);
        
        // 模拟搜索结果（实际应用中调用真实搜索API）
        List<SearchResult> results = List.of(
            new SearchResult(
                query + "详细介绍 - 百科全书",
                "这是关于" + query + "的详细介绍，包含概念、原理和应用场景...",
                "https://example.com/article1"
            ),
            new SearchResult(
                query + "最新发展趋势 - 技术博客",
                query + "在2026年的最新发展趋势和技术突破...",
                "https://example.com/article2"
            ),
            new SearchResult(
                query + "实践案例分享 - 开发者社区",
                "多个" + query + "的实际应用案例和最佳实践...",
                "https://example.com/article3"
            )
        );
        
        log.info("搜索完成，找到{}条结果", results.size());
        return new Response(query, results, results.size());
    }
}

