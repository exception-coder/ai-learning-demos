package com.learning.llm.controller;

import com.learning.llm.service.AgentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * Agent控制器
 * 
 * 提供Agent和Skill的演示API
 * 
 * @author Learning
 */
@Slf4j
@RestController
@RequestMapping("/api/agent")
public class AgentController {
    
    private final AgentService agentService;
    
    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }
    
    /**
     * 获取Agent信息
     */
    @GetMapping("/info")
    public String getAgentInfo() {
        return agentService.getAgentInfo();
    }
    
    /**
     * 获取所有技能信息
     */
    @GetMapping("/skills")
    public String getSkills() {
        return agentService.getAllSkillsInfo();
    }
    
    /**
     * 处理用户请求
     */
    @PostMapping("/chat")
    public String chat(@RequestBody ChatRequest request) {
        log.info("收到聊天请求：{}", request.message());
        return agentService.processRequest(request.message());
    }
    
    /**
     * 快速测试接口（GET方式）
     */
    @GetMapping("/test")
    public String test(@RequestParam(defaultValue = "北京今天天气怎么样？") String message) {
        log.info("收到测试请求：{}", message);
        return agentService.processRequest(message);
    }
    
    /**
     * 聊天请求对象
     */
    public record ChatRequest(String message) {}
}

