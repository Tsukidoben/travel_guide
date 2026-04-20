package com.own.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.extension.activerecord.Model;

/**
 * 景点收藏表
 * 实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("attraction_collection")
public class AttractionCollection  extends Model<AttractionCollection> {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 景点id
     */
    @TableField(value = "attraction_id")
    private String attractionId;
    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private String userId;
    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(exist = false)
    private String attractionName;

    @TableField(exist = false)
    private String attractionDesc;

    @TableField(exist = false)
    private String attractionPic;

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
