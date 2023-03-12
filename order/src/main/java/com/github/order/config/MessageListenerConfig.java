package com.github.order.config;

import com.github.order.dto.OrderMessageDTO;
import com.github.order.entity.OrderDetail;
import com.github.order.enummeration.OrderStatusEnum;
import com.github.order.mapper.OrderDetailMapper;
import com.github.order.service.OrderMessageService;
import com.github.order.utils.JSONUtils;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @Author Dooby Kim
 * @Date 2023/3/7 10:17 下午
 * @Version 1.0
 */
@Configuration
@Slf4j
public class MessageListenerConfig {

    @Autowired
    private OrderMessageService orderMessageService;

    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer messageListenerContainer = new SimpleMessageListenerContainer(connectionFactory);
        messageListenerContainer.setQueueNames("queue.order");
        messageListenerContainer.setConcurrentConsumers(3);
        messageListenerContainer.setMaxConcurrentConsumers(5);
        // 开启自动确认 ack
        // messageListenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
        // 设置回调方法，相当于 channel.basicConsume
        // messageListenerContainer.setMessageListener(this::handle);

        // 消费端开启手动确认
        messageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        // 消费端限流
        messageListenerContainer.setPrefetchCount(20);

        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter();
        // 设置代理
        messageListenerAdapter.setDelegate(orderMessageService);
        messageListenerContainer.setMessageListener(messageListenerAdapter);
        return messageListenerContainer;
    }

}
