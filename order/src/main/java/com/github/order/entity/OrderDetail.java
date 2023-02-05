package com.github.order.entity;

import com.github.order.enummeration.OrderStatusEnum;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * PO 或者 Entity，PO 为 persistant object，即持久化对象，为对应到数据库的对象
 *
 * @Author Dooby Kim
 * @Date 2022/10/29 8:43 下午
 * @Version 1.0
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetail {
    /**
     * 订单 ID
     */
    private Integer id;
    /**
     * 订单状态
     */
    private OrderStatusEnum status;
    /**
     * 订单地址
     */
    private String address;
    /**
     * 用户 ID
     */
    private Integer accountId;
    /**
     * 产品 ID
     */
    private Integer productId;
    /**
     * 骑手 ID
     */
    private Integer deliverymanId;
    /**
     * 结算 ID
     */
    private Integer settlementId;
    /**
     * 积分 ID
     */
    private Integer rewardId;
    /**
     * 价格
     */
    private BigDecimal price;
    /**
     * 日期
     */
    private Date date;
}
