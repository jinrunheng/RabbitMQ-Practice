package com.github.takeaway.service;

import com.github.takeaway.dao.IOrderDetailDao;
import com.github.takeaway.dto.OrderMessageDTO;
import com.github.takeaway.entity.OrderDetail;
import com.github.takeaway.enummeration.OrderStatusEnum;
import com.github.takeaway.utils.JSONUtils;
import com.github.takeaway.vo.OrderCreateVO;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
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
public class OrderService {

    @Resource
    private IOrderDetailDao orderDetailDao;

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

        orderDetailDao.insert(orderDetail);

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

            // s : exchange
            // s1 : routing key
            channel.basicPublish(
                    "exchange.order.restaurant",
                    "key.restaurant",
                    null,
                    message.getBytes()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
