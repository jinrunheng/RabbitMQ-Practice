package com.github.order.config;

import com.github.order.service.OrderMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * 该类的作用是作为一个配置类，在 SpringBoot 启动时，去找到这样一个配置类，去启动异步线程调用 OrderMessageService.handleMessage
 *
 * @Author Dooby Kim
 * @Date 2022/10/31 9:02 下午
 * @Version 1.0
 */
@Configuration
public class RabbitConfig {

    @Autowired
    private OrderMessageService orderMessageService;

    @Autowired
    public void startListenMessage() {
        orderMessageService.handleMessage();
    }
}
