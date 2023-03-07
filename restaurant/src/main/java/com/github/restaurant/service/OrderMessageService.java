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
import org.springframework.beans.factory.annotation.Autowired;
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
    private Channel channel;

    @Async
    public void handleMessage() {

        try {
            // 声明订单与商家微服务使用的 Exchange
            channel.exchangeDeclare(
                    "exchange.order.restaurant",
                    BuiltinExchangeType.DIRECT,
                    true,
                    false,
                    null
            );

            // 声明死信交换机
            channel.exchangeDeclare(
                    "exchange.dlx",
                    BuiltinExchangeType.TOPIC,
                    true,
                    false,
                    null
            );

            // 声明接收死信的队列，需要和死信队列区别开，死信队列是设置了 x-dead-letter-exchange 属性的队列
            channel.queueDeclare(
                    "queue.dlx",
                    true,
                    false,
                    false,
                    null
            );

            // 将死信队列与死信交换机进行绑定
            channel.queueBind(
                    "queue.dlx",
                    "exchange.dlx",
                    "#"
            );

            // 设置队列 TTL 为 1 min
            Map<String, Object> args = new HashMap<>(16);
            args.put("x-message-ttl", 60000);
            // 设置死信队列
            args.put("x-dead-letter-exchange", "exchange.dlx");
            // x-expire 为队列的存活时间，如果在一定的时间内，队列没有接收到消息，队列会被删除；不要加入这样一个参数
            // args.put("x-expire",60000);
            // 声明队列
            channel.queueDeclare(
                    "queue.restaurant",
                    true,
                    false,
                    false,
                    args
            );
            // Queue 与 Exchange 绑定
            channel.queueBind(
                    "queue.restaurant",
                    "exchange.order.restaurant",
                    "key.restaurant"
            );
            // 消费端限流机制
            // 同时可以有五条消息被处理
            channel.basicQos(5);
            // 监听，消费的回调方法
            // autoAck 设置为 false,关闭消息自动确认
            channel.basicConsume(
                    "queue.restaurant",
                    false,
                    deliverCallback,
                    consumerTag -> {
                    }
            );

            while (true) {
                Thread.sleep(1000000);
            }

        } catch (
                Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    DeliverCallback deliverCallback = ((consumerTag, message) -> {
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

            // 消息返回机制，开启监听
            channel.addReturnListener((replyCode, replyText, exchange, routingKey, properties, body) -> {
                log.info("Message Return");
                // TODO:说明消息不可达
            });

            // 消费端手动确认
            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);

            // mandatory 设置为 true，开启消息返回机制
            channel.basicPublish("exchange.order.restaurant",
                    "key.order",
                    true,
                    null,
                    messageToSend.getBytes()
            );

            Thread.sleep(10000);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    });
}
