package com.github.deliveryman.entity;

import com.github.deliveryman.enummeration.DeliverymanStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * PO
 *
 * @Author Dooby Kim
 * @Date 2022/11/2 7:01 下午
 * @Version 1.0
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Deliveryman {
    private Integer id;
    private String name;
    private String district;
    private DeliverymanStatusEnum status;
    private Date date;

}
