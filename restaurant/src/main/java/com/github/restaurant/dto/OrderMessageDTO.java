package com.github.restaurant.dto;

import com.github.restaurant.enummeration.OrderStatusEnum;
import lombok.*;

import java.math.BigDecimal;

/**
 * 订单 DTO，为传递的消息对象
 *
 * @Author Dooby Kim
 * @Date 2022/10/29 8:24 下午
 * @Version 1.0
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderMessageDTO {
    /**
     * 订单 ID
     */
    private Integer orderId;
    /**
     * 订单状态
     */
    private OrderStatusEnum orderStatus;

    /**
     * 订单价格
     */
    private BigDecimal price;

    /**
     * 骑手 ID
     */
    private Integer deliverymanId;

    /**
     * 产品 ID
     */
    private Integer productId;

    /**
     * 用户 ID
     */
    private Integer accountId;

    /**
     * 结算 ID
     */
    private Integer settlementId;

    /**
     * 积分结算 ID
     */
    private Integer rewardId;

    /**
     * 积分奖励数量
     */
    private Integer rewardAmount;

    /**
     * 确认
     */
    private Boolean confirmed;

}
