package com.github.restaurant.entity;

import com.github.restaurant.enummeration.RestaurantStatusEnum;
import lombok.*;

import java.util.Date;

/**
 * 商户 PO
 *
 * @Author Dooby Kim
 * @Date 2022/10/31 9:47 下午
 * @Version 1.0
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Restaurant {
    private Integer id;
    private String name;
    private String address;
    private RestaurantStatusEnum status;
    private Date date;
}
