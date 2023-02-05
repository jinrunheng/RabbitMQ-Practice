package com.github.order.service;

import com.github.order.dto.OrderMessageDTO;
import com.github.order.entity.OrderDetail;
import com.github.order.enummeration.OrderStatusEnum;
import com.github.order.mapper.OrderDetailMapper;
import com.github.order.utils.JSONUtils;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author Dooby Kim
 * @Date 2022/10/29 9:46 下午
 * @Version 1.0
 * <p>
 * 消息处理相关业务逻辑
 */
@Service
@Slf4j
public class OrderMessageService {

    @Resource
    private OrderDetailMapper orderDetailMapper;

    /**
     * 声明消息队列，交换机，绑定，消息的处理，为异步线程，使用 @Async 注解
     */
    @Async
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

            // 声明结算的 Exchange
            channel.exchangeDeclare("exchange.order.settlement", BuiltinExchangeType.FANOUT, true, false, null);
            // Queue 与 Exchange 绑定
            channel.queueBind("queue.order", "exchange.settlement.order", "key.order");

            // 声明积分的 Exchange
            channel.exchangeDeclare(
                    "exchange.order.reward",
                    BuiltinExchangeType.TOPIC,
                    true,
                    false,
                    null
            );
            // Queue 与 Exchange 绑定
            channel.queueBind(
                    "queue.order",
                    "exchange.order.reward",
                    "key.order"
            );

            // 注册消费方法
            channel.basicConsume("queue.order", true, deliverCallback, consumerTag -> {
            });

            while (true) {
                Thread.sleep(10000);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    // 消费者消费的回调方法
    DeliverCallback deliverCallback = (this::handle);

    private void handle(String consumerTag, Delivery message) {
        String msg = new String(message.getBody());

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try {
            // 将消息体反序列化为 DTO
            OrderMessageDTO orderMessageDTO = (OrderMessageDTO) JSONUtils.jsonToObject(msg, OrderMessageDTO.class);
            // 从数据库中读取订单
            OrderDetail orderDetail = orderDetailMapper.queryOrder(orderMessageDTO.getOrderId());

            // 判断订单状态
            switch (orderDetail.getStatus()) {

                case ORDER_CREATING:
                    // 订单为创建中状态
                    // 如果商家已确认，且价格不为空，则将订单状态设置为商家已确认,并设置价格
                    if (orderMessageDTO.getConfirmed() && orderMessageDTO.getPrice() != null) {
                        orderDetail.setStatus(OrderStatusEnum.RESTAURANT_CONFIRMED);
                        orderDetail.setPrice(orderMessageDTO.getPrice());
                        orderDetailMapper.update(orderDetail);
                        // 接下来向骑手微服务发送消息
                        try (
                                Connection connection = connectionFactory.newConnection();
                                Channel channel = connection.createChannel()
                        ) {
                            String messageToSend = JSONUtils.objectToJson(orderMessageDTO);
                            channel.basicPublish("exchange.order.deliveryman",
                                    "key.deliveryman",
                                    null,
                                    messageToSend.getBytes());
                        }
                    } else {
                        // 否则订单失败
                        orderDetail.setStatus(OrderStatusEnum.ORDER_FAILED);
                        orderDetailMapper.update(orderDetail);
                    }
                    break;
                case RESTAURANT_CONFIRMED:
                    if (orderMessageDTO.getDeliverymanId() != null) {
                        orderDetail.setStatus(OrderStatusEnum.DELIVERYMAN_CONFIRMED);
                        orderDetail.setDeliverymanId(orderMessageDTO.getDeliverymanId());
                        orderDetailMapper.update(orderDetail);
                        // 将消息发送给结算微服务
                        try (
                                Connection connection = connectionFactory.newConnection();
                                Channel channel = connection.createChannel();
                        ) {
                            String messageToSend = JSONUtils.objectToJson(orderMessageDTO);
                            channel.basicPublish(
                                    "exchange.order.settlement",
                                    "key.settlement",
                                    null,
                                    messageToSend.getBytes()
                            );
                        }
                    } else {
                        orderDetail.setStatus(OrderStatusEnum.ORDER_FAILED);
                        orderDetailMapper.update(orderDetail);
                    }
                    break;
                case DELIVERYMAN_CONFIRMED:
                    if (orderMessageDTO.getSettlementId() != null) {
                        orderDetail.setStatus(OrderStatusEnum.SETTLEMENT_CONFIRMED);
                        orderDetail.setSettlementId(orderMessageDTO.getSettlementId());
                        orderDetailMapper.update(orderDetail);
                        // 给积分微服务发送消息
                        try (Connection connection = connectionFactory.newConnection();
                             Channel channel = connection.createChannel()) {
                            String messageToSend = JSONUtils.objectToJson(orderMessageDTO);
                            channel.basicPublish(
                                    "exchange.order.reward",
                                    "key.reward",
                                    null,
                                    messageToSend.getBytes()
                            );
                        } catch (Exception e) {

                        }
                    } else {
                        // 如果返回订单消息体中 settleId 为空，则代表订单失败
                        orderDetail.setStatus(OrderStatusEnum.ORDER_FAILED);
                        orderDetailMapper.update(orderDetail);
                    }

                    break;
                case SETTLEMENT_CONFIRMED:

                    if (orderMessageDTO.getRewardId() != null) {
                        orderDetail.setStatus(OrderStatusEnum.ORDER_CREATED);
                        orderDetail.setRewardId(orderMessageDTO.getRewardId());
                        orderDetailMapper.update(orderDetail);
                    } else {
                        orderDetail.setStatus(OrderStatusEnum.ORDER_FAILED);
                        orderDetailMapper.update(orderDetail);
                    }
                case ORDER_CREATED:
                case ORDER_FAILED:
                    break;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
