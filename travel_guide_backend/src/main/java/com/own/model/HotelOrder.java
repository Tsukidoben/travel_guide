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
 * 酒店订单表
 * 实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("hotel_order")
public class HotelOrder  extends Model<HotelOrder> {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 订单状态 10 待付款 20 待核销 80 已核销 -1 已取消
     */
    @TableField(value = "order_state")
    private String orderState;

    /**
     * 酒店快照
     */
    @TableField(value = "hotel_shot")
    private String hotelShot;

    /**
     * 酒店id
     */
    @TableField(value = "hotel_id")
    private String hotelId;

    /**
     * 订单价格
     */
    @TableField(value = "total_price")
    private BigDecimal totalPrice;

    /**
     * 房间快照
     */
    @TableField(value = "room_shot")
    private String roomShot;

    /**
     * 房间id
     */
    @TableField(value = "room_id")
    private String roomId;

    /**
     * 订单编码
     */
    @TableField(value = "order_code")
    private String orderCode;

    /**
     * 订单备注
     */
    @TableField(value = "order_desc")
    private String orderDesc;

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
     * 购买用户
     */
    @TableField(value = "user_id")
    private String userId;

    /**
     * 购买数量
     */
    @TableField(value = "buy_count")
    private Integer buyCount;

    /**
     * 天数
     */
    @TableField(value = "days")
    private Integer days;

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
     * 开始日期
     */
    @TableField(value = "start_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;

    /**
     * 结束日期
     */
    @TableField(value = "end_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;

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
    * 关键字查询
    */
    @TableField(exist = false)
    private String keyword;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
