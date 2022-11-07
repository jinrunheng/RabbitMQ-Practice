package com.github.order.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @Author Dooby Kim
 * @Date 2022/10/29 8:18 下午
 * @Version 1.0
 * <p>
 * 订单创建 VO；VO 为前端传递给后端的数据格式
 */
@Data
@ToString
public class OrderCreateVO {
    /**
     * 用户 ID
     */
    private Integer accountId;
    /**
     * 用户地址
     */
    private String address;

    /**
     * 产品 ID
     */
    private Integer productId;
}
