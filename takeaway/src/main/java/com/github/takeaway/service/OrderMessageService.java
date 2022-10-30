package com.github.takeaway.service;

import com.github.takeaway.dao.IOrderDetailDao;
import com.github.takeaway.dto.OrderMessageDTO;
import com.github.takeaway.entity.OrderDetail;
import com.github.takeaway.enummeration.OrderStatusEnum;
import com.github.takeaway.utils.JSONUtils;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
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
    private IOrderDetailDao orderDetailDao;

    /**
     * 声明消息队列，交换机，绑定，消息的处理
     */
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


        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    DeliverCallback deliverCallback = ((consumerTag, message) -> {
        String msg = new String(message.getBody());

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try {
            // 将消息体反序列化为 DTO
            OrderMessageDTO orderMessageDTO = (OrderMessageDTO) JSONUtils.jsonToObject(msg, OrderMessageDTO.class);
            // 从数据库中读取订单
            OrderDetail orderDetail = orderDetailDao.queryOrder(orderMessageDTO.getOrderId());

            // 判断订单状态
            switch (orderDetail.getStatus()) {

                case ORDER_CREATING:
                    // 订单为创建中状态
                    // 如果商家已确认，且价格不为空，则将订单状态设置为商家已确认,并设置价格
                    if (orderMessageDTO.getConfirmed() && orderMessageDTO.getPrice() != null) {
                        orderDetail.setStatus(OrderStatusEnum.RESTAURANT_CONFIRMED);
                        orderDetail.setPrice(orderMessageDTO.getPrice());
                        orderDetailDao.update(orderDetail);
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
                        orderDetailDao.update(orderDetail);
                    }
                    break;
                case RESTAURANT_CONFIRMED:
                    break;
                case DELIVERYMAN_CONFIRMED:
                    break;
                case SETTLEMENT_CONFIRMED:
                    break;
                case ORDER_CREATED:
                    break;
                case ORDER_FAILED:
                    break;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    });
}
