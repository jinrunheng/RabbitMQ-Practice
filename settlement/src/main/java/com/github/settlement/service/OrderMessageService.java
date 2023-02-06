package com.github.settlement.service;

import com.github.settlement.dao.ISettlementDao;
import com.github.settlement.dto.OrderMessageDTO;
import com.github.settlement.entity.Settlement;
import com.github.settlement.enummeration.SettlementStatus;
import com.github.settlement.utils.JSONUtils;
import com.rabbitmq.client.*;
import com.rabbitmq.tools.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author Dooby Kim
 * @Date 2022/11/3 9:43 下午
 * @Version 1.0
 */
@Service
@Slf4j
public class OrderMessageService {

    @Autowired
    private SettlementService settlementService;

    @Autowired
    private ISettlementDao settlementDao;

    @Async
    public void handleMessage() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        try (
                Connection connection = connectionFactory.newConnection();
                Channel channel = connection.createChannel()
        ) {
            // 声明订单与结算微服务使用的 Exchange
            channel.exchangeDeclare(
                    "exchange.settlement.order",
                    BuiltinExchangeType.FANOUT,
                    true,
                    false,
                    null
            );

            // 声明队列
            channel.queueDeclare(
                    "queue.settlement",
                    true,
                    false,
                    false,
                    null
            );

            // Queue 与 Exchange 绑定
            channel.queueBind(
                    "queue.settlement",
                    "exchange.settlement.order",
                    "key.settlement"
            );

            channel.basicConsume(
                    "queue.settlement",
                    true,
                    deliverCallback,
                    consumerTag -> {
                    }
            );

            while (true) {
                Thread.sleep(1000000);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    DeliverCallback deliverCallback = (this::handle);

    private void handle(String consumerTag, Delivery message) {


        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try {
            String messageBody = new String(message.getBody());
            OrderMessageDTO orderMessageDTO = (OrderMessageDTO) JSONUtils.jsonToObject(messageBody, OrderMessageDTO.class);

            assert orderMessageDTO != null;
            Integer transactionId = settlementService.settlement(orderMessageDTO.getAccountId(), orderMessageDTO.getPrice());
            Settlement settlement = Settlement.builder()
                    .amount(orderMessageDTO.getPrice())
                    .date(new Date())
                    .orderId(orderMessageDTO.getOrderId())
                    .transactionId(transactionId)
                    .status(SettlementStatus.SUCCESS)
                    .build();

            settlementDao.insert(settlement);
            orderMessageDTO.setSettlementId(settlement.getId());
            // 向订单微服务发送消息
            try (
                    Connection connection = connectionFactory.newConnection();
                    Channel channel = connection.createChannel()
            ) {
                String messageToSend = JSONUtils.objectToJson(orderMessageDTO);
                assert messageToSend != null;
                channel.basicPublish(
                        "exchange.order.settlement",
                        "key.order",
                        null,
                        messageToSend.getBytes()
                );
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
