package com.github.takeaway.entity;

import com.github.takeaway.enummeration.OrderStatusEnum;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author Dooby Kim
 * @Date 2022/10/29 8:43 下午
 * @Version 1.0
 */
@Data
@ToString
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
