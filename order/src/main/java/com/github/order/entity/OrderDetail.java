package com.github.order.entity;

import com.github.order.enummeration.OrderStatusEnum;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

/**
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
    private Integer id;
    private OrderStatusEnum status;
    private String address;
    private Integer accountId;
    private Integer productId;
    private Integer deliverymanId;
    private Integer settlementId;
    private Integer rewardId;
    private BigDecimal price;
    private Date date;
}
