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
            channel.exchangeDeclare(
                    "exchange.settlement.order",
                    BuiltinExchangeType.FANOUT,
                    true,
                    false,
                    null
            );

            channel.queueDeclare(
                    "queue.settlement",
                    true,
                    false,
                    false,
                    null
            );

            channel.queueBind(
                    "queue.settlement",
                    "exchange.order.settlement",
                    "key.settlement"
            );

            channel.basicConsume(
                    "queue.settlement",
                    true,
                    deliverCallback,
                    consumerTag -> {

                    }
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    DeliverCallback deliverCallback = ((consumerTag, message) -> {
        String messageBody = new String(message.getBody());

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try {
            OrderMessageDTO orderMessageDTO = (OrderMessageDTO) JSONUtils.jsonToObject(messageBody, OrderMessageDTO.class);

            Integer transactionId = settlementService.settlement(orderMessageDTO.getAccountId(), orderMessageDTO.getPrice());
            Settlement settlement = Settlement.builder()
                    .amount(orderMessageDTO.getPrice())
                    .date(new Date())
                    .orderId(orderMessageDTO.getOrderId())
                    .transactionId(transactionId)
                    .status(SettlementStatus.SUCCESS)
                    .build();

            settlementDao.insert(settlement);


            try (
                    Connection connection = connectionFactory.newConnection();
                    Channel channel = connection.createChannel();
            ) {
                String messageToSend = JSONUtils.objectToJson(orderMessageDTO);
                channel.basicPublish(
                        "exchange.settlement.order",
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
    });
}
