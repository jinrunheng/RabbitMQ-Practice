package com.github.restaurant.config;

import com.github.restaurant.service.OrderMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

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
