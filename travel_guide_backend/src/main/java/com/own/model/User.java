package com.own.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.extension.activerecord.Model;

/**
 * 用户信息表
 * 实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("user")
public class User  extends Model<User> {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 用户名
     */
    @TableField(value = "user_name")
    private String userName;

    /**
     * 用户账号
     */
    @TableField(value = "user_account")
    private String userAccount;

    /**
     * 密码
     */
    @TableField(value = "password")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * 用户头像
     */
    @TableField(value = "head_pic_url")
    private String headPicUrl;

    /**
     * 角色 1 管理员 3 商家 2 用户
     */
    @TableField(value = "user_role")
    private String userRole;

    /**
     * 性别 1 男 0 女
     */
    @TableField(value = "user_sex")
    private String userSex;

    /**
     * 联系电话
     */
    @TableField(value = "user_phone")
    private String userPhone;

    /**
     * 注册时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;


    /**
    * 关键字查询
    */
    @TableField(exist = false)
    private String keyword;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
