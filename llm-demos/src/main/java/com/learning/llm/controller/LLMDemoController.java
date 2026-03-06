package com.learning.llm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * LLM Demo 控制器
 * 
 * @author Learning
 */
@RestController
@RequestMapping("/api/llm")
public class LLMDemoController {

    @GetMapping("/hello")
    public String hello() {
        return "Welcome to LLM Demos! 欢迎来到大语言模型学习示例项目！";
    }
}

