package com.github.reward.entity;

import com.github.reward.enummeration.RewardStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author Dooby Kim
 * @Date 2022/11/7 7:33 下午
 * @Version 1.0
 */
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Reward {
    private Integer id;
    private Integer orderId;
    private BigDecimal amount;
    private RewardStatus status;
    private Date date;
}
