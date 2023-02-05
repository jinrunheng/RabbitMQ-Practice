package com.github.restaurant.service;

import com.github.restaurant.dao.IProductDao;
import com.github.restaurant.dao.IRestaurantDao;
import com.github.restaurant.dto.OrderMessageDTO;
import com.github.restaurant.entity.Product;
import com.github.restaurant.entity.Restaurant;
import com.github.restaurant.enummeration.OrderStatusEnum;
import com.github.restaurant.enummeration.ProductStatusEnum;
import com.github.restaurant.enummeration.RestaurantStatusEnum;
import com.github.restaurant.utils.JSONUtils;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author Dooby Kim
 * @Date 2022/11/2 6:01 下午
 * @Version 1.0
 */
@Service
@Slf4j
public class OrderMessageService {

    @Resource
    private IProductDao productDao;

    @Resource
    private IRestaurantDao restaurantDao;

    @Async
    public void handleMessage() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try (
                Connection connection = connectionFactory.newConnection();
                Channel channel = connection.createChannel();
        ) {
            // 声明订单与商家微服务使用的 Exchange
            channel.exchangeDeclare(
                    "exchange.order.restaurant",
                    BuiltinExchangeType.DIRECT,
                    true,
                    false,
                    null
            );
            // 声明队列
            channel.queueDeclare(
                    "queue.restaurant",
                    true,
                    false,
                    false,
                    null
            );
            // Queue 与 Exchange 绑定
            channel.queueBind(
                    "queue.restaurant",
                    "exchange.order.restaurant",
                    "key.restaurant"
            );
            // 监听，消费的回调方法
            channel.basicConsume(
                    "queue.restaurant",
                    true,
                    deliverCallback,
                    consumerTag -> {
                    }
            );

            while (true) {
                Thread.sleep(1000000);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    DeliverCallback deliverCallback = ((consumerTag, message) -> {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        try {
            String msgBody = new String(message.getBody());
            OrderMessageDTO orderMessageDTO = (OrderMessageDTO) JSONUtils.jsonToObject(msgBody, OrderMessageDTO.class);
            assert orderMessageDTO != null;

            Product product = productDao.queryProduct(orderMessageDTO.getProductId());
            Restaurant restaurant = restaurantDao.queryRestaurant(product.getRestaurantId());
            // 如果产品状态为可用且商户状态为营业时，将 DTO 设置为"确认"，并设置好价格
            if (product.getStatus() == ProductStatusEnum.AVAILABLE
                    && restaurant.getStatus() == RestaurantStatusEnum.OPEN) {
                orderMessageDTO.setConfirmed(true);
                orderMessageDTO.setPrice(product.getPrice());
            } else {
                orderMessageDTO.setConfirmed(false);
            }

            // 向订单微服务发送消息
            try (
                    Connection connection = connectionFactory.newConnection();
                    Channel channel = connection.createChannel();
            ) {
                String messageToSend = JSONUtils.objectToJson(orderMessageDTO);
                assert messageToSend != null;
                channel.basicPublish("exchange.order.restaurant",
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
