package com.github.order.service;

import com.github.order.dto.OrderMessageDTO;
import com.github.order.entity.OrderDetail;
import com.github.order.enummeration.OrderStatusEnum;
import com.github.order.mapper.OrderDetailMapper;
import com.github.order.utils.JSONUtils;
import com.github.order.vo.OrderCreateVO;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Author Dooby Kim
 * @Date 2022/10/29 9:46 下午
 * @Version 1.0
 * 处理用户关于订单的业务请求
 */
@Service
@Slf4j
public class OrderService {

    @Resource
    private OrderDetailMapper orderDetailMapper;

    /**
     * 创建订单
     * <p>
     * 逻辑流程：
     * 1. 创建订单，将订单持久化到数据库
     * 2. 向商家微服务发送消息
     *
     * @param createVO
     */
    public void createOrder(OrderCreateVO createVO) {
        OrderDetail orderDetail = OrderDetail.builder()
                .address(createVO.getAddress())
                .accountId(createVO.getAccountId())
                .productId(createVO.getProductId())
                .status(OrderStatusEnum.ORDER_CREATING)
                .date(new Date())
                .build();

        orderDetailMapper.insert(orderDetail);

        OrderMessageDTO orderMessageDTO = OrderMessageDTO.builder()
                .orderId(orderDetail.getId())
                .productId(orderDetail.getProductId())
                .accountId(orderDetail.getAccountId())
                .build();

        // 向商家微服务发送消息
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
            String message = JSONUtils.objectToJson(orderMessageDTO);
            // 消息确认机制开启
            channel.confirmSelect();
            // s : exchange
            // s1 : routing key
            channel.basicPublish(
                    "exchange.order.restaurant",
                    "key.restaurant",
                    null,
                    message.getBytes()
            );

            log.info("message send");
            if (channel.waitForConfirms()) {
                log.info("RabbitMQ confirm success");
            } else {
                log.error("RabbitMQ confirm failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
