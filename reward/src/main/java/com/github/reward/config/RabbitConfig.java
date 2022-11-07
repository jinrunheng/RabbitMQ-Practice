package com.github.reward.config;

import com.github.reward.service.OrderMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Dooby Kim
 * @Date 2022/11/2 6:42 下午
 * @Version 1.0
 */
@Slf4j
@Configuration
public class RabbitConfig {
    @Autowired
    private OrderMessageService orderMessageService;

    @Autowired
    public void startListenMessage() {
        orderMessageService.handleMessage();
    }
}
