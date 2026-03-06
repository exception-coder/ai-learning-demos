package com.learning.llm.agent;

import com.learning.llm.skill.Skill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 技能注册中心
 * 
 * 管理所有可用的技能
 * Agent通过注册中心获取和调用技能
 * 
 * @author Learning
 */
@Slf4j
@Component
public class SkillRegistry {
    
    private final Map<String, Skill> skills = new HashMap<>();
    
    /**
     * 构造函数注入所有技能
     */
    public SkillRegistry(List<Skill> skillList) {
        for (Skill skill : skillList) {
            registerSkill(skill);
        }
        log.info("技能注册中心初始化完成，共注册{}个技能", skills.size());
    }
    
    /**
     * 注册技能
     */
    public void registerSkill(Skill skill) {
        skills.put(skill.getName(), skill);
        log.info("注册技能：{} - {}", skill.getName(), skill.getDescription());
    }
    
    /**
     * 获取技能
     */
    public Skill getSkill(String name) {
        return skills.get(name);
    }
    
    /**
     * 获取所有技能
     */
    public Map<String, Skill> getAllSkills() {
        return new HashMap<>(skills);
    }
    
    /**
     * 获取技能列表描述（用于提示词）
     */
    public String getSkillsDescription() {
        StringBuilder sb = new StringBuilder("可用技能列表：\n");
        for (Skill skill : skills.values()) {
            sb.append(String.format("- %s: %s\n", skill.getName(), skill.getDescription()));
        }
        return sb.toString();
    }
}

