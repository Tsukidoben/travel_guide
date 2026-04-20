package com.own.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class AgentPlan {
    private String targetTask;
    private List<AgentStep> steps;
}
