package com.own.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 密码重置类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class PwdVo {
    /**
     * 用户id，传了就是重置密码，没传就是修改自身密码
     */
    private String userId;
    /**
     * 旧密码
     */
    private String oldPwd;
    /**
     * 新密码
     */
    private String newPwd;
    /**
     * 重复新密码
     */
    private String repeatPwd;
}
