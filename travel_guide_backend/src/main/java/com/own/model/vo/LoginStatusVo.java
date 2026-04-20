package com.own.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * 登录状态记录表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class LoginStatusVo {
    /**
     * 用户id
     */
    private String userId;
    /**
     * 上次登录失败时间
     */
    private LocalDate lastErrorTime;
    /**
     * 失败次数
     */
    private Integer errorCount;
}
