package com.github.takeaway.config;

import com.github.takeaway.service.OrderMessageService;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @Author Dooby Kim
 * @Date 2022/10/31 9:02 下午
 * @Version 1.0
 */
@Configuration
public class RabbitConfig {

    @Resource
    private OrderMessageService orderMessageService;

    @Resource
    public void startListenMessage() {
        orderMessageService.handleMessage();
    }
}
