package com.github.order.config;

import com.github.order.service.OrderMessageService;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * 该类的作用是作为一个配置类，在 SpringBoot 启动时，去找到这样一个配置类，去启动异步线程调用 OrderMessageService.handleMessage
 *
 * @Author Dooby Kim
 * @Date 2022/10/31 9:02 下午
 * @Version 1.0
 */
@Configuration
public class RabbitConfig {

    @Autowired
    private OrderMessageService orderMessageService;

    @Autowired
    @Order(2)
    public void startListenMessage() {
        orderMessageService.handleMessage();
    }

    @Autowired
    @Order(1)
    public void initRabbit() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");

        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);

        // 声明队列 queue.order
        Queue queue = new Queue("queue.order");
        rabbitAdmin.declareQueue(queue);

        /*-------------------- restaurant --------------------*/
        // 声明订单与商家微服务使用的 Exchange
        Exchange orderRestaurantExchange = new DirectExchange("exchange.order.restaurant");
        rabbitAdmin.declareExchange(orderRestaurantExchange);
        // 将 exchange.order.restaurant 这个 Exchange 与 queue.order 这个队列进行绑定（Binding）
        Binding orderBinding = new Binding("queue.order",
                Binding.DestinationType.QUEUE,
                "exchange.order.restaurant",
                "key.order",
                null);
        rabbitAdmin.declareBinding(orderBinding);
        /*-----------------------------------------------------*/

        /*-------------------- deliveryman --------------------*/
        // 声明订单与骑手微服务使用的 Exchange
        Exchange orderDeliverymanExchange = new DirectExchange("exchange.order.deliveryman");
        rabbitAdmin.declareExchange(orderDeliverymanExchange);
        // 将 exchange.order.deliveryman 这个 Exchange 与 queue.order 这个队列进行绑定（Binding）
        orderBinding = new Binding("queue.order",
                Binding.DestinationType.QUEUE,
                "exchange.order.deliveryman",
                "key.order",
                null);
        rabbitAdmin.declareBinding(orderBinding);
        /*-----------------------------------------------------*/

        /*-------------------- settlement --------------------*/
        // 声明订单与结算微服务使用的 Exchange
        Exchange orderSettlementExchange = new FanoutExchange("exchange.order.settlement");
        rabbitAdmin.declareExchange(orderSettlementExchange);
        orderBinding = new Binding("queue.order",
                Binding.DestinationType.QUEUE,
                "exchange.order.settlement",
                "key.order",
                null);
        rabbitAdmin.declareBinding(orderBinding);
        /*-----------------------------------------------------*/

        /*-------------------- reward --------------------*/
        // 声明订单与积分微服务使用的 Exchange
        Exchange orderRewardExchange = new TopicExchange("exchange.order.reward");
        rabbitAdmin.declareExchange(orderRewardExchange);
        orderBinding = new Binding("queue.order",
                Binding.DestinationType.QUEUE,
                "exchange.order.reward",
                "key.order",
                null);
        rabbitAdmin.declareBinding(orderBinding);
        /*-------------------- reward --------------------*/

    }
}
