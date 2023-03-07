package com.github.restaurant.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Author Dooby Kim
 * @Date 2023/3/7 10:44 下午
 * @Version 1.0
 */
@Configuration
@AutoConfigureOrder(1)
public class RabbitChannelConfig {

    @Bean
    public Channel rabbitChannel() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Connection connection = connectionFactory.newConnection();
        return connection.createChannel();
    }
}
