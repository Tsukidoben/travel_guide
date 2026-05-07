package com.own.model.vo;

import lombok.Data;

import java.util.List;

/**
 * 批量删除请求VO
 */
@Data
public class BatchDeleteVo {
    
    /**
     * 要删除的行程ID列表
     */
    private List<String> ids;
}
