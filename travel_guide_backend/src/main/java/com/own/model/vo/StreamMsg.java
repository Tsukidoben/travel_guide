package com.own.model.vo;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StreamMsg {
    private String type; // status / ai
    private String content;
}
