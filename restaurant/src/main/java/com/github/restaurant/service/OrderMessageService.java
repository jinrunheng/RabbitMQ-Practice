package com.github.restaurant.service;

import com.github.restaurant.dao.IProductDao;
import com.github.restaurant.dao.IRestaurantDao;
import com.github.restaurant.dto.OrderMessageDTO;
import com.github.restaurant.entity.Product;
import com.github.restaurant.entity.Restaurant;
import com.github.restaurant.enummeration.ProductStatusEnum;
import com.github.restaurant.enummeration.RestaurantStatusEnum;
import com.github.restaurant.utils.JSONUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

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

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // 消费者收到消息并消费的回调方法
    @RabbitListener(
            bindings = {
                    @QueueBinding(
                            value = @Queue(name = "queue.restaurant"),
                            exchange = @Exchange(name = "exchange.order.restaurant"),
                            key = "key.restaurant",
                            arguments = {
                                    // 设置队列 TTL 为 1 min
                                    @Argument(name = "x-message-ttl", value = "60000"),
                                    // 设置死信队列
                                    @Argument(name = "x-dead-letter-exchange", value = "exchange.dlx")
                            }
                    ),
                    @QueueBinding(
                            value = @Queue(name = "queue.dlx"),
                            exchange = @Exchange(name = "exchange.dlx", type = ExchangeTypes.TOPIC),
                            key = "#"
                    ),
            }
    )
    public void handleMessage(@Payload Message message) {

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
            String messageToSend = JSONUtils.objectToJson(orderMessageDTO);
            assert messageToSend != null;
            rabbitTemplate.send(
                    "exchange.order.restaurant",
                    "key.order",
                    new Message(messageToSend.getBytes())
            );

        } catch (
                Exception e) {
            log.error(e.getMessage(), e);
        }

    }
}
