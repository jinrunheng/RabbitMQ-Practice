package com.github.order.config;

import com.github.order.service.OrderMessageService;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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
    @Order(4)
    public void startListenMessage() {
        orderMessageService.handleMessage();
    }


    /**
     * 声明队列 queue.order
     *
     * @return
     */
    @Bean
    @Order(3)
    public Queue orderQueue() {
        return new Queue("queue.order");
    }

    /*--------------------- restaurant --------------------*/

    /**
     * 声明订单与商家微服务使用的 Exchange
     *
     * @return
     */
    @Bean
    public Exchange orderRestaurantExchange() {
        return new DirectExchange("exchange.order.restaurant");
    }

    /**
     * exchange.order.restaurant 这个 Exchange 与 queue.order 这个队列进行绑定（Binding）
     *
     * @return
     */
    @Bean
    public Binding orderRestaurantBinding() {
        return new Binding("queue.order",
                Binding.DestinationType.QUEUE,
                "exchange.order.restaurant",
                "key.order",
                null);
    }

    /*--------------------- restaurant --------------------*/

    /*--------------------- deliveryman --------------------*/

    /**
     * 声明订单与骑手微服务使用的 Exchange
     *
     * @return
     */
    @Bean
    public Exchange orderDeliverymanExchange() {
        return new DirectExchange("exchange.order.deliveryman");
    }

    /**
     * exchange.order.deliveryman 这个 Exchange 与 queue.order 这个队列进行绑定（Binding）
     *
     * @return
     */
    @Bean
    public Binding orderDeliverymanBinding() {
        return new Binding("queue.order",
                Binding.DestinationType.QUEUE,
                "exchange.order.deliveryman",
                "key.order",
                null);
    }

    /*--------------------- deliveryman --------------------*/

    /*--------------------- settlement --------------------*/

    /**
     * 声明订单与结算微服务使用的 Exchange
     *
     * @return
     */
    @Bean
    public Exchange orderSettlementExchange() {
        return new FanoutExchange("exchange.order.settlement");
    }

    /**
     * exchange.order.settlement 这个 Exchange 与 queue.order 这个队列进行绑定（Binding）
     *
     * @return
     */
    @Bean
    public Binding orderSettlementBinding() {
        return new Binding("queue.order",
                Binding.DestinationType.QUEUE,
                "exchange.order.settlement",
                "key.order",
                null);
    }
    /*--------------------- settlement --------------------*/

    /*--------------------- reward --------------------*/

    /**
     * 声明订单与积分微服务使用的 Exchange
     *
     * @return
     */
    @Bean
    public Exchange orderRewardExchange() {
        return new TopicExchange("exchange.order.reward");
    }

    /**
     * exchange.order.reward 这个 Exchange 与 queue.order 这个队列进行绑定（Binding）
     *
     * @return
     */
    @Bean
    public Binding orderRewardBinding() {
        return new Binding("queue.order",
                Binding.DestinationType.QUEUE,
                "exchange.order.reward",
                "key.order",
                null);
    }

    /*--------------------- reward --------------------*/

    /**
     * 将 ConnectionFactory 交给 Spring 管理
     *
     * @return
     */
    @Bean
    @Order(1)
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.createConnection();
        return connectionFactory;
    }

    /**
     * 将 RabbitAdmin 交给 Spring 管理
     *
     * @param connectionFactory
     * @return
     */
    @Bean
    @Order(2)
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

}
