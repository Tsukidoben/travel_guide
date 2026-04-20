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
 * 景点订单表
 * 实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("attraction_order")
public class AttractionOrder  extends Model<AttractionOrder> {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 订单编码
     */
    @TableField(value = "order_code")
    private String orderCode;

    /**
     * 景点id
     */
    @TableField(value = "attraction_id")
    private String attractionId;

    /**
     * 门票id
     */
    @TableField(value = "ticket_id")
    private String ticketId;

    /**
     * 购买数量
     */
    @TableField(value = "buy_count")
    private Integer buyCount;

    /**
     * 订单价格
     */
    @TableField(value = "total_price")
    private BigDecimal totalPrice;

    /**
     * 景点快照
     */
    @TableField(value = "attraction_shot")
    private String attractionShot;

    /**
     * 门票快照
     */
    @TableField(value = "ticket_shot")
    private String ticketShot;

    /**
     * 订单备注
     */
    @TableField(value = "order_desc")
    private String orderDesc;

    /**
     * 购买用户
     */
    @TableField(value = "user_id")
    private String userId;

    /**
     * 核销码
     */
    @TableField(value = "captcha")
    private String captcha;

    /**
     * 核销时间
     */
    @TableField(value = "writeoff_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date writeoffTime;

    /**
     * 订单状态 10 待付款 20 待核销 80 已核销 -1 已取消
     */
    @TableField(value = "order_state")
    private String orderState;

    /**
     * 支付流水号
     */
    @TableField(value = "pay_no")
    private String payNo;

    /**
     * 支付时间
     */
    @TableField(value = "pay_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date payTime;

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
