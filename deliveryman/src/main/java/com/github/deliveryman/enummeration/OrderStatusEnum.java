package com.github.deliveryman.enummeration;

/**
 * @Author Dooby Kim
 * @Date 2022/11/2 7:10 下午
 * @Version 1.0
 */
public enum OrderStatusEnum {
    /**
     * 订单创建中
     */
    ORDER_CREATING,
    /**
     * 订单为商户已确认状态
     */
    RESTAURANT_CONFIRMED,

    /**
     * 订单为骑手已确认状态
     */
    DELIVERYMAN_CONFIRMED,

    /**
     * 订单为已结算状态
     */
    SETTLEMENT_CONFIRMED,

    /**
     * 订单已创建
     */
    ORDER_CREATED,

    /**
     * 订单创建失败
     */
    ORDER_FAILED;
}
