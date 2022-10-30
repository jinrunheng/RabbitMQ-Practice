package com.github.takeaway.service;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.stereotype.Service;

/**
 * @Author Dooby Kim
 * @Date 2022/10/29 9:46 下午
 * @Version 1.0
 * <p>
 * 消息处理相关业务逻辑
 */
@Service
public class OrderMessageService {

    /**
     * 声明消息队列，交换机，绑定，消息的处理
     */
    public void handleMessage() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try (
                Connection connection = connectionFactory.newConnection();
                Channel channel = connection.createChannel()
        ) {

            // 声明 Queue
            channel.queueDeclare("queue.order", true, false, false, null);

            // 声明商家的 Exchange
            channel.exchangeDeclare("exchange.order.restaurant", BuiltinExchangeType.DIRECT, true, false, null);
            // Queue 与 Exchange 绑定
            channel.queueBind("queue.order", "exchange.order.restaurant", "key.order");

            // 声明骑手的 Exchange
            channel.exchangeDeclare("exchange.order.deliveryman", BuiltinExchangeType.DIRECT, true, false, null);
            // Queue 与 Exchange 绑定
            channel.queueBind("queue.order", "exchange.order.deliveryman", "key.order");


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
