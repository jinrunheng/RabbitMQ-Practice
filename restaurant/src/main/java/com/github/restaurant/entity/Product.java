package com.github.restaurant.entity;

import com.github.restaurant.enummeration.ProductStatusEnum;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author Dooby Kim
 * @Date 2022/10/31 9:44 下午
 * @Version 1.0
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    private Integer id;
    private String name;
    private BigDecimal price;
    private Integer restaurantId;
    private ProductStatusEnum status;
    private Date date;
}
