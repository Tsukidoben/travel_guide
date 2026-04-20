package com.own.model.vo;

import lombok.Data;

import java.util.Map;

@Data
public class AgentStep {
    private String intent;
    private String tool;
    private Map<String, Object> params;
}
