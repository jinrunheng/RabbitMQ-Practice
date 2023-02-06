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
     * 声明消息队列，交换机，绑定，消息的处理；异步线程，使用 @Async 注解
     */
    @Async
    public void handleMessage() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try (
                Connection connection = connectionFactory.newConnection();
                Channel channel = connection.createChannel()
        ) {

            // 声明队列 Queue
            channel.queueDeclare("queue.order",
                    true,
                    false,
                    false,
                    null);

            /*-------------------- restaurant --------------------*/
            // 声明订单与商家微服务使用的 Exchange
            channel.exchangeDeclare("exchange.order.restaurant",
                    BuiltinExchangeType.DIRECT,
                    true,
                    false,
                    null);

            // 将 exchange.order.restaurant 这个 Exchange 与 queue.order 这个队列进行绑定（Binding）
            channel.queueBind("queue.order",
                    "exchange.order.restaurant",
                    "key.order");
            /*-----------------------------------------------------*/

            /*-------------------- deliveryman --------------------*/
            // 声明订单与骑手微服务使用的 Exchange
            channel.exchangeDeclare("exchange.order.deliveryman",
                    BuiltinExchangeType.DIRECT,
                    true,
                    false,
                    null);
            // 将 exchange.order.deliveryman 这个 Exchange 与 queue.order 这个队列进行绑定（Binding）
            channel.queueBind("queue.order",
                    "exchange.order.deliveryman",
                    "key.order");
            /*-----------------------------------------------------*/

            /*-------------------- settlement --------------------*/
            // 声明订单与结算微服务使用的 Exchange
            channel.exchangeDeclare("exchange.order.settlement",
                    BuiltinExchangeType.FANOUT,
                    true,
                    false,
                    null);

            channel.queueBind("queue.order",
                    "exchange.order.settlement",
                    "key.order");
            /*-----------------------------------------------------*/

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

            // 监听，消费的回调方法
            channel.basicConsume("queue.order",
                    true,
                    deliverCallback,
                    consumerTag -> {
                    });

            while (true) {
                Thread.sleep(1000000);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    // 消费者收到消息并消费的回调方法
    DeliverCallback deliverCallback = (this::handle);

    private void handle(String consumerTag, Delivery message) {
        String msg = new String(message.getBody());

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try {
            // 将消息体反序列化为 DTO
            OrderMessageDTO orderMessageDTO = (OrderMessageDTO) JSONUtils.jsonToObject(msg, OrderMessageDTO.class);
            // 从数据库中读取订单
            assert orderMessageDTO != null;
            OrderDetail orderDetail = orderDetailMapper.queryOrder(orderMessageDTO.getOrderId());

            // 判断订单状态
            switch (orderDetail.getStatus()) {
                /*------------------ 订单为创建中状态 ------------------*/
                case ORDER_CREATING:
                    // 如果订单状态为创建中：
                    // 首先判断接收的消息 DTO 中的状态是已确认，且价格设置不为空，如果是，则更新 PO（entity） 的订单信息状态为商户已确认并设置价格后持久化到数据库中
                    // 接着，向骑手微服务发送消息
                    // 如果判断失败，则更新订单状态为失败
                    if (orderMessageDTO.getConfirmed()
                            && orderMessageDTO.getPrice() != null) {
                        orderDetail.setStatus(OrderStatusEnum.RESTAURANT_CONFIRMED);
                        orderDetail.setPrice(orderMessageDTO.getPrice());
                        orderDetailMapper.update(orderDetail);
                        // 向骑手微服务发送消息
                        try (
                                Connection connection = connectionFactory.newConnection();
                                Channel channel = connection.createChannel()
                        ) {
                            String messageToSend = JSONUtils.objectToJson(orderMessageDTO);
                            assert messageToSend != null;
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
                /*------------------ 订单为商家已确认状态 ------------------*/
                case RESTAURANT_CONFIRMED:
                    // 如果订单状态为商家已确认状态：
                    // 根据项目流程图，说明订单微服务已经收到了骑手微服务发送的消息，接下来就要向结算微服务发送消息
                    // 首先判读 DTO 中骑手 ID 是否为空，如果为空则将 DTO 的订单状态设置为失败
                    // 如果不为空，则将 PO 中的订单状态设置为骑手已确认，并设置骑手ID，将更新的数据持久化到数据库
                    // 并向结算微服务发送消息
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
                            assert messageToSend != null;
                            // 因为 exchange.order.settlement 交换机 为 fanout （广播）模式，所以 routingKey 是什么无关紧要
                            channel.basicPublish(
                                    "exchange.settlement.order",
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
