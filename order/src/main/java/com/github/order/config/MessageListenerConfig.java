package com.github.order.config;

import com.github.order.dto.OrderMessageDTO;
import com.github.order.entity.OrderDetail;
import com.github.order.enummeration.OrderStatusEnum;
import com.github.order.mapper.OrderDetailMapper;
import com.github.order.utils.JSONUtils;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @Author Dooby Kim
 * @Date 2023/3/7 10:17 下午
 * @Version 1.0
 */
@Configuration
@Slf4j
public class MessageListenerConfig {

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer messageListenerContainer = new SimpleMessageListenerContainer(connectionFactory);
        messageListenerContainer.setQueueNames("queue.order");
        messageListenerContainer.setConcurrentConsumers(3);
        messageListenerContainer.setMaxConcurrentConsumers(5);
        // 开启自动确认 ack
        // messageListenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
        // 设置回调方法，相当于 channel.basicConsume
        // messageListenerContainer.setMessageListener(this::handle);

        // 消费端开启手动确认
        messageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        messageListenerContainer.setMessageListener((ChannelAwareMessageListener) this::handle);
        // 消费端限流
        messageListenerContainer.setPrefetchCount(20);
        return messageListenerContainer;
    }

    private void handle(Message message, Channel channel) {
        try {
            // 消费端开启手动确认
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            handle(message);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void handle(Message message) {
        String msg = new String(message.getBody());
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
                        String messageToSend = JSONUtils.objectToJson(orderMessageDTO);
                        assert messageToSend != null;
                        // 向骑手微服务发送消息
                        rabbitTemplate.send("exchange.order.deliveryman",
                                "key.deliveryman",
                                new Message(messageToSend.getBytes())
                        );
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
                    // 首先判读 DTO 中骑手 ID 是否为空，如果为空则将 PO 的订单状态设置为失败
                    // 如果不为空，则将 PO 中的订单状态设置为骑手已确认，并设置骑手ID，将更新的数据持久化到数据库
                    // 并向结算微服务发送消息
                    if (orderMessageDTO.getDeliverymanId() != null) {
                        orderDetail.setStatus(OrderStatusEnum.DELIVERYMAN_CONFIRMED);
                        orderDetail.setDeliverymanId(orderMessageDTO.getDeliverymanId());
                        orderDetailMapper.update(orderDetail);
                        // 将消息发送给结算微服务
                        String messageToSend = JSONUtils.objectToJson(orderMessageDTO);
                        assert messageToSend != null;
                        rabbitTemplate.send("exchange.settlement.order",
                                "key.settlement",
                                new Message(messageToSend.getBytes()));
                    } else {
                        orderDetail.setStatus(OrderStatusEnum.ORDER_FAILED);
                        orderDetailMapper.update(orderDetail);
                    }
                    break;
                /*------------------ 订单为骑手已确认状态 ------------------*/
                case DELIVERYMAN_CONFIRMED:
                    // 如果订单状态为骑手已确认状态：
                    // 根据项目流程图，说明订单微服务已经收到了结算微服务发送的消息，接下来就要向积分微服务发送消息
                    // 首先判读 DTO 中结算 ID 是否为空，如果为空则将 PO 的订单状态设置为失败
                    // 如果不为空，则将 PO 中的订单状态设置为结算已确认，并设置结算ID，将更新的数据持久化到数据库
                    // 并向积分微服务发送消息
                    if (orderMessageDTO.getSettlementId() != null) {
                        orderDetail.setStatus(OrderStatusEnum.SETTLEMENT_CONFIRMED);
                        orderDetail.setSettlementId(orderMessageDTO.getSettlementId());
                        orderDetailMapper.update(orderDetail);
                        // 给积分微服务发送消息
                        String messageToSend = JSONUtils.objectToJson(orderMessageDTO);
                        assert messageToSend != null;
                        rabbitTemplate.send(
                                "exchange.order.reward",
                                "key.reward",
                                new Message(messageToSend.getBytes())
                        );
                    } else {
                        // 如果返回订单消息体中 settleId 为空，则代表订单失败
                        orderDetail.setStatus(OrderStatusEnum.ORDER_FAILED);
                        orderDetailMapper.update(orderDetail);
                    }
                    break;
                /*------------------ 订单为结算已确认状态 ------------------*/
                // 如果订单状态为结算已确认状态：
                // 根据项目流程图，说明订单微服务已经收到了积分微服务发送的消息
                // 首先判读 DTO 中积分 ID 是否为空，如果为空则将 PO 的订单状态设置为失败
                // 如果不为空，则将 PO 中的订单状态设置为订单创建成功，并将更新的数据持久化到数据库
                case SETTLEMENT_CONFIRMED:

                    if (orderMessageDTO.getRewardId() != null) {
                        orderDetail.setStatus(OrderStatusEnum.ORDER_CREATED);
                        orderDetail.setRewardId(orderMessageDTO.getRewardId());
                    } else {
                        orderDetail.setStatus(OrderStatusEnum.ORDER_FAILED);
                    }
                    orderDetailMapper.update(orderDetail);
                case ORDER_CREATED:
                case ORDER_FAILED:
                    break;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
