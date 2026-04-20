package com.own.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
* 上下文用户vo类
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserContext {
    private String userId;
    private String userName;
    private String userAccount;
    private String userPhone;
    private String userRole;
}
