package com.own.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 登录Vo类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class LoginVo {
    /**
     * 登录账号
     */
    private String loginAccount;
    /**
     * 密码
     */
    private String password;
    /**
     * 验证码
     */
    private String captcha;
}
