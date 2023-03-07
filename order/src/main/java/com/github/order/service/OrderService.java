package com.github.order.service;

import com.github.order.dto.OrderMessageDTO;
import com.github.order.entity.OrderDetail;
import com.github.order.enummeration.OrderStatusEnum;
import com.github.order.mapper.OrderDetailMapper;
import com.github.order.utils.JSONUtils;
import com.github.order.vo.OrderCreateVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 处理用户关于订单的业务请求
 *
 * @Author Dooby Kim
 * @Date 2022/10/29 9:46 下午
 * @Version 1.0
 */
@Service
@Slf4j
public class OrderService {


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Resource
    private OrderDetailMapper orderDetailMapper;

    /**
     * 创建订单
     * <p>
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

        // 持久化保存到数据库
        orderDetailMapper.insert(orderDetail);

        OrderMessageDTO orderMessageDTO = OrderMessageDTO.builder()
                .orderId(orderDetail.getId())
                .productId(orderDetail.getProductId())
                .accountId(orderDetail.getAccountId())
                .build();

        // 使用 RabbitTemplate 向商家微服务发送消息
        String messageString = JSONUtils.objectToJson(orderMessageDTO);
        MessageProperties messageProperties = new MessageProperties();
        //  设置单条消息 TTL 为 1 min
        messageProperties.setExpiration("60000");
        assert messageString != null;
        Message message = new Message(messageString.getBytes(), messageProperties);
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(orderDetail.getId().toString());
        rabbitTemplate.send(
                "exchange.order.restaurant",
                "key.restaurant",
                message,
                correlationData
        );

//        rabbitTemplate.convertAndSend(
//                "exchange.order.restaurant",
//                "key.restaurant",
//                messageString
//        );

        log.info("message send");
//        // 向商家微服务发送消息
//        ConnectionFactory connectionFactory = new ConnectionFactory();
//        connectionFactory.setHost("localhost");
//        try (Connection connection = connectionFactory.newConnection();
//             Channel channel = connection.createChannel()) {
//            String message = JSONUtils.objectToJson(orderMessageDTO);
//            // 消息确认机制开启
//            channel.confirmSelect();
//            // 发送消息
//            assert message != null;
//
//            // 设置单条消息 TTL 为 1 min
//            AMQP.BasicProperties properties = new AMQP.BasicProperties()
//                    .builder()
//                    .expiration("60000")
//                    .build();
//
//
//            channel.basicPublish(
//                    "exchange.order.restaurant",
//                    "key.restaurant",
//                    properties,
//                    message.getBytes()
//            );
//
//            log.info("message send");
//            if (channel.waitForConfirms()) {
//                log.info("RabbitMQ confirm success");
//            } else {
//                log.error("RabbitMQ confirm failed");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }
}
