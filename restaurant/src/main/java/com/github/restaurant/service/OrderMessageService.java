package com.github.restaurant.service;

import com.github.restaurant.dao.IProductDao;
import com.github.restaurant.dao.IRestaurantDao;
import com.github.restaurant.dto.OrderMessageDTO;
import com.github.restaurant.entity.Product;
import com.github.restaurant.entity.Restaurant;
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
            // 声明 Exchange
            channel.exchangeDeclare(
                    "exchange.order.restaurant",
                    BuiltinExchangeType.DIRECT,
                    true,
                    false,
                    null
            );
            // 声明 Queue
            channel.queueDeclare(
                    "queue.restaurant",
                    true,
                    false,
                    false,
                    null
            );
            // 声明 Exchange 和 Queue 的绑定关系
            channel.queueBind(
                    "queue.restaurant",
                    "exchange.order.restaurant",
                    "key.restaurant"
            );
            // 接收消息
            channel.basicConsume(
                    "queue.restaurant",
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
            Product product = productDao.queryProduct(orderMessageDTO.getProductId());
            Restaurant restaurant = restaurantDao.queryRestaurant(product.getRestaurantId());

            if (product.getStatus() == ProductStatusEnum.AVAILABLE
                    && restaurant.getStatus() == RestaurantStatusEnum.OPEN) {
                orderMessageDTO.setConfirmed(true);
                orderMessageDTO.setPrice(product.getPrice());
            } else {
                orderMessageDTO.setConfirmed(false);
            }

            try (
                    Connection connection = connectionFactory.newConnection();
                    Channel channel = connection.createChannel();
            ) {
                String messageToSend = JSONUtils.objectToJson(orderMessageDTO);
                channel.basicPublish("exchange.order.restaurant",
                        "key.order"
                        , null,
                        messageToSend.getBytes()
                );
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    });
}
