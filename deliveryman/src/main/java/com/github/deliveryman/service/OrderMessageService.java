package com.github.deliveryman.service;

import com.github.deliveryman.dao.IDeliverymanDao;
import com.github.deliveryman.dto.OrderMessageDTO;
import com.github.deliveryman.entity.Deliveryman;
import com.github.deliveryman.enummeration.DeliverymanStatusEnum;
import com.github.deliveryman.utils.JSONUtils;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author Dooby Kim
 * @Date 2022/11/2 7:16 下午
 * @Version 1.0
 */
@Service
@Slf4j
public class OrderMessageService {

    @Resource
    private IDeliverymanDao deliverymanDao;

    @Async
    public void handleMessage() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try (
                Connection connection = connectionFactory.newConnection();
                Channel channel = connection.createChannel();
        ) {
            // 声明 Exchange
            channel.exchangeDeclare(
                    "exchange.order.deliveryman",
                    BuiltinExchangeType.DIRECT,
                    true,
                    false,
                    null
            );

            // 声明 Queue
            channel.queueDeclare(
                    "queue.deliveryman",
                    true,
                    false,
                    false,
                    null
            );

            // 声明 Exchange 和 Queue 的绑定关系
            channel.queueBind(
                    "queue.deliveryman",
                    "exchange.order.deliveryman",
                    "key.deliveryman"
            );

            // 接收消息
            channel.basicConsume(
                    "queue.deliveryman",
                    true,
                    deliverCallback,
                    consumerTag -> {
                    }
            );

            while (true) {
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    DeliverCallback deliverCallback = ((consumerTag, message) -> {
        String msgBody = new String(message.getBody());
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try {
            OrderMessageDTO orderMessageDTO = (OrderMessageDTO) JSONUtils.jsonToObject(msgBody, OrderMessageDTO.class);
            List<Deliveryman> deliverymen = deliverymanDao.queryDeliverymanByStatus(DeliverymanStatusEnum.AVAILABLE);
            orderMessageDTO.setDeliverymanId(deliverymen.get(0).getId());

            try (
                    Connection connection = connectionFactory.newConnection();
                    Channel channel = connection.createChannel();
            ) {
                String messageToSend = JSONUtils.objectToJson(orderMessageDTO);
                channel.basicPublish("exchange.order.deliveryman",
                        "key.order",
                        null,
                        messageToSend.getBytes()
                );

            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    });

}
