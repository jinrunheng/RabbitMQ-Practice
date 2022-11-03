package com.github.settlement.entity;

import com.github.settlement.enummeration.SettlementStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author Dooby Kim
 * @Date 2022/11/3 9:54 下午
 * @Version 1.0
 */
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Settlement {
    private Integer id;
    private Integer orderId;
    private Integer transactionId;
    private SettlementStatus status;
    private BigDecimal amount;
    private Date date;
}
