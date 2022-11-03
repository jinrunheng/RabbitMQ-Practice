package com.github.settlement.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

/**
 * @Author Dooby Kim
 * @Date 2022/11/3 10:03 下午
 * @Version 1.0
 */
@Service
public class SettlementService {
    // Mock
    // 实际上应返回结算 ID
    public Integer settlement(Integer accountId, BigDecimal amount) {
        Random random = new Random(100);
        return random.nextInt(100000);
    }
}
