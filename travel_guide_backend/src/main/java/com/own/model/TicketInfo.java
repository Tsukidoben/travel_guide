package com.own.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 门票管理表
 * 实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("ticket_info")
public class TicketInfo  extends Model<TicketInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 门票名称
     */
    @TableField(value = "ticket_name")
    private String ticketName;

    /**
     * 门票价格
     */
    @TableField(value = "ticket_price")
    private BigDecimal ticketPrice;

    /**
     * 所属景点
     */
    @TableField(value = "attraction_id")
    private String attractionId;

    /**
     * 使用范围
     */
    @TableField(value = "use_scope")
    private String useScope;

    /**
     * 门票状态 1 正常 2 下架
     */
    @TableField(value = "status")
    private String status;

    /**
     * 创建人
     */
    @TableField(value = "creator", fill = FieldFill.INSERT)
    private String creator;

    /**
     * 创建人
     */
    @TableField(value = "create_name", fill = FieldFill.INSERT)
    private String createName;

    /**
     * 创建时间
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
