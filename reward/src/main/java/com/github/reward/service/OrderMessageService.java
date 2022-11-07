package com.github.reward.service;

import com.github.reward.dao.IRewardDao;
import com.github.reward.dto.OrderMessageDTO;
import com.github.reward.entity.Reward;
import com.github.reward.enummeration.RewardStatus;
import com.github.reward.utils.JSONUtils;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author Dooby Kim
 * @Date 2022/11/7 7:31 下午
 * @Version 1.0
 */
@Slf4j
@Service
public class OrderMessageService {

    @Autowired
    private IRewardDao rewardDao;

    @Async
    public void handleMessage() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        try (
                Connection connection = connectionFactory.newConnection();
                Channel channel = connection.createChannel()
        ) {
            channel.exchangeDeclare(
                    "exchange.order.reward",
                    BuiltinExchangeType.TOPIC,
                    true,
                    false,
                    null
            );

            channel.queueDeclare(
                    "queue.reward",
                    true,
                    false,
                    false,
                    null
            );

            channel.queueBind(
                    "queue.reward",
                    "exchange.order.reward",
                    "key.reward"
            );

            channel.basicConsume(
                    "queue.reward",
                    true, deliverCallback,
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

    DeliverCallback deliverCallback = (consumerTag, message) -> {
        String messageBody = new String(message.getBody());
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        try {
            OrderMessageDTO orderMessageDTO = (OrderMessageDTO) JSONUtils.jsonToObject(messageBody, OrderMessageDTO.class);

            Reward reward = Reward.builder().
                    orderId(orderMessageDTO.getOrderId())
                    .status(RewardStatus.SUCCESS)
                    .amount(orderMessageDTO.getPrice())
                    .date(new Date())
                    .build();

            rewardDao.insert(reward);

            try (
                    Connection connection = connectionFactory.newConnection();
                    Channel channel = connection.createChannel();
            ) {
                String messageToSend = JSONUtils.objectToJson(orderMessageDTO);
                // 向订单微服务发送消息
                channel.basicPublish(
                        "exchange.order.reward",
                        "key.order",
                        null,
                        messageToSend.getBytes()
                );
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    };
}
