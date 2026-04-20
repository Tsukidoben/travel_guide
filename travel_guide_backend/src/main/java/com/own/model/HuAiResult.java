package com.own.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * ai对话结果表
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@TableName(value = "hu_ai_result")
public class HuAiResult {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 对话用户id
     */
    @TableField(value = "user_id")
    private String userId;

    /**
     * ai的对话结果
     */
    @TableField(value = "ai_result")
    private String aiResult;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 用户的提问
     */
    @TableField(value = "user_ques")
    private String userQues;
}
