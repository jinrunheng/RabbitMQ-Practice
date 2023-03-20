package com.github.restaurant.config;

import com.github.restaurant.service.OrderMessageService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Author Dooby Kim
 * @Date 2022/11/2 6:42 下午
 * @Version 1.0
 */
@Slf4j
@Configuration
public class RabbitConfig {

//    @Autowired
//    private OrderMessageService orderMessageService;
//
//    @Autowired
//    public void startListenMessage() {
//        orderMessageService.handleMessage();
//    }

}
