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
            // 声明订单与骑手微服务使用的 Exchange
            channel.exchangeDeclare(
                    "exchange.order.deliveryman",
                    BuiltinExchangeType.DIRECT,
                    true,
                    false,
                    null
            );

            // 声明队列
            channel.queueDeclare(
                    "queue.deliveryman",
                    true,
                    false,
                    false,
                    null
            );

            // Queue 与 Exchange 绑定
            channel.queueBind(
                    "queue.deliveryman",
                    "exchange.order.deliveryman",
                    "key.deliveryman"
            );

            // 监听，消费的回调方法
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

    // 消费者收到消息并消费的回调方法
    DeliverCallback deliverCallback = (this::handle);

    private void handle(String consumerTag, Delivery message) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try {
            String msgBody = new String(message.getBody());
            OrderMessageDTO orderMessageDTO = (OrderMessageDTO) JSONUtils.jsonToObject(msgBody, OrderMessageDTO.class);
            // 查询当前空闲的骑手，并将该订单分配给返回列表的第一个骑手
            List<Deliveryman> deliverymen = deliverymanDao.queryDeliverymanByStatus(DeliverymanStatusEnum.AVAILABLE);
            // 当前无空闲的骑手时，直接查询所有忙碌的骑手，并将该订单分配给返回列表的第一个骑手
            if (deliverymen == null || deliverymen.size() <= 0) {
                deliverymen = deliverymanDao.queryDeliverymanByStatus(DeliverymanStatusEnum.NOT_AVAILABLE);
            }
            assert orderMessageDTO != null;
            orderMessageDTO.setDeliverymanId(deliverymen.get(0).getId());

            // 向订单微服务发送消息
            try (
                    Connection connection = connectionFactory.newConnection();
                    Channel channel = connection.createChannel();
            ) {
                String messageToSend = JSONUtils.objectToJson(orderMessageDTO);
                assert messageToSend != null;
                channel.basicPublish("exchange.order.deliveryman",
                        "key.order",
                        null,
                        messageToSend.getBytes()
                );

            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
